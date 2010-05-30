package forum.server.domainlayer.search; 

import forum.server.domainlayer.interfaces.UIMessage; 


/** 
 * @author Tomer Heber 
 * 
 */ 
public interface SearchEngine { 

	/** 
	 * Adds the Message msg to the search engine. 
	 * Fields that should be indexed:<br> 
	 *  1. Each word in the content of the message.<br> 
	 *  2. The username of the author who wrote the message.<br> 
	 *   
	 * @param msg The message which we want to add to the indexing data base of the search engine. 
	 */ 
	public void addData(UIMessage msg); 

	/** 
	 * Search for all the messages(SearchHit) written by the author called username.<br><br> 
	 *        
	 * Example: <i>searchByAuthor("Jonny82",0,10)</i> will return the first ten hits(messages) written 
	 * by the author/user Jonny82.  
	 *  
	 * @param username The username of the author who wrote the message. 
	 * @param from hits from index from 
	 * @param to hits until index to 
	 *  
	 * @return The search hits from index from till index to - 1 
	 */ 
	public SearchHit[] searchByAuthor(long usrID, int from, int to); 

	/** 
	 * Search for all the messages(SearchHit) which contain the phrase.<br><br> 
	 *        
	 * Examples: 
	 * // Search for the exact phrase read my lips and return hits 10-19. 
	 * searchByContent(\""read my lips"\",10,20); 
	 *  
	 * // Search for messages who contain either apples, bananas <b>or</b> both and return hits 5-10. 
	 * searchByContent("apples OR bananas",5,10);  
	 *  
	 * //Search for messages who must contain both oranges <b>and</b> apples and return hits 0-9. 
	 * searchByContent("oranges AND apples",0,10);    
	 *  
	 * @param phrase The phrase which we would like to search 
	 * @param from hits from index from 
	 * @param to hits until index to 
	 *  
	 * @return The search hits from index from till index to - 1 
	 */ 
	public SearchHit[] searchByContent(String phrase, int from, int to); 
}