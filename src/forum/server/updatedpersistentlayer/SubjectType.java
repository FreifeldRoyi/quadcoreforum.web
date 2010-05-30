
package forum.server.updatedpersistentlayer;

import java.util.Set;

public class SubjectType
    
{

    protected long subjectID;
    protected long fatherID;
    protected Set<Long> subSubjectsIDs; 
    protected Set<Long> threadsIDs;
    protected Long lastAddedMessageID;
    protected String name;
    protected String description;
  
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the fatherID property.
     * 
     * @param value
     *     allowed object is
     *     {@link long }
     *     
     */
    public void setFatherID(long fatherID) {
        this.fatherID = fatherID;
    }
    
    
    /**
     * Gets the value of the fatherID property.
     * 
     * @return
     *     possible object is
     *     {@link long }
     *     
     */
    public long getFatherID() {
        return this.fatherID;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    
    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the subjectID property.
     * 
     */
    public long getSubjectID() {
        return subjectID;
    }

    /**
     * Sets the value of the subjectID property.
     * 
     */
    public void setSubjectID(long value) {
        this.subjectID = value;
    }

    /**
     * Gets the value of the subSubjectsIDs property.
     */ 
    public Set<Long> getSubSubjectsIDs() {
        return this.subSubjectsIDs;
    }

    /**
     * Sets the value of the subSubjectsIDs property.
     */ 
    public void setSubSubjectsIDs(Set<Long> subSubjectsIDs) {
        this.subSubjectsIDs = subSubjectsIDs;
    }    
    
    /**
     * Gets the value of the threadsIDs property.
     * 
     */
    public Set<Long> getThreadsIDs() {
        return this.threadsIDs;
    }

    /**
     * Sets the value of the subSubjectsIDs property.
     */ 
    public void setThreadsIDs(Set<Long> threadsIDs) {
        this.threadsIDs = threadsIDs;
    }    
    
    /**
     * Gets the value of the lastAddedMessageID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastAddedMessageID() {
        return lastAddedMessageID;
    }

    /**
     * Sets the value of the lastAddedMessageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastAddedMessageID(Long value) {
        this.lastAddedMessageID = value;
    }
}
