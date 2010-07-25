package forum.client;

import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
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

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private Button replyButton;
	private Button deleteButton; 
	private Button modifyButton;

	private ContentPanel messagesPanel;

	private TreeGrid<MessageModel> tree;
	private TreeLoader<MessageModel> loader;
	private TreeStore<MessageModel> store;

	private long threadID;

	private RowExpander expander;

	private AsyncThreadsTableGrid threadsTable;

	private ToolBar toolbar;

	private boolean selectionChanged;

	public AsyncMessagesTreeGrid(AsyncThreadsTableGrid threadsTable) {
		this.threadsTable = threadsTable;
		this.threadID = -1;
	}


	public void changeThreadID(long threadID, boolean selectionChanged) {
		this.selectionChanged = selectionChanged;
		System.out.println("Changing threadID");
		this.threadID = threadID;
		if (loader != null)
			loader.load(null);
	}

	private String getColoredRowStyle(String style) {
		return this.getRowStyle(style, "background-color:#F5F9EE;");
	}

	private String getRegularRowStyle(String style) {
		return this.getRowStyle(style, "background-color:#FFFFFF;");
	}

	private String getRowStyle(String style, String color) {
		int index = style.indexOf("background-color"); 
		if (index == -1)
			return style += color;
		else
			return style.substring(0, index) + color;
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		setLayout(new FitLayout());

		messagesPanel = new ContentPanel();
		messagesPanel.setBodyBorder(false);

		messagesPanel.setHeading("Messages");

		messagesPanel.setButtonAlign(HorizontalAlignment.CENTER);  

		//		cp.setHeight(600);

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
						tree.getStore().getLoader().load();
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}

					@Override
					public void onSuccess(List<MessageModel> result) {
						callback.onSuccess(result);
						if (loadConfig == null && result.size() > 0) {
							tree.getSelectionModel().select(0, false);
						}
						else if (result.size() == 0)
							setGuestView();
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					}
				};

				if (threadID != -1) {
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Loading messages...");
					service.getReplies(threadID, (MessageModel) loadConfig, selectionChanged, tNewCallback);
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
		name.setRenderer(new TreeGridCellRenderer<MessageModel>() {

			@Override
			public Object render(MessageModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MessageModel> store,
					Grid<MessageModel> grid) {
				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);
				return super.render(model, property, config, rowIndex, colIndex, store, grid);
			}
		});

		ColumnConfig date = new ColumnConfig("date", "Date", 180);  
		date.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());  
		date.setRenderer(new GridCellRenderer<MessageModel>() {

			@Override
			public Object render(MessageModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MessageModel> store,
					Grid<MessageModel> grid) {
				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);

				return model.get("date");


			}
		});



		ColumnModel cm = new ColumnModel(Arrays.asList(tNumberer, expander, name, date));  


		store = new TreeStore<MessageModel>(loader);  

		tree = new TreeGrid<MessageModel>(store, cm);  





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


		Listener<TreeGridEvent<MessageModel>> tUpdateListener =
			new Listener<TreeGridEvent<MessageModel>>() {  
			public void handleEvent(final TreeGridEvent<MessageModel> be) {  

				if (be.getModel() != null) {
					invokeExpansionOperation(be.getModel());
				}
				else
					setGuestView();

			}  
		};



		// change in node check state  
		tree.addListener(Events.Expand, tUpdateListener);  

		// change in node check state  
		tree.addListener(Events.Collapse, tUpdateListener);  


		tree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<MessageModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<MessageModel> se) {
				if (se.getSelectedItem() != null) {
					invokeSelectListenerOperation(se.getSelectedItem());
				}
				else
					setGuestView();

			}
		});

		tree.setCaching(false);

		tree.setBorders(true);  
		//     tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-page"));  


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





	private void invokeSelectListenerOperation(final MessageModel model) {
		service.getMessageByID(model.getID(), new AsyncCallback<MessageModel>() {

			@Override
			public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

			@Override
			public void onSuccess(MessageModel result) {
				model.setTitle(result.getTitle());
				model.setContent(result.getContent());
				model.setDate(result.getDate());
				store.update(model);
				store.commitChanges();

				MessageModel tSelected = tree.getSelectionModel().getSelectedItem();

				if (tSelected != null) {
					com.google.gwt.dom.client.Element tRow = tree.getTreeView().getRow(tSelected);
					if (tRow != null)
						expander.expandRow(tree.getView().findRowIndex(tRow));
					allowReplyModifyDeleteButtons(tSelected);

				}
			}
		});
	}


	private void invokeExpansionOperation(final MessageModel model) {
		service.getMessageByID(model.getID(), new AsyncCallback<MessageModel>() {

			@Override
			public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

			@Override
			public void onSuccess(MessageModel result) {
				model.setTitle(result.getTitle());
				model.setContent(result.getContent());
				model.setDate(result.getDate());
				store.update(model);

				// Update all the rows in order to render them again
				for (MessageModel tModel : store.getAllItems())
					store.update(tModel);

				System.out.println("pppppppppppppppppppppppppppppppppppp");
				store.commitChanges();
				MessageModel tSelected = tree.getSelectionModel().getSelectedItem();

				/*				if (tSelected != null) {
					com.google.gwt.dom.client.Element tRow = tree.getTreeView().getRow(tSelected);
					if (tRow != null)
						expander.expandRow(tree.getView().findRowIndex(tRow));
					allowReplyModifyDeleteButtons(tSelected);

				}
				 */

			}
		});
	}


	public void setToolBarVisible(boolean value) {
		this.toolbar.setVisible(value);
		this.messagesPanel.layout();

		messagesPanel.fireEvent(Events.Resize);
		tree.fireEvent(Events.Resize);
		fireEvent(Events.Resize);
		/*		messagesPanel.add(tree);
		setScrollMode(Scroll.AUTOY);
		add(messagesPanel);  
		 */

	}

	public void setGuestView() {
		System.out.println("guests viewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
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
		if (selected == null || QuadCoreForumWeb.CONNECTED_USER_DATA == null)
			setGuestView();
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