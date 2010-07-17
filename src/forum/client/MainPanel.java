package forum.client; 

import com.google.gwt.user.client.Element;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
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
		
		ContentPanel panel = new ContentPanel();
		panel.setLayout(this.layout);
		panel.setHeaderVisible(false);
		panel.setSize(1080, 800); //TODO there must be another way to do expand it over the entire screen
		
		//this.setLayout(this.layout);
		this.setAutoHeight(true);
		
		this.mainContentSettingsInit(panel);
		this.navigationSettingsInit(panel);
		
		this.add(panel);
	} 
	
	private void mainContentSettingsInit(ContentPanel panel)
	{
		this.mainContent.setHeading("Main Content");
		
		//Main Content Panel
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));
		
		VBoxLayout mainLayout = new VBoxLayout();
		mainLayout.setPadding(new Padding(5));
		mainLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
		mainLayout.setPack(BoxLayoutPack.CENTER);
		this.mainContent.setLayout(mainLayout);
		
		panel.add(this.mainContent, centerData);
	}
	
	private void navigationSettingsInit(ContentPanel panel)
	{
		this.navigatorPanel.setHeading("Navigator");
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200, 100, 300);
		//TODO set min and max size
		westData.setSplit(true);
		
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setMargins(new Margins(0,5,0,0));
		
		//sets layout
		VBoxLayout navPanelLayout = new VBoxLayout();
		navPanelLayout.setPadding(new Padding(5));
		navPanelLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		this.navigatorPanel.setLayout(navPanelLayout);
		
		this.loadNavigatorLinks();
		
		panel.add(this.navigatorPanel, westData);
	}
	
	/**
	 * loads all links appearing on the navigation panel
	 */
	private void loadNavigatorLinks()
	{
		this.navigatorPanel.add(createButton("Register", new Listener<ButtonEvent>() 
				{

					@Override
					public void handleEvent(ButtonEvent be) 
					{
						if (!be.<ToggleButton>getComponent().isPressed())
						{
							return;
						}
						RegistrationForm rf = new RegistrationForm();

						addToMainContent(rf);					
					}
			
				}));
	}
	
	private void addToMainContent(LayoutContainer c)
	{
		this.mainContent.removeAll();
		this.mainContent.add(c);
		this.mainContent.layout();
	}
	
	private ToggleButton createButton(String text, Listener<ButtonEvent> l)
	{
		ToggleButton btn = new ToggleButton(text);
		
		btn.setToggleGroup("navigationbuttons");
		btn.addListener(Events.Toggle, l);
		btn.setAllowDepress(false);
		
		return btn;
	}
}
