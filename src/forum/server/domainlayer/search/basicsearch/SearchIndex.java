/**

 */
package forum.server.domainlayer.search.basicsearch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.search.SearchHit;

/**
 * @author Royi Freifeld <br></br>
 * This class is responsible for the search engine's data.
 * While searching for an item, it will add it to the data tables
 * managed by this class, if it is not already in it, thus enabling
 * faster access for later search.
 * All data is saved on volatile memory only, which means every search data
 * saved after a computer restart, will be lost.
 */
public class SearchIndex
{
	private Vector<String> reserved_words;
	
	long wordIdNumber; //word indexing count
	private Map<String, Long> words; //word <-> wordID
	private Map<Long, Collection<Long>> relations; //wordID <-> collection of msgIDs
	private Map<Long, UIMessage> items; //msgID <-> msg
	private Map<Long, Collection<Long>> users; //usrID <-> collection of msgIDs
	
	private static SearchIndex INDEXER;
	
	public static SearchIndex getInstance()
	{
		if (SearchIndex.INDEXER == null)
			SearchIndex.INDEXER = new SearchIndex();
		
		return SearchIndex.INDEXER;
	}
	
	public void clear() {
		this.words.clear();
		this.relations.clear();
		this.items.clear();
		this.users.clear();
	}
	
	private SearchIndex()
	{
		this.reserved_words = new Vector<String>();
		this.words = new HashMap<String, Long>();
		this.relations = new HashMap<Long, Collection<Long>>();
		this.items = new HashMap<Long, UIMessage>();
		this.users = new HashMap<Long, Collection<Long>>();
		
		//special search operators
		this.reserved_words.add("OR");
		this.reserved_words.add("AND");	
		
		//nullify the number of word index
		this.wordIdNumber = 0;		
	}
	
	/**
	 * Adds data to the search tables.
	 * 
	 * @param msg - the message item to add
	 */
	public void addMessage(UIMessage msg)
	{
		String[] tContentSplit = msg.getContent().split(" ");
		String[] tSubjectSplit = msg.getTitle().split(" ");
		Vector<String> tNoReservedWords = new Vector<String>();
		
		if (!this.items.containsKey(msg.getMessageID()))
		{
			//removal of reserved words from content
			for (int tIndex = 0; tIndex < tContentSplit.length; ++tIndex)
			{
				if (!this.reserved_words.contains(tContentSplit[tIndex]))
				{
					tNoReservedWords.add(tContentSplit[tIndex]);
				}
			}
			//removal of reserved words from subject
			for (int tIndex = 0; tIndex < tSubjectSplit.length; ++tIndex)
			{
				if (!this.reserved_words.contains(tSubjectSplit[tIndex]))
				{
					tNoReservedWords.add(tSubjectSplit[tIndex]);
				}
			}
		
			//add words and relation bindings
			for (int tIndex = 0; tIndex < tNoReservedWords.size(); ++tIndex)
			{
				this.addWord(tNoReservedWords.elementAt(tIndex), msg.getMessageID());
			}
			
			//message ID <-> message binding
			this.items.put(new Long (msg.getMessageID()), msg);
			
			//user name <-> message ID binding
			Long tUsrID = new Long(msg.getAuthorID());
			Long tMsgID = new Long(msg.getMessageID());
			if (this.users.containsKey(tUsrID))
			{
				Collection<Long> tCol = this.users.get(tUsrID);
				tCol.add(tMsgID);
			}
			else
			{
				Collection<Long> tCol = new Vector<Long>();
				tCol.add(tMsgID);
				this.users.put(tUsrID, tCol);
			}				
		}
	}
	
