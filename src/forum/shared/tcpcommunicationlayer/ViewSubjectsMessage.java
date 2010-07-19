package forum.shared.tcpcommunicationlayer;

public class ViewSubjectsMessage extends ClientMessage {

	private static final long serialVersionUID = 3449046847115401697L;
	/*
	 * The id of the root subject, whose sub-subjects' data should be returned.
	 * 	If the id is -1, then the forum root subjects data is returned
	 */
	private long fatherID;

	public ViewSubjectsMessage() {}
	public ViewSubjectsMessage(final long fatherID){
		this.fatherID = fatherID;
	}

	public long getFatherID() {
		return this.fatherID;
	}

	public void setFatherID(long fatherID) {
		this.fatherID = fatherID;
	}
}
