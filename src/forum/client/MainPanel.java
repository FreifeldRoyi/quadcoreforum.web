package forum.client; 




import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;

public class MainPanel extends LayoutContainer
{ 
	/*All private Members*/
	final private ContentPanel navigatorPanel = new ContentPanel();
	final private ContentPanel mainContent = new ContentPanel();
	final private BorderLayout layout = new BorderLayout();
	
	protected void onRender(Element target, int index) 
	{  
		super.onRender(target, index);
		
		this.setLayout(this.layout);
		this.setAutoHeight(true);
		
		this.mainContentSettingsInit();
		this.navigationSettingsInit();
	} 
	
	private void mainContentSettingsInit()
	{
		this.mainContent.setHeading("Main Content");
		
		//Main Content Panel
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));
		
		this.add(this.mainContent, centerData);
	}
	
	private void navigationSettingsInit()
	{
		this.navigatorPanel.setHeading("Navigator");
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST);
		//TODO set min and max size
		//westData.setSplit(true);
		
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setMargins(new Margins(0,5,0,0));
		
		//sets layout
		VBoxLayout navPanelLayout = new VBoxLayout();
		navPanelLayout.setPadding(new Padding(5));
		navPanelLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		this.navigatorPanel.setLayout(navPanelLayout);
		
		this.loadNavigatorLinks();
		
		this.add(this.navigatorPanel, westData);
	}
	
	/**
	 * loads all links appearing on the navigation panel
	 */
	private void loadNavigatorLinks()
	{
		Button btn1 = new Button("Search");
		this.navigatorPanel.add(btn1);
		Button btn2 = new Button("Search");
		this.navigatorPanel.add(btn2);
	}
}
