/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.ConnectedUserData;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;

/**
 * @author sepetnit
 *
 */
public class MainPanelToolBar extends ToolBar {
	private Button fastLoginButton = new Button("Login");
	private Button loginButton = new Button("Login");
	private Button registerButton = new Button("Register");
	private Button searchButton = new Button("Search");
	private TextField<String> tPassword;
	private TextField<String> tUsername;
	
	public MainPanelToolBar() {
		super();
		tUsername = new TextField<String>();
		tPassword = new TextField<String>();
		this.fastLoginButton = new Button("Login");
		this.loginButton = new Button("Login");
		this.registerButton = new Button("Register");
		this.searchButton = new Button("Search");

		Button tFastLoginButtonMenu = new Button("Fast Login"); 

		Menu tFastLoginMenu = new Menu();
		tFastLoginMenu.add(new LabelToolItem());
		tFastLoginMenu.add(tUsername);  
		tFastLoginMenu.add(new LabelToolItem());
		tPassword.setPassword(true);
		tFastLoginMenu.add(tPassword);
		tFastLoginMenu.add(new LabelToolItem());
		
		
		ContentPanel tPanel = new ContentPanel();
		tPanel.setButtonAlign(HorizontalAlignment.RIGHT);
		tPanel.setHeaderVisible(false);
		tPanel.setBorders(false);
		tPanel.setFrame(false);
		tPanel.addButton(fastLoginButton);
		tPanel.setStyleName("transparent");
		tFastLoginMenu.add(tPanel);
		
		
		tFastLoginButtonMenu.setMenu(tFastLoginMenu);
		
		this.add(tFastLoginButtonMenu);


		this.add(loginButton);
		
		this.add(new SeparatorMenuItem());
		
		this.add(registerButton);
		
		this.add(searchButton);
		
		this.addLoginFunctionality();
	}
	
	private void addLoginFunctionality() {
		
		final AsyncCallback<ConnectedUserData> tLoginCallBack = new AsyncCallback<ConnectedUserData>() {
			private MainPanel mainPanel = Registry.get("MainPanel");
			
			@Override
			public void onSuccess(ConnectedUserData result) {
				QuadCoreForumWeb.CONNECTED_USER_DATA = result;
				mainPanel.setConnectedUserName();
				QuadCoreForumWeb.updateActiveConnectedNumbers();
				Info.display("Login success", "Hello " + 
						QuadCoreForumWeb.CONNECTED_USER_DATA.getLastName() + 
						" " +
						QuadCoreForumWeb.CONNECTED_USER_DATA.getFirstName() + "! Welcome to " +
						" the QuadCoreForum");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof NotRegisteredException ||
						(caught instanceof WrongPasswordException))
					MessageBox.alert("Login error", "Wrong username or password", null);
				else if (caught instanceof DatabaseRetrievalException)
					MessageBox.alert("Login error", "The connection to the forum server failed " +
							" please try again later", null);
			}
		};

		
		// TODO: Open login dialog
		
		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				QuadCoreForumWeb.SERVICE.login(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						"admin", "1234", tLoginCallBack);
			}
			
		});
		
	}
}
