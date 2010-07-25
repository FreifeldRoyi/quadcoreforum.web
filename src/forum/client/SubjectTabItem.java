/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

import forum.shared.SubjectModel;
import forum.shared.UserModel.UserType;

/**
 * @author sepetnit
 *
 */
public class SubjectTabItem extends TabItem {

	private AsyncThreadsTableGrid threadsTable;


	private SubjectModel subject;


	private AsyncMessagesTreeGrid messagesTree;


	private ContentPanel threadsPanel;

	public SubjectTabItem(SubjectModel subject) {
		super();
		this.subject = subject;
		this.setItemId(subject == null? "default" : subject.getID() + "");

		if (subject == null)
			Registry.register("defaulttab", this);
		
		
		this.setClosable(subject != null);

		BorderLayoutData tSouthData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);
		tSouthData.setCollapsible(false);
		tSouthData.setSplit(true);
		tSouthData.setMargins(new Margins(0));
		tSouthData.setCollapsible(false);

		BorderLayoutData tNorthData = new BorderLayoutData(LayoutRegion.NORTH, 200, 0, Short.MAX_VALUE);
		tNorthData.setMargins(new Margins(0, 0, 5, 0));


		tNorthData.setCollapsible(true);
		tNorthData.setFloatable(true);
		tNorthData.setSplit(true);
		
		tNorthData.setMargins(new Margins(0));
		tNorthData.setCollapsible(true);

		tNorthData.setCollapsible(true);
		this.setLayout(new BorderLayout());

		threadsPanel = new ContentPanel();
		this.updateTabTitle();

		threadsPanel.setLayout(new FitLayout());


		threadsTable = (subject == null)? new AsyncThreadsTableGrid(new SubjectModel()) :
			new AsyncThreadsTableGrid(subject);
		threadsPanel.add(threadsTable);

		this.add(threadsPanel, tNorthData);

		messagesTree = new AsyncMessagesTreeGrid(threadsTable);

		System.out.println("ggggggggggggggggggggggggg " + messagesTree != null);
		threadsTable.setMessagesTree(messagesTree);
		System.out.println("ggggggggggggggggggggggggg " + messagesTree != null);

		this.add(messagesTree, tSouthData);

		this.addListener(Events.Close, new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(BaseEvent be) {
				
				TabPanel tMainTabbedPanel = (TabPanel)Registry.get("maincontentpanel");
				if (tMainTabbedPanel.getItems().size() == 0) {
					tMainTabbedPanel.add((TabItem) Registry.get("defaulttab"));
					TreePanel<SubjectModel> subjectsTree = (TreePanel<SubjectModel>)Registry.get("SubjectsTree");
					if (subjectsTree != null)
						subjectsTree.getSelectionModel().deselectAll();
				}
				
			}
		});
		
		this.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {

					System.out.println("Tab selected................................................................................");
					if (SubjectTabItem.this.subject != null) {
/*
						System.out.println("'''''''''''''''''''''''''''''''''' " + Registry.get("NoTabExpand"));
						if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") > 0)) {
							System.out.println("enters and exits");
							Registry.register("NoTabExpand", ((Long)Registry.get("NoTabExpand") - 1));
							return;
						}
						else if ((Registry.get("NoTabExpand") != null) && ((Long)Registry.get("NoTabExpand") == 0)) {
							Registry.register("NoTabExpand", null);
							return;
						}
						*/
						threadsTable.load(); // changing
						
						
					}
			}
		});
	}

	public void changeToolBarVisible() {
		this.threadsTable.setToolBarVisible(QuadCoreForumWeb.CONNECTED_USER_DATA != null &&
				QuadCoreForumWeb.CONNECTED_USER_DATA.getType() != UserType.GUEST);

		this.messagesTree.setToolBarVisible(QuadCoreForumWeb.CONNECTED_USER_DATA != null &&
				QuadCoreForumWeb.CONNECTED_USER_DATA.getType() != UserType.GUEST);


		if (this.getItemId().equals("default")) {
			this.messagesTree.setButtonsEnableStatus(false);
		}
		else {
			this.messagesTree.setButtonsEnableStatus(QuadCoreForumWeb.CONNECTED_USER_DATA != null &&
					QuadCoreForumWeb.CONNECTED_USER_DATA.getType() != UserType.GUEST);
		}

		threadsPanel.setSize(threadsPanel.getWidth(), threadsPanel.getHeight() + 1);
		threadsPanel.setSize(threadsPanel.getWidth(), threadsPanel.getHeight() - 1);

		messagesTree.setSize(messagesTree.getWidth(), messagesTree.getHeight() + 1);
		messagesTree.setSize(messagesTree.getWidth(), messagesTree.getHeight() - 1);


	}

//	public void loadThreads() {
	//	this.threadsTable.load();
	//}

	public AsyncThreadsTableGrid getThreadsTable() {
		return this.threadsTable;
	}

	public SubjectModel getSubject() {
		return this.subject;
	}

	public void updateTabTitle() {
		threadsPanel.setHeading(this.subject == null? "Threads" :
			("Threads of subject " + subject.getName()));
		if (subject != null) {
			String tTitle = subject.getName();
			if (tTitle.length() > 7)
				tTitle = tTitle.substring(0, 8) + " ...";
			this.setText(tTitle);
		}
		else
			this.setText("Default");
	}
}
