package forum.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.extjs.gxt.ui.client.data.ModelData;

public class SearchHitModel implements ModelData, Serializable 
{
	private static final long serialVersionUID = -702632342109376469L;

	private Map<String, Object> properties;
	
	public SearchHitModel()
	{
		this.properties = new HashMap<String, Object>();
	}
	
	public SearchHitModel(long msgID, Stack<MessageModel> msgPath,
			ThreadModel contThread, Stack<SubjectModel> subjPath, String title, 
			String authorUserName, String content, double score)
	{
		this();
		this.setMessageID(msgID);
		this.setMessagePath(msgPath);
		this.setContainingThread(contThread);
		this.setSubjectPath(subjPath);
		this.setTitle(title);
		this.setAuthorUserName(authorUserName);
		this.setContent(content);
		this.setScore(score);
	}

	public Collection<MessageModel> getMessagePath()
	{
		return this.get("messagePath");
	}
	
	public ThreadModel getContainingThread()
	{
		return this.get("containingThread");
	}
	
	public Stack<SubjectModel> getSubjectPath()
	{
		return this.get("subjectPath");
	}
	
	/**
	 * @return the title of the message
	 */
	public String getTitle()
	{
		return this.get("title");
	}
	
	/**
	 * @return the user name of the author
	 */
	public String getAuthorUserName()
	{
		return this.get("authorUserName");
	}
	
	/**
	 * @return the content
	 */
	public String getContent()
	{
		return this.get("content");
	}
	
	/**
	 * @return the message ID
	 */
	public long getMessageID()
	{
		return this.get("msgID");
	}
	
	/**
	 * @return the message's score
	 */
	public double getScore()
	{
		return this.get("score");
	}
	
	/**
	 * sets the title
	 * @param title - the title
	 */
	public void setTitle(String title)
	{
		this.set("title", title);
	}
	
	/**
	 * @param userName - the user name of the author to set
	 */
	public void setAuthorUserName(String userName)
	{
		this.set("authorUserName", userName);
	}
	
	/**
	 * @param content - the content of the message
	 */
	public void setContent(String content)
	{
		this.set("content", content);
	}
	
	public void setSubjectPath(Stack<SubjectModel> subjPath) 
	{
		this.set("subjectPath", subjPath);
	}

	public void setContainingThread(ThreadModel contThread) 
	{
		this.set("containingThread", contThread);		
	}

	public void setMessagePath(Stack<MessageModel> msgPath) 
	{
		this.set("messagePath", msgPath);		
	}
	
	/**
	 * @param msgID - the message ID to set
	 */
	public void setMessageID(long msgID)
	{
		this.set("msgID", msgID);
	}
	
	/**
	 * @param score - the score of the message
	 */
	public void setScore(double score)
	{
		this.set("score", score);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(String property) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		return (X) this.properties.get(property);
	}

	@Override
	public Map<String, Object> getProperties() 
	{
		return this.properties;
	}

	@Override
	public Collection<String> getPropertyNames() 
	{
		return this.properties.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X remove(String property) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		return (X) this.properties.remove(property);
	}

	@Override
	public <X> X set(String property, X value) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		this.properties.put(property, value);
		return value;
	}
}
