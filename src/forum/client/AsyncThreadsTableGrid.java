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
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.ThreadModel;

public class AsyncThreadsTableGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");


	
	private PagingLoader<PagingLoadResult<ThreadModel>> loader;
	
	private long getFatherSubjectID() {
		Long toReturn = Registry.get("fathersubjectid");
		if (toReturn == null)
			return -1;
		else
			return toReturn.longValue();
	}
	
	public void load() {
		System.out.println("loading threads ...");
		this.loader.load(0, 9);
	}
	
	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		setLayout(new FlowLayout(10));  


		RpcProxy<PagingLoadResult<ThreadModel>> proxy = new RpcProxy<PagingLoadResult<ThreadModel>>() {  

			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<PagingLoadResult<ThreadModel>> callback) {
				long tFatherSubjectID = getFatherSubjectID();
				if (tFatherSubjectID != -1)
					service.getThreads((PagingLoadConfig) loadConfig, tFatherSubjectID, callback);			}  
		};  

		// loader  
		loader = new BasePagingLoader<PagingLoadResult<ThreadModel>>(proxy);  

		ListStore<ThreadModel> store = new ListStore<ThreadModel>(loader);  

		final PagingToolBar toolBar = new PagingToolBar(10);  
		toolBar.bind(loader);  

		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig("topic", "Topic", 200);
		
		column.setAlignment(HorizontalAlignment.LEFT);  
		configs.add(column);  

		column = new ColumnConfig("responses", "Responses#", 100);  
		column.setAlignment(HorizontalAlignment.CENTER);  

		configs.add(column);  

		column = new ColumnConfig("views", "Views#", 100);
		column.setAlignment(HorizontalAlignment.CENTER);  
		configs.add(column);  

		ColumnModel cm = new ColumnModel(configs);  

		ContentPanel cp = new ContentPanel();  

		cp.setFrame(true);  
		cp.setHeading("Threads");
		cp.setButtonAlign(HorizontalAlignment.CENTER);  
		cp.setLayout(new FitLayout());  
		cp.setBottomComponent(toolBar);  
		cp.setSize(600, 200);  

		Grid<ThreadModel> grid = new Grid<ThreadModel>(store, cm);  
	
		grid.setBorders(true);  
		grid.setAutoExpandColumn("topic");  

		cp.add(grid);  

		add(cp);  
	}  
}  