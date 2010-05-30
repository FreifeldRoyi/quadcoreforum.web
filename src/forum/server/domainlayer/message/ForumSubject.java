/**
 * Represents a subject (or directory) in the forum 
 */
package forum.server.domainlayer.message;

import java.util.*;

import forum.server.domainlayer.interfaces.UISubject;

public class ForumSubject implements UISubject {
	
	private long subjectID;
	private String name;
	private String description;
	private Collection<Long> subSubjectsIDs;
	private Collection<Long> threadsIDs;
	private long fatherSubjectID;
	

	/**
	 * A full constructor of the forum subject which initializes all its attributes according to the give
	 * parameters
	 * 
	 * This constructor is used while constructing the subject according to the database
	 * 
	 * @param id
	 * 		The id of the subject
	 * @param name
	 * 		The name of the subject
	 * @param description
	 * 		The description of the subject
	 * @param subSubjectsIDs
	 * 		A collection of this subject sub-subject ids
	 * @param threadsIDs
	 * 		A collection of this subjects threads ids
	 * @param isTopLevel
	 * 		Whether this subject is a top level one
	 */
	public ForumSubject(long id, final String name, final String description, final Collection<Long> subSubjectsIDs,
			final Collection<Long> threadsIDs, long fatherSubjectID) {
		this(id, name, description, fatherSubjectID);
		this.subSubjectsIDs.addAll(subSubjectsIDs);
		this.threadsIDs.addAll(threadsIDs);
	}	

	/**
	 * 
	 * The class constructor which is used to construct a new forum subject which doesn't exist in the database
	 * and initializes some of the fields with default values.
	 * 
	 * @param id
	 * 		The unique identification number of this subject
	 * @param name
	 * 		The name of this subject
	 * @param description
	 * 		The description of this subject
	 * @param isTopLevel
	 * 		Whether this subject should be a forum top-level subject
	 */
	public ForumSubject(long id, final String name, final String description, long fatherSubjectID) {
		this.name = name;
		this.description = description;
		this.subjectID = id;
		this.subSubjectsIDs = new Vector<Long>();
		this.threadsIDs = new Vector<Long>();
		this.fatherSubjectID = fatherSubjectID;
	}

	// getters
	
	/**
	 * @see
	 * 		UISubject#getID()
	 */
	public long getID() {
		return this.subjectID;
	}

	/**
	 * @see
	 * 		UISubject#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see
	 * 		UISubject#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @see
	 * 		UISubject#getNumOfSubSubjects()
	 */
	public long getNumOfSubSubjects() {
		return this.subSubjectsIDs.size();
	}

	/**
	 * @see
	 * 		UISubject#getNumOfThreads()
	 */
	public long getNumOfThreads() {
		return this.threadsIDs.size();
	}
	
	/**
	 * 
	 * @return
	 * 		A collection of this subject sub-subjects ids
	 */
	public Collection<Long> getSubSubjects() {
		return this.subSubjectsIDs;
	}

	/**
	 * 
	 * @return
	 * 		A collection of this subject threads ids
	 */
	public Collection<Long> getThreads() {
		return this.threadsIDs;
	}

	public long getFatherID() {
		return this.fatherSubjectID;
	}
	
	/**
	 * 
	 * This method overrides the standard equals method and 
	 * checks whether two subjects are the same one 
	 * 
	 * @see
	 * 		Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (obj != null) && (obj instanceof ForumSubject) && (((ForumSubject)obj).getID() == 
			this.getID());
	}
	
	/**
	 * @see
	 * 		UISubject#toString()
	 */
	public String toString() {
		return this.getID() + "\t" + this.getName() + "\t" + this.getDescription();
	}

	/**
	 * 
	 * @return
	 * 		Whether this subject is a top level one
	 */
	public boolean isTopLevel() {
		return this.fatherSubjectID == -1;
	}

	// methods
	
	/**
	 * 
	 * Adds a new id of a sub-subject of this subject
	 * 
	 * @param subjectID
	 * 		The id of the new sub-subject which should be added to this subject
	 */
	public void addSubSubject(final long subjectID) {
		this.subSubjectsIDs.add(subjectID);
	}

	/**
	 * 
	 * Removes an id of a sub-subject from this subject
	 * 
	 * @param subjectID
	 * 		The id of the sub-subject which should be removed
	 */
	public void deleteSubSubject(final long subjectID) {
		this.subSubjectsIDs.remove(subjectID);
	}

	/**
	 * 
	 * Adds a new id of a thread of this subject
	 * 
	 * @param threadID
	 * 		The id of the new thread which should be added to this subject
	 */
	public void addThread(final long threadID) {
		this.threadsIDs.add(threadID);
	}

	/**
	 * 
	 * Removes an id of a thread from this subject
	 * 
	 * @param threadID
	 * 		The id of the thread which should be removed
	 */
	public void deleteThread(final long threadID) {
		this.threadsIDs.remove(threadID);
	}
}