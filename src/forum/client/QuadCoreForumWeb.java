package forum.client;

import java.util.Collection;
import java.util.Vector;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import forum.shared.Permission;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.tcpcommunicationlayer.ServerResponse;

public class QuadCoreForumWeb implements EntryPoint {
	public static ConnectedUserData CONNECTED_USER_DATA = null;

	@Override
	public void onModuleLoad() {
		ControllerServiceAsync service = GWT.create(ControllerService.class);
		Registry.register("Servlet", service);
		service.addNewGuest(new AsyncCallback<ServerResponse>() {
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
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof DatabaseUpdateException) {
					Window.alert("Can't communicate with the server!");
					// TODO: Close the client window
				}
			}
		});
		MainPanel mp = new MainPanel();
		Viewport v = new Viewport();  
		v.setLayout(new FitLayout());  
		v.add(mp, new FitData(5)); // FitData controls the margins of the mp
		RootPanel.get("mainPanelID").add(v);
	}
   
	//public static  native void close() /*-{  $wnd.close();}-*/;

}
