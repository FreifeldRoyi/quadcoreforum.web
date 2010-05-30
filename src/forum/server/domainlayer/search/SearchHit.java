package forum.server.domainlayer.search;

import forum.server.domainlayer.interfaces.UIMessage;


/**
 * A single search hit returned after a search operation.
 * 
 * @author Tomer Heber
 */
public class SearchHit {
	
	private UIMessage message;
	private double score;

	public SearchHit(UIMessage message, double score) {
		this.message = message;
		this.score = score;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public UIMessage getMessage() {
		return this.message;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean toReturn = false;
		
		if (o instanceof SearchHit)
		{
			SearchHit tSe = (SearchHit)o;
			toReturn = this.message.equals(tSe.getMessage()) &&
							this.score == tSe.getScore();
		}
		
		return toReturn;	
	}
}