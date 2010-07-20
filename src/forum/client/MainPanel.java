package forum.client; 

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainPanel extends LayoutContainer
{ 
	/*All private Members*/
	private final ContentPanel miscellaneousPanel = new ContentPanel();
	private final ContentPanel mainContentPanel = new ContentPanel();
	private final ContentPanel settingsPanel = new ContentPanel();  
	private final ContentPanel navigatorPanel = new ContentPanel();  

	private AsyncThreadsTableGrid threadsTable = new AsyncThreadsTableGrid();
	
	private final BorderLayout rootPanelLayout = new BorderLayout();


	final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	protected void onRender(Element target, int index) {  
		super.onRender(target, index);

		setLayout(rootPanelLayout); 
		setStyleAttribute("padding", "10px");  

		this.mainContentPanelInit();
		this.miscellaneousPanelInit();

		this.layout();

		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if (QuadCoreForumWeb.CONNECTED_USER_DATA != null)
					service.disconnectClient(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(), new AsyncCallback<Void>() {
						public void onSuccess(Void result) {}
						public void onFailure(Throwable caught) {}
					});
			}
		});
		
	} 

	private void mainContentPanelInit() {
		this.mainContentPanel.setHeading("Main Content");
		this.mainContentPanel.setBorders(true);

		//Main Content Panel
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));
		centerData.setCollapsible(false);

		this.mainContentPanel.add(threadsTable);
		
		add(this.mainContentPanel, centerData);
	}

	private void settingsPanelInit() {
		settingsPanel.setHeading("Settings");  
		settingsPanel.setBorders(false);  
		settingsPanel.setCollapsible(true);
		settingsPanel.setSize(300, 200);
		settingsPanel.setBodyStyle("fontSize: 12px; padding: 0px");  
		settingsPanel.collapse();
		this.loadSettingsLinks();
		miscellaneousPanel.add(settingsPanel);
		/*
		settingsPanel.addListener(Events.Collapse, new collap {
			@Override
			public void resizeEnd(ResizeEvent re) {
			System.out.println("ddddddddddddddddddddddddddddddddddddd " + settingsPanel.getHeight());
				// TODO Auto-generated method stub
				super.resizeEnd(re);
			}
		});*/
		
		settingsPanel.setBottomComponent(this.navigatorPanel);
		
	}

	private void navigationPanelInit() {
		
		navigatorPanel.setHeading("Navigation");  
		navigatorPanel.setBorders(false);
		navigatorPanel.setBodyStyle("" +
				"fontSize: 12px; padding: 0px");  
		navigatorPanel.setScrollMode(Scroll.AUTOY);
		navigatorPanel.setCollapsible(false);

		Viewport v = new Viewport();
		v.add(new AsyncSubjectsTreeGrid(threadsTable));
		v.setLayout(new FitLayout());
		navigatorPanel.add(v);
		this.navigatorPanel.setTopComponent(this.settingsPanel);
		
		
		
		miscellaneousPanel.add(navigatorPanel);
	}

	private void miscellaneousPanelInit() {
		this.miscellaneousPanel.setHeading("QuadCoreForum");
		this.miscellaneousPanel.setBorders(true);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300, 100, 300);
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setMargins(new Margins(0,5,0,0));
		add(this.miscellaneousPanel, westData);
//		miscellaneousPanel.setLayout(new AnchorLayout());

		
		
		this.settingsPanelInit();
		this.navigationPanelInit();
		miscellaneousPanel.layout();

	}

	
	private void loadSettingsLinks() {
		this.settingsPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {

				System.out.println("ddddddddddddddddddddd");
				System.out.println("eeeeeeeeeeeeeeeeeeeee");

			}
		}));
	}


	/**
	 * loads all links appearing on the navigation panel
	 */
	private void loadNavigatorLinks() {
		this.miscellaneousPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
/*
				service.getSubjects(-1, 
						new AsyncCallback<ServerResponse>() {

					@Override
					public void onSuccess(ServerResponse result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});*/
			}
		}));




		/*		this.navigatorPanel.add(createButton("Register", new Listener<ButtonEvent>() 
				{

			@Override
			public void handleEvent(ButtonEvent be) 
			{
				if (!be.<ToggleButton>getComponent().isPressed())
				{
					return;
				}
				RegistrationForm rf = new RegistrationForm();

				addToMainContent(rf);					
			}

				}));
		 */	
	}

	private void addToMainContent(LayoutContainer c)
	{
		this.mainContentPanel.removeAll();
		this.mainContentPanel.add(c);
		this.mainContentPanel.layout();
	}

	private ToggleButton createButton(String text, Listener<ButtonEvent> l)
	{
		ToggleButton btn = new ToggleButton(text);

		btn.setToggleGroup("navigationbuttons");
		btn.addListener(Events.Toggle, l);
		btn.setAllowDepress(true);

		return btn;
	}
}
