/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

/**
 * @author Freifeld Royi
 *
 */
public class RegistrationForm  extends FormPanel
{
	protected void onRender(Element target, int index)
	{
		super.onRender(target,index);
		final Button registerButton = new Button("Register"); 
		final Button cancelButton = new Button("Cancel"); 
		final TextField<String> username = new TextField<String>(); 
		final TextField<String> password = new TextField<String>(); 
		final TextField<String> confirmPassword = new TextField<String>(); 
		final TextField<String> email = new TextField<String>(); 
		final TextField<String> confirmEmail = new TextField<String>(); 
		final TextField<String> firstName = new TextField<String>(); 
		final TextField<String> lastName = new TextField<String>(); 
		final ControllerServiceAsync controlAsync = GWT.create(ControllerService.class); 
		
		System.out.println("please be here");
		
		//FormPanel tRegistrationPanel = new FormPanel();

		//tRegistrationPanel.setAutoHeight(true);
		this.setAutoHeight(true);

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
		email.setValidator(new Validator() 
		{
			public String validate(Field<?> field, String value) 
			{
				if (!value.matches(".+@.+\\.[a-z]+"))
					return "Invaild address. The email address must be in the form: username@domain.extension";
				return null;
			}
		});

		email.setAllowBlank(false);

		username.setValidateOnBlur(false);
		password.setValidateOnBlur(false);
		confirmPassword.setValidator(new Validator() 
		{
			public String validate(Field<?> field, String value) 
			{
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
		confirmEmail.setValidator(new Validator() 
		{
			public String validate(Field<?> field, String value) 
			{
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


		/*tRegistrationPanel.add(username);
		tRegistrationPanel.add(password);
		tRegistrationPanel.add(confirmPassword);
		tRegistrationPanel.add(lastName);
		tRegistrationPanel.add(firstName);
		tRegistrationPanel.add(email);
		tRegistrationPanel.add(confirmEmail);
		


		tRegistrationPanel.setButtonAlign(HorizontalAlignment.CENTER);
		tRegistrationPanel.addButton(registerButton);
		tRegistrationPanel.addButton(cancelButton);
		tRegistrationPanel.setHeading("Registration Form");
		tRegistrationPanel.setWidth(450);*/
		
		this.add(username);
		this.add(password);
		this.add(confirmPassword);
		this.add(lastName);
		this.add(firstName);
		this.add(email);
		this.add(confirmEmail);
		


		this.setButtonAlign(HorizontalAlignment.CENTER);
		this.addButton(registerButton);
		this.addButton(cancelButton);
		this.setHeading("Registration Form");
		this.setWidth(450);

		registerButton.addSelectionListener(new SelectionListener<ButtonEvent>() 
				{

					public void componentSelected(ButtonEvent ce) 
					{
						boolean valid = username.validate() && password.validate() && confirmPassword.validate() && 
							lastName.validate() && firstName.validate() && email.validate() && confirmEmail.validate();
						if (valid) 
						{
							//TODO WTF is this ?! System.out.println("2");
		
							RegisterMessage toSend = new RegisterMessage(username.getValue(),
									password.getValue(), lastName.getValue(), firstName.getValue(), email.getValue()); 
							controlAsync.registerToForum(toSend, new AsyncCallback<ServerResponse>()
									{ 
										public void onFailure(Throwable caught) 
										{ 
											//TODO Window.alert("NOT WORKING !"); 
										} 
				
										@Override 
										public void onSuccess(ServerResponse result) 
										{ 
											String tEncodedResult = result.getResponse();
											if (tEncodedResult.startsWith("registersuccess\t")) {
												Info.display("Register Success", "The registration process was completed successfully!");
												//tRegistrationPanel.hide();
											}
											else if (tEncodedResult.startsWith("registererror\t")) 
											{
												String[] tSplittedMessage = tEncodedResult.split("\t");
												String tErrorMessage = tSplittedMessage[1];
												com.google.gwt.user.client.Window.alert(tErrorMessage);
											}
										}
									}
							); 
						}
						else
						{
							username.clearInvalid();
							password.clearInvalid();
							confirmPassword.clearInvalid();
							lastName.clearInvalid();
							firstName.clearInvalid();
							email.clearInvalid();
							confirmEmail.clearInvalid();
						}
					}
			}); 
	} 
}
