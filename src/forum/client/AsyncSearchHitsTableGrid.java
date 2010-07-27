package forum.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SearchHitModel;
import forum.shared.SubjectModel;

public class AsyncSearchHitsTableGrid extends LayoutContainer 
{
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
		configs = new ArrayList<ColumnConfig>();
		this.searchType = type;
		this.resultsPerPage = numOfPages;
		this.searchPhrase = searchPhrase;
	}

	@Override  
	protected void onRender(Element parent, int index) 
	{  
		super.onRender(parent, index);

		this.initializeProxy();
		this.initializeLoader();
		this.initializeStore();
		this.initializePagingBar();
		this.initializeGrid();

		this.setLayout(new FitLayout());

		ContentPanel tContent = new ContentPanel();
		tContent.setHeaderVisible(false);
		tContent.setBorders(false);
		tContent.setFrame(false);
		tContent.setLayout(new FitLayout());
		tContent.setBottomComponent(hitsPager);
		tContent.add(this.grid);
		this.add(tContent);

		loader.load(0, this.resultsPerPage);
	}



	private void loadThreadsAndMessages(SearchHitModel tHitToShow) {
		final TreePanel<SubjectModel> subjectsTree = Registry.get("SubjectsTree");
		subjectsTree.fireEvent(Events.OnMouseDown);
		subjectsTree.unmask();
	}

	@SuppressWarnings("unchecked")
	private void notFound() {
		QuadCoreForumWeb.SEARCH_STATE = false;
		final TreePanel<SubjectModel> subjectsTree = (TreePanel<SubjectModel>)Registry.get("SubjectsTree");

		subjectsTree.unmask();

	}

	private void initializeGrid() 
	{
		RowNumberer rn = new RowNumberer();
		rn.setWidth(30);
		this.configs.add(rn);

		this.addToConfigs("title", "Title", 100);
		this.addToConfigs("authorUserName", "Author", 100);
		this.addToConfigs("content", "Content", 100);
		this.addToConfigs("score", "Score", 100);
		
		ColumnModel cm = new ColumnModel(this.configs);
		this.grid = new Grid<SearchHitModel>(this.store, cm);

		GridView view = new GridView();
		view.setEmptyText("Searching for \"" + this.searchPhrase + "\" has yielded no results");
		this.grid.setView(view);
		this.grid.setAutoExpandMax(3000);
		this.grid.setAutoExpandColumn("title");
		this.grid.getView().setShowDirtyCells(false);
		this.grid.setStripeRows(true);

		grid.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(BaseEvent be) {
				MainPanel.changeMainViewToSubjectsAndThreads();
				final SearchHitModel tModel = grid.getSelectionModel().getSelectedItem();
				if (tModel == null) {
					be.setCancelled(true);
					return;
				}
				else {
					QuadCoreForumWeb.SEARCHING_MESSAGES = false;
					QuadCoreForumWeb.SEARCH_STATE = false;

					
					QuadCoreForumWeb.SEARCH_STATE = true;
				//	QuadCoreForumWeb.SEARCH_OF_THREADS_NOT_PERFORMED = false;
					QuadCoreForumWeb.SEARCH_STATE_HIT = tModel;
					final TreePanel<SubjectModel> subjectsTree = (TreePanel<SubjectModel>)Registry.get("SubjectsTree");

					subjectsTree.mask();

					subjectsTree.collapseAll();

					final Stack<SubjectModel> tSubjectsPath = new Stack<SubjectModel>();
					final Stack<SubjectModel> tTempStack = new Stack<SubjectModel>();

					while (!tModel.getSubjectPath().isEmpty()) {
						tTempStack.push(tModel.getSubjectPath().pop());
					}
					while (!tTempStack.isEmpty()) {
						SubjectModel tCurrent = tTempStack.pop();
						tSubjectsPath.push(tCurrent);
						tModel.getSubjectPath().push(tCurrent);
					}

					Listener<BaseEvent> tExpandListener = new Listener<BaseEvent>() {
						@Override
						public void handleEvent(BaseEvent be) {
							SubjectModel tCurrent = tSubjectsPath.pop();

							SubjectModel tCurrentInTree = subjectsTree.getStore().findModel(tCurrent);
							if (tCurrentInTree == null) {
								subjectsTree.removeListener(Events.Expand, this);
								notFound();
							}
							else if (tSubjectsPath.isEmpty()) {
								subjectsTree.getSelectionModel().select(tCurrentInTree, false);
								subjectsTree.removeListener(Events.Expand, this);
								loadThreadsAndMessages(tModel);
							}
							else {
								subjectsTree.setExpanded(tCurrentInTree, true);
							}
						}
					};

					subjectsTree.addListener(Events.Expand, tExpandListener); 


					SubjectModel tCurrent = tSubjectsPath.pop();
					SubjectModel tCurrentInTree = subjectsTree.getStore().findModel(tCurrent);
					if (tCurrentInTree == null) {
						subjectsTree.removeListener(Events.Expand, tExpandListener);
						notFound();
					}
					else if (tSubjectsPath.isEmpty()) {
						subjectsTree.removeListener(Events.Expand, tExpandListener);
						subjectsTree.getSelectionModel().select(tCurrentInTree, false);
						loadThreadsAndMessages(tModel);
					}
					else {
						subjectsTree.setExpanded(tCurrentInTree, true);
					}


				}

			}


		});

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

				final AsyncCallback<PagingLoadResult<SearchHitModel>> resultCallback =
					new AsyncCallback<PagingLoadResult<SearchHitModel>>() {

					@Override
					public void onFailure(Throwable caught) 
					{
						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
						callback.onFailure(caught);
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(
							PagingLoadResult<SearchHitModel> result) 
					{
						callback.onSuccess(result);

						if(result.getTotalLength() == 0)
						{
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
						}
						else
						{
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
							grid.getSelectionModel().select(0, false);
						}
					}
				};


				QuadCoreForumWeb.WORKING_STATUS.setBusy("Searching messages...");
				if (searchType.equals("author"))
				{
					QuadCoreForumWeb.SERVICE.searchByAuthor((PagingLoadConfig)loadConfig, 
							searchPhrase, resultCallback);
				}
				else if (searchType.equals("content"))
				{
					QuadCoreForumWeb.SERVICE.searchByContent((PagingLoadConfig)loadConfig, searchPhrase, 
							resultCallback);
				}
				else //search type unknown
				{						
					QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					return;
				}
			}
		};
	}
}
