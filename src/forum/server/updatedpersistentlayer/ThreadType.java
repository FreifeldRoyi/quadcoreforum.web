
package forum.server.updatedpersistentlayer;

public class ThreadType {

    protected long threadID;
    protected String topic;
    protected long fatherSubjectID;
    protected long startMessageID;
    protected long lastMessageID;
    protected long numOfViews;
    protected long numOfResponses;

    /**
     * Gets the value of the threadID property.
     * 
     */
    public long getThreadID() {
        return threadID;
    }

    /**
     * Sets the value of the threadID property.
     * 
     */
    public void setThreadID(long value) {
        this.threadID = value;
    }

    /**
     * Gets the value of the fatherSubjectID property.
     * 
     */
    public long getFatherSubjectID() {
        return fatherSubjectID;
    }

    /**
     * Sets the value of the fatherSubjectID property.
     * 
     */
    public void setFatherSubjectID(long fatherSubjectID) {
        this.fatherSubjectID = fatherSubjectID;
    }

    
    /**
     * Gets the value of the topic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the value of the topic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopic(String value) {
        this.topic = value;
    }

    /**
     * Gets the value of the startMessageID property.
     * 
     */
    public long getStartMessageID() {
        return startMessageID;
    }

    /**
     * Sets the value of the startMessageID property.
     * 
     */
    public void setStartMessageID(long value) {
        this.startMessageID = value;
    }

    /**
     * Gets the value of the lastMessageID property.
     * 
     */
    public long getLastMessageID() {
        return lastMessageID;
    }

    /**
     * Sets the value of the lastMessageID property.
     * 
     */
    public void setLastMessageID(long value) {
        this.lastMessageID = value;
    }

    /**
     * Gets the value of the numOfViews property.
     * 
     */
    public long getNumOfViews() {
        return numOfViews;
    }

    /**
     * Sets the value of the numOfViews property.
     * 
     */
    public void setNumOfViews(long value) {
        this.numOfViews = value;
    }

    /**
     * Gets the value of the numOfResponses property.
     * 
     */
    public long getNumOfResponses() {
        return numOfResponses;
    }

    /**
     * Sets the value of the numOfResponses property.
     * 
     */
    public void setNumOfResponses(long value) {
        this.numOfResponses = value;
    }

}
