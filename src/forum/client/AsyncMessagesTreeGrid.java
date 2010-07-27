package forum.client;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.MessageModel;
import forum.shared.UserModel.UserType;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.user.NotPermittedException;
import forum.shared.exceptions.user.NotRegisteredException;

public class AsyncMessagesTreeGrid extends LayoutContainer {  

	private ContentPanel messagesPanel;

	private Button replyButton;
	private Button deleteButton; 
	private Button modifyButton;

	private TreeGrid<MessageModel> tree;
	private TreeLoader<MessageModel> loader;
	private TreeStore<MessageModel> store;

	private RowExpander expander;

	private AsyncThreadsTableGrid threadsTable;

	private ToolBar toolbar;

	private long threadID;
	private boolean selectionChanged;


	private SelectionChangedListener<MessageModel> selectionChangedListener;
	private Listener<TreeGridEvent<MessageModel>> updateListener;

	private ColumnModel columnModel;

	private MessageModel currentInTree;


	/**
	 * The class constructor
	 * 
	 * @param threadsTable
	 * 		The top threads table of this messages tree
	 */
	public AsyncMessagesTreeGrid(AsyncThreadsTableGrid threadsTable) {
		this.threadsTable = threadsTable;
		this.threadID = -1;

		messagesPanel = new ContentPanel();
		messagesPanel.setBodyBorder(false);
		messagesPanel.setHeading("Messages");
		messagesPanel.setButtonAlign(HorizontalAlignment.CENTER);  
		messagesPanel.setLayout(new FitLayout());
		messagesPanel.setFrame(false);  

		// data proxy  
		RpcProxy<List<MessageModel>> proxy = new RpcProxy<List<MessageModel>>() {  
			@Override
			protected void load(final Object loadConfig, final
					AsyncCallback<List<MessageModel>> callback) {
				final AsyncCallback<List<MessageModel>> tNewCallback = new AsyncCallback<List<MessageModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						loader.load(loadConfig);
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}
					@Override
					public void onSuccess(List<MessageModel> result) {
						callback.onSuccess(result);
						if (result.size() > 0) {
							if (QuadCoreForumWeb.SEARCH_STATE) {
								loadMessages3();
							}
							else {// if (!QuadCoreForumWeb.AFTER_SEARCH_STATE) {

								tree.getSelectionModel().select(0, false);
							}
						}
						else if (result.size() == 0) {
							setButtonsEnableStatus(true);
							if (QuadCoreForumWeb.SEARCH_STATE) {
								Info.display("Not found", "Could not found message");
							}
						}
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}
				};
				if (threadID != -1) {//&& !QuadCoreForumWeb.AFTER_SEARCH_STATE) {
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Loading messages...");
					QuadCoreForumWeb.SERVICE.getReplies(threadID, (MessageModel) loadConfig, selectionChanged, tNewCallback);
				}
			} 
		};  	

		// tree loader  
		loader = new BaseTreeLoader<MessageModel>(proxy) {  

			@Override  
			public boolean hasChildren(MessageModel parent) {
				return true;
			}
		};  


		RowNumberer tNumberer = new RowNumberer();


		XTemplate tpl = XTemplate.create("<p><b>Date of publish:</b> {date}</p><br>" +
				"<p><b>Author:</b> {authorUsername}</p><br>" +
				"<p><b>Title:</b> {title}</p><br>" +
		"<p><b>Content:</b> {SelectedContent}</p>");

		this.expander = new RowExpander(); 

		expander.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());  


		expander.setTemplate(tpl);




		ColumnConfig name = new ColumnConfig("display", "Name", 800);  
		name.setRenderer(new TreeGridCellRenderer<MessageModel>());

		ColumnConfig date = new ColumnConfig("date", "Date", 180);  
		date.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());  
		date.setRenderer(null);
		columnModel = new ColumnModel(Arrays.asList(tNumberer, expander, name, date));  

		store = new TreeStore<MessageModel>(loader);  
		tree = new TreeGrid<MessageModel>(store, columnModel);  
		tree.setStripeRows(true);
		tree.addPlugin(expander);  
		tree.getView().setAutoFill(true);  
		tree.getTreeView().setBufferEnabled(false); 
		tree.setStateful(true);  
		tree.setLoadMask(true);
		tree.setId("messagestable");  

		store.setKeyProvider(new ModelKeyProvider<MessageModel>() {  
			public String getKey(MessageModel model) {  
				return model.getID() + "";  
			}  
		});  

		updateListener = new Listener<TreeGridEvent<MessageModel>>() {  
			public void handleEvent(final TreeGridEvent<MessageModel> be) {  
				//				if (!QuadCoreForumWeb.SEARCH_STATE) {
				if (be.getModel() != null) {
					invokeExpansionOperation(be.getModel());
				}
				else
					setGuestView();
				//				}
			}
		};

		selectionChangedListener = new SelectionChangedListener<MessageModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<MessageModel> se) {
				//			if (!QuadCoreForumWeb.SEARCH_STATE) {
				if (se.getSelectedItem() != null) {
					invokeSelectListenerOperation(se.getSelectedItem());
				}
				else
					setGuestView();
				//		}
			}
		};


		tree.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				MessageModel tSelected = tree.getSelectionModel().getSelectedItem();
				if (tSelected == null) 
					return;
				else {
					QuadCoreForumWeb.SEARCH_STATE = false;
					QuadCoreForumWeb.SEARCHING_MESSAGES = false;
					allowReplyModifyDeleteButtons(tSelected);
				}
			}

		});
		// change in node check state  
		tree.addListener(Events.Expand, updateListener);  
		// change in node check state  
		tree.addListener(Events.Collapse, updateListener);  

		tree.getSelectionModel().addSelectionChangedListener(selectionChangedListener);
		tree.setCaching(false);
		tree.setBorders(true);  
		tree.setAutoExpandColumn("display");
		tree.setAutoExpandMax(3000);
		tree.getView().setEmptyText("No messages are available");
		tree.setTrackMouseOver(true);  
		tree.getTreeView().setSortingEnabled(false);
		tree.getTreeView().setRowHeight(40);
		messagesPanel.setScrollMode(Scroll.AUTOY);

		messagesPanel.add(tree);
		setScrollMode(Scroll.AUTOY);
		add(messagesPanel);  
		initializeToolbar();

	}

	/**
	 * Changes the id of the current shown thread and updates the loader to load
	 * its messages
	 * 
	 * @param threadID
	 * 		The id of the new thread
	 * @param selectionChanged
	 * 		Whether the number of the thread views should be updated
	 */
	public void changeThreadID(long threadID, boolean selectionChanged) {
		this.selectionChanged = selectionChanged;
		this.threadID = threadID;
		if (loader != null)
			loader.load(null);
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  
		setLayout(new FitLayout());

	}

	private void notFound() {
		QuadCoreForumWeb.SEARCH_STATE = false;
		MessageBox.alert("Not found", "The required message not found", null);
	}


	private void loadOtherMessages() {
		RpcProxy<List<MessageModel>> tProxy = new RpcProxy<List<MessageModel>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<MessageModel>> callback) {
				QuadCoreForumWeb.SERVICE.getReplies(threadID, (MessageModel) loadConfig, selectionChanged, callback);
			}
		};
		// tree loader  
		BaseTreeLoader<MessageModel> tLoader = new BaseTreeLoader<MessageModel>(tProxy) {  
			@Override  
			public boolean hasChildren(MessageModel parent) {
				return true;
			}
		};  
		TreeStore<MessageModel> tStore = new TreeStore<MessageModel>(tLoader);  
		tStore.setKeyProvider(new ModelKeyProvider<MessageModel>() {  
			public String getKey(MessageModel model) {  
				return model.getID() + "";  
			}  
		});  
		final TreeGrid<MessageModel> tTree = new TreeGrid<MessageModel>(tStore, columnModel);


		tTree.collapseAll();
		tStore.add(store.getRootItems(), false);
		tStore.commitChanges();

		final Stack<MessageModel> tMessagesPath = new Stack<MessageModel>();
		final Stack<MessageModel> tTempStack = new Stack<MessageModel>();

		Stack<MessageModel> tRealMessagesPath = (Stack<MessageModel>) 
		QuadCoreForumWeb.SEARCH_STATE_HIT.getMessagePath();

		while (!tRealMessagesPath.isEmpty()) {
			tTempStack.push(tRealMessagesPath.pop());
		}
		while (!tTempStack.isEmpty()) {
			MessageModel tCurrent = tTempStack.pop();
			tMessagesPath.push(tCurrent);
			tRealMessagesPath.push(tCurrent);
		}

		Listener<BaseEvent> tExpandListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				MessageModel tCurrent = tMessagesPath.pop();

				MessageModel tCurrentInTree = tTree.getStore().findModel(tCurrent);
				if (tCurrentInTree == null) {
					tTree.removeListener(Events.Expand, this);
					notFound();
				}
				else if (tMessagesPath.isEmpty()) {
					tTree.removeListener(Events.Expand, this);
					tTree.getSelectionModel().select(tCurrentInTree, false);
					//tree.getSelectionModel().select(tCurrentInTree, false);
					QuadCoreForumWeb.SEARCH_STATE = false;
					//MainPanel.changeMainViewToSubjectsAndThreads();

					/*				tree.addListener(Events.Expand, updateListener);  
					tree.addListener(Events.Collapse, updateListener);  
					tree.getSelectionModel().addSelectionChangedListener(selectionChangedListener);
					 */

				}
				else {
					tTree.setExpanded(tCurrentInTree, true);
				}
			}
		};

		tTree.addListener(Events.Expand, tExpandListener); 

		MessageModel tCurrent = tMessagesPath.pop();
		MessageModel tCurrentInTree = tTree.getStore().findModel(tCurrent);
		if (tCurrentInTree == null) {
			MessageBox.alert("rror", "sdsd", null);
			tTree.removeListener(Events.Expand, tExpandListener);
			notFound();
		}
		else if (tMessagesPath.isEmpty()) {
			tTree.removeListener(Events.Expand, tExpandListener);
			tTree.getSelectionModel().select(tCurrentInTree, false);
			QuadCoreForumWeb.SEARCH_STATE = false;
			/*			tree.addListener(Events.Expand, updateListener);  
			tree.addListener(Events.Collapse, updateListener);  
			tree.getSelectionModel().addSelectionChangedListener(selectionChangedListener);
			 */
		}
		else {
			tTree.setExpanded(tCurrentInTree, true);
		}


		ContentPanel tNewPanel = new ContentPanel();
		tNewPanel.setLayout(new FitLayout());
		tNewPanel.add(tTree);
		Window tWin = new Window();
		tWin.setLayout(new FitLayout());
		tWin.add(tNewPanel);
		tWin.show();
	}



	private void loadMessages3() {
		if (threadID != QuadCoreForumWeb.SEARCH_STATE_HIT.getContainingThread().getID())
			return;

		if (!QuadCoreForumWeb.SEARCHING_MESSAGES)
			QuadCoreForumWeb.SEARCHING_MESSAGES = true;
		else
			return;


		currentInTree = null;

		tree.addListener(Events.Expand, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (currentInTree == null) {
					currentInTree = 
						store.findModel(((Stack<MessageModel>)QuadCoreForumWeb.SEARCH_STATE_HIT.getMessagePath()).firstElement());
					if (currentInTree != null) {
						tree.getSelectionModel().select(currentInTree, false);
					}
				}
			}
		});

		tree.expandAll();
	}

	private void loadMessages() {
		if (threadID != QuadCoreForumWeb.SEARCH_STATE_HIT.getContainingThread().getID())
			return;


		if (!QuadCoreForumWeb.SEARCHING_MESSAGES)
			QuadCoreForumWeb.SEARCHING_MESSAGES = true;
		else
			return;

		tree.clearState();

		//	loadOtherMessages();
		//if (1==1) return;

		/*		tree.removeListener(Events.Expand, updateListener);  
		tree.removeListener(Events.Collapse, updateListener);  
		tree.getSelectionModel().removeSelectionListener(selectionChangedListener);
		 */



		//		tree.collapseAll();

		final Stack<MessageModel> tMessagesPath = new Stack<MessageModel>();
		final Stack<MessageModel> tTempStack = new Stack<MessageModel>();

		Stack<MessageModel> tRealMessagesPath = (Stack<MessageModel>) 
		QuadCoreForumWeb.SEARCH_STATE_HIT.getMessagePath();

		while (!tRealMessagesPath.isEmpty()) {
			tTempStack.push(tRealMessagesPath.pop());
		}
		while (!tTempStack.isEmpty()) {
			MessageModel tCurrent = tTempStack.pop();
			tMessagesPath.push(tCurrent);
			tRealMessagesPath.push(tCurrent);
		}


		currentInTree = null;

		Listener<BaseEvent> tExpandListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {



				MessageModel tCurrent = tMessagesPath.pop();


				currentInTree = tree.getStore().findModel(tCurrent);
				if (currentInTree == null) {
					tree.removeListener(Events.Expand, this);
					notFound();
				}
				else if (tMessagesPath.isEmpty()) {
					//					QuadCoreForumWeb.AFTER_SEARCH_STATE = true;

					tree.removeListener(Events.Expand, this);
					tree.setExpanded(currentInTree, false);

					tree.getSelectionModel().select(currentInTree, false);


					//tree.getSelectionModel().select(tCurrentInTree, false);

					QuadCoreForumWeb.SEARCH_STATE = false;
					//MainPanel.changeMainViewToSubjectsAndThreads();

					/*				tree.addListener(Events.Expand, updateListener);  
					tree.addListener(Events.Collapse, updateListener);  
					tree.getSelectionModel().addSelectionChangedListener(selectionChangedListener);
					 */

				}
				else {
					tree.setExpanded(currentInTree, true);
				}
			}
		};

		tree.addListener(Events.Expand, tExpandListener); 


		MessageModel tCurrent = tMessagesPath.pop();
		currentInTree = tree.getStore().findModel(tCurrent);



		if (currentInTree == null) {
			tree.removeListener(Events.Expand, tExpandListener);
			notFound();
		}
		else if (tMessagesPath.isEmpty()) {
			//			QuadCoreForumWeb.AFTER_SEARCH_STATE = true;

			tree.removeListener(Events.Expand, tExpandListener);
			tree.setExpanded(currentInTree, false);
			tree.getSelectionModel().select(currentInTree, false);
			QuadCoreForumWeb.SEARCH_STATE = false;
			/*			tree.addListener(Events.Expand, updateListener);  
			tree.addListener(Events.Collapse, updateListener);  
			tree.getSelectionModel().addSelectionChangedListener(selectionChangedListener);
			 */
		}
		else {
			tree.setExpanded(currentInTree, true);

		}
	}

	private void invokeSelectListenerOperation(final MessageModel model) {
		if (!QuadCoreForumWeb.SEARCHING_MESSAGES) {

			QuadCoreForumWeb.SERVICE.getMessageByID(model.getID(), new AsyncCallback<MessageModel>() {

				@Override
				public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

				@Override
				public void onSuccess(MessageModel result) {
					model.setTitle(result.getTitle());
					model.setContent(result.getContent());
					model.setDate(result.getDate());
					store.update(model);
					store.commitChanges();

					//				MessageModel tSelected = tree.getSelectionModel().getSelectedItem();
					/*
				if (tSelected != null) {
					com.google.gwt.dom.client.Element tRow = tree.getTreeView().getRow(tSelected);
					if (tRow != null)
						expander.expandRow(tree.getView().findRowIndex(tRow));
					allowReplyModifyDeleteButtons(tSelected);

				}
					 */
				}
			});
			allowReplyModifyDeleteButtons(model);
		}
	}


	private void invokeExpansionOperation(final MessageModel model) {

		if (!QuadCoreForumWeb.SEARCHING_MESSAGES) {

			QuadCoreForumWeb.SERVICE.getMessageByID(model.getID(), new AsyncCallback<MessageModel>() {

				@Override
				public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

				@Override
				public void onSuccess(MessageModel result) {
					model.setTitle(result.getTitle());
					model.setContent(result.getContent());
					model.setDate(result.getDate());
					store.update(model);

					store.commitChanges();

					/*				if (tSelected != null) {
					com.google.gwt.dom.client.Element tRow = tree.getTreeView().getRow(tSelected);
					if (tRow != null)
						expander.expandRow(tree.getView().findRowIndex(tRow));
					allowReplyModifyDeleteButtons(tSelected);

				}
					 */
				}
			});

			allowReplyModifyDeleteButtons(model);


		}		


	}

	public void setToolBarVisible(boolean value) {
		if (this.toolbar != null)
			this.toolbar.setVisible(value);
	}

	public void setGuestView() {
		if (replyButton != null)
			replyButton.setEnabled(false);
		if (deleteButton != null)
			deleteButton.setEnabled(false);
		if (modifyButton != null)
			modifyButton.setEnabled(false);
	}

	public void setButtonsEnableStatus(boolean value) {
		if (value && tree != null && tree.getSelectionModel() != null)
			allowReplyModifyDeleteButtons(tree.getSelectionModel().getSelectedItem());
		else
			setGuestView();
	}

	private void allowReplyModifyDeleteButtons(MessageModel selected) {
		if (selected == null || QuadCoreForumWeb.CONNECTED_USER_DATA == null) {
			setGuestView();
		}
		else {
			UserType tType = QuadCoreForumWeb.CONNECTED_USER_DATA.getType();
			replyButton.setEnabled(true);
			if (tType == UserType.ADMIN || tType == UserType.MODERATOR) {
				deleteButton.setEnabled(true);
				modifyButton.setEnabled(true);
			}
			else {
				deleteButton.setEnabled(false);
				if (selected.getAuthorID() == QuadCoreForumWeb.CONNECTED_USER_DATA.getID())
					modifyButton.setEnabled(true);
				else
					modifyButton.setEnabled(false);
			}
		}
	}

	private void initializeToolbar() {
		toolbar = new ToolBar(); 
		toolbar.setVisible(QuadCoreForumWeb.CONNECTED_USER_DATA != null
				&& QuadCoreForumWeb.CONNECTED_USER_DATA.getType() != UserType.GUEST);
		toolbar.setBorders(true); 
		replyButton = new Button("Reply");
		replyButton.setWidth(115);
		deleteButton = new Button("Delete");
		modifyButton = new Button("Modify");
		modifyButton.setWidth(115);


		replyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ContentPanel tMainViewPanel = (ContentPanel)Registry.get("MainViewPanel");

				tMainViewPanel.removeAll();
				AddReplyModifyForm tAddReply = new AddReplyModifyForm();//(AddReplyModifyForm)Registry.get("AddReply");
				tAddReply.initReplyDialog(tree.getSelectionModel().getSelectedItem(), tree, store);
				tMainViewPanel.add(tAddReply);
				tMainViewPanel.layout();
				tree.setExpanded(tree.getSelectionModel().getSelectedItem(), true);
			}
		});



		modifyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ContentPanel tMainViewPanel = (ContentPanel)Registry.get("MainViewPanel");

				tMainViewPanel.removeAll();
				AddReplyModifyForm tModifyForm = new AddReplyModifyForm();//(AddReplyModifyForm)Registry.get("AddReply");
				tModifyForm.initModifyDialog(tree.getSelectionModel().getSelectedItem(), tree, store);
				tMainViewPanel.add(tModifyForm);
				tMainViewPanel.layout();
			}
		});


		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final MessageModel tSelecteModel;
				if ((tree == null) || (tree.getSelectionModel() == null) ||
						((tSelecteModel = tree.getSelectionModel().getSelectedItem()) == null)) {
					deleteButton.setEnabled(false);
					return;
				}

				final Listener<MessageBoxEvent> tDeleteListener = new Listener<MessageBoxEvent>() {  
					public void handleEvent(MessageBoxEvent ce) {  
						Button btn = ce.getButtonClicked();
						if (btn.getText().equals("No"))
							return;
						QuadCoreForumWeb.WORKING_STATUS.setBusy("Deleting message...");
						QuadCoreForumWeb.SERVICE.deleteMessage(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
								(store.getParent(tSelecteModel) == null? -1 : store.getParent(tSelecteModel).getID()),
								tSelecteModel.getID(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
								if (caught instanceof MessageNotFoundException)
									loader.load(null);
								else if (caught instanceof NotRegisteredException) {
									//
								}
								else if (caught instanceof NotPermittedException) {

								}
								else if (caught instanceof DatabaseUpdateException) {

								}
							}

							@Override
							public void onSuccess(Void result) {
								QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
								MessageModel tParent = store.getParent(tSelecteModel);
								store.remove(tSelecteModel);
								store.commitChanges();
								if (tParent != null) {
									tree.getSelectionModel().select(false, tParent);
									Info.display("Message deletion success", "The message was deleted successfully " +
									"from the forum");
								}
								else { // thread deletion
									threadsTable.deleteSelectedThreadRow();
									Info.display("Thread deletion success", "The thread was deleted successfully " +
									"from the forum");
								}


							}
						});
					}  
				};  

				if (store.getParent(tSelecteModel) == null) // thread
					MessageBox.confirm("Confirm", "Deleting this message will delete the " +
							"entire thread, are you sure you want to continue?", tDeleteListener);  
				else				
					MessageBox.confirm("Confirm", "Are you sure you want to delete the message?", tDeleteListener);  
			}
		});

		deleteButton.setWidth(115);


		this.setGuestView();
		toolbar.add(replyButton);
		toolbar.add(modifyButton);
		toolbar.add(deleteButton);

		toolbar.setAlignment(HorizontalAlignment.CENTER);
		messagesPanel.setTopComponent(toolbar);  

		if (QuadCoreForumWeb.CONNECTED_USER_DATA == null ||
				QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.GUEST);
	}
}