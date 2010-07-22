package forum.client;

import java.util.Collection;
import java.util.Vector;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import forum.shared.ActiveConnectedData;
import forum.shared.ConnectedUserData;
import forum.shared.Permission;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.tcpcommunicationlayer.ServerResponse;

public class QuadCoreForumWeb implements EntryPoint {
	public static ConnectedUserData CONNECTED_USER_DATA = null;

	private ToolBar statusBar; 
	private MainPanelToolBar toolBar;

	public static Status GUESTS_NUMBER_STATUS;  
	public static Status MEMBERS_NUMBER_STATUS;
	public static Status WORKING_STATUS = new Status();  

	private MainPanel mainPanel;

	private ContentPanel mainContentPanel;
	
	public static ControllerServiceAsync SERVICE = GWT.create(ControllerService.class);
	
	@Override
	public void onModuleLoad() {
		
		Registry.register("Servlet", SERVICE);
		
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if (QuadCoreForumWeb.CONNECTED_USER_DATA != null)
					SERVICE.disconnectClient(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(), 
							new AsyncCallback<Void>() {
						public void onSuccess(Void result) {}
						public void onFailure(Throwable caught) {}
					});
			}
		});

		mainPanel = new MainPanel();
		Registry.register("MainPanel", mainPanel);

		
		mainContentPanel = new ContentPanel();
		mainContentPanel.setBorders(false);
		mainContentPanel.setHeaderVisible(false);
		mainContentPanel.setFrame(false);
		mainContentPanel.setLayout(new FitLayout());
		this.mainContentPanel.add(mainPanel);

		this.initializeToolBar();
		this.initializeStatusBar();

		connectAsGuest();
		Viewport v = new Viewport();  
		v.setLayout(new FitLayout());  
		v.add(mainContentPanel, new FitData(5)); // FitData controls the margins of the mp
		RootPanel.get("mainPanelID").add(v);


//		new AddReplyForm().show();

	}

	public static void connectAsGuest() {
		SERVICE.addNewGuest(new AsyncCallback<ServerResponse>() {
			@Override
			public void onSuccess(ServerResponse result) {
				String encodedView = result.getResponse();
				String[] tSplitted = encodedView.split("\n");
				String[] tUserDetails = tSplitted[0].split("\t");
				long connectedUserID = Long.parseLong(tUserDetails[0]);
				Collection<Permission> tPermissions = new Vector<Permission>();
				for (int i = 1; i < tSplitted.length; i++)
					tPermissions.add(Permission.valueOf(tSplitted[i]));
				QuadCoreForumWeb.CONNECTED_USER_DATA = new ConnectedUserData(connectedUserID, tPermissions);
				((MainPanel)Registry.get("MainPanel")).changeLoginView();
				((MainPanelToolBar)Registry.get("ToolBar")).switchToGuestView();
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof DatabaseUpdateException) {
					Window.alert("Can't communicate with the server!");
					// TODO: Close the client window
				}
			}
		});
	}
	
	private void initializeToolBar() {
		this.toolBar = new MainPanelToolBar();
		Registry.register("ToolBar", toolBar);
		this.mainContentPanel.setTopComponent(toolBar);  
	}

	private void initializeStatusBar() {
		statusBar = new ToolBar(); 
		WORKING_STATUS.setText("Not working");
		WORKING_STATUS.setStyleAttribute("font-weight", "bold");
		WORKING_STATUS.setWidth(150);  
		statusBar.add(WORKING_STATUS);  
		statusBar.add(new FillToolItem());  

		GUESTS_NUMBER_STATUS = new Status();  
		GUESTS_NUMBER_STATUS.setStyleAttribute("font-weight", "bold");
		GUESTS_NUMBER_STATUS.setWidth(120);  
		GUESTS_NUMBER_STATUS.setText("0 Guests");  
		GUESTS_NUMBER_STATUS.setBox(true);  
		statusBar.add(GUESTS_NUMBER_STATUS);  
		statusBar.add(new LabelToolItem(" "));  
		MEMBERS_NUMBER_STATUS = new Status();  
		MEMBERS_NUMBER_STATUS.setWidth(120);  
		MEMBERS_NUMBER_STATUS.setText("0 Members"); 
		MEMBERS_NUMBER_STATUS.setStyleAttribute("font-weight", "bold");
		MEMBERS_NUMBER_STATUS.setBox(true);  
		statusBar.add(MEMBERS_NUMBER_STATUS);  


		Timer tTimer = new Timer() {

			@Override
			public void run() {
				updateActiveConnectedNumbers();
			}
		};
		// every 4 seconds the numbers of users and guests will be updating
		tTimer.scheduleRepeating(5000);
		tTimer.run();

		mainContentPanel.setBottomComponent(statusBar);

	}	

	public static void updateActiveConnectedNumbers() {
		WORKING_STATUS.setBusy("updating connected numbers ...");  
		SERVICE.getActiveUsersNumber(new AsyncCallback<ActiveConnectedData>() {

			@Override
			public void onFailure(Throwable caught) {
				WORKING_STATUS.clearStatus("Not working");
			}

			@Override
			public void onSuccess(ActiveConnectedData result) {
				long tGuestsNumber = result.getGuestsNumber();
				GUESTS_NUMBER_STATUS.setText(tGuestsNumber + ((tGuestsNumber == 1)? " Guest" : " Guests"));
				Collection<String> activeUsernames = result.getActiveUsernames();
				MEMBERS_NUMBER_STATUS.setText(activeUsernames.size() +
						((activeUsernames.size() == 1)? " Members" : " Members"));
				WORKING_STATUS.clearStatus("Not working");
			}
		});
	}

	//public static  native void close() /*-{  $wnd.close();}-*/;
}
