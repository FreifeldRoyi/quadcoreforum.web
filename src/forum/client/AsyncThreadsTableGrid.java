package forum.client;  

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.UserModel;
import forum.shared.UserModel.UserType;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.user.NotPermittedException;
import forum.shared.exceptions.user.NotRegisteredException;

public class AsyncThreadsTableGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private PagingToolBar statusBar;

	private ToolBar toolbar;

	private SubjectModel subjectID;

	private RpcProxy<PagingLoadResult<ThreadModel>> proxy;
	private PagingLoader<PagingLoadResult<ThreadModel>> loader;
	private ListStore<ThreadModel> store;

	// fetch
	private Grid<ThreadModel> grid;
	private ContentPanel threadsPanel;

	private AsyncMessagesTreeGrid messagesTree;

	private Button openNewThreadButton;

	private Button deleteThreadButton;

	private ToggleButton modifyThreadButton;

	private TextField<String> topicEditorField;

	private ColumnConfig topicColumn;

	private RowEditor<ThreadModel> threadRowEditor;


	private boolean shouldUpdateViews;

	/*	@Override
	public void setTitle(String title) {
		if (title != null)
			cp.setHeading("Threads: " + title);
	}
	 */

	public AsyncThreadsTableGrid(SubjectModel subject) {
		this.shouldUpdateViews = false;
		this.subjectID = subject;
		topicColumn = new ColumnConfig("topic", "Topic", 800);
		threadRowEditor = new RowEditor<ThreadModel>();  
		threadRowEditor.setEnabled(false);
		
		initializeProxy();
		// loader  
		initializeLoader();

		this.initializeStore();

		this.initializeStatusbar();

		this.initializeGrid();

		this.initializeToolbar();

		threadRowEditor.addListener(Events.AfterEdit, new Listener<RowEditorEvent>() {
			@Override
			public void handleEvent(final RowEditorEvent be) {
				be.setCancelled(true);
				ThreadModel tSelectedModel = grid.getSelectionModel().getSelectedItem();
				QuadCoreForumWeb.SERVICE.modifyThread(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
						tSelectedModel.getID(), topicEditorField.getValue(),
						new AsyncCallback<ThreadModel>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Modification error", "Can't modify thread");
						threadRowEditor.stopEditing(false);
					}

					@Override
					public void onSuccess(ThreadModel result) {
						Info.display("Modification success", "The thread was modified successfully");
						threadRowEditor.stopEditing(true);
					}
				});
			}
		});




		grid.addPlugin(threadRowEditor);  


		openNewThreadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AddReplyModifyForm tOpenThread = new AddReplyModifyForm();
				tOpenThread.initThreadsOpenningDialog(AsyncThreadsTableGrid.this.subjectID, grid, 
						store);
				MainPanel.changeMainViewToPanel(tOpenThread);
			}
		});

		modifyThreadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override  
			public void componentSelected(ButtonEvent ce) {
				if (modifyThreadButton.isPressed()) {
					threadRowEditor.setEnabled(true);
					threadRowEditor.stopEditing(false);
					threadRowEditor.startEditing(store.indexOf(grid.getSelectionModel().getSelectedItem()), true);
				}
				else {
					threadRowEditor.stopEditing(false);
					threadRowEditor.setEnabled(false);
				}
			}
		}); 




		threadsPanel.layout();
	}  

	public void setToolBarVisible(boolean value) {
		if (this.toolbar != null)
			this.toolbar.setVisible(value);
	}

	public void setGuestView() {
		if (openNewThreadButton != null)
			this.openNewThreadButton.setEnabled(false);
		if (modifyThreadButton != null)
			this.modifyThreadButton.setEnabled(false);
		if (deleteThreadButton != null)
			this.deleteThreadButton.setEnabled(false);
	}
	
	public void setButtonsEnableStatus() {
		UserModel tConnectedUser = QuadCoreForumWeb.CONNECTED_USER_DATA;
		if (openNewThreadButton != null)
			this.openNewThreadButton.setEnabled(subjectID.getID() != -1);
		
		if (modifyThreadButton != null && subjectID.getID() != -1)
			this.modifyThreadButton.setEnabled(tConnectedUser.getType() == UserType.ADMIN ||
					tConnectedUser.getType() == UserType.MODERATOR);		
		else if (subjectID.getID() == -1)
			this.modifyThreadButton.setEnabled(false);
		
		if (deleteThreadButton != null && subjectID.getID() != -1)
			this.deleteThreadButton.setEnabled(tConnectedUser.getType() == UserType.ADMIN ||
					tConnectedUser.getType() == UserType.MODERATOR);
		else if (subjectID.getID() == -1)
			this.deleteThreadButton.setEnabled(false);
	}


	private void initializeGrid() {


		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		RowNumberer rn = new RowNumberer();
		rn.setWidth(30);
		configs.add(rn);

		topicColumn.setRenderer(new GridCellRenderer<ThreadModel>() {

			@Override
			public Object render(ThreadModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThreadModel> store, Grid<ThreadModel> grid) {
				return "<b><a style=\"color: #385F95; text-decoration: none;\" >"
				+ model.get("topic")
				+ "</a></b><br /><a style=\"color: #385F95; text-decoration: none;\"> Thread</a>";
			}
		});


		topicEditorField = new TextField<String>();  
		topicEditorField.setAllowBlank(false);
		topicColumn.setEditor(new CellEditor(topicEditorField));  

		topicColumn.setAlignment(HorizontalAlignment.LEFT);  
		configs.add(topicColumn);  

		ColumnConfig column = new ColumnConfig("responses", "Responses#", 100);  
		column.setAlignment(HorizontalAlignment.CENTER);  


		configs.add(column);  

		column = new ColumnConfig("views", "Views#", 100);
		column.setAlignment(HorizontalAlignment.CENTER);  

		

		configs.add(column);  

		ColumnModel cm = new ColumnModel(configs);  

		threadsPanel = new ContentPanel();  

		threadsPanel.setFrame(false);  
		threadsPanel.setHeaderVisible(false);
		threadsPanel.setButtonAlign(HorizontalAlignment.CENTER);  
		threadsPanel.setLayout(new FitLayout());  
		threadsPanel.setBottomComponent(statusBar);  

		grid = new Grid<ThreadModel>(store, cm);  

		GridView view = new GridView();
		view.setEmptyText("The subject has no threads.");
		grid.setView(view); 

		grid.setAutoExpandMax(3000);
		grid.setBorders(false);  
		grid.setAutoExpandColumn("topic");

		
		grid.setStripeRows(true);
		grid.getView().setShowDirtyCells(false);

		Listener<BaseEvent> tRowSelectionListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				invokeListenerOperation();
			}
		};

		grid.addListener(Events.RowClick, tRowSelectionListener);

		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ThreadModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ThreadModel> se) {
				shouldUpdateViews = true;
				invokeListenerOperation();
			}
		});



		
	}

	public void setMessagesTree(AsyncMessagesTreeGrid messagesTree) {
		this.messagesTree = messagesTree;
	}

	private void continueSearch() {
		if (QuadCoreForumWeb.SEARCHING_MESSAGES)
			return;
		ThreadModel tTreadToSearch = 
			QuadCoreForumWeb.SEARCH_STATE_HIT.getContainingThread();
			ThreadModel tThread = store.findModel(tTreadToSearch);

			if (tThread == null) {
				if (statusBar.getActivePage() <= statusBar.getPageSize()) {
					statusBar.next();
				}
				else
					MessageBox.alert("path error", "the message wasn't found " +
							"probably it was deleted by another user", new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									MainPanel.changeMainViewToSubjectsAndThreads();
								}
							});
			}
			else {
				grid.getSelectionModel().select(tThread, false);
			}
		}	


	private void initializeProxy() {
		proxy = new RpcProxy<PagingLoadResult<ThreadModel>>() {
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				if (subjectID == null || subjectID.getID() == -1)
					return;

				if (QuadCoreForumWeb.SEARCH_STATE)
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Searching...");
				else
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Loading threads...");

				service.getThreads((PagingLoadConfig) loadConfig, subjectID.getID(), 
						new AsyncCallback<PagingLoadResult<ThreadModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}

					@Override
					public void onSuccess(
							PagingLoadResult<ThreadModel> result) {
						callback.onSuccess(result);
						if (result.getTotalLength() == 0) {
							messagesTree.setGuestView();
							setButtonsEnableStatus();
						}
						else {
							if (!QuadCoreForumWeb.SEARCH_STATE) {
								grid.getSelectionModel().select(0, false);
							}
						}

						if (QuadCoreForumWeb.SEARCH_STATE)
							continueSearch();
						else
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");


					}

				});
			}  
		};  
	}


	private void initializeLoader() {
		this.loader = new BasePagingLoader<PagingLoadResult<ThreadModel>>(proxy); 
		Registry.register("ThreadsLoader", loader);
	}

	private void initializeStore() {
		store = new ListStore<ThreadModel>(loader);  
		Registry.register("ThreadsTreeStore", store);
	}

	public void load() {
		if (!QuadCoreForumWeb.SEARCH_STATE) {
			if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") > 0)) {
				Registry.register("NoTabExpand", ((Long)Registry.get("NoTabExpand") - 1));
				QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
				return;
			}
			else if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") == 0)) {
				Registry.register("NoTabExpand", null);
				QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
				return;
			}
		}
		
		
		if (this.subjectID != null && this.subjectID.getID() != -1)
			this.loader.load(0, 10);
	}


	private void initializeToolbar() {
		toolbar = new ToolBar();  
		toolbar.setVisible(QuadCoreForumWeb.CONNECTED_USER_DATA != null && 
				QuadCoreForumWeb.CONNECTED_USER_DATA.getType() != UserType.GUEST);
		toolbar.setBorders(true); 
		openNewThreadButton = new Button("Open new"); 
		openNewThreadButton.setWidth(115);
		deleteThreadButton = new Button("Delete"); 
		deleteThreadButton.setWidth(115);

		deleteThreadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final ThreadModel tSelecteModel;
				if ((grid == null) || (grid.getSelectionModel() == null) ||
						((tSelecteModel = grid.getSelectionModel().getSelectedItem()) == null)) {
					deleteThreadButton.setEnabled(false);
					return;
				}

				final Listener<MessageBoxEvent> tDeleteListener = new Listener<MessageBoxEvent>() {  
					public void handleEvent(MessageBoxEvent ce) {  
						Button btn = ce.getButtonClicked();
						if (btn.getText().equals("No"))
							return;
						QuadCoreForumWeb.WORKING_STATUS.setBusy("Deleting thread...");
						QuadCoreForumWeb.SERVICE.deleteMessage(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
								-1, tSelecteModel.getID(), new AsyncCallback<Void>() {

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
								deleteSelectedThreadRow();
								Info.display("Thread deletion success", "The thread was deleted successfully " +
								"from the forum");
							}
						});
					}  
				};  

				MessageBox.confirm("Confirm", "Are you sure you want to delete the thread?", tDeleteListener);  
			}
		});


		modifyThreadButton = new ToggleButton("Modify");
		modifyThreadButton.setWidth(115);

		toolbar.add(openNewThreadButton);
		toolbar.add(modifyThreadButton);
		toolbar.add(deleteThreadButton);

		toolbar.setAlignment(HorizontalAlignment.CENTER);

		threadsPanel.setTopComponent(toolbar);  
		setGuestView();
	}



	private void initializeStatusbar() {
		statusBar  = new PagingToolBar(10);
		statusBar.setPageSize(10);
		statusBar.setEnabled(subjectID != null && subjectID.getID() != -1);
		statusBar.bind(loader);
		//		statusBar.setEnabled(false);
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		setLayout(new FlowLayout(10));  



		threadsPanel.add(grid);  
		add(threadsPanel);  





		this.setLayout(new FitLayout());


	}

	public void deleteSelectedThreadRow() {
		ThreadModel toDelete = null;
		if ((this.grid == null) || (this.grid.getSelectionModel() == null) ||
				((toDelete = this.grid.getSelectionModel().getSelectedItem()) == null))
			return;
		if (this.store.getCount() > 0) { 
			if (this.store.getAt(0) == toDelete) // the first thread
				this.grid.getSelectionModel().selectNext(false);
			else
				this.grid.getSelectionModel().selectPrevious(false);
		}

		this.store.remove(toDelete);
		this.store.commitChanges();
	}


	private void invokeListenerOperation() {
		if (subjectID != null && grid.getSelectionModel() != null &&
				grid.getSelectionModel().getSelectedItem() != null) {
			
			if (QuadCoreForumWeb.SEARCH_STATE &&
					QuadCoreForumWeb.SEARCH_STATE_HIT.getContainingThread().getID() != grid.getSelectionModel().getSelectedItem().getID()) return;

			
			setButtonsEnableStatus();
			
			
			final ThreadModel tSelectedThread = grid.getSelectionModel().getSelectedItem();
			boolean tShouldUpdateViews = shouldUpdateViews;
			shouldUpdateViews = false;
			
			QuadCoreForumWeb.SERVICE.getThreadByID(tSelectedThread.getID(), tShouldUpdateViews, new AsyncCallback<ThreadModel>() {

				@Override
				public void onFailure(Throwable caught) {
					loader.load();
				}

				@Override
				public void onSuccess(ThreadModel result) {
					if (QuadCoreForumWeb.SEARCH_STATE) return;
					tSelectedThread.setTopic(result.getTopic());
					tSelectedThread.setResponsesNumber(result.getResponsesNumber());
					tSelectedThread.setViewsNumber(result.getViewsNumber());
					store.update(tSelectedThread);
					store.commitChanges();
				}
			});
		}
		else if (grid != null && grid.getSelectionModel() != null)
			setButtonsEnableStatus();

		if (messagesTree != null) {
			if (grid != null && grid.getSelectionModel() != null && grid.getSelectionModel().getSelectedItem() != null)
				messagesTree.changeThreadID(grid.getSelectionModel().getSelectedItem().getID(), false);
		}
	}



}