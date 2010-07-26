package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.UserModel;
import forum.shared.UserModel.UserType;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;

public class LoginForm extends FormPanel {


	protected void onRender(Element target, int index)
	{
		super.onRender(target,index);
		final Button loginButton = new Button("login"); 
		final Button cancelButton = new Button("Cancel"); 
		final TextField<String> username = new TextField<String>(); 
		final TextField<String> password = new TextField<String>(); 
		

		
		username.setFieldLabel("Username");
		username.setMinLength(4);
		username.setMaxLength(8);
		username.setAllowBlank(false);
		
		password.setFieldLabel("Password");
		password.setMinLength(6);
		password.setPassword(true);
		password.setAllowBlank(false);
				
		username.setValidateOnBlur(false);
		password.setValidateOnBlur(false);
		
		username.setStyleName("label");
		password.setStyleName("label");
	
		
		username.setLabelStyle("padding-left: 0.05cm; padding-top: 0.015cm; padding-bottom: 0.015cm; width: 2cm");
		password.setLabelStyle("padding-left: 0.05cm; padding-top: 0.015cm; padding-bottom: 0.015cm; width: 2cm");
		
		this.add(username);
		this.add(password);
	
						
		this.setBodyBorder(true);
		
		this.addButton(loginButton);
		this.addButton(cancelButton);
		this.add(loginButton);
		this.add(cancelButton);
	    
		
		
	    loginButton.setVisible(true);
	//	cancelButton.setVisible(true);
		
		//this.setButtonAlign(HorizontalAlignment.CENTER);
		
		this.setHeading("Login Form");
		//this.setWidth(100);
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				MainPanel.changeMainViewToSubjectsAndThreads();
			}
		});

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() 
				{

					public void componentSelected(ButtonEvent ce) 
					{
						//boolean valid = username.validate() && password.validate();
						//if (valid) 
						//{
							//TODO WTF is this ?! System.out.println("2");
		
							
							// TODO: Open login dialog
							final AsyncCallback<UserModel> tLoginCallBack = new AsyncCallback<UserModel>() {
								private MainPanel mainPanel = Registry.get("MainPanel");
								private MainPanelToolBar mptb = Registry.get("ToolBar");
								@Override
								public void onSuccess(UserModel result) {
									
									

									MainPanel.changeMainViewToSubjectsAndThreads();
									
									
									
									System.out.println("Lital");
									loginButton.setEnabled(false);
									username.clear();
									password.clear();
									
									QuadCoreForumWeb.CONNECTED_USER_DATA = result;
									mainPanel.changeLoginView();
									QuadCoreForumWeb.updateActiveConnectedNumbers();
								
									if (QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.ADMIN)
									
										mptb.switchToAdministratorView();
									else
										mptb.switchToRegisteredView();

									
									Info.display("Login success", "Hello " + 
											QuadCoreForumWeb.CONNECTED_USER_DATA.getLastName() + 
											" " +
											QuadCoreForumWeb.CONNECTED_USER_DATA.getFirstName() + "! Welcome to " +
									" the QuadCoreForum");
									
								//mainPanel.removeLogin();
								//mainPanel.layout();
								}

								@Override
								public void onFailure(Throwable caught) {
									loginButton.setEnabled(true);
								

									if (caught instanceof NotRegisteredException ||
											(caught instanceof WrongPasswordException)) {
										MessageBox.alert("Login error", "Wrong username or password",

												new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(MessageBoxEvent be) {

											}
										});
									}
									else if (caught instanceof DatabaseRetrievalException)
										MessageBox.alert("Login error", "The connection to the forum server failed " +
												" please try again later", null);
								}
							};

						
							
							QuadCoreForumWeb.SERVICE.login(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
									username.getValue(), password.getValue(), tLoginCallBack);
							
						
					
					}
			});
	
	} 

	
	


	

}
