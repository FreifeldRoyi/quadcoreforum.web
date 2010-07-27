package forum.client; 

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

import forum.shared.UserModel.UserType;

public class MainPanel extends LayoutContainer
{ 
	/*All private Members*/
	private final ContentPanel miscellaneousPanel = new ContentPanel();
	private final ContentPanel navigatorPanel = new ContentPanel();  

	
	private final ContentPanel mainPanel = new ContentPanel();

	private final TabPanel mainContentPanel = new TabPanel();

	private final BorderLayout rootPanelLayout = new BorderLayout();
	
	private final BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300, 100, Short.MAX_VALUE);

	final ControllerServiceAsync service = (ControllerServiceAsync) Registry.get("Servlet");

	AsyncSubjectsTreeGrid subjectsGrid = new AsyncSubjectsTreeGrid();
	
	protected void onRender(Element target, int index) {  
		super.onRender(target, index);
		setLayout(rootPanelLayout); 
		setStyleAttribute("padding", "10px");  

		this.mainContentPanelInit();
		this.miscellaneousPanelInit();
		this.layout();
	} 


	private void mainContentPanelInit() {
		
//		AddReplyModifyForm tAddReplyForm = new AddReplyModifyForm();
//		tAddReplyForm.setVisible(true);
		
		
		Registry.register("MainViewPanel", mainPanel);
		Registry.register("maincontentpanel", mainContentPanel);
//		Registry.register("AddReply", tAddReplyForm);
		Registry.register("NavigatorPanel", navigatorPanel);
		
		
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


		this.initializeThreadsPanel();
		add(this.mainPanel, centerData);
	}

	private void initializeThreadsPanel() {
		SubjectTabItem tDefaultItem = new SubjectTabItem(null);
		Registry.register("default", tDefaultItem);
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

		this.miscellaneousPanel.setLayout(new FitLayout());

		this.navigationPanelInit();
		miscellaneousPanel.layout();
	}

/*	private void settingsPanelInit() {
		settingsPanel.setHeading("Settings");  
		settingsPanel.setBorders(false);  
		settingsPanel.setSize(300, 200);
		settingsPanel.setBodyStyle("fontSize: 12px; padding: 0px"); 
		settingsPanel.setCollapsible(false);
		settingsPanel.setHideCollapseTool(true);

	
		settingsPanel.setTopComponent(settingsPanelToolbar);
		
		VBoxLayout tVBoxLayout = new VBoxLayout();
		tVBoxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		VBoxLayoutData vBoxData = new VBoxLayoutData(5, 5, 5, 5); 
		vBoxData.setFlex(1);  
		
		settingsPanel.setLayout(tVBoxLayout);
	
		

		settingsPanelNorthData.setCollapsible(false);
		settingsPanelNorthData.setFloatable(true);
		
		
		this.miscellaneousPanel.add(this.settingsPanel, settingsPanelNorthData);
		this.miscellaneousPanel.remove(this.settingsPanel);
		this.miscellaneousPanel.add(this.settingsPanel, settingsPanelNorthData);

	}*/

	private void navigationPanelInit() {

		Button tRefreshRootSubjectsButton = new Button("", IconHelper.createStyle("subjectsRefresh"));
		Registry.register("RefreshRootSubjectsButton", tRefreshRootSubjectsButton);
		navigatorPanel.getHeader().addTool(tRefreshRootSubjectsButton);
		navigatorPanel.setHeading("Navigation");  
		navigatorPanel.setBorders(false);
		navigatorPanel.setBodyStyle("fontSize: 12px; padding: 0px");  
		navigatorPanel.setCollapsible(false);

		navigatorPanel.setLayout(new FitLayout());		navigatorPanel.add(subjectsGrid);

		this.miscellaneousPanel.add(navigatorPanel);
	}

	public void changeLoginView() {
		if (QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.GUEST) {
			this.miscellaneousPanel.setHeading("Hello Guest!");
			this.subjectsGrid.setToolBarVisible(false);
//			QuadCoreForumWeb.SHOW_CONNECTED.setEnabled(false);

		}
		else {
			this.miscellaneousPanel.setHeading("Hello " + QuadCoreForumWeb.CONNECTED_USER_DATA.getLastName() +
					" " + QuadCoreForumWeb.CONNECTED_USER_DATA.getFirstName() + "!");
			this.subjectsGrid.setToolBarVisible(QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.ADMIN);
//			QuadCoreForumWeb.SHOW_CONNECTED.setEnabled(true);

		/*	if (QuadCoreForumWeb.CONNECTED_USER_DATA.getType() == UserType.ADMIN)
				this.navigatorPanelToolbar.setVisible(true);
			else
				this.navigatorPanelToolbar.setVisible(false);
				*/
		}

		miscellaneousPanel.removeAll();
		miscellaneousPanel.add(navigatorPanel);
		miscellaneousPanel.layout();
		this.changeToolBarsVisible();
	}

	public void changeToolBarsVisible() {
		for (TabItem tItem : this.mainContentPanel.getItems()) {
			SubjectTabItem tSubjectTab = (SubjectTabItem)tItem;
			tSubjectTab.changeToolBarVisible();
		}
	}
	
	public static void changeMainViewToPanel(LayoutContainer container) {
		ContentPanel tMainViewPanel = (ContentPanel)Registry.get("MainViewPanel");
		tMainViewPanel.removeAll();
		tMainViewPanel.add(container);
		tMainViewPanel.layout();
	}
	
	public static void changeMainViewToSubjectsAndThreads() {
/*		if (QuadCoreForumWeb.SEARCH_STATE) {
			Registry.register("NoTabExpand", 2L);
		}
		else*/
			Registry.register("NoTabExpand", true);
//		QuadCoreForumWeb.SEARCH_STATE = false;
		ContentPanel tMainViewPanel = ((ContentPanel)Registry.get("MainViewPanel")); 
		tMainViewPanel.removeAll();
//		Registry.register("NoTabExpand", 1L);
		tMainViewPanel.add((TabPanel)Registry.get("maincontentpanel"));
		tMainViewPanel.layout();
	}
}
