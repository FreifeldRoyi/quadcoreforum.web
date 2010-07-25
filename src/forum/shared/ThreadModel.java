/**
 * 
 */
package forum.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;
/**
 * @author sepetnit
 *
 */
public class ThreadModel implements ModelData, Serializable {
	private static final long serialVersionUID = 3529080073742060764L;

	private Map<String, Object> properties;
	
	public ThreadModel() {
		properties = new HashMap<String, Object>();
	}
	
	public ThreadModel(long id, String topic, long responsesNumber, long viewsNumber) {
		this();
		this.setId(id);
		this.setTopic(topic);
		this.setResponsesNumber(responsesNumber);
		this.setViewsNumber(viewsNumber);
	}
	
	/**
	 * @return the id
	 */
	public long getID() {
		return this.get("id");
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.set("id", id);
	}
	/**
	 * @return the topic
	 */
	public String getTopic() {
		return this.get("topic");
	}
	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.set("topic", topic);
	}
	
	public String toString() {
		return this.getTopic();
	}
	
	/**
	 * @return the responsesNumber
	 */
	public long getResponsesNumber() {
		return this.get("responses");
	}
	/**
	 * @param responsesNumber the responsesNumber to set
	 */
	public void setResponsesNumber(long responsesNumber) {
		this.set("responses", responsesNumber);
	}
	/**
	 * @return the viewsNumber
	 */
	public long getViewsNumber() {
		return this.get("views");
	}
	/**
	 * @param viewsNumber the viewsNumber to set
	 */
	public void setViewsNumber(long viewsNumber) {
		this.set("views", viewsNumber);
	}


	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(String property) {
		if (properties == null)
			properties = new HashMap<String, Object>();
		return (X) properties.get(property);
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X remove(String property) {
		if (properties == null)
			properties = new HashMap<String, Object>();
		return (X) properties.remove(property);
	}

	@Override
	public <X> X set(String property, X value) {
		if (properties == null)
			properties = new HashMap<String, Object>();
		properties.put(property, value);
		return value;
	}	
}
