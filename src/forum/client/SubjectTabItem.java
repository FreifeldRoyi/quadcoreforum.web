/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import forum.shared.SubjectModel;

/**
 * @author sepetnit
 *
 */
public class SubjectTabItem extends TabItem {

	private AsyncThreadsTableGrid threadsTable;

	private ContentPanel threadsPanel;

	private SubjectModel subject;

	private ToolBar toolbar;
	public SubjectTabItem(SubjectModel subject) {
		super();
		this.subject = subject;
		this.setItemId(subject == null? "default" : subject.getID() + "");

		this.setClosable(true);

		BorderLayoutData tSouthData = new BorderLayoutData(LayoutRegion.CENTER, 0, 0, Short.MAX_VALUE);
		tSouthData.setCollapsible(false);
		tSouthData.setSplit(true);
		tSouthData.setMargins(new Margins(0));
		tSouthData.setCollapsible(false);

		BorderLayoutData tNorthData = new BorderLayoutData(LayoutRegion.NORTH, 200, 0, Short.MAX_VALUE);
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


		threadsTable = (subject == null)? new AsyncThreadsTableGrid(-1) :
			new AsyncThreadsTableGrid(subject.getID());
		threadsPanel.add(threadsTable);

		this.add(threadsPanel, tNorthData);


		ContentPanel tMessagesPanel = new ContentPanel();

		tMessagesPanel.setHeading("Messages");

		this.add(tMessagesPanel, tSouthData);

		this.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (SubjectTabItem.this.subject != null)
					loadThreads();
			}
		});


		initializeToolbar();
	}

	private void initializeToolbar() {
		toolbar = new ToolBar();  
		toolbar.setBorders(true); 
		Button tOpenNewThread = new Button("Open new"); 
		tOpenNewThread.setWidth(115);
		Button tDeleteThread = new Button("Delete"); 
		tDeleteThread.setWidth(115);

		Button tModifyThread = new Button("Modify");
		tModifyThread.setWidth(115);

		toolbar.add(tOpenNewThread);
		toolbar.add(tModifyThread);
		toolbar.add(tDeleteThread);

		toolbar.setAlignment(HorizontalAlignment.CENTER);
		threadsPanel.setTopComponent(toolbar);  

		this.changeToolBarVisible(false);
	}

	public void changeToolBarVisible(boolean value) {
		this.toolbar.setVisible(value);
	}
	
	public void loadThreads() {
		this.threadsTable.load();
	}

	public AsyncThreadsTableGrid getThreadsTable() {
		return this.threadsTable;
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
