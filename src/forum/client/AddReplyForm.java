
package forum.client;  


import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.MessageModel;

public class AddReplyForm extends LayoutContainer {  

	private MessageModel messageModel;
	
	private final ContentPanel previousPanel = new ContentPanel();
	private FormPanel panel = new FormPanel();

	private TextField<String> titleField;

	private HtmlEditor contentField;
	
	@Override  
	protected void onRender(Element parent, int index) { 
		previousPanel.setHeading("Previous message");
		super.onRender(parent, index);
		this.setSize(650, 400);  
		this.setLayout(new BorderLayout());
		
		this.createColumnForm();
	}

	public void setMessage(MessageModel message) {
		this.messageModel = message;
		this.layout();
		XTemplate tpl = XTemplate.create("<p><b>Date of publish:</b> {date}</p><br>" +
				"<p><b>Author:</b> {authorUsername}</p><br>" +
				"<p><b>Title:</b> {title}</p><br>" +
		"<p><b>Content:</b><br> {content}</p>");  		
		
		previousPanel.removeAll();  
		previousPanel.addText(tpl.applyTemplate(Util.getJsObject(message, 3)));  
		previousPanel.layout();  
	}
	

	private void createColumnForm() {  

		BorderLayoutData tPreviousData = new BorderLayoutData(LayoutRegion.NORTH, 200, 0, 400);
		tPreviousData.setCollapsible(true);
		tPreviousData.setFloatable(true);
		
		
		BorderLayoutData tReplyData = new BorderLayoutData(LayoutRegion.CENTER, Short.MAX_VALUE, 0, Short.MAX_VALUE);
		
		panel.setHeading("Add new reply");  

		
		panel.setHeaderVisible(true);
		panel.setFrame(false);  
//		panel.setLayout(new FlowLayout());

		//panel.setLayout(new FlowLayout());
		panel.setFieldWidth(400);
		
		titleField = new TextField<String>();
		titleField.setFieldLabel("Title");
		titleField.setBorders(true);
		
		panel.add(titleField, new FormData("-20"));  
		
		
		contentField = new HtmlEditor();  
		contentField.setFieldLabel("Content");
		panel.add(contentField, new FormData("-20"));
		
		Button tReplyButton = new Button("Reply");
		
		tReplyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				QuadCoreForumWeb.SERVICE.addReplyToMessage(
						QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						messageModel.getID(), 
						titleField.getValue(), 
						contentField.getValue(),
						new AsyncCallback<MessageModel>() {
					
					@Override
					public void onSuccess(MessageModel result) {
						
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
		});
		
		
		panel.addButton(tReplyButton);
		panel.addButton(new Button("Cancel"));  

		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		this.add(panel, tReplyData);

		this.add(previousPanel, tPreviousData);
		
	}	
}  
