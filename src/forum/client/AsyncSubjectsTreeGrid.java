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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SubjectModel;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.message.SubjectNotFoundException;
import forum.shared.exceptions.user.NotPermittedException;
import forum.shared.exceptions.user.NotRegisteredException;

public class AsyncSubjectsTreeGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private TreePanel<SubjectModel> tree;

	private TreeStore<SubjectModel> store;

	private TabPanel mainPanel;

	private ToolBar subjectsPanelToolbar = new ToolBar();

	ContentPanel subjectsContentPanel = new ContentPanel();  
	Button addRootSubject = new Button("Add root");

	Button addNewSubject = new Button("Add new");
	
	Button modifySubject = new Button("Modify");

	Button deleteSubject = new Button("Delete");

	private TreeLoader<SubjectModel> loader;
	
	public void setToolBarVisible(boolean value) {
		this.subjectsPanelToolbar.setVisible(value);
		subjectsContentPanel.layout();
		layout();
		((ContentPanel)Registry.get("NavigatorPanel")).layout();
	}
	
	@Override  
	protected void onRender(Element parent, int index) {
		addNewSubject.setEnabled(false);
		modifySubject.setEnabled(false);
		deleteSubject.setEnabled(false);

		subjectsPanelToolbar.setVisible(false);
		super.onRender(parent, index);  

		mainPanel = Registry.get("maincontentpanel");

		setLayout(new FitLayout());


		subjectsContentPanel.setBodyBorder(false);
		subjectsContentPanel.setHeaderVisible(false);
		subjectsContentPanel.setButtonAlign(HorizontalAlignment.CENTER);  

		//		cp.setHeight(600);

		subjectsContentPanel.setLayout(new FitLayout());

		subjectsPanelToolbar.add(addRootSubject);
		subjectsPanelToolbar.add(new SeparatorMenuItem());
		subjectsPanelToolbar.add(addNewSubject);
		subjectsPanelToolbar.add(modifySubject);
		subjectsPanelToolbar.add(deleteSubject);

		
		addNewSubject.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (tree == null || tree.getSelectionModel() == null)
					return;
				
				AddReplyModifyForm tAddSubjectForm = (AddReplyModifyForm)Registry.get("AddReply");
				tAddSubjectForm.initAddSubjectDialog(tree.getSelectionModel().getSelectedItem(), tree, store);
				MainPanel.changeMainViewToPanel(tAddSubjectForm);
			}
		});


		addRootSubject.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (tree == null || tree.getSelectionModel() == null)
					return;
				
				AddReplyModifyForm tAddSubjectForm = (AddReplyModifyForm)Registry.get("AddReply");
				tAddSubjectForm.initAddSubjectDialog(null, tree, store);
				MainPanel.changeMainViewToPanel(tAddSubjectForm);
			}
		});
		
		deleteSubject.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final SubjectModel tSelected = tree.getSelectionModel().getSelectedItem();
				if (tSelected == null) {
					deleteSubject.setEnabled(false);
					return;
				}
				else {	
					final Listener<MessageBoxEvent> tDeleteListener = new Listener<MessageBoxEvent>() {  
						public void handleEvent(MessageBoxEvent ce) {  
							Button btn = ce.getButtonClicked();
							if (btn.getText().equals("No"))
								return;
							deleteSubject.setEnabled(false);
							QuadCoreForumWeb.WORKING_STATUS.setBusy("Deleting subject...");
							QuadCoreForumWeb.SERVICE.deleteSubject(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
									(store.getParent(tSelected) != null? store.getParent(tSelected).getID() : -1),
									tSelected.getID() , new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
									deleteSubject.setEnabled(true);
									if (caught instanceof SubjectNotFoundException)
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
									deleteSubject.setEnabled(true);
									QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
									SubjectModel tParentOfSelected = store.getParent(tSelected);
									store.remove(tSelected);
									store.commitChanges();
									mainPanel.remove(mainPanel.getItemByItemId(tSelected.getID() + ""));
									if (tParentOfSelected != null) {
										tree.getSelectionModel().select(tParentOfSelected, false);
										SubjectTabItem tTabItem =
											(SubjectTabItem)mainPanel.getItemByItemId(tParentOfSelected.getID() + "");
										if (tTabItem != null)
											mainPanel.setSelection(tTabItem);
									}
									else {
										mainPanel.removeAll();
										mainPanel.add((TabItem) Registry.get("default"));
									}
										// 
									Info.display("Thread deletion success", "The subject was deleted successfully " +
									"from the forum");
								}
							});
						}  
					};  

					MessageBox.confirm("Confirm", "Are you sure you want to delete the subject?", tDeleteListener);  

					
					
					
					
					
					
					
					
					
					

					
				}
			}
				
		});
		
		
		subjectsContentPanel.setFrame(true);  

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


		loader = new BaseTreeLoader<SubjectModel>(proxy) {  
			@Override  
			public boolean hasChildren(SubjectModel parent) {
				return true;
				//			return parent != null && parent.getChildCount() > 0;
			}
		};  


		store = new TreeStore<SubjectModel>(loader);  


		tree = new TreePanel<SubjectModel>(store);  

		Registry.register("SubjectsTree", tree);


		tree.setDisplayProperty("name");

		tree.setStateful(true);  


		tree.setId("subjectstable");  
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
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Retrieving subjects...");

					final SubjectModel toUpdate = be.getItem();
					//					tree.getSelectionModel().select(toUpdate, false);

					service.getSubjectByID(be.getItem().getID(), new AsyncCallback<SubjectModel>() {
						@Override
						public void onFailure(Throwable caught) {
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");

							/* do nothing - the expand listener will delete the row */ 
							}

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
							QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
						}
					});

				}
			}  
		};


		Listener<BaseEvent> tMouseListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				SubjectModel tSelected = tree.getSelectionModel().getSelectedItem();
				
				System.out.println("opennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
				
				if (tSelected != null) {
					QuadCoreForumWeb.WORKING_STATUS.setBusy("Retrieving subjects...");

					modifySubject.setEnabled(true);
					deleteSubject.setEnabled(true);
					addNewSubject.setEnabled(true);

					TabItem tDefault = mainPanel.getItemByItemId("default");
					if (tDefault != null)
						mainPanel.remove(tDefault);

					TabItem tCurrentItem = null;
					Stack<TabItem> tStack = new Stack<TabItem>(); 

					while (tSelected  != null) {
						if ((tCurrentItem = mainPanel.getItemByItemId(tSelected.getID() + "")) == null) {
							final TabItem tNewItem = new SubjectTabItem(tSelected);
							tNewItem.addListener(Events.Select, new Listener<BaseEvent>() {

								@Override
								public void handleEvent(BaseEvent be) {
									System.out.println("SubjectTabItem.this " + ((SubjectTabItem)tNewItem).getSubject().getID());
										
									
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

						QuadCoreForumWeb.WORKING_STATUS.clearStatus("Not working");
					
				}
				else {
					addNewSubject.setEnabled(false);
					modifySubject.setEnabled(false);
					deleteSubject.setEnabled(false);
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

		subjectsContentPanel.setScrollMode(Scroll.AUTOY);

		subjectsContentPanel.setTopComponent(subjectsPanelToolbar);

		subjectsContentPanel.add(tree);
		subjectsContentPanel.setLayout(new FitLayout());
		subjectsContentPanel.layout();
		layout();
		add(subjectsContentPanel);  

	}
	
	

}