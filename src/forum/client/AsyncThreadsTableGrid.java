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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.ThreadModel;
import forum.shared.ConnectedUserData.UserType;

public class AsyncThreadsTableGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private PagingToolBar statusBar;

	private ToolBar toolbar;

	private long subjectID;

	private RpcProxy<PagingLoadResult<ThreadModel>> proxy;
	private PagingLoader<PagingLoadResult<ThreadModel>> loader;
	private ListStore<ThreadModel> store;

	private Grid<ThreadModel> grid;
	private ContentPanel threadsPanel;

	private AsyncMessagesTreeGrid messagesTree;

	private Button openNewThreadButton;

	private Button deleteThreadButton;

	private Button modifyThreadButton;

	/*	@Override
	public void setTitle(String title) {
		if (title != null)
			cp.setHeading("Threads: " + title);
	}
	 */

	public AsyncThreadsTableGrid(long subjectID) {
		System.out.println("initializing " + subjectID);
		this.subjectID = subjectID;
	}

	public void setMessagesTree(AsyncMessagesTreeGrid messagesTree) {
		System.out.println("setting  " + messagesTree != null);
		this.messagesTree = messagesTree;
	}

	private void initializeProxy() {
		proxy = new RpcProxy<PagingLoadResult<ThreadModel>>() {  

			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				if (subjectID == -1) {
					System.out.println("suuuuuuuuuuuuuuuuuuuuuuuuu " + subjectID);
					grid.el().unmask();
					statusBar.setEnabled(false);
					return;
				}
				service.getThreads((PagingLoadConfig) loadConfig, subjectID, new AsyncCallback<PagingLoadResult<ThreadModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
						grid.el().unmask();
						statusBar.setEnabled(true);
					}

					@Override
					public void onSuccess(
							PagingLoadResult<ThreadModel> result) {
						System.out.println("0000000000000000000000000");

						System.out.println(result.getTotalLength());
						callback.onSuccess(result);

						if (result.getTotalLength() == 0) {
							statusBar.setEnabled(false);
							messagesTree.setGuestView();
							setButtonsEnableStatus(false);
						}
						else {
							statusBar.setEnabled(true);
							// select the first row
							store.commitChanges();
							grid.getSelectionModel().select(0, false);
						}
						/*							if (shouldRefresh) {
								System.out.println("shouldddddddddddddddddddddddddddd");
								shouldRefresh = false;
								toolBar.refresh();
							}*/
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
		this.loader.load(0, 9);
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

		modifyThreadButton = new Button("Modify");
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
		statusBar.bind(loader);  
		statusBar.setReuseConfig(false);
		statusBar.setEnabled(false);
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


		threadsPanel.layout();
	}  

	public void setToolBarVisible(boolean value) {
		if (this.toolbar != null && this.toolbar.isVisible())
			this.toolbar.setVisible(value);
		this.threadsPanel.layout();
		this.threadsPanel.repaint();
	}

	public void setButtonsEnableStatus(boolean value) {
		System.out.println("TTTTTTTTTTTTTTTTTTTTTTJJJJJJJJJJJJJJJJJJJJJJJJJJ");
		if (openNewThreadButton != null)
			this.openNewThreadButton.setEnabled(true);
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

		ColumnConfig column = new ColumnConfig("topic", "Topic", 800);
		column.setRenderer(new GridCellRenderer<ThreadModel>() {

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


		column.setAlignment(HorizontalAlignment.LEFT);  
		configs.add(column);  

		column = new ColumnConfig("responses", "Responses#", 100);  
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
		grid.setBorders(true);  
		grid.setAutoExpandColumn("topic");  


		Listener<BaseEvent> tRowSelectionListener = new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				System.out.println("row click event .................................. null");

				invokeListenerOperation();
			}
		};

		grid.addListener(Events.RowClick, tRowSelectionListener);


		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ThreadModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ThreadModel> se) {
				// TODO Auto-generated method stub
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
		if (grid.getSelectionModel() != null && grid.getSelectionModel().getSelectedItem() != null) {
			setButtonsEnableStatus(true);
		}
		else
			setButtonsEnableStatus(false);
		if (messagesTree != null) {
			//		System.out.println("row click event .................................. "  +
			//				grid.getSelectionModel().getSelectedItem().getId());

			

			if (grid != null && grid.getSelectionModel() != null && grid.getSelectionModel().getSelectedItem() != null)
				messagesTree.changeThreadID(grid.getSelectionModel().getSelectedItem().getId());


		}
	}


}