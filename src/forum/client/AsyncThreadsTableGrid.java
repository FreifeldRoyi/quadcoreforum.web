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
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.ThreadModel;

public class AsyncThreadsTableGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private PagingToolBar toolBar;

	private long subjectID;

	private RpcProxy<PagingLoadResult<ThreadModel>> proxy;
	private PagingLoader<PagingLoadResult<ThreadModel>> loader;
	private ListStore<ThreadModel> store;

	private Grid<ThreadModel> grid;
	private ContentPanel cp;

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

	private void initializeProxy() {
		proxy = new RpcProxy<PagingLoadResult<ThreadModel>>() {  

			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				if (subjectID == -1) {
					System.out.println("suuuuuuuuuuuuuuuuuuuuuuuuu " + subjectID);
					grid.el().unmask();
					toolBar.setEnabled(false);
					return;
				}
				service.getThreads((PagingLoadConfig) loadConfig, subjectID, new AsyncCallback<PagingLoadResult<ThreadModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
						grid.el().unmask();
						toolBar.setEnabled(true);
					}

					@Override
					public void onSuccess(
							PagingLoadResult<ThreadModel> result) {
						System.out.println("0000000000000000000000000");

						System.out.println(result.getTotalLength());
						callback.onSuccess(result);

						if (result.getTotalLength() == 0)
							toolBar.setEnabled(false);
						else {
							toolBar.setEnabled(true);
							// select the first row
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
		toolBar  = new PagingToolBar(10);  
		toolBar.bind(loader);  
		toolBar.setReuseConfig(false);
		toolBar.setEnabled(false);
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		setLayout(new FlowLayout(10));  


		initializeProxy();
		// loader  
		initializeLoader();


		this.initializeStore();

		this.initializeToolbar();

		this.initializeGrid();

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
				System.out.println(model.toString());
				System.out.println(config.style  + " 0");

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
				System.out.println(config.style);

				if (rowIndex % 2 == 0)
					config.style = getColoredRowStyle(config.style);
				else
					config.style = getRegularRowStyle(config.style);

				return model.get("views");
			}

		});


		configs.add(column);  

		ColumnModel cm = new ColumnModel(configs);  

		cp = new ContentPanel();  

		cp.setFrame(false);  
		cp.setHeaderVisible(false);
		cp.setButtonAlign(HorizontalAlignment.CENTER);  
		cp.setLayout(new FitLayout());  
		cp.setBottomComponent(toolBar);  

		grid = new Grid<ThreadModel>(store, cm);  

		GridView view = new GridView();
		view.setEmptyText("The subject has no threads.");
		grid.setView(view); 

		grid.setAutoExpandMax(3000);
		grid.setBorders(true);  
		grid.setAutoExpandColumn("topic");  




		cp.add(grid);  

		cp.layout();
		add(cp);  
		this.setLayout(new FitLayout());

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
}