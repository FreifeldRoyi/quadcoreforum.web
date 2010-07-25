/**
 * 
 */
package forum.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * @author Freifeld Royi
 *
 */
public class SearchPanel extends LayoutContainer
{
	final TextField<String> txtSearch = new TextField<String>();

	final RadioGroup radGrpSearch = new RadioGroup();
	final Radio radSrchByAuth = new Radio();
	final Radio radSrchByCont = new Radio();
	
	final RadioGroup radGrpResults = new RadioGroup();
	final Radio rad5 = new Radio();
	final Radio rad10 = new Radio();
	final Radio rad15 = new Radio();
	final Radio rad20 = new Radio();
	
	ControllerServiceAsync controllerService = GWT.create(ControllerService.class);
	
	protected void onRender(Element target, int index)
	{
		super.onRender(target, index);
		
		this.setLayout(new FitLayout());
		
		//containing panel
		FormPanel panel = new FormPanel();
		panel.setBorders(false);  
	    panel.setBodyBorder(false);  
	    panel.setLabelWidth(55);  
	    panel.setPadding(5);  
		panel.setHeading("Search");
		
		//Search field
		this.txtSearch.setFieldLabel("Search");
		panel.add(this.txtSearch, new FormData("90%"));
		
		//Search Button
		Button btnSearch = new Button("Search", new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				System.out.println("BUTTON SEARCH;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
				openSearchHitsTable();
			}
		});
		
		panel.add(btnSearch, new FormData("10%"));
		
		//Search Type
		final FieldSet fldStSrchType = new FieldSet();
		fldStSrchType.setHeading("Select search type");
		
		this.radSrchByAuth.setBoxLabel("Search by Author");
		this.radSrchByCont.setBoxLabel("Search by Content");
		
		this.radGrpSearch.add(this.radSrchByAuth);
		this.radGrpSearch.add(this.radSrchByCont);
		
		fldStSrchType.add(this.radGrpSearch);
		
		panel.add(fldStSrchType);
		
		//Search results number per page
		final FieldSet fldStNumResult = new FieldSet();
		fldStNumResult.setHeading("Number of results per page");
		
		this.rad5.setBoxLabel("5");
		this.rad10.setBoxLabel("10");
		this.rad15.setBoxLabel("15");
		this.rad20.setBoxLabel("20");
		
		this.radGrpResults.add(this.rad5);
		this.radGrpResults.add(this.rad10);
		this.radGrpResults.add(this.rad15);
		this.radGrpResults.add(this.rad20);
					
		fldStNumResult.add(this.radGrpResults);
		
		panel.add(fldStNumResult);
		
		this.add(panel);
	}
	
	private void openSearchHitsTable()
	{
		String toSearch = this.txtSearch.getValue();
		System.out.println("preparing for search. search phrase entered is: " + toSearch);
		int resultsPerPage = getResultsPerPagesValue();
		AsyncSearchHitsTableGrid table = 
			new AsyncSearchHitsTableGrid(resultsPerPage, this.getSearchType(), toSearch);
		
		this.add(table);
		this.layout();
	}
	
	private int getResultsPerPagesValue()
	{
		int toReturn = -1;
		Radio selected = this.radGrpResults.getValue();
		if (selected == this.rad5)
			toReturn = 5;
		else if (selected == this.rad10)
			toReturn = 10;
		else if (selected == this.rad15)
			toReturn = 15;
		else
			toReturn = 20;
		
		return toReturn;
	}
	
	private String getSearchType()
	{
		String toReturn = "";
		Radio selected = this.radGrpSearch.getValue();
		
		if (selected == this.radSrchByAuth)
			toReturn = "author";
		else
			toReturn = "content";
		
		return toReturn;
	}
}