	/**
	 * Returns a collection of search hits, containing the messages.
	 * The hit score is measured according to the number
	 * of words appearing in the words array (given as a parameter) 
	 * and in the message's content
	 * 
	 * @param words - the array of words to search
	 * @return a collection of search hits 
	 */
	public Collection<SearchHit> getDataByContent(String[] wordsArr)
	{
		Vector<SearchHit> toReturn = new Vector<SearchHit>();
		Vector<Vector<String>> tLogicalOperationDivided = divideByBooleanOperators(wordsArr);
		Vector<HashMap<UIMessage,Double>> tLogicalOperationsHashes =  new Vector<HashMap<UIMessage,Double>>();
		
		for (int tCount = 0; tCount < tLogicalOperationDivided.size(); ++tCount)
		{
			HashMap<UIMessage, Double> toAdd = new HashMap<UIMessage, Double>();
			tLogicalOperationsHashes.add(toAdd);
		}
		
		for (int tIndex = 0; tIndex < tLogicalOperationDivided.size(); ++tIndex)
		{
			for (String tWord : tLogicalOperationDivided.elementAt(tIndex))
			{
				if (this.words.containsKey(tWord))
				{
					Collection<Long> tMsgID = this.relations.get(this.words.get(tWord));
					for (Long tUImsgID : tMsgID)
					{
						UIMessage tUIMsg = this.items.get(tUImsgID);
						Double tValue;
						
						if (tLogicalOperationsHashes.elementAt(tIndex).containsKey(tUIMsg))
						{
							tValue = new Double(tLogicalOperationsHashes.elementAt(tIndex).get(tUIMsg).doubleValue() + 1);
						}
						else
						{
							tValue = new Double(1);
						}
						
						tLogicalOperationsHashes.elementAt(tIndex).put(tUIMsg, tValue);
					}
				}
			}
		}
		
		HashMap<UIMessage, Pair<Integer, Double>> tMerge = merge(tLogicalOperationsHashes);
		HashMap<Integer,Double> tMax = findMaxScores(tMerge);
		
		
		//the next block of code will normalize the scores given to all hits
		Set<Map.Entry<UIMessage, Pair<Integer,Double>>> tSetMappings = tMerge.entrySet();
		Iterator<Map.Entry<UIMessage, Pair<Integer,Double>>> tItr = tSetMappings.iterator();
		
		while (tItr.hasNext())
		{
			Map.Entry<UIMessage, Pair<Integer,Double>> tEntry = tItr.next();
			double tScore = 
				tEntry.getValue().getFirst().intValue() + 
					(tEntry.getValue().getSecond().doubleValue() / 
							tMax.get(tEntry.getValue().getFirst()).doubleValue());
			SearchHit tSH = new SearchHit(tEntry.getKey(), tScore);
			toReturn.add(tSH);
		}
		
		return toReturn;		
	}
	
	/**
	 * Returns a collection of search hits, containing the messages
	 * written by the user who's ID is the same as specified in @param usrID
	 * The score is the same for every hit and is set to 1
	 * 
	 * @param usrID - the user's ID given in a Long object format
	 * @return a collection of search hits
	 */
	public Collection<SearchHit> getDataByAuthor(Long usrID)
	{
		Vector<SearchHit> toReturn = new Vector<SearchHit>();
		Collection<Long> tValues = this.users.get(usrID);
		if (tValues == null) return toReturn;
		for (Long tVal : tValues)
		{
			toReturn.add(new SearchHit(this.items.get(tVal),1));
		}
		
		return toReturn;
	}
	
	/**
	 * @return a copy of the reserved words
	 */
	public Vector<String> getReservedWords()
	{
		Vector<String> toReturn = new Vector<String>();
		for (String tStr : this.reserved_words)
		{
			toReturn.add(tStr.substring(0));
		}
		return toReturn;
	}
	
	/**
	 * adds a word to the reserved words volatile data.
	 * The search engine will not search according to these words
	 * @param word - the word to add
	 */
	public void addReservedWord(String word)
	{
		this.reserved_words.add(word);
	}
	
	/**
	 * Adding a word to the data tables
	 * creating word <-> word ID binding
	 * and word ID <-> message ID
	 * 
	 * @param word - the word to add
	 * @param msgID - the message ID that will be bound to the word
	 */
	private void addWord(String word, Long msgID)
	{
		if (!this.words.containsKey(word))
		{
			++this.wordIdNumber;
			this.words.put(word, this.wordIdNumber);
		}
		
		Long tWordID = this.words.get(word); 
		
		if (this.relations.containsKey(tWordID))
		{
			Collection<Long> tColl = this.relations.get(tWordID);
			tColl.add(msgID);
		}
		else
		{
			Vector<Long> toAdd = new Vector<Long>();
			toAdd.add(msgID);
			this.relations.put(tWordID, toAdd);
		}
	}
	
