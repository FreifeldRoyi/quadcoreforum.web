package forum.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class QuadCoreForumWeb implements EntryPoint 
{
	@Override
	public void onModuleLoad() 
	{
		MainPanel mp = new MainPanel();
		RootPanel.get("mainPanelID").add(mp);
	}

}
