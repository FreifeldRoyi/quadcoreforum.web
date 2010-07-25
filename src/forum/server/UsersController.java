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
import forum.shared.UserModel;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;

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


	private UserModel userToConnectedUserDataConverter(UIUser user) {
		Collection<forum.shared.Permission> permissions = new ArrayList<forum.shared.Permission>();
		for (Permission tCurrentPermission : user.getPermissions())
			permissions.add(forum.shared.Permission.valueOf(tCurrentPermission.toString()));
		return new UserModel(user.getID(), permissions);
	}

	public UserModel logout(String username) throws
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

	public UserModel addNewGuest() throws forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			UIUser tNewGuest = facade.addGuest();
			connectedGuests.add(tNewGuest.getID());			
			Collection<forum.shared.Permission> tSharedPermissions = new HashSet<forum.shared.Permission>();
			for (Permission p : tNewGuest.getPermissions())
				tSharedPermissions.add(forum.shared.Permission.valueOf(p.toString()));

			UserModel toReturn = new UserModel(tNewGuest.getID(), tSharedPermissions);

			return toReturn;

		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public void registerToForum(final String username, final String password, final String lastName, 
			final String firstName, final String email) throws forum.shared.exceptions.user.MemberAlreadyExistsException,
			forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			facade.registerNewMember(username, password, 
					lastName, firstName, email);
		}
		catch (MemberAlreadyExistsException e) {
			throw new forum.shared.exceptions.user.MemberAlreadyExistsException(e.getMessage());
		} 
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	
	public UserModel login(long guestID, String username, String password) throws 
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

			UserModel toReturn = new UserModel(tResponseUser.getID(), tResponseUser.getUsername(),
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
