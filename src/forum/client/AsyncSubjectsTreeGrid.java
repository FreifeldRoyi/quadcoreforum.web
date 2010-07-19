package forum.client;

 import java.util.Arrays;  
 import java.util.List;  
   
 import com.extjs.gxt.ui.client.Registry;  
 import com.extjs.gxt.ui.client.Style.HorizontalAlignment;  
 import com.extjs.gxt.ui.client.data.BaseTreeLoader;  
import com.extjs.gxt.ui.client.data.BaseTreeModel;
 import com.extjs.gxt.ui.client.data.ModelData;  
 import com.extjs.gxt.ui.client.data.ModelKeyProvider;  
 import com.extjs.gxt.ui.client.data.RpcProxy;  
 import com.extjs.gxt.ui.client.data.TreeLoader;  
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
 import com.extjs.gxt.ui.client.store.Store;  
 import com.extjs.gxt.ui.client.store.StoreSorter;  
 import com.extjs.gxt.ui.client.store.TreeStore;  
 import com.extjs.gxt.ui.client.util.IconHelper;  
 import com.extjs.gxt.ui.client.widget.ContentPanel;  
 import com.extjs.gxt.ui.client.widget.LayoutContainer;  
 import com.extjs.gxt.ui.client.widget.button.ToolButton;  
 import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;  
 import com.extjs.gxt.ui.client.widget.grid.ColumnModel;  
 import com.extjs.gxt.ui.client.widget.layout.FitLayout;  
 import com.extjs.gxt.ui.client.widget.layout.FlowLayout;  
 import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;  
 import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;  
 import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;  
 import com.google.gwt.i18n.client.DateTimeFormat;  
 import com.google.gwt.user.client.Element;  
import com.google.gwt.user.client.rpc.AsyncCallback;  

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;
   
 public class AsyncSubjectsTreeGrid extends LayoutContainer {  
   
   @Override  
   protected void onRender(Element parent, int index) {  
     super.onRender(parent, index);  
   
     setLayout(new FlowLayout(10));  
   
     final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");
   
   
     ColumnConfig name = new ColumnConfig("name", "Name", 100);  
     name.setRenderer(new TreeGridCellRenderer<ModelData>());  

     ColumnConfig description = new ColumnConfig("description", "Description", 100);  
     description.setRenderer(new TreeGridCellRenderer<ModelData>());  
     
     ColumnConfig messagesNumber = new ColumnConfig("messages#", "Messages#", 100);  
     messagesNumber.setRenderer(new TreeGridCellRenderer<ModelData>());  
     
     ColumnConfig subjectsNumber = new ColumnConfig("subjects#", "Subjects#", 100);  
     subjectsNumber.setRenderer(new TreeGridCellRenderer<ModelData>());  

     ColumnModel tColumnModel = new ColumnModel(Arrays.asList(name, description, subjectsNumber, messagesNumber));  
   
     ContentPanel cp = new ContentPanel();  
     cp.setBodyBorder(false);
     cp.setHeaderVisible(false);
     cp.setButtonAlign(HorizontalAlignment.CENTER);  
     cp.setLayout(new FitLayout());  
     cp.setFrame(true);  

     
     
     final TreeStore<BaseTreeModel> tStore = new TreeStore<BaseTreeModel>();  
  
     
     TreeGrid<BaseTreeModel> tree = new TreeGrid<BaseTreeModel>(tStore, tColumnModel);  
     
     
     tree.setStateful(true);  

          
     // stateful components need a defined id  
     tree.setId("statefullasynctreegrid");  
     tStore.setKeyProvider(new ModelKeyProvider<BaseTreeModel>() {  
   
       public String getKey(BaseTreeModel model) {  
         return model.get("id");  
       }  
     });  
     
     BaseTreeModel tModel = new BaseTreeModel();
     
     tModel.setSilent(true);
     tModel.set("name", "1");
     tModel.set("description", "20");
     tModel.set("messages#", "29");
     tModel.set("subjects#", "90");
     

     tModel.set("id", "10");

     tStore.add(tModel, true);

     
     tree.setBorders(true);  
     tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-page"));  
     tree.setSize(400, 400);  
     tree.setAutoExpandColumn("name");  
     tree.setTrackMouseOver(false);  
     cp.add(tree);  
   
     add(cp);  
   }  
   
 } 