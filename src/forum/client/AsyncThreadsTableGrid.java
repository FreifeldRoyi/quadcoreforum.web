package forum.client;  

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
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
	}

	public void setMessagesTree(AsyncMessagesTreeGrid messagesTree) {
		System.out.println("setting  " + messagesTree != null);
		this.messagesTree = messagesTree;
	}

	private void initializeProxy() {
		proxy = new RpcProxy<PagingLoadResult<ThreadModel>>() {
			/*			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				service.getThreads((PagingLoadConfig) loadConfig, subjectID.getID(), callback);			
				}
		};
	}*/

			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				if (subjectID == null || subjectID.getID() == -1)
					return;


				QuadCoreForumWeb.WORKING_STATUS.setBusy("Loading threads...");
				service.getThreads((PagingLoadConfig) loadConfig, subjectID.getID(), new AsyncCallback<PagingLoadResult<ThreadModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
//						grid.el().unmask();
//						statusBar.setEnabled(true);
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}

					@Override
					public void onSuccess(
							PagingLoadResult<ThreadModel> result) {
						System.out.println("0000000000000000000000000");

						System.out.println(result.getTotalLength());

						callback.onSuccess(result);

						if (result.getTotalLength() == 0) {
		//					statusBar.setEnabled(false);
							messagesTree.setGuestView();
							setButtonsEnableStatus(false);
						}
						else {
//							if (!statusBar.isEnabled())
	//							statusBar.setEnabled(true);
//							statusBar.setEnabled(true);
							// select the first row
//							store.commitChanges();
							grid.getSelectionModel().select(0, false);
						}
							//				grid.el().unmask();
					//					statusBar.setEnabled(true);
						
						
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");


						//						/*							if (shouldRefresh) {
						//System.out.println("shouldddddddddddddddddddddddddddd");
						//shouldRefresh = false;
						//toolBar.refresh();
						//}

					}

				});
			}  
		};  
	}


	private void initializeLoader() {
		this.loader = new BasePagingLoader<PagingLoadResult<ThreadModel>>(proxy); 
	}

	private void initializeStore() {
		store = new ListStore<ThreadModel>(loader);  
	}

	public void load() {
		System.out.println("loading threads ... " + subjectID);
		System.out.println(this.loader);
		System.out.println((this.grid == null) + " dddddddddddddddddd");
		System.out.println("111111111111111111111111111111111111 " + proxy);
		System.out.println("'''''''''''''''''''''''''''''''''' " + Registry.get("NoTabExpand"));
		
		if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") > 0)) {
			System.out.println("enters and exits");
			Registry.register("NoTabExpand", ((Long)Registry.get("NoTabExpand") - 1));
			//					statusBar.setEnabled(true);
			QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
			return;
		}
		else if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") == 0)) {
			Registry.register("NoTabExpand", null);

			//					statusBar.setEnabled(true);
			QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
			return;
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
		setButtonsEnableStatus(false);
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
				AddReplyModifyForm tOpenThread = (AddReplyModifyForm)Registry.get("AddReply");
				System.out.println("Opens new threads under " + 
						AsyncThreadsTableGrid.this.subjectID.getID());
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
		//		this.threadsPanel.layout();
		//		this.threadsPanel.repaint();
	}

	public void setButtonsEnableStatus(boolean value) {
		System.out.println("TTTTTTTTTTTTTTTTTTTTTTJJJJJJJJJJJJJJJJJJJJJJJJJJ " + value );
		if (openNewThreadButton != null)
			this.openNewThreadButton.setEnabled(subjectID.getID() != -1);
		if (modifyThreadButton != null)
			this.modifyThreadButton.setEnabled(value);		

		if (deleteThreadButton != null)
			this.deleteThreadButton.setEnabled(value);
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

				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);

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

		column.setRenderer(new GridCellRenderer<ThreadModel>() {

			@Override
			public Object render(ThreadModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThreadModel> store, Grid<ThreadModel> grid) {

				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);

				return model.get("responses");
			}

		});


		configs.add(column);  

		column = new ColumnConfig("views", "Views#", 100);
		column.setAlignment(HorizontalAlignment.CENTER);  
		column.setRenderer(new GridCellRenderer<ThreadModel>() {

			@Override
			public Object render(ThreadModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThreadModel> store, Grid<ThreadModel> grid) {

				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);

				return model.get("views");
			}

		});

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

		grid.getView().setShowDirtyCells(false);

		Listener<BaseEvent> tRowSelectionListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				System.out.println("row click event .................................. null");

				invokeListenerOperation();
			}
		};

		grid.addListener(Events.RowClick, tRowSelectionListener);

/*
		grid.addListener(Events.Attach, new Listener<GridEvent<ThreadModel>>() {  
			public void handleEvent(GridEvent<ThreadModel> be) {  
				if (subjectID != null && subjectID.getID() != -1) {
					PagingLoadConfig config = new BasePagingLoadConfig();  
					config.setOffset(0);  
					config.setLimit(10);

					Map<String, Object> state = grid.getState();  
					if (state.containsKey("offset")) {  
						int offset = (Integer)state.get("offset");  
						int limit = (Integer)state.get("limit");  
						config.setOffset(offset);  
						config.setLimit(limit);  
					}  
					System.out.println("atttttttttttttttttttttttttttttttttttttttttttttttttttt");
					loader.load(config);  
				}
			}  
		});  
*/



		//	grid.setLoadMask(true);

		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ThreadModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ThreadModel> se) {
				System.out.println("Sel changed");
				shouldUpdateViews = true;
				invokeListenerOperation();
			}
		});



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


	private String getRowStyle(String style, String color) {
		int index = style.indexOf("background-color"); 
		if (index == -1)
			return style += color;
		else
			return style.substring(0, index) + color;
	}

	private String getRegularRowStyle(String style) {
		return this.getRowStyle(style, "background-color:#FFFFFF;");
	}
	private String getColoredRowStyle(String style) {
		return this.getRowStyle(style, "background-color:#F5F9EE;");
	}

	private void invokeListenerOperation() {
		System.out.println("sssssssssssssssssssssssssss ");
		if (subjectID != null && grid.getSelectionModel() != null && grid.getSelectionModel().getSelectedItem() != null) {
			setButtonsEnableStatus(true);
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
					tSelectedThread.setTopic(result.getTopic());
					tSelectedThread.setResponsesNumber(result.getResponsesNumber());
					tSelectedThread.setViewsNumber(result.getViewsNumber());
					store.update(tSelectedThread);
					store.commitChanges();
				}
			});


		}
		else
			setButtonsEnableStatus(false);
		if (messagesTree != null) {
			//		System.out.println("row click event .................................. "  +
			//				grid.getSelectionModel().getSelectedItem().getId());
			if (grid != null && grid.getSelectionModel() != null && grid.getSelectionModel().getSelectedItem() != null)
				messagesTree.changeThreadID(grid.getSelectionModel().getSelectedItem().getID(), false);
		}
	}



}