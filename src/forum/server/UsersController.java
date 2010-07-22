/**
 * 
 */
package forum.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.shared.ConnectedUserData;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

/**
 * @author sepetnit
 *
 */
public class UsersController {

	private Collection<Long> connectedGuests;
	private Map<Long, String> connectedMembers;


	private ForumFacade facade;

	public UsersController(ForumFacade facade) {
		connectedGuests = new HashSet<Long>();
		connectedMembers = new HashMap<Long, String>();
		this.facade = facade;
	}

	public void destroy() {
		for (long guestID : this.connectedGuests)
			this.disconnectClient(guestID);
		for (long userID : connectedMembers.keySet())
			this.disconnectClient(userID);
	}

	public void disconnectClient(long clientID) {
		if (clientID < 0) {
			this.connectedGuests.remove(clientID);
			this.facade.removeGuest(clientID);
		}
		else {
			String tUsername = this.connectedMembers.get(clientID);
			if (tUsername != null)
				try {
					this.facade.logout(tUsername);
				}
			catch (NotConnectedException e) {}
		}
	}


	private ConnectedUserData userToConnectedUserDataConverter(UIUser user) {
		Collection<forum.shared.Permission> permissions = new ArrayList<forum.shared.Permission>();
		for (Permission tCurrentPermission : user.getPermissions())
			permissions.add(forum.shared.Permission.valueOf(tCurrentPermission.toString()));
		return new ConnectedUserData(user.getID(), permissions);
	}

	public ConnectedUserData logout(String username) throws
	forum.shared.exceptions.user.NotConnectedException, 
	forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			facade.logout(username);
			/* register the user as guest again */
			return userToConnectedUserDataConverter(facade.addGuest());
		} 
		catch (NotConnectedException e) {
			throw new forum.shared.exceptions.user.NotConnectedException(e.getUsername());
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public ServerResponse addNewGuest() throws forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			ServerResponse returnObj = new ServerResponse("", true); 
			UIUser tNewGuest = facade.addGuest();
			returnObj.setGuestIDChanged();
			returnObj.setConnectedGuestID(tNewGuest.getID());
			connectedGuests.add(tNewGuest.getID());
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tNewGuest.getID() + "");
			return returnObj;
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public ServerResponse registerToForum(RegisterMessage data) {
		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			facade.registerNewMember(data.getUsername(), data.getPassword(), 
					data.getLastName(), data.getFirstName(), data.getEmail());
			returnObj.setHasExecuted(true);
			returnObj.setResponse("registersuccess\t" + "you successfuly registered the forum");
		}
		catch (MemberAlreadyExistsException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + "The following data already exists: " + e.getMessage());
		} 
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + e.getMessage());
		}
		return returnObj;
	}

	
	public ConnectedUserData login(long guestID, String username, String password) throws 
	NotRegisteredException, WrongPasswordException, DatabaseRetrievalException {
		try {
			/* unregister the user from being a guest */
			this.disconnectClient(guestID);
			UIMember tResponseUser = facade.login(username, password);

			String type = null;
			if (tResponseUser.isAllowed(Permission.SET_MODERATOR))
				type = "ADMIN";
			else
				if (tResponseUser.isAllowed(Permission.DELETE_MESSAGE))
					type = "MODERATOR";
				else
					type = "MEMBER";

			Collection<forum.shared.Permission> tSharedPermissions = new HashSet<forum.shared.Permission>();
			for (Permission p : tResponseUser.getPermissions())
				tSharedPermissions.add(forum.shared.Permission.valueOf(p.toString()));

			ConnectedUserData toReturn = new ConnectedUserData(tResponseUser.getID(), tResponseUser.getUsername(),
					tResponseUser.getLastName(), tResponseUser.getFirstName(), tResponseUser.getEmail(), type,
					tSharedPermissions);

			return toReturn;
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUsername());
		} 
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.WrongPasswordException e) {
			throw new WrongPasswordException();
		}
		catch (forum.server.updatedpersistentlayer.DatabaseRetrievalException e) {
			throw new DatabaseRetrievalException();
		}		
	}
}
