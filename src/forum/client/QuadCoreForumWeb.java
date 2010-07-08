package forum.client; 




import com.google.gwt.core.client.EntryPoint; 
import com.google.gwt.core.client.GWT; 
import com.google.gwt.user.client.rpc.AsyncCallback; 
import com.google.gwt.user.client.ui.RootPanel;
//import com.google.gwt.user.client.ui.PasswordTextField; 
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

import com.extjs.gxt.ui.client.widget.Info;


/** 
 * Entry point classes define <code>onModuleLoad()</code>. 
 */ 
public class QuadCoreForumWeb implements EntryPoint { 

	/** 
	 * This is the entry point method. 
	 */ 
	final Button registerButton = new Button("Register"); 
	final Button cancelButton = new Button("Cancel"); 
	final TextField<String> username = new TextField<String>(); 
	final TextField<String> password = new TextField<String>(); 
	final TextField<String> confirmPassword = new TextField<String>(); 
	final TextField<String> email = new TextField<String>(); 
	final TextField<String> confirmEmail = new TextField<String>(); 
	final TextField<String> firstName = new TextField<String>(); 
	final TextField<String> lastName = new TextField<String>(); 
	private final ControllerServiceAsync controlAsync = GWT.create(ControllerService.class); 

	public void onModuleLoad() {  
		//final Window tRegistrationPanel = new Window();
		FormPanel tRegistrationPanel = new FormPanel();

//		tRegistrationPanel.setLayout(new FormLayout());
		tRegistrationPanel.setAutoHeight(true);
		//tRegistrationPanel.setAutoWidth(true);
		//tRegistrationPanel.setModal(true);

		

		username.setFieldLabel("Username");
		username.setMinLength(4);
		username.setMaxLength(8);
		username.setAllowBlank(false);

		password.setFieldLabel("Password");
		password.setMinLength(6);
		password.setPassword(true);
		password.setAllowBlank(false);

		confirmPassword.setPassword(true);
		confirmPassword.setFieldLabel("Confirm Password");
		confirmPassword.setAllowBlank(false);

		lastName.setFieldLabel("Last Name");
		lastName.setAllowBlank(false);
		lastName.setAllowBlank(false);

		firstName.setFieldLabel("First Name");
		firstName.setAllowBlank(false);
		firstName.setAllowBlank(false);


		
		email.setFieldLabel("E-mail");
		email.setValidator(new Validator()  {
			
			public String validate(Field<?> field, String value) {
				if (!value.matches(".+@.+\\.[a-z]+"))
						return "Invaild address. The email address must be in the form: username@domain.extension";
				return null;
			}
		});
		
//		email.setRegex(".+@.+\\.[a-z]+");
		email.setAllowBlank(false);

		username.setValidateOnBlur(false);
		password.setValidateOnBlur(false);
		confirmPassword.setValidator(new Validator() {

			public String validate(Field<?> field, String value) {
				if (value.equals(password.getValue()))
					return null;
				return "The password fields must be identical";
			}
		});
		confirmPassword.setValidateOnBlur(false);
		firstName.setValidateOnBlur(false);
		lastName.setValidateOnBlur(false);
		email.setValidateOnBlur(false);

		confirmEmail.setAllowBlank(false);

		confirmEmail.setFieldLabel("Confirm Email");
		confirmEmail.setValidator(new Validator() {
			public String validate(Field<?> field, String value) {
				if (value.equals(email.getValue()))
					return null;
				return "The email fields must be identical";
			}
		});

		confirmEmail.setValidateOnBlur(false);

		username.setStyleName("label");
		password.setStyleName("label");
		confirmPassword.setStyleName("label");
		lastName.setStyleName("label");
		firstName.setStyleName("label");
		email.setStyleName("label");
		confirmEmail.setStyleName("label");

		username.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		password.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		confirmPassword.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		lastName.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		firstName.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		email.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		confirmEmail.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");

		
		tRegistrationPanel.add(username);
		tRegistrationPanel.add(password);
		tRegistrationPanel.add(confirmPassword);
		tRegistrationPanel.add(lastName);
		tRegistrationPanel.add(firstName);
		tRegistrationPanel.add(email);
		tRegistrationPanel.add(confirmEmail);


		/*		
		final HorizontalPanel forUserName = new HorizontalPanel(); 
		final Label userNameLabel =new Label("Username: "); 
		forUserName.add(userNameLabel); 
		forUserName.add(userName); 

		final HorizontalPanel forPassword = new HorizontalPanel(); 
		final Label passwordLabel =new Label("Password:"); 
		forPassword.add(passwordLabel); 
		forPassword.add(password); 


		final HorizontalPanel forConfirmPassword = new HorizontalPanel(); 
		final Label confirmPasswordLabel =new Label("Confirm Password:"); 
		forConfirmPassword.add(confirmPasswordLabel); 
		forConfirmPassword.add(confirmPassword); 

		final HorizontalPanel forEmail = new HorizontalPanel(); 
		final Label emailLabel = new Label("e-mail:"); 
		forEmail.add(emailLabel); 
		forEmail.add(email); 

		final HorizontalPanel forConfirmEmail = new HorizontalPanel(); 
		final Label confirmEmailLabel = new Label("Confirm e-mail:"); 
		forConfirmEmail.add(confirmEmailLabel); 
		forConfirmEmail.add(confirmEmail); 

		final HorizontalPanel forFirstName = new HorizontalPanel(); 
		final Label firstNameLabel =new Label("First Name:"); 
		forFirstName.add(firstNameLabel); 
		forFirstName.add(firstName); 

		lastName.setFieldLabel("kkkk");
		lastName.setMinLength(4);
		lastName.setMessageTarget("side");

		final HorizontalPanel forLastName = new HorizontalPanel(); 
		//final Label lastNameLabel =new Label("Last Name :"); 
		//forLastName.add(lastNameLabel); 
		forLastName.add(lastName); 



		final HorizontalPanel forButtons = new HorizontalPanel(); 
		forButtons.add(registerButton); 
		forButtons.add(cancelButton); 


		final VerticalPanel mainPanel= new VerticalPanel(); 
		mainPanel.insert(forUserName, 0); 
		mainPanel.insert(forPassword, 1); 
		mainPanel.insert(forConfirmPassword, 2); 
		mainPanel.insert(forEmail, 3); 
		mainPanel.insert(forConfirmEmail, 4); 
		mainPanel.insert(forFirstName, 5); 
		mainPanel.insert(forLastName, 6); 
		mainPanel.insert(forButtons, 7); 


		userName.setValue("1sfdhsf");
		password.setValue("123456");
		confirmPassword.setValue("123456");
		firstName.setValue("abcdef");
		lastName.setValue("fhfhhf");
		email.setValue("ff@gmg.com");
		confirmEmail.setValue("ff@gmg.com");
		 */

		tRegistrationPanel.setButtonAlign(HorizontalAlignment.CENTER);
		tRegistrationPanel.addButton(registerButton);
		tRegistrationPanel.addButton(cancelButton);
		//tRegistrationPanel.setStyleName("registrationForm");
		//tRegistrationPanel.setLabelWidth(100);
		tRegistrationPanel.setHeading("Registration Form");
		tRegistrationPanel.setWidth(450);
		//tRegistrationPanel.setBlinkModal(false);

		//tRegistrationPanel.show();



		registerButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {


				if (checkDataValidity()) {

					System.out.println("2");

					RegisterMessage toSend = new RegisterMessage(username.getValue(),
							password.getValue(), lastName.getValue(), firstName.getValue(), email.getValue()); 
					controlAsync.registerToForum(toSend, new AsyncCallback<ServerResponse>(){ 


						public void onFailure(Throwable caught) { 
							//Window.alert("NOT WORKING !"); 

						} 

						@Override 
						public void onSuccess(ServerResponse result) { 
							String tEncodedResult = result.getResponse();
							if (tEncodedResult.startsWith("registersuccess\t")) {
								Info.display("Register Success", "The registration process was completed successfully!");
								//tRegistrationPanel.hide();
							}
							else if (tEncodedResult.startsWith("registererror\t")) {
								String[] tSplittedMessage = tEncodedResult.split("\t");
								String tErrorMessage = tSplittedMessage[1];
								com.google.gwt.user.client.Window.alert(tErrorMessage);
							}
						}}); 
				} 
			}}); 




				RootPanel.get("registerPanelId").add(tRegistrationPanel); 


	} 


	public boolean checkDataValidity() {
		username.clearInvalid();
		password.clearInvalid();
		confirmPassword.clearInvalid();
		lastName.clearInvalid();
		firstName.clearInvalid();
		email.clearInvalid();
		confirmEmail.clearInvalid();
		return username.validate() && password.validate() && confirmPassword.validate() &&
		lastName.validate() && firstName.validate() && email.validate() && confirmEmail.validate();
	} 
} 
