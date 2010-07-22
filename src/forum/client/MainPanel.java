package forum.client; 

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

import forum.shared.ConnectedUserData.UserType;

public class MainPanel extends LayoutContainer
{ 
	/*All private Members*/
	private final ContentPanel miscellaneousPanel = new ContentPanel();
	private final ContentPanel settingsPanel = new ContentPanel();  
	private final ContentPanel navigatorPanel = new ContentPanel();  

	
	private final ContentPanel mainPanel = new ContentPanel();
	private final TabPanel mainContentPanel = new TabPanel();

	private final BorderLayout rootPanelLayout = new BorderLayout();
	private final BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300, 100, Short.MAX_VALUE);

	final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	protected void onRender(Element target, int index) {  
		super.onRender(target, index);

		

		setLayout(rootPanelLayout); 
		setStyleAttribute("padding", "10px");  

		this.mainContentPanelInit();
		this.miscellaneousPanelInit();
		
		
		
		this.layout();

	} 


	private void mainContentPanelInit() {
		
		AddReplyForm tAddReplyForm = new AddReplyForm();
		tAddReplyForm.setVisible(true);
		
		
		Registry.register("MainViewPanel", mainPanel);
		Registry.register("maincontentpanel", mainContentPanel);
		Registry.register("AddReply", tAddReplyForm);
		
		
		mainPanel.setLayout(new FitLayout());
				
		mainPanel.setCollapsible(false);
		mainPanel.setHeaderVisible(false);
		mainPanel.add(mainContentPanel);
		mainPanel.setBorders(false);
		
		this.mainContentPanel.setBorders(true);
		this.mainContentPanel.setMinTabWidth(115);  
		this.mainContentPanel.setResizeTabs(true);  
		this.mainContentPanel.setAnimScroll(true);  
		this.mainContentPanel.setTabScroll(true);  
		this.mainContentPanel.setCloseContextMenu(true);

		//Main Content Panel
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));
		centerData.setCollapsible(false);




		//		this.mainContentPanel.setLayout(new FitLayout());

		this.initializeThreadsPanel();

		//		this.mainContentPanel.add(threadsPanel);

		add(this.mainPanel, centerData);

	}

	private void initializeThreadsPanel() {
		System.out.println("initttttttttttttttttttt");
		SubjectTabItem tDefaultItem = new SubjectTabItem(null);
		mainContentPanel.add(tDefaultItem);
		mainContentPanel.setSelection(tDefaultItem);


	}

	private void miscellaneousPanelInit() {


		this.miscellaneousPanel.setHeading("QuadCoreForum");
		this.miscellaneousPanel.setBorders(true);
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setMargins(new Margins(0,5,0,0));
		add(this.miscellaneousPanel, westData);

		this.miscellaneousPanel.setLayout(new BorderLayout());

		this.settingsPanelInit();
		this.navigationPanelInit();
		miscellaneousPanel.layout();
	}

	private void settingsPanelInit() {
		settingsPanel.setHeading("Settings");  
		settingsPanel.setBorders(false);  
		settingsPanel.setSize(300, 200);
		settingsPanel.setBodyStyle("fontSize: 12px; padding: 0px");  
		this.loadSettingsLinks();

		BorderLayoutData tNorthData= new BorderLayoutData(LayoutRegion.NORTH, 200, 200, Short.MAX_VALUE);

		settingsPanel.setCollapsible(true);
		settingsPanel.setHideCollapseTool(true);
		tNorthData.setCollapsible(true);
		tNorthData.setFloatable(true);
		this.miscellaneousPanel.add(this.settingsPanel, tNorthData);

		com.extjs.gxt.ui.client.widget.button.Button b = new Button();
		b.setShim(true);
		this.settingsPanel.add(b);
	}

	private void navigationPanelInit() {

		navigatorPanel.setHeading("Navigation");  
		navigatorPanel.setBorders(false);
		navigatorPanel.setBodyStyle("" +
		"fontSize: 12px; padding: 0px");  
		navigatorPanel.setScrollMode(Scroll.AUTOY);
		navigatorPanel.setCollapsible(false);

		navigatorPanel.add(new AsyncSubjectsTreeGrid());

		navigatorPanel.setLayout(new FitLayout());

		BorderLayoutData tCenterData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);

		tCenterData.setMargins(new Margins(5, 0, 0, 0));
		this.miscellaneousPanel.add(navigatorPanel, tCenterData);
	}

	public void changeLoginView() {
		if (QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.GUEST) {
			this.miscellaneousPanel.setHeading("Hello Guest!");
			this.settingsPanel.collapse();
		}
		else {
			this.miscellaneousPanel.setHeading("Hello " + QuadCoreForumWeb.CONNECTED_USER_DATA.getLastName() +
					" " + QuadCoreForumWeb.CONNECTED_USER_DATA.getFirstName() + "!");
			this.settingsPanel.expand();
		}
		this.changeToolBarsVisible();
	}

	public void changeToolBarsVisible() {
		for (TabItem tItem : this.mainContentPanel.getItems()) {
			SubjectTabItem tSubjectTab = (SubjectTabItem)tItem;
			tSubjectTab.changeToolBarVisible();
		}
	}

	private void loadSettingsLinks() {
		this.settingsPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				System.out.println("ddddddddddddddddddddd");
				System.out.println("eeeeeeeeeeeeeeeeeeeee");

			}
		}));
	}


	/**
	 * loads all links appearing on the navigation panel
	 */
	private void loadNavigatorLinks() {
		this.miscellaneousPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				
				/*
				service.getSubjects(-1, 
						new AsyncCallback<ServerResponse>() {

					@Override
					public void onSuccess(ServerResponse result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});*/
			}
		}));




		/*		this.navigatorPanel.add(createButton("Register", new Listener<ButtonEvent>() 
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
		 */	
	}
	/*
	private void addToMainContent(LayoutContainer c)
	{
		this.mainContentPanel.removeAll();
		this.mainContentPanel.add(c);
		this.mainContentPanel.layout();
	}
	 */
	private ToggleButton createButton(String text, Listener<ButtonEvent> l)
	{
		ToggleButton btn = new ToggleButton(text);

		btn.setToggleGroup("navigationbuttons");
		btn.addListener(Events.Toggle, l);
		btn.setAllowDepress(true);

		return btn;
	}
}
