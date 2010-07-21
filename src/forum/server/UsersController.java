/**
 * 
 */
package forum.server;

import java.util.Collection;
import java.util.HashSet;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.user.Permission;
import forum.shared.ConnectedUserData;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;

/**
 * @author sepetnit
 *
 */
public class UsersController {
	private ForumFacade facade;

	public UsersController(ForumFacade facade) {
		this.facade = facade;
	}
	
	public ConnectedUserData login(long guestID, String username, String password) throws 
	NotRegisteredException, WrongPasswordException, DatabaseRetrievalException {
		try {
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