	/**
	 * returns the words array divided to vector.
	 * each Vector(String) contains words divided by OR
	 * whereas each Vector(String) represents another element in 
	 * an AND expression.
	 * e.g - 
	 * 		the returned vector contains V1, V2, V3. 
	 * 		V1 - s11 OR s12 OR s13.....
	 * 		V2 - s21 OR s22 OR s23.....
	 * 		V3 - s31 OR s32 OR s33.....
	 * 
	 * 		V1 AND V2 AND V3		
	 * 		
	 * @param words - the split phrase wanted to be divided - NON EMPTY
	 * @return a vector of vector of strings
	 */
	private Vector<Vector<String>> divideByBooleanOperators(String[] words)
	{
		Vector<Vector<String>> toReturn = new Vector<Vector<String>>();
	
		int tEndPlace = 0;
		
		while (tEndPlace < words.length)
		{
			Vector<String> toAdd = new Vector<String>();
			while (tEndPlace != words.length && !words[tEndPlace].equals("AND"))
			{
				if (!words[tEndPlace].equals("OR"))
					toAdd.add(words[tEndPlace]);
				++tEndPlace;
			}
			toReturn.add(toAdd);
			++tEndPlace;
		}
		
		return toReturn;
	}
	
	/**
	 * The next block of code will merge all scores into one hash map
	 * In Pair, the integer is the number of times the message appeared
	 * and the double was the score sum
	 * @param tLogicalOperationsHashes
	 * @return a HashMap<UIMessage,Pair<Integer,Double>>
	 */
	private HashMap<UIMessage, Pair<Integer, Double>> merge(Vector<HashMap<UIMessage,Double>> tLogicalOperationsHashes)
	{
		HashMap<UIMessage,Pair<Integer, Double>> tMerge = new HashMap<UIMessage, Pair<Integer,Double>>();
		for (HashMap<UIMessage,Double> tHash : tLogicalOperationsHashes)
		{
			Set<Map.Entry<UIMessage, Double>> tMappings = tHash.entrySet();
			Iterator<Map.Entry<UIMessage, Double>> tItr = tMappings.iterator();
			
			while (tItr.hasNext())
			{
				Map.Entry<UIMessage, Double> tEntry = tItr.next();
				
				//handle new scores
				if (tMerge.containsKey(tEntry.getKey()))
				{
					Pair<Integer,Double> tPair = tMerge.get(tEntry.getKey());
					tPair.setFirst(tPair.getFirst() + 1);
					tPair.setSecond(tPair.getSecond() + tEntry.getValue());
				}
				else
				{
					Pair<Integer,Double> tPair = new Pair<Integer,Double>(new Integer(1), tEntry.getValue());
					tMerge.put(tEntry.getKey(), tPair);
				}	
			}
		}
		
		return tMerge;
	}
	
	/**
	 * the next block of code will find the maximum score, 
	 * and divide it according to appearance time
	 * 
	 * @param tMerge
	 * @return
	 */
	private HashMap<Integer,Double> findMaxScores(HashMap<UIMessage, Pair<Integer, Double>> tMerge)
	{
		HashMap<Integer,Double> tMax = new HashMap<Integer, Double>();
		Set<Map.Entry<UIMessage, Pair<Integer,Double>>> tSetMappings = tMerge.entrySet();
		Iterator<Map.Entry<UIMessage, Pair<Integer,Double>>> tItr = tSetMappings.iterator();
		
		while (tItr.hasNext())
		{
			Map.Entry<UIMessage, Pair<Integer,Double>> tEntry = tItr.next();
			
			// TODO find a better structure for that if-else block
			if (tMax.containsKey(tEntry.getValue().getFirst()))
			{
				if (tMax.get(tEntry.getValue().getFirst()).doubleValue() < tEntry.getValue().getSecond().doubleValue())
					tMax.put(tEntry.getValue().getFirst(),tEntry.getValue().getSecond());
			}
			else
			{
				tMax.put(tEntry.getValue().getFirst(), tEntry.getValue().getSecond());
			}
		}
		
		return tMax;
	}
}
