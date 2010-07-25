package forum.shared;

import java.io.Serializable;
import java.util.Collection;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class SubjectModel extends BaseTreeModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3965074362252069771L;

	public SubjectModel() {
		setID(-1);
	}
	
	public SubjectModel(long id, String name, String description, long subjectsNum, long messagesNum) {
		setID(id);
		setName(name);
		setDescription(description);
		setSubjectsNumber(subjectsNum);
		setMessagesNumber(messagesNum);
	}

	public void setChildren(Collection<BaseTreeModel> children) {
		for (BaseTreeModel child : children)
			this.add(child);
		
	}

	public long getID() {
		return get("id");
	}

	public String getName() {
		return get("name");
	}

	public String getDescription() {
		return get("description");
	}

	public String getSubjectsNumber() {
		return get("subjectsNumber") + "";
	}

	public String getMessagesNumber() {
		return get("messagesNumber") + "";
	}

	/**
	 * @param id the id to set
	 */
	public void setID(long id) {
		set("id", id);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		set("name", name);
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		set("description", description);
	}

	/**
	 * @param subjectsNumber the subjectsNumber to set
	 */
	public void setSubjectsNumber(long subjectsNumber) {
		set("subjectsNumber", subjectsNumber);
	}

	/**
	 * @param messagesNumber the messagesNumber to set
	 */
	public void setMessagesNumber(long messagesNumber) {
		set("messagesNumber", messagesNumber);
	}

	public String toString() {
		return getName();
	}
}