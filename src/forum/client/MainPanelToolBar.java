/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
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

import forum.shared.UserModel;
import forum.shared.UserModel.UserType;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;

/**
 * @author sepetnit
 *
 */
public class MainPanelToolBar extends ToolBar {
	private Button fastLoginButton = new Button("Login");
	private Button loginButton = new Button("Login");
	private Button logoutButton = new Button("Logout");
	private Button registerButton = new Button("Register");
	private Button searchButton = new Button("Search");
	private TextField<String> fastLoginPasswordField;
	private TextField<String> fastLoginUsernameField;
	private Button fastLoginMenuButton;

	private Button showMembersButton;
	private Button changeProfileButton;

	private boolean fastLoginAsked;
	private String fastLoginUsername;
	private String fastLoginPassword;


	public MainPanelToolBar() {
		super();
		showMembersButton = new Button("Show Members");
		showMembersButton.setVisible(false);
		changeProfileButton = new Button("Change Profile");
		changeProfileButton.setVisible(false);
		showMembersButton.setVisible(false);

		fastLoginUsernameField = new TextField<String>();
		fastLoginUsernameField.setFieldLabel("Username");
		fastLoginPasswordField = new TextField<String>();
		fastLoginPasswordField.setFieldLabel("Password");

		fastLoginUsernameField.setMaxLength(20);
		fastLoginPasswordField.setMaxLength(20);
		this.fastLoginButton = new Button("Login");
		this.loginButton = new Button("Login");
		this.registerButton = new Button("Register");
		this.searchButton = new Button("Search", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				MainPanel.changeMainViewToPanel(new SearchPanel());
			}
		});
		
		fastLoginMenuButton = new Button("Fast Login"); 
		fastLoginMenuButton.setVisible(false);

		Menu tFastLoginMenu = new Menu();
		tFastLoginMenu.add(new LabelToolItem());
		tFastLoginMenu.add(fastLoginUsernameField);  
		tFastLoginMenu.add(new LabelToolItem());
		fastLoginPasswordField.setPassword(true);
		tFastLoginMenu.add(fastLoginPasswordField);
		tFastLoginMenu.add(new LabelToolItem());

		
		registerButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				MainPanel.changeMainViewToPanel((RegistrationForm)Registry.get("RegistrationForm"));
			}
		});
		
		
		
		
		
		
		fastLoginMenuButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fastLoginUsernameField.selectAll();
				fastLoginUsernameField.focus();

			}});


		tFastLoginMenu.addListener(Events.BeforeHide, new Listener<MenuEvent>() {

			@Override
			public void handleEvent(MenuEvent be) {
				fastLoginUsernameField.clear();
				fastLoginPasswordField.clear();
			}
		});


		ContentPanel tPanel = new ContentPanel();
		tPanel.setButtonAlign(HorizontalAlignment.RIGHT);
		tPanel.setHeaderVisible(false);
		tPanel.setBorders(false);
		tPanel.setFrame(false);
		tPanel.addButton(fastLoginButton);
		tPanel.setStyleName("transparent");
		tFastLoginMenu.add(tPanel);


		fastLoginMenuButton.setMenu(tFastLoginMenu);

		this.add(fastLoginMenuButton);


		this.add(loginButton);

		this.logoutButton.setVisible(false);
		this.add(logoutButton);

		this.add(new SeparatorMenuItem());

		this.add(registerButton);

		this.add(searchButton);

		this.add(showMembersButton);
		this.add(changeProfileButton);

		this.addLoginFunctionality();
	}

	private void addLoginFunctionality() {

		final AsyncCallback<UserModel> tLoginCallBack = new AsyncCallback<UserModel>() {
			private MainPanel mainPanel = Registry.get("MainPanel");

			@Override
			public void onSuccess(UserModel result) {
				fastLoginMenuButton.setEnabled(true);
				loginButton.setEnabled(true);
				QuadCoreForumWeb.CONNECTED_USER_DATA = result;
				mainPanel.changeLoginView();
				QuadCoreForumWeb.updateActiveConnectedNumbers();
				fastLoginMenuButton.hideMenu();
				if (QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.ADMIN)
					switchToAdministratorView();
				else
					switchToRegisteredView();
				Info.display("Login success", "Hello " + 
						QuadCoreForumWeb.CONNECTED_USER_DATA.getLastName() + 
						" " +
						QuadCoreForumWeb.CONNECTED_USER_DATA.getFirstName() + "! Welcome to " +
				" the QuadCoreForum");
				fastLoginAsked = false;
			}

			@Override
			public void onFailure(Throwable caught) {
				fastLoginMenuButton.setEnabled(true);
				loginButton.setEnabled(true);

				if (caught instanceof NotRegisteredException ||
						(caught instanceof WrongPasswordException)) {
					MessageBox.alert("Login error", "Wrong username or password",

							new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (fastLoginAsked) {
								System.out.println("udsdsdsddsd");
								fastLoginMenuButton.showMenu();
								fastLoginUsernameField.setValue(fastLoginUsername);
								fastLoginPasswordField.setValue(fastLoginPassword);
								fastLoginUsernameField.selectAll();
								fastLoginUsernameField.focus();
							}
							fastLoginAsked = false;
						}
					});
				}
				else if (caught instanceof DatabaseRetrievalException)
					MessageBox.alert("Login error", "The connection to the forum server failed " +
							" please try again later", null);
			}
		};

		fastLoginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (QuadCoreForumWeb.CONNECTED_USER_DATA == null)
					return;
				fastLoginAsked = true;
				fastLoginMenuButton.setEnabled(false);
				loginButton.setEnabled(false);
				fastLoginUsername = fastLoginUsernameField.getValue();
				fastLoginPassword = fastLoginPasswordField.getValue();
				QuadCoreForumWeb.SERVICE.login(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						fastLoginUsername, 
						fastLoginPassword, 
						tLoginCallBack);
			}

		});

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fastLoginMenuButton.setEnabled(false);
				loginButton.setEnabled(false);
				// TODO: Open login dialog
				
				MainPanel.changeMainViewToPanel(new LoginForm());
				
