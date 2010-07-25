
package forum.client;  


import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.message.SubjectAlreadyExistsException;

public class AddReplyModifyForm extends LayoutContainer {  

	// used for replying and modifying
	private MessageModel messageModel;
	private TreeGrid<MessageModel> messagesTree;
	private TreeStore<MessageModel> messagesStore;

	// used for threads opening
	private ListStore<ThreadModel> threadsStore;
	private Grid<ThreadModel> threadsGrid;

	// used for subjects adding and modifying
	private SubjectModel subjectModel;
	private TreePanel<SubjectModel> subjectsTree;
	private TreeStore<SubjectModel> subjectsStore;	

	private final ContentPanel previousPanel = new ContentPanel();
	private FormPanel panel = new FormPanel();

	private TextField<String> topicField;

	private TextField<String> titleOrNameField;

	private HtmlEditor contentOrDescriptionField;
	private Button replyModifyOpenAddButton;

	private long subjectID;




	private SelectionListener<ButtonEvent> replyListener;
	private SelectionListener<ButtonEvent> modifyListener;
	private SelectionListener<ButtonEvent> openThreadListener;
	private SelectionListener<ButtonEvent> addSubjectListener;

	private Button cancelButton;

	public AddReplyModifyForm() {
		replyModifyOpenAddButton = new Button("");
		cancelButton = new Button("Cancel");
		titleOrNameField = new TextField<String>();
		titleOrNameField.setAllowBlank(false);
		contentOrDescriptionField = new HtmlEditor();  
		topicField = new TextField<String>();
		topicField.setAllowBlank(false);
		titleOrNameField.setAutoValidate(true);
		topicField.setAutoValidate(true);

		
		addSubjectListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
			
				String tDescription = null;
				
				if (!titleOrNameField.validate())
					return;
				else if (emptyContent(contentOrDescriptionField.getValue())) {
					MessageBox.alert("Empty description", "The subject description can't be empty", new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							contentOrDescriptionField.focus();
						}
					});
					return;
				}
				else if ((tDescription = getMaxThreeLinesDescription(contentOrDescriptionField.getValue())) == null) {
					MessageBox.alert("Not valid content", "The subject description can't contain " +
							"more than 3 lines", new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							contentOrDescriptionField.focus();
						}
					});
					return;
				}

				QuadCoreForumWeb.WORKING_STATUS.setBusy("Adding new subject...");

				replyModifyOpenAddButton.setEnabled(false);
				cancelButton.setEnabled(false);

				QuadCoreForumWeb.SERVICE.addNewSubject(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						subjectID, titleOrNameField.getValue(),
						tDescription,
						new AsyncCallback<SubjectModel>() {

					@Override
					public void onFailure(Throwable caught) {
						replyModifyOpenAddButton.setEnabled(true);
						cancelButton.setEnabled(true);
						if (caught instanceof SubjectAlreadyExistsException) {
							MessageBox.alert("Duplicate subject name", "There already exists a subject " +
									"with the given name in this level, please change the name to be " +
									"a different one", new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									titleOrNameField.selectAll();
									titleOrNameField.focus();
								}
							});
						}
					}

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(SubjectModel result) {
						replyModifyOpenAddButton.setEnabled(true);
						cancelButton.setEnabled(true);
						if (subjectModel == null)
							subjectsStore.add(result, true);
						else
							subjectsStore.add(subjectModel, result, true);
						subjectsStore.commitChanges();
						subjectsTree.getSelectionModel().select(result, false);
						subjectsTree.getSelectionModel().fireEvent(Events.SelectionChange);
						MainPanel.changeMainViewToSubjectsAndThreads();			
						
						
						titleOrNameField.clear();
						contentOrDescriptionField.clear();
						if (topicField != null)
							topicField.clear();
						((TreePanel<SubjectModel>)Registry.get("SubjectsTree")).fireEvent(Events.OnMouseDown);
						
						Info.display("Successful subject adding", "The subject was added successfully");
					}
				});
			}
		};

		openThreadListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (!topicField.validate() || !titleOrNameField.validate())
					return;
				else {
					if (emptyContent(contentOrDescriptionField.getValue())) {
						MessageBox.alert("Empty content", "The message content can't be empty", null);
						return;
					}
				}
				QuadCoreForumWeb.WORKING_STATUS.setBusy("Opening thread...");
				QuadCoreForumWeb.SERVICE.addNewThread(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						subjectModel.getID(), topicField.getValue(),
						titleOrNameField.getValue(),
						contentOrDescriptionField.getValue(), new AsyncCallback<ThreadModel>() {
					@Override
					public void onFailure(Throwable caught) {
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}

					@Override
					public void onSuccess(ThreadModel result) {
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
						threadsStore.add(result);
						threadsStore.commitChanges();
						threadsGrid.getSelectionModel().select(result, false);
						MainPanel.changeMainViewToSubjectsAndThreads();
						titleOrNameField.clear();
						contentOrDescriptionField.clear();
						topicField.clear();
						Info.display("Successful thread openning", "The thread was added successfully");
					}
				});
			}
		};

		replyListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (!titleOrNameField.validate())
					return;
				else {
					if (emptyContent(contentOrDescriptionField.getValue())) {
						MessageBox.alert("Empty content", "The message content can't be empty", null);
						return;
					}
				}
				replyModifyOpenAddButton.setEnabled(false);
				QuadCoreForumWeb.WORKING_STATUS.setBusy("Adding reply...");
				QuadCoreForumWeb.SERVICE.addReplyToMessage(
						QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						messageModel.getID(), 
						titleOrNameField.getValue(), 
						trim(contentOrDescriptionField.getValue()),
						new AsyncCallback<MessageModel>() {

							@Override
							public void onFailure(Throwable caught) {
								replyModifyOpenAddButton.setEnabled(true);
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");

							}

							@Override
							public void onSuccess(MessageModel result) {
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
								messagesStore.add(messageModel, result, true);
								messagesStore.commitChanges();
//								messagesTree.getView().ensureVisible(
//										messagesTree.getView().findRowIndex(messagesTree.getView().getRow(result)),
//										0, false);
								MainPanel.changeMainViewToSubjectsAndThreads();
								messagesTree.getSelectionModel().select(result, false);
								titleOrNameField.clear();
								contentOrDescriptionField.clear();
								replyModifyOpenAddButton.setEnabled(true);
								Info.display("Successful reply", "The reply was added successfully");
							}
						});
			}
		};


		modifyListener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (!titleOrNameField.validate())
					return;
				else if (emptyContent(contentOrDescriptionField.getValue())) {
					MessageBox.alert("Empty content", "The message content can't be empty", null);
					return;
				}
				QuadCoreForumWeb.WORKING_STATUS.setBusy("Modifiying message...");

				QuadCoreForumWeb.SERVICE.modifyMessage(messageModel.getAuthorID(),
						messageModel.getID(), 
						titleOrNameField.getValue(),
						contentOrDescriptionField.getValue(), 
						new AsyncCallback<MessageModel>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						// TODO Auto-generated method stub
						// TODO: IF THE MESSAGE WAD DELETED
					}

					@Override
					public void onSuccess(MessageModel result) {
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
						messageModel.setTitle(result.getTitle());
						messageModel.setContent(result.getContent());
						messagesStore.commitChanges();

						MainPanel.changeMainViewToSubjectsAndThreads();
	
						messagesTree.getSelectionModel().select(result, false);

						titleOrNameField.clear();
						contentOrDescriptionField.clear();
						Info.display("Successful modification", "The message was modified successfully");
					}
				});
			}
		};


	}

	private String trim(String htmlContent) {
		System.out.println("beforrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr " + htmlContent);
		boolean cont = false;
		do {
			if (htmlContent.startsWith("<br>")) {
				htmlContent = htmlContent.substring(4);
				cont = true;
			}
			else if (htmlContent.startsWith("&nbsp;")) {
				htmlContent = htmlContent.substring(6);
				cont = true;
			}
			else
				cont = false;
		}
		while (cont);
		cont = false;
		do {
			if (htmlContent.endsWith("<br>")) {
				htmlContent = htmlContent.substring(0, htmlContent.length() - 4);
				cont = true;
			} 
			else if (htmlContent.startsWith("&nbsp;")) {
				htmlContent = htmlContent.substring(0, htmlContent.length() - 6);
				cont = true;
			}
			else
				cont = false;
		}
		while (cont);
		if (htmlContent.startsWith("?"))
			htmlContent = htmlContent.substring(1);
		if (htmlContent.endsWith("?"))
			htmlContent = htmlContent.substring(0, htmlContent.length() - 1);
			
		System.out.println("trimmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmed " + htmlContent);
		return htmlContent;
	}

	private String getMaxThreeLinesDescription(String htmlContent) {
		htmlContent = this.trim(htmlContent);
		if (htmlContent.split("<br>").length > 3)
			return null;
		else
			return htmlContent;
	}


	private boolean emptyContent(String htmlContent) {
		String tWithoutNewline = htmlContent.replace("<br>", "");
		String tWithoutSpace = tWithoutNewline.replace("&nbsp;", "");
		if (tWithoutSpace.trim().isEmpty())
			return true;
		else
			return false;
	}

	private void setPanelView(boolean addTopic) {
		panel.removeAll();
		if (addTopic)
			panel.add(topicField, new FormData("100%"));
		panel.add(titleOrNameField, new FormData("100%"));  
		panel.add(contentOrDescriptionField, new FormData("100% -53"));
		panel.addButton(replyModifyOpenAddButton);
		panel.addButton(cancelButton);  
	}

	private void createColumnForm() {  

		BorderLayoutData tPreviousData = new BorderLayoutData(LayoutRegion.NORTH, 150, 0, 400);
		tPreviousData.setCollapsible(true);
		tPreviousData.setFloatable(true);


		BorderLayoutData tReplyData = new BorderLayoutData(LayoutRegion.CENTER, Short.MAX_VALUE, 0, Short.MAX_VALUE);

		panel.setHeaderVisible(true);
		panel.setFrame(false);  
		panel.setFieldWidth(400);

		topicField.setFieldLabel("Topic");
		topicField.setBorders(true);

		titleOrNameField.setBorders(true);


		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				titleOrNameField.clear();
				contentOrDescriptionField.clear();
				topicField.clear();
				ContentPanel tMainViewPanel = ((ContentPanel)Registry.get("MainViewPanel")); 
				tMainViewPanel.removeAll();
				tMainViewPanel.add((TabPanel)Registry.get("maincontentpanel"));
				tMainViewPanel.layout();
			}
		});



		this.add(panel, tReplyData);

		this.add(previousPanel, tPreviousData);

	}

	private void initMessageReplyModifyDialog(MessageModel message, TreeGrid<MessageModel> messagesTree, TreeStore<MessageModel> store) {
		this.messagesTree = messagesTree;
		this.messagesStore = store;
		this.messageModel = message;
		titleOrNameField.setFieldLabel("Title");
		contentOrDescriptionField.setFieldLabel("Content");
		this.layout();
		previousPanel.setHeading("Previous message");
		XTemplate tpl = XTemplate.create("<p><b>Date of publish:</b> {date}</p><br>" +
				"<p><b>Author:</b> {authorUsername}</p><br>" +
				"<p><b>Title:</b> {title}</p><br>" +
		"<p><b>Content:</b><br> {content}</p>");  		

		previousPanel.removeAll();  
		previousPanel.addText(tpl.applyTemplate(Util.getJsObject(message, 3)));  
		previousPanel.layout();  

		setPanelView(false);

	}

	public void initModifyDialog(MessageModel message, TreeGrid<MessageModel> messagesTree, TreeStore<MessageModel> store) {
		initMessageReplyModifyDialog(message, messagesTree, store);
		this.titleOrNameField.setValue(message.getTitle());
		this.contentOrDescriptionField.setValue(message.getContent());
		this.replyModifyOpenAddButton.setText("Modify");
		this.replyModifyOpenAddButton.removeAllListeners();
		this.replyModifyOpenAddButton.addSelectionListener(modifyListener);
		panel.setHeading("Modify message");
	}	

	public void initReplyDialog(MessageModel message, TreeGrid<MessageModel> messagesTree, TreeStore<MessageModel> store) {
		initMessageReplyModifyDialog(message, messagesTree, store);
		this.replyModifyOpenAddButton.setText("Reply");
		this.replyModifyOpenAddButton.removeAllListeners();
		this.replyModifyOpenAddButton.addSelectionListener(replyListener);
		panel.setHeading("Add new reply");
	}

	public void initThreadsOpenningDialog(SubjectModel fatherSubject, Grid<ThreadModel> threadsGrid,
			ListStore<ThreadModel> threadsStore) {
		setPanelView(true);
		titleOrNameField.setFieldLabel("Title");
		contentOrDescriptionField.setFieldLabel("Content");
		this.threadsGrid = threadsGrid;
		this.threadsStore = threadsStore;
		this.replyModifyOpenAddButton.setText("Open new");
		this.replyModifyOpenAddButton.removeAllListeners();
		this.replyModifyOpenAddButton.addSelectionListener(this.openThreadListener);
		panel.setHeading("Open new thread");
		previousPanel.setHeading("Father subject");
		this.subjectModel = fatherSubject;
		XTemplate tpl = XTemplate.create("<p><b>Name:</b> {name}</p><br>" +
		"<p><b>Description:</b> {description}</p>");  		
		
		previousPanel.removeAll();
		previousPanel.addText(tpl.applyTemplate(Util.getJsObject(fatherSubject, 2)));  
		previousPanel.layout();
	}

	public void initAddSubjectDialog(SubjectModel fatherSubject, TreePanel<SubjectModel> subjectsTree,
			TreeStore<SubjectModel> subjectsStore) {
		setPanelView(false);
		titleOrNameField.setFieldLabel("Name");
		contentOrDescriptionField.setFieldLabel("Description");
		this.subjectID = fatherSubject == null? -1 : fatherSubject.getID();
		this.subjectModel = fatherSubject;
		this.subjectsTree = subjectsTree;
		this.subjectsStore = subjectsStore;
		this.replyModifyOpenAddButton.setText("Add");
		this.replyModifyOpenAddButton.removeAllListeners();
		this.replyModifyOpenAddButton.addSelectionListener(this.addSubjectListener);
		panel.setHeading("Add new subject");
		previousPanel.setHeading("Father subject");

		XTemplate tpl = XTemplate.create("<p><b>Name:</b> {name}</p><br>" +
		"<p><b>Description:</b> {description}</p>");  		
		
		previousPanel.removeAll();
		if (fatherSubject == null)
			previousPanel.addText("No father subject - the new subject will be added as a root subject");
		else
			previousPanel.addText(tpl.applyTemplate(Util.getJsObject(fatherSubject, 2)));  
		previousPanel.layout();
	}

	
	
	

	@Override  
	protected void onRender(Element parent, int index) { 
		super.onRender(parent, index);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setPadding(5);
		previousPanel.setScrollMode(Scroll.AUTOY);
		this.setSize(650, 400);  
		this.setLayout(new BorderLayout());
		this.createColumnForm();

	}	
}  
