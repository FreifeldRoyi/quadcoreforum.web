/**
 * 
 */
package forum.shared;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * @author sepetnit
 *
 */
public class MessageModel extends BaseTreeModel implements Serializable {

	private static final long serialVersionUID = -3784056061324370307L;


	public MessageModel() { }

	public MessageModel(long id, long authorID, String authorUsername,
			String title, String content, Date date) {
		this.setId(id);
		this.setAuthorID(authorID);
		this.setTitle(title);
		this.setAuthorUsername(authorUsername);
		this.setContent(content);
		this.setDate(date);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.get("date");
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.set("date", date);
	}

	/**
	 * @return the id
	 */
	public long getID() {
		return this.get("id");
	}


	/**
	 * @return the authorID
	 */
	public long getAuthorID() {
		return this.get("authorID");
	}


	/**
	 * @return the authorUsername
	 */
	public String getAuthorUsername() {
		return this.get("authorUsername");
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.get("title");
	}



	/**
	 * @return the content
	 */
	public String getContent() {
		return this.get("content");
	}



	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		set("id", id);
	}

	/**
	 * @param authorID the authorID to set
	 */
	public void setAuthorID(long authorID) {
		set("authorID", authorID);
	}

	/**
	 * @param authorUsername the authorUsername to set
	 */
	public void setAuthorUsername(String authorUsername) {
		set("authorUsername", authorUsername);
		this.updateDisplay();
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		set("title", title);
		this.updateDisplay();
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		set("content", content);
		this.updateDisplay();
	}

	private void updateDisplay() {
		this.set("display", "<b><a style=\"color: #385F95; text-decoration: none;\" >" 
				+ this.getTitle() + "&nbsp</a></b>--&nbsp<a style=\"color: #385F95; " +
				"text-decoration: none;\"> "+
				"By " + this.getAuthorUsername() + "</a>");
		if (getContent() != null) {
			String tRenderedContent = getContent().replace('\n' + "", "<br>");
			tRenderedContent = tRenderedContent.replace(' ' + "", "&nbsp;");
			this.set("SelectedContent", tRenderedContent); 
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
		&& 
		other instanceof MessageModel
		&& 
		((MessageModel)other).getID() == getID();
	}
}
