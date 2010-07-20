package forum.shared;

import java.io.Serializable;
import java.util.Collection;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class SubjectModel extends BaseTreeModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3965074362252069771L;

	private long id;
	private String name;
	private String description;
	private long subjectsNumber;
	private long messagesNumber;

	public SubjectModel() {}
	
	public SubjectModel(long id, String name, String description, long subjectsNum, long messagesNum) {
		set("id", id);
		set("name", name);
		this.description = description;
		this.subjectsNumber = subjectsNum;
		this.messagesNumber = messagesNum;
	}

	public void setChildren(Collection<BaseTreeModel> children) {
		for (BaseTreeModel child : children)
			this.add(child);
	}

	public long getID() {
		return (Long) get("id");
	}

	public String getName() {
		return get("name");
	}

	public String getDescription() {
		return description;
	}

	public String getSubjectsNumber() {
		return subjectsNumber + "";
	}

	public String getMessagesNumber() {
		return messagesNumber + "";
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
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
		this.description = description;
	}

	/**
	 * @param subjectsNumber the subjectsNumber to set
	 */
	public void setSubjectsNumber(long subjectsNumber) {
		this.subjectsNumber = subjectsNumber;
	}

	/**
	 * @param messagesNumber the messagesNumber to set
	 */
	public void setMessagesNumber(long messagesNumber) {
		this.messagesNumber = messagesNumber;
	}

	public String toString() {
		return getName();
	}
}