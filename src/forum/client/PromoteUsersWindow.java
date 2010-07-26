package forum.client;

import java.util.List;
import java.util.Vector;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.UserModel;
import forum.shared.UserModel.UserType;

public class PromoteUsersWindow extends LayoutContainer
{
	private ListStore<UserModel> store;
	
	private List<ColumnConfig> configs;
	private Grid<UserModel> grid;
	
	final private ContentPanel panel = new ContentPanel();
	private Button btnCancel;
	private Button btnPromote;
	
	public PromoteUsersWindow()
	{
		this.store = new ListStore<UserModel>();
		this.configs = new Vector<ColumnConfig>();
	}
	
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		this.setLayout(new FitLayout());
		
		this.addColumnConfigs();
		this.initContainerPanel();		
		this.initGrid();
		this.loadUsers();
		this.initButtons();

		this.add(this.panel);
	}
	
	private void initContainerPanel()
	{
		this.panel.setBodyBorder(false);
		this.panel.setHeading("All users");
		this.panel.setLayout(new FitLayout());
	}
	
	private void addColumnConfigs()
	{
		this.addToConfigs("username","User Name", 100);
		this.addToConfigs("firstName", "First Name", 100);
		this.addToConfigs("lastName", "Last Name", 100);
		this.addToConfigs("type", "User Type", 100);
	}
	
	private void initButtons()
	{
		this.btnPromote = new Button("Promote", new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				UserModel um = grid.getSelectionModel().getSelectedItem();
				if (um.getType() == UserType.GUEST)
					Info.display("Error", "Canno't promote a guest user");
				else if (um.getType() == UserType.ADMIN)
					Info.display("Error", "Canno't change admin's status");
				else if (um.getType() == UserType.MEMBER)
				{
					QuadCoreForumWeb.SERVICE.PromoteMemberToModerator(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(), 
							um.getUsername(), new AsyncCallback<Void>() 
					{
							@Override
							public void onFailure(Throwable caught) 
							{
								onFailureDo(caught);
							}

							@Override
							public void onSuccess(Void result) 
							{
								onSuccessDo(result, grid.getSelectionModel().getSelectedItem(), "MODERATOR");
							}
					});
				}
				else if (um.getType() == UserType.MODERATOR)
				{
					QuadCoreForumWeb.SERVICE.DemoteModeratorToMember(QuadCoreForumWeb.CONNECTED_USER_DATA.getID(),
							um.getUsername(), new AsyncCallback<Void>() 
					{
						@Override
						public void onFailure(Throwable caught) 
						{
							onFailureDo(caught);
						}

						@Override
						public void onSuccess(Void result) 
						{
							onSuccessDo(result, grid.getSelectionModel().getSelectedItem(), "MEMBER");
						}
					});
				}
			}
		});
		
		this.btnPromote.setEnabled(false);
		
		this.btnCancel = new Button("Cancel", new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				MainPanel.changeMainViewToSubjectsAndThreads();
			}
		});
		
		this.panel.addButton(btnPromote);
		this.panel.addButton(btnCancel);
	}
	
	private void onFailureDo(Throwable caught)
	{
		Info.display("Error", caught.getMessage());
	}
	
	private void onSuccessDo(Void result, UserModel selected, String type)
	{
		selected.setUserType(type);
		Info.display("Info", selected.getUsername() + " is now a " + selected.getType());
		this.store.update(selected);
		this.store.commitChanges();
		this.updatePromoteButton();
	}
	
	private void updatePromoteButton()
	{
		UserModel um = this.grid.getSelectionModel().getSelectedItem();
		if (um.getType() == UserType.GUEST)
		{
			btnPromote.setEnabled(false);
			btnPromote.setText("Unavailable");
		}
		else if (um.getType() == UserType.MEMBER)
		{
			btnPromote.setEnabled(true);
			btnPromote.setText("Promote");
		}
		else if (um.getType() == UserType.MODERATOR)
		{
			btnPromote.setEnabled(true);
			btnPromote.setText("Demote");
		}
		else if (um.getType() == UserType.ADMIN)
		{
			btnPromote.setEnabled(false);
			btnPromote.setText("Uber");
		}
		else
		{
			Info.display("Error", "Type is incorrect");
		}
	}
	
	private void initGrid()
	{
		ColumnModel cm = new ColumnModel(this.configs);
		
		this.grid = new Grid<UserModel>(this.store, cm);
		this.grid.setStyleAttribute("borderTop", "none");
		this.grid.setAutoExpandColumn("username");
		this.setBorders(true);
		this.grid.setStripeRows(true);
		
		GridView view = new GridView();
		view.setEmptyText("No users at this time");
		this.grid.setView(view);
		this.grid.getView().setShowDirtyCells(false);
		
		this.grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<UserModel>() 
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<UserModel> se) 
			{
				updatePromoteButton();
			}
		});
		
		this.panel.add(this.grid);
	}
	
	private void addToConfigs(String value, String givenName, int width)
	{
		this.configs.add(new ColumnConfig(value,givenName,width));
	}
	
	public void loadUsers()
	{
		System.out.println("loading all users");
		QuadCoreForumWeb.SERVICE.getUsers(new AsyncCallback<List<UserModel>>() 
		{
				@Override
				public void onFailure(Throwable caught) 
				{
					onFailureDo(caught);						
				}

				@Override
				public void onSuccess(List<UserModel> result) 
				{
					Info.display("Info", "Fetching of users was done");
					store.add(result);
					store.commitChanges();
				}
		});
	}
	
}
