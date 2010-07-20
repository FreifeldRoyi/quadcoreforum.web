package forum.client;

import java.util.List;

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
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SubjectModel;

public class AsyncSubjectsTreeGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private AsyncThreadsTableGrid threadsTable;
	
	private TreePanel<SubjectModel> tree;

	public AsyncSubjectsTreeGrid(AsyncThreadsTableGrid threads) {
		threadsTable = threads;
	}
	
	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		//				setLayout(new FlowLayout(0));


		ContentPanel cp = new ContentPanel();  
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setButtonAlign(HorizontalAlignment.CENTER);  

		cp.setHeight(600);

		//cp.setLayout(new AnchorLayout());

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


		final TreeStore<SubjectModel> tStore = new TreeStore<SubjectModel>(loader);  


		tree = new TreePanel<SubjectModel>(tStore);  




		tree.setDisplayProperty("name");


		tree.setStateful(true);  


		tree.setId("statefullasynctreegrid");  
		tStore.setKeyProvider(new ModelKeyProvider<SubjectModel>() {  
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


		Listener<TreePanelEvent<SubjectModel>>	tListener = new Listener<TreePanelEvent<SubjectModel>>() {  
			public void handleEvent(TreePanelEvent<SubjectModel> be) {  
				if (be.getItem() != null) {
					final SubjectModel toUpdate = be.getItem();
					service.getSubjectByID(be.getItem().getID(), new AsyncCallback<SubjectModel>() {
						@Override
						public void onFailure(Throwable caught) { /* do nothing - the expand listener will delete the row */ }

						@Override
						public void onSuccess(SubjectModel result) {
							toUpdate.setName(result.getName());
							toUpdate.setDescription(result.getDescription());
							toUpdate.setSubjectsNumber(Long.parseLong(result.getSubjectsNumber()));
							toUpdate.setMessagesNumber(Long.parseLong(result.getMessagesNumber()));
							tStore.update(toUpdate);
							tStore.commitChanges();
						}
					});

				}
			}  
		};

		// change in node check state  
		tree.addListener(Events.Expand, tListener);  

		// change in node check state  
		tree.addListener(Events.Collapse, tListener);  

		
		
		tree.addListener(Events.OnMouseUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				SubjectModel tSelected = tree.getSelectionModel().getSelectedItem();
				if (tSelected != null) {
					Registry.register("fathersubjectid", tSelected.getID());
					threadsTable.load();
				}
			}
		});

		tree.setCaching(false);

		tree.setBorders(true);  
		//     tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-page"));  

		tree.setHeight(500);

		tree.setTrackMouseOver(true);  
		cp.setScrollMode(Scroll.AUTOY);


		cp.add(tree);
		add(cp);  

	}

}