package forum.client; 

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.tcpcommunicationlayer.ServerResponse;
import forum.shared.tcpcommunicationlayer.ViewSubjectsMessage;

public class MainPanel extends LayoutContainer
{ 
	/*All private Members*/
	private final ContentPanel miscellaneousPanel = new ContentPanel();
	private final ContentPanel mainContentPanel = new ContentPanel();
	final private ContentPanel settingsPanel = new ContentPanel();  
	final private ContentPanel navigatorPanel = new ContentPanel();  

	private final BorderLayout rootPanelLayout = new BorderLayout();

	
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
		this.mainContentPanel.setHeading("Main Content");
		this.mainContentPanel.setBorders(true);
		this.addToMainContent(new AsyncSubjectsTreeGrid());

		//Main Content Panel
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));
		centerData.setCollapsible(false);
		add(this.mainContentPanel, centerData);
	}

	private void settingsPanelInit() {
		settingsPanel.setHeading("Settings");  
		settingsPanel.setBorders(false);  
		settingsPanel.setCollapsible(true);
		settingsPanel.setLayout(new FillLayout(Orientation.VERTICAL));
		settingsPanel.setSize(300, 200);
		settingsPanel.setBodyStyle("fontSize: 12px; padding: 0px");  
		settingsPanel.collapse();
		this.loadSettingsLinks();
		miscellaneousPanel.add(settingsPanel);
	}

	private void navigationPanelInit() {
		navigatorPanel.setHeading("Navigation");  
		navigatorPanel.setBorders(false);
		navigatorPanel.setBodyStyle("fontSize: 12px; padding: 0px");  
		navigatorPanel.setScrollMode(Scroll.AUTOY);
		navigatorPanel.setCollapsible(false);
		miscellaneousPanel.add(navigatorPanel);
		miscellaneousPanel.layout();
	}
	
	private void miscellaneousPanelInit() {
		this.miscellaneousPanel.setHeading("QuadCoreForum");
		this.miscellaneousPanel.setBorders(true);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300, 100, 300);
		westData.setSplit(false);
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setMargins(new Margins(0,5,0,0));
		add(this.miscellaneousPanel, westData);
		this.settingsPanelInit();
		this.navigationPanelInit();
	}

	
	private void loadSettingsLinks() {
		this.settingsPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {
			private final ViewSubjectsMessage ROOT_SUBJECTS_MESSAGE = new ViewSubjectsMessage(-1);

			@Override
			public void handleEvent(ButtonEvent be) {

				service.getSubjects(ROOT_SUBJECTS_MESSAGE, 
						new AsyncCallback<ServerResponse>() {

					@Override
					public void onSuccess(ServerResponse result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
			}
		}));
	}
	
	
	/**
	 * loads all links appearing on the navigation panel
	 */
	private void loadNavigatorLinks() {
		this.miscellaneousPanel.add(createButton("Refresh", 
				new Listener<ButtonEvent>() {
			private final ViewSubjectsMessage ROOT_SUBJECTS_MESSAGE = new ViewSubjectsMessage(-1);

			@Override
			public void handleEvent(ButtonEvent be) {

				service.getSubjects(ROOT_SUBJECTS_MESSAGE, 
						new AsyncCallback<ServerResponse>() {

					@Override
					public void onSuccess(ServerResponse result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
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

	private void addToMainContent(LayoutContainer c)
	{
		this.mainContentPanel.removeAll();
		this.mainContentPanel.add(c);
		this.mainContentPanel.layout();
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