//				QuadCoreForumWeb.SERVICE.login(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
//						"admin", "1234", tLoginCallBack);
			}

		});

		logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				logoutButton.setEnabled(false);
				QuadCoreForumWeb.WORKING_STATUS.setBusy("Logging out...");
				QuadCoreForumWeb.SERVICE.logout(
						QuadCoreForumWeb.CONNECTED_USER_DATA.getUsername(), 
						new AsyncCallback<UserModel>() {
							private MainPanel mainPanel = Registry.get("MainPanel");

							@Override
							public void onFailure(Throwable caught) {
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");

								logoutButton.setEnabled(true);
								if (caught instanceof forum.shared.exceptions.user.NotConnectedException) {
									QuadCoreForumWeb.connectAsGuest();
								}
								else if (caught instanceof DatabaseUpdateException) {
									MessageBox.alert("Login error", "The connection to the forum server failed " +
											" please try again later", null);
								}
							}

							@Override
							public void onSuccess(UserModel result) {
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
								logoutButton.setEnabled(true);
								QuadCoreForumWeb.CONNECTED_USER_DATA = result;
								mainPanel.changeLoginView();
								switchToGuestView();
								QuadCoreForumWeb.updateActiveConnectedNumbers();
								Info.display("Logout success", "Disconnected successfully");
							}
						});
				}
		});

		fastLoginButton.setEnabled(false);

		Listener<FieldEvent> tLoginDataKeyPressListener = new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if (((fastLoginUsernameField.getValue() != null) &&
						(fastLoginUsernameField.getValue().length() > 0)) && 
						((fastLoginPasswordField.getValue() != null) &&
								(fastLoginPasswordField.getValue().length() > 0)) 
								&& fastLoginUsernameField.validate() &&
								fastLoginPasswordField.validate())
					fastLoginButton.setEnabled(true);
				else
					fastLoginButton.setEnabled(false);
			}
		};

		fastLoginUsernameField.addListener(Events.OnKeyPress, tLoginDataKeyPressListener);
		fastLoginPasswordField.addListener(Events.OnKeyPress, tLoginDataKeyPressListener);
	}

	public void switchToGuestView() {
		fastLoginMenuButton.setVisible(false);
		loginButton.setVisible(true);
		registerButton.setVisible(true);
		logoutButton.setVisible(false);
		showMembersButton.setVisible(false);
		changeProfileButton.setVisible(false);
	}

	private void switchToMemberView() {
		fastLoginMenuButton.setVisible(false);
		loginButton.setVisible(false);
		registerButton.setVisible(false);
		logoutButton.setVisible(true);
		changeProfileButton.setVisible(true);
	}

	public void switchToAdministratorView() {
		switchToMemberView();
		showMembersButton.setVisible(true);
	}

	public void switchToRegisteredView() {
		switchToMemberView();
		showMembersButton.setVisible(false);
	}
}
