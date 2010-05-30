package forum.server.updatedpersistentlayer.pipe.user;

import java.util.*;

import org.hibernate.*;
import org.hibernate.classic.Session;

import forum.server.updatedpersistentlayer.*;
import forum.server.domainlayer.message.ForumSubject;
import forum.server.domainlayer.user.*;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum members
 */

/**
 * @author Sepetnitsky Vitali
 */
public class UsersPersistenceHandler {

	/*	private void deleteMember(Session session, MemberType toDelete) throws DatabaseUpdateException {
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(toDelete);
			tx.commit();
		} 
		catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// add logging
				}
				throw new DatabaseUpdateException();
			}
		}
	}
	 */

	private Session getSessionAndBeginTransaction(SessionFactory ssFactory) throws DatabaseRetrievalException {
		try {
			Session toReturn = ssFactory.getCurrentSession();
			toReturn.beginTransaction();
			return toReturn;
		}
		catch (RuntimeException e) {
			throw new DatabaseRetrievalException();
		}
	}

	private void commitTransaction(Session session) throws DatabaseUpdateException {
		try {
			session.getTransaction().commit();
		}
		catch (RuntimeException e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				try {
					// Second try catch as the roll-back could fail as well
					session.getTransaction().rollback();
				}
				catch (HibernateException e1) {
					// add logging
				}
			}
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMemberID()
	 */
	@SuppressWarnings("unchecked")
	public long getFirstFreeMemberID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select max(userID) from MemberType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();
		if (tResult.get(0) != null)
			toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the forum members should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getAllMembers()
	 */
	@SuppressWarnings("unchecked")
	public Collection<ForumMember> getAllMembers(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType where UserID != -1";
		List tResult = session.createQuery(query).list();
		for (MemberType tCurrentMemberType : (List<MemberType>)tResult) 
			toReturn.add(PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType));
		try {
			this.commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	private MemberType getMemberTypeByID(final Session session, final long userID) throws 
	DatabaseRetrievalException {
		try {
			return (MemberType)session.get(MemberType.class, userID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required user should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumMember getMemberByID(SessionFactory ssFactory, long userID) throws NotRegisteredException, 
	DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			MemberType tMemberType = this.getMemberTypeByID(session, userID);
			if (tMemberType == null) {
				this.commitTransaction(session);
				throw new NotRegisteredException(userID);
			}
			else {
				ForumMember toReturn = PersistentToDomainConverter.convertMemberTypeToForumMember(tMemberType);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	@SuppressWarnings("unchecked")
	private ForumMember getMemberByField(final SessionFactory ssFactory, final String field, 
			final String value) throws NotRegisteredException, DatabaseRetrievalException {
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType where " + field  + " like '" + value + "'";
		List tResult = session.createQuery(query).list();
		if (tResult.size() != 1)
			throw new NotRegisteredException(field);
		else {
			ForumMember toReturn = 
				PersistentToDomainConverter.convertMemberTypeToForumMember((MemberType)tResult.get(0));
			try {
				this.commitTransaction(session);
			}
			catch (DatabaseUpdateException e) {
				throw new DatabaseRetrievalException();
			}
			return toReturn;
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	public ForumMember getMemberByUsername(final SessionFactory ssFactory, final String username) throws 
	NotRegisteredException, DatabaseRetrievalException {
		return this.getMemberByField(ssFactory, "Username", username);
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * @throws DatabaseRetrievalException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByEmail(String)
	 */
	public ForumMember getMemberByEmail(final SessionFactory ssFactory, final String email) throws 
	NotRegisteredException, DatabaseRetrievalException {
		return this.getMemberByField(ssFactory, "Email", email);
	}

	/**
	 * @param data
	 * 		The forum data to which the new member should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewMember(long, String, String, String, String, String, Collection)
	 */
	public void addNewMember(final SessionFactory ssFactory, final long id, final String username, final String password,
			final String lastName, final String firstName, final String email, 
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, firstName, email, permissions);
			session.save(tNewMemberType);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * Parses and converts a collection of permissions of type {@link Permission} to a collection of strings
	 * 
	 * @param permissions
	 * 		The collection of permissions which should be parsed and converted to collection of strings
	 * 
	 * @return
	 * 		The created collection of {@link String} objects
	 */
	private Collection<String> parsePermissionsToString(Collection<Permission> permissions) {
		Collection<String> toReturn = new HashSet<String>();
		for (Permission tCurrentPermission : permissions)
			toReturn.add(tCurrentPermission.toString());
		return toReturn;
	}

	/**
	 * @param data
	 * 		The forum data where the user details should be updated
	 * 
	 * @see
	 * 		PersistenceDataHandler#updateUser(long, Collection)
	 */
	public void updateUser(final SessionFactory ssFactory, final long userID, final Collection<Permission> permissions)
	throws NotRegisteredException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tMemberToUpdate = this.getMemberTypeByID(session, userID);
			tMemberToUpdate.getPermissions().clear();
			tMemberToUpdate.getPermissions().addAll(this.parsePermissionsToString(permissions));
			session.update(tMemberToUpdate);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}