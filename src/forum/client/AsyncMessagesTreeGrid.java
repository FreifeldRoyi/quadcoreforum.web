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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
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

import forum.client.ConnectedUserData.UserType;
import forum.shared.MessageModel;

public class AsyncMessagesTreeGrid extends LayoutContainer {  

	private final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	private Button replyButton;
	private Button deleteButton; 
	private Button modifyButton;
	
	private ContentPanel cp;
	
	private TreeGrid<MessageModel> tree;

	private TreeLoader<MessageModel> loader;

	private TreeStore<MessageModel> store;

	private long threadID;

	private RowExpander expander;

	private ToolBar toolbar;
	
	public AsyncMessagesTreeGrid() {
		this.threadID = -1;
	}

	public void changeThreadID(long threadID) {
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

		cp = new ContentPanel();
		cp.setBodyBorder(false);
		
		cp.setHeading("Messages");
		
		cp.setButtonAlign(HorizontalAlignment.CENTER);  

		//		cp.setHeight(600);

		cp.setLayout(new FitLayout());

		cp.setFrame(false);  

		// data proxy  
		RpcProxy<List<MessageModel>> proxy = new RpcProxy<List<MessageModel>>() {  

			@Override
			protected void load(final Object loadConfig, final
					AsyncCallback<List<MessageModel>> callback) {

				final AsyncCallback<List<MessageModel>> tNewCallback = new AsyncCallback<List<MessageModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						tree.getStore().getLoader().load(null);
					}

					@Override
					public void onSuccess(List<MessageModel> result) {
						callback.onSuccess(result);
						if (loadConfig == null && result.size() > 0) {
							tree.getSelectionModel().select(0, false);
							
							
						}
					}
				};

				if (threadID != -1)
					service.getReplies(threadID, (MessageModel) loadConfig, false, tNewCallback);
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
		"<p><b>Content:</b> {content}</p>");  

	
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
					invokeListenerOperation(be.getModel());
				}
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
					invokeListenerOperation(se.getSelectedItem());
				}
				
			}
		});

		tree.setCaching(false);

		tree.setBorders(true);  
		//     tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-page"));  


		tree.setAutoExpandColumn("display");

		tree.setAutoExpandMax(3000);


		tree.getView().setEmptyText("No messages are available");

		tree.setTrackMouseOver(true);  

		cp.setScrollMode(Scroll.AUTOY);

		cp.add(tree);
		add(cp);  
		
		initializeToolbar();

	}

	private void invokeListenerOperation(final MessageModel model) {
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
/*							if (shouldExpandZeroRow > 0) {
					shouldExpandZeroRow--;
					expander.expandRow(0);
				}
*/
				com.google.gwt.dom.client.Element tRow = tree.getTreeView().getRow(model);
				if (tRow != null)
					expander.expandRow(tree.getView().findRowIndex(tRow));
//				System.out.println(	"indexxxxxxxxxxxxxxxxx : " +	store.getAllItems().indexOf(model));

			}
		});

	}

	private void setGuestView() {
		toolbar.setVisible(false);
	}
	
	private void setMemberView() {
		toolbar.setVisible(true);
	}
	
	private void initializeToolbar() {
		toolbar = new ToolBar();  
		toolbar.setBorders(true); 
		replyButton = new Button("Reply");
		replyButton.setWidth(115);
		deleteButton = new Button("Delete"); 
		deleteButton.setWidth(115);

		modifyButton = new Button("Modify");
		modifyButton.setWidth(115);

		toolbar.add(replyButton);
		toolbar.add(modifyButton);
		toolbar.add(deleteButton);

		toolbar.setAlignment(HorizontalAlignment.CENTER);
		cp.setTopComponent(toolbar);  

		if (QuadCoreForumWeb.CONNECTED_USER_DATA == null ||
				QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.GUEST)
			this.setGuestView();
		else
			this.setMemberView();
	}
}