package forum.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
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
	private List<ColumnConfig> configs;
	
	private int resultsPerPage;
	private String searchType;
	private String searchPhrase;

	public AsyncSearchHitsTableGrid(int numOfPages, String type,
			String searchPhrase)
	{
		super();
		this.searchType = type;
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
		
		this.add(this.grid);
		this.setLayout(new FitLayout());
		
		//TODO add listener here
	}

	private void initializeGrid() 
	{
		RowNumberer rn = new RowNumberer();
		rn.setWidth(30);
		this.configs.add(rn);
		
		this.addToConfigs("title", "Title", 100);
		this.addToConfigs("authorUserName", "Author", 100);
		this.addToConfigs("date", "Date", 100);
		
		ColumnModel cm = new ColumnModel(this.configs);
		this.grid = new Grid<SearchHitModel>(this.store, cm);
		
		GridView view = new GridView();
		view.setEmptyText("Searching for \"" + this.searchPhrase + "\" has yielded no results");
		this.grid.setView(view);
		this.grid.setAutoExpandColumn("title");
		this.grid.getView().setShowDirtyCells(false);
		
		//TODO define a listener
	}
	
	private void addToConfigs(String value, String givenName, int width)
	{
		this.configs.add(new ColumnConfig(value,givenName,width));
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
					final AsyncCallback<PagingLoadResult<SearchHitModel>> callback) 
			{
				if (searchType.equals("author"))
				{
					service.searchByAuthor((PagingLoadConfig)loadConfig, 
							searchPhrase, new AsyncCallback<PagingLoadResult<SearchHitModel>>() {

								@Override
								public void onFailure(Throwable caught) 
								{
									callback.onFailure(caught);
									grid.el().unmask();
									hitsPager.setEnabled(true);
								}

								@Override
								public void onSuccess(
										PagingLoadResult<SearchHitModel> result) 
								{
									callback.onSuccess(result);
									grid.el().unmask();
									hitsPager.setEnabled(true);
									
									if(result.getTotalLength() == 0)
									{
										hitsPager.setEnabled(false);
										//TODO
									}
									else
									{
										hitsPager.setEnabled(true);
										store.commitChanges();
										grid.getSelectionModel().select(0, false);
									}
								}
							});
				}
				else if (searchType.equals("content"))
				{
					service.searchByContent((PagingLoadConfig)loadConfig, searchPhrase, 
							new AsyncCallback<PagingLoadResult<SearchHitModel>>()
							{
								@Override
								public void onFailure(Throwable caught) 
								{
									callback.onFailure(caught);
									grid.el().unmask();
									hitsPager.setEnabled(true);
								}

								@Override
								public void onSuccess(
										PagingLoadResult<SearchHitModel> result)
								{
									callback.onSuccess(result);
									grid.el().unmask();
									hitsPager.setEnabled(true);
									
									if (result.getTotalLength() == 0)
									{
										
									}
									else 
									{
										hitsPager.setEnabled(true);
										store.commitChanges();
										grid.getSelectionModel().select(0, false);
									}
								}
							});
				}
			}
		};
	}
}
