/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.UserModel;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.user.MemberAlreadyExistsException;

/**
 * @author Freifeld Royi
 *
 */
public class RegistrationAndProfileForm  extends LayoutContainer
{

	private FormPanel registrationPanel;
	private final Button registerButton = new Button("Register"); 
	private final Button cancelButton = new Button("Cancel"); 
	private final TextField<String> txt_username = new TextField<String>(); 
	private final TextField<String> password = new TextField<String>(); 
	private final TextField<String> confirmPassword = new TextField<String>(); 
	private final TextField<String> txt_email = new TextField<String>(); 
	private final TextField<String> confirmEmail = new TextField<String>(); 
	private final TextField<String> txt_firstName = new TextField<String>(); 
	private final TextField<String> txt_lastName = new TextField<String>();
	private SelectionListener<ButtonEvent> registrationListener; 
	private SelectionListener<ButtonEvent> changingProfileListener; 


	public RegistrationAndProfileForm() {
		registrationPanel = new FormPanel();

		//tRegistrationPanel.setAutoHeight(true);
		//		this.setAutoHeight(true);

		txt_username.setFieldLabel("Username");
		txt_username.setMinLength(4);
		txt_username.setMaxLength(8);
		txt_username.setAllowBlank(false);

		password.setFieldLabel("Password");
		password.setMinLength(6);
		password.setPassword(true);
		password.setAllowBlank(false);

		confirmPassword.setPassword(true);
		confirmPassword.setFieldLabel("Confirm Password");
		confirmPassword.setAllowBlank(false);

		txt_lastName.setFieldLabel("Last Name");
		txt_lastName.setAllowBlank(false);
		txt_lastName.setAllowBlank(false);

		txt_firstName.setFieldLabel("First Name");
		txt_firstName.setAllowBlank(false);
		txt_firstName.setAllowBlank(false);



		txt_email.setFieldLabel("E-mail");
		txt_email.setValidator(new Validator() 
		{
			public String validate(Field<?> field, String value) 
			{
				if (!value.matches(".+@.+\\.[a-z]+"))
					return "Invaild address. The email address must be in the form: username@domain.extension";
				return null;
			}
		});

		txt_email.setAllowBlank(false);

		txt_username.setValidateOnBlur(false);
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
		txt_firstName.setValidateOnBlur(false);
		txt_lastName.setValidateOnBlur(false);
		txt_email.setValidateOnBlur(false);

		confirmEmail.setAllowBlank(false);

		confirmEmail.setFieldLabel("Confirm Email");
		confirmEmail.setValidator(new Validator() 
		{
			public String validate(Field<?> field, String value) 
			{
				if (value.equals(txt_email.getValue()))
					return null;
				return "The email fields must be identical";
			}
		});

		confirmEmail.setValidateOnBlur(false);

		txt_username.setStyleName("label");
		password.setStyleName("label");
		confirmPassword.setStyleName("label");
		txt_lastName.setStyleName("label");
		txt_firstName.setStyleName("label");
		txt_email.setStyleName("label");
		confirmEmail.setStyleName("label");
		/*
		username.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		password.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		confirmPassword.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		lastName.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		firstName.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		email.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		confirmEmail.setLabelStyle("padding-left: 0.5cm; padding-top: 0.35cm; padding-bottom: 0.25cm; width: 4cm");
		 */

		registrationPanel.setLabelAlign(LabelAlign.LEFT);


		registrationPanel.setButtonAlign(HorizontalAlignment.RIGHT);
		registrationPanel.addButton(registerButton);
		registrationPanel.addButton(cancelButton);
		registrationPanel.setHeading("Registration Form");



		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				MainPanel.changeMainViewToSubjectsAndThreads();
			}
		});


		registrationListener = new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {
				boolean valid = txt_username.validate() && password.validate() && confirmPassword.validate() && 
				txt_lastName.validate() && txt_firstName.validate() && txt_email.validate() && confirmEmail.validate();
				if (valid) 
				{
					registerButton.setEnabled(false);
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Registers...");
					QuadCoreForumWeb.SERVICE.registerToForum(txt_username.getValue(),
							password.getValue(), txt_lastName.getValue(), txt_firstName.getValue(), txt_email.getValue(), new AsyncCallback<Void>()
							{ 
						public void onFailure(Throwable caught) {
							registerButton.setEnabled(true);
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
							if (caught instanceof MemberAlreadyExistsException)
								MessageBox.alert("Member already exists", 
										caught.getMessage(), 
										new Listener<MessageBoxEvent>() {
									@Override
									public void handleEvent(MessageBoxEvent be) {
										txt_username.selectAll();
										txt_email.selectAll();
									}
								});
							else if (caught instanceof DatabaseUpdateException)
								MessageBox.alert("Communication error", "can't communicate with the " +
										"forum server", null);
						} 

						@Override 
						public void onSuccess(Void result) 
						{
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
							MainPanel.changeMainViewToSubjectsAndThreads();
							Info.display("Register Success", "The registration process was completed successfully!");
						}
							}
					); 
				}
			}
		};

		changingProfileListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				boolean valid = txt_lastName.validate() && txt_firstName.validate() && txt_email.validate() && confirmEmail.validate();
				if (valid) {
					registerButton.setEnabled(false);
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Updates profile...");


					QuadCoreForumWeb.SERVICE.updateMemberProfile(
							QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
							txt_username.getValue(),
							txt_firstName.getValue(), 
							txt_lastName.getValue(),
							txt_email.getValue(),
							new AsyncCallback<UserModel>() {
								public void onFailure(Throwable caught) {
									registerButton.setEnabled(true);
									QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
									if (caught instanceof MemberAlreadyExistsException)
										MessageBox.alert("Member already exists", 
												caught.getMessage(), 
												new Listener<MessageBoxEvent>() {
											@Override
											public void handleEvent(MessageBoxEvent be) {
												txt_username.selectAll();
												txt_email.selectAll();
											}
										});
									else if (caught instanceof DatabaseUpdateException)
										MessageBox.alert("Communication error", "can't communicate with the " +
												"forum server", null);
								} 

								@Override 
								public void onSuccess(UserModel result) 
								{
									QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
									MainPanel.changeMainViewToSubjectsAndThreads();
									QuadCoreForumWeb.CONNECTED_USER_DATA = result;
									MainPanel tMainPanel = Registry.get("MainPanel");
									tMainPanel.changeLoginView();
									Info.display("Profile update success", "The profile was updated successfully");

								}
							}
					);























				}
			}
		};
	}



	public void setupProfileForm(String username, String firstName, String lastName, String email) {
		registrationPanel.add(txt_username);
		txt_username.setEnabled(false);
		registrationPanel.add(txt_lastName);
		registrationPanel.add(txt_firstName);
		registrationPanel.add(txt_email);
		registrationPanel.add(confirmEmail);
		txt_username.setValue(username);
		txt_lastName.setValue(lastName);
		txt_firstName.setValue(firstName);
		txt_email.setValue(email);
		confirmEmail.setValue(email);
		registrationPanel.setHeading("Profile changing form");
		registerButton.addSelectionListener(changingProfileListener);
		registerButton.setText("Change");
	}

	public void setupRegistrationForm() {
		registrationPanel.add(txt_username);
		registrationPanel.add(password);
		registrationPanel.add(confirmPassword);
		registrationPanel.add(txt_lastName);
		registrationPanel.add(txt_firstName);
		registrationPanel.add(txt_email);
		registrationPanel.add(confirmEmail);
		registerButton.addSelectionListener(registrationListener);
	}


	@Override
	protected void onRender(Element target, int index)
	{
		super.onRender(target,index);



		this.setLayout(new FitLayout());


		this.add(registrationPanel);


	} 
}
