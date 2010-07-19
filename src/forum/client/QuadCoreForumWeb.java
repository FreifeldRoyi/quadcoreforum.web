package forum.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class QuadCoreForumWeb implements EntryPoint 
{
	@Override
	public void onModuleLoad() {
		Registry.register("Servlet", GWT.create(ControllerService.class));
		MainPanel mp = new MainPanel();
		Viewport v = new Viewport();  
		v.setLayout(new FitLayout());  
		v.add(mp, new FitData(5)); // FitData controls the margins of the mp
		RootPanel.get("mainPanelID").add(v); 
	}
}
