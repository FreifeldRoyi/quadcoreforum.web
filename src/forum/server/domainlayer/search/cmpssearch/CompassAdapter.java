/**
 * 
 */
package forum.server.domainlayer.search.cmpssearch;

import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassHit;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.search.SearchEngine;
import forum.server.domainlayer.search.SearchHit;

/**
 * @author Freifeld Royi <br></br>
 * Class using the Adapter design pattern to implement a new searching capability
 * using the Compass 
 */
public class CompassAdapter implements SearchEngine 
{
	/* The adaptee */
	private Compass compass;
	
	public CompassAdapter(Compass cmp)
	{
		this.compass = cmp;
	}
	
	/**
	 * @see forum.server.domainlayer.search.SearchEngine#addData(forum.server.domainlayer.interfaces.UIMessage)
	 */
	@Override
	public void addData(UIMessage msg) 
	{
		CompassSession session = this.compass.openSession();
		try 
		{
			session.save(msg);
		}
		catch (CompassException e)
		{
			session.rollback();
		}
		finally
		{
			session.close();
		}
	}

	/**
	 * @see forum.server.domainlayer.search.SearchEngine#searchByAuthor(long, int, int)
	 */
	@Override
	public SearchHit[] searchByAuthor(long usrID, int from, int to) 
	{
		System.out.println("usrID = " + usrID);

		SearchHit[] toReturn = null;
		
		CompassSession session = this.compass.openSession();
		
		try 
		{
			CompassHits hits = session.find("authorID:\"" + usrID + "\"");
			CompassHit[] detachedHits = hits.detach(from,to).getHits();
			
			toReturn = new SearchHit[detachedHits.length];
			
			for (int i = 0; i < detachedHits.length; ++i)
			{
				toReturn[i] = new SearchHit((UIMessage)detachedHits[i].data(), detachedHits[i].score());
			}
		}
		catch (CompassException e) 
		{
			toReturn = new SearchHit[0];
			session.rollback();
		}
		finally
		{
			session.close();
		}

		return toReturn;
	}

	/**
	 * @see forum.server.domainlayer.search.SearchEngine#searchByContent(java.lang.String, int, int)
	 */
	@Override
	public SearchHit[] searchByContent(String phrase, int from, int to) 
	{
		SearchHit[] toReturn = null;
		
		CompassSession session = this.compass.openSession();
		
		try
		{
			System.out.println("phrase = " + phrase);
			CompassHits hits = session.find("title:" + phrase + " OR content:" + phrase);
//			CompassHits hits = session.find("content: \"" + phrase + "\" OR \" title:\" " + phrase + "\"");
			CompassHit[] detachedHits = hits.detach(from,to).getHits();
			
			toReturn = new SearchHit[detachedHits.length];
			
			for (int i = 0; i < detachedHits.length; ++i)
			{
				toReturn[i] = new SearchHit((UIMessage)detachedHits[i].data(), detachedHits[i].score());
			}
		}
		catch (CompassException e)
		{
			toReturn = new SearchHit[0];
			session.rollback();
		}
		finally
		{
			session.close();
		}
		
		return toReturn;
	}
}
