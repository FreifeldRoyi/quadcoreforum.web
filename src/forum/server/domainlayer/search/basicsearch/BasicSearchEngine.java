/**
 * 
 */
package forum.server.domainlayer.search.basicsearch;

import java.util.Arrays;
import java.util.Collection;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.search.SearchEngine;
import forum.server.domainlayer.search.SearchHit;

/**
 * @author Royi Freifeld <br></br>
 * This class is the one responsible for searched data mining.
 * Will search on volatile memory and data base.
 */
public class BasicSearchEngine implements SearchEngine
{
	private SearchIndex indexer;
	
	public BasicSearchEngine()
	{
		this.indexer = SearchIndex.getInstance();
	}

	
	/**
	 * @see SearchEngine#addData(UIMessage)
	 */
	@Override
	public void addData(UIMessage msg) 
	{
		this.indexer.addMessage(msg);
	}

	/**
	 * @see SearchEngine#searchByAuthor(String, int, int)
	 */
	@Override
	public SearchHit[] searchByAuthor(long usrID, int from, int to) 
	{
		SearchHit[] toReturn = null;
				
		if (usrID >= 0)
		{
			Long tUsrID = new Long (usrID);
				
			Collection<SearchHit> tVolatileHits = this.indexer.getDataByAuthor(tUsrID);
			if (-1 < from && from < to && from < tVolatileHits.size())
			{
				if (tVolatileHits.size() < to)
					to = tVolatileHits.size();
	
				toReturn = new SearchHit[to];
				SearchHit[] tVolHitsArr = tVolatileHits.toArray(new SearchHit[0]);
				
				int tIndex = from;
				while (tIndex != to)
				{
					toReturn[tIndex - from] = tVolHitsArr[tIndex];
					++tIndex;
				}
			}
			else
			{
				toReturn = new SearchHit[0];
			}
		}
		else
		{
			toReturn = new SearchHit[0];
		}
		
		return toReturn;
	}

	/**
	 * @see SearchEngine#searchByContent(String, int, int)
	 */
	@Override
	public SearchHit[] searchByContent(String phrase, int from, int to) 
	{
		SearchHit[] toReturn = null;
		
		if (phrase != null && !phrase.equals(""))
		{
			Collection<SearchHit> tSearchHitUnsorted = this.indexer.getDataByContent(phrase.split(" "));
			SearchHit[] tSearchHitSorted = this.sortByHitScore(tSearchHitUnsorted);
			
			if (-1 < from && from < to && from < tSearchHitSorted.length)
			{
				if (tSearchHitSorted.length < to)
					to = tSearchHitSorted.length;
				
				toReturn = new SearchHit[to];
				
				int tIndex = 0;
				int newFrom = tSearchHitSorted.length - from - 1;
				while (tIndex != to)
				{
					toReturn[newFrom - tIndex] = tSearchHitSorted[tIndex];
					++tIndex;
				}
			}
			else
			{
				toReturn = new SearchHit[0];
			}
		}
		else
		{
			toReturn = new SearchHit[0];
		}
		
		return toReturn;
	}
	
	private SearchHit[] sortByHitScore(Collection<SearchHit> hits)
	{
		SearchHit[] toReturn = hits.toArray(new SearchHit[0]);
		
		Arrays.sort(toReturn, new SearchHitComparator());
				
		return toReturn;
	}
}