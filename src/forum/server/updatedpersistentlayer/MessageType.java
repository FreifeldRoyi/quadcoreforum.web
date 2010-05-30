


package forum.server.updatedpersistentlayer;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

public class MessageType {

    protected long messageID;
    protected long authorID;
   
    protected String title;
  
    protected String content;
   
    protected Set<Long> repliesIDs;
    protected GregorianCalendar postTime;
    protected long fatherID; 

    /**
     * Gets the value of the messageID property.
     * 
     */
    public long getMessageID() {
        return messageID;
    }

    /**
     * Sets the value of the messageID property.
     * 
     */
    public void setMessageID(long value) {
        this.messageID = value;
    }
    /**
     * Gets the value of the fatherID property.
     * 
     */
    public long getFatherID() {
        return fatherID;
    }

    /**
     * Sets the value of the fatherID property.
     * 
     */
    public void setFatherID(long value) {
        this.fatherID = value;
    }

    /**
     * Gets the value of the author property.
     * 
     */
    public long getAuthorID() {
        return authorID;
    }

    /**
     * Sets the value of the author property.
     * 
     */
    public void setAuthorID(long value) {
        this.authorID = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the postTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public GregorianCalendar getPostTime() {
        return postTime;
    }

    /**
     * Sets the value of the postTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPostTime(GregorianCalendar value) {
        this.postTime = value;
    }

    /**
     * Gets the value of the repliesIDs property.
     * 
     */
    public Set<Long> getRepliesIDs() {
        return this.repliesIDs;
    }

    /**
     * Gets the value of the repliesIDs property.
     * 
     */
    public void setRepliesIDs(Set<Long> repliesIDs) {
        this.repliesIDs = repliesIDs;
    }

}
