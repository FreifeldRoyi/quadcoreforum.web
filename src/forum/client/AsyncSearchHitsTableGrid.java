package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SearchHitModel;

public class AsyncSearchHitsTableGrid extends LayoutContainer 
{
	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private PagingToolBar hitsPager;

	private RpcProxy<PagingLoadResult<SearchHitModel>> proxy;
	private PagingLoader<PagingLoadResult<SearchHitModel>> loader;
	private ListStore<SearchHitModel> store;

	private Grid<SearchHitModel> grid;
	
	private int resultsPerPage;

	public AsyncSearchHitsTableGrid(int numOfPages)
	{
		super();
		this.resultsPerPage = numOfPages;
	}
	
	@Override  
	protected void onRender(Element parent, int index) 
	{  
		super.onRender(parent, index);

		this.setLayout(new FlowLayout(10));

		this.initializeProxy();
		this.initializeLoader();
		this.initializeStore();
		this.initializePagingBar();
		this.initializeGrid();
	}

	private void initializeGrid() {
		// TODO Auto-generated method stub

	}

	private void initializePagingBar() 
	{
		this.hitsPager = new PagingToolBar(this.resultsPerPage);
		this.hitsPager.bind(loader);
		this.hitsPager.setReuseConfig(false);
		this.hitsPager.setEnabled(false);
	}

	private void initializeStore() 
	{
		this.store = new ListStore<SearchHitModel>(loader);
	}

	private void initializeLoader() 
	{
		this.loader = new BasePagingLoader<PagingLoadResult<SearchHitModel>>(proxy);		
	}

	private void initializeProxy() 
	{
		this.proxy = new RpcProxy<PagingLoadResult<SearchHitModel>>() 
		{
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<SearchHitModel>> callback) 
			{
				
			}
		};
	}
}
