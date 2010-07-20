package forum.client;

import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SubjectModel;

public class AsyncSubjectsTreeGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private TreePanel<SubjectModel> tree;

	private TreeStore<SubjectModel> store;

	private TabPanel mainPanel;


	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		mainPanel = Registry.get("maincontentpanel");

		setLayout(new FitLayout());


		ContentPanel cp = new ContentPanel();  
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setButtonAlign(HorizontalAlignment.CENTER);  

		//		cp.setHeight(600);

		cp.setLayout(new FitLayout());

		cp.setFrame(true);  

		// data proxy  
		RpcProxy<List<SubjectModel>> proxy = new RpcProxy<List<SubjectModel>>() {  

			@Override
			protected void load(final Object loadConfig, final
					AsyncCallback<List<SubjectModel>> callback) {

				final AsyncCallback<List<SubjectModel>> tNewCallback = new AsyncCallback<List<SubjectModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						tree.getStore().getLoader().load(null);
					}

					@Override
					public void onSuccess(List<SubjectModel> result) {
						callback.onSuccess(result);
						//						System.out.println("pppppppppppppppppppppppppppppp");
						if (loadConfig == null)
							tree.getSelectionModel().select(0, false);
						//					if (store.getAllItems().size() > 0) {

						//				}

					}
				};

				service.getSubjects((SubjectModel) loadConfig, tNewCallback);
			} 
		};  	


		// tree loader  
		final TreeLoader<SubjectModel> loader = new BaseTreeLoader<SubjectModel>(proxy) {  
			@Override  
			public boolean hasChildren(SubjectModel parent) {
				return true;
				//			return parent != null && parent.getChildCount() > 0;
			}
		};  


		store = new TreeStore<SubjectModel>(loader);  


		tree = new TreePanel<SubjectModel>(store);  




		tree.setDisplayProperty("name");


		tree.setStateful(true);  


		tree.setId("statefullasynctreegrid");  
		store.setKeyProvider(new ModelKeyProvider<SubjectModel>() {  
			public String getKey(SubjectModel model) {  
				return model.get("id") + "";  
			}  
		});  

		tree.setLabelProvider(new ModelStringProvider<SubjectModel>() {
			@Override
			public String getStringValue(SubjectModel model, String property) {
				return model.getName() + " (" + model.getSubjectsNumber() + ")";
			}
		});


		Listener<TreePanelEvent<SubjectModel>> tUpdateListener = new Listener<TreePanelEvent<SubjectModel>>() {  
			public void handleEvent(TreePanelEvent<SubjectModel> be) {  
				if (be.getItem() != null) {
					final SubjectModel toUpdate = be.getItem();
					//					tree.getSelectionModel().select(toUpdate, false);

					service.getSubjectByID(be.getItem().getID(), new AsyncCallback<SubjectModel>() {
						@Override
						public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

						@Override
						public void onSuccess(SubjectModel result) {
							toUpdate.setName(result.getName());
							toUpdate.setDescription(result.getDescription());
							toUpdate.setSubjectsNumber(Long.parseLong(result.getSubjectsNumber()));
							toUpdate.setMessagesNumber(Long.parseLong(result.getMessagesNumber()));
							store.update(toUpdate);
							store.commitChanges();
							SubjectTabItem tTabItem =
								(SubjectTabItem)mainPanel.getItemByItemId(toUpdate.getID() + "");
							if (tTabItem != null)
								tTabItem.updateTabTitle();
							
							System.out.println("apapapapappapappapapapapapapapp");
						}
					});

				}
			}  
		};


		Listener<BaseEvent> tMouseListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				SubjectModel tSelected = tree.getSelectionModel().getSelectedItem();
				if (tSelected != null) {


					TabItem tDefault = mainPanel.getItemByItemId("default");
					if (tDefault != null)
						mainPanel.remove(tDefault);

					TabItem tCurrentItem = null;
					Stack<TabItem> tStack = new Stack<TabItem>(); 

					while (tSelected  != null) {
						if ((tCurrentItem = mainPanel.getItemByItemId(tSelected.getID() + "")) == null) {
							TabItem tNewItem = new SubjectTabItem(tSelected);
							tNewItem.addListener(Events.Select, new Listener<BaseEvent>() {

								@Override
								public void handleEvent(BaseEvent be) {
									// TODO Auto-generated method stub
									
								}
							});
							tStack.push(tNewItem);
							
					}
						else
							tStack.push(tCurrentItem);
						tSelected =  tree.getStore().getParent(tSelected);
					}

					mainPanel.removeAll();
					while (!tStack.isEmpty()) {
						tCurrentItem = tStack.pop();
						mainPanel.add(tCurrentItem);
					}
					
						mainPanel.setSelection(tCurrentItem);

					
				}
			}
		};



		// change in node check state  
		tree.addListener(Events.Expand, tUpdateListener);  
		
		// change in node check state  
		tree.addListener(Events.Collapse, tUpdateListener);  

		tree.addListener(Events.OnMouseDown, tMouseListener);









		tree.setCaching(false);

		tree.setBorders(true);  
		//     tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-page"));  

		tree.setAutoSelect(true);
		//		tree.setHeight(500);

		tree.setTrackMouseOver(true);  

		cp.setScrollMode(Scroll.AUTOY);


		cp.add(tree);
		add(cp);  

	}
	
	

}