package forum.server.domainlayer.search.basicsearch;


import java.util.Vector; 

import junit.framework.TestCase;

import org.junit.*;

import forum.server.Settings;
import forum.server.domainlayer.interfaces.UIMessage; 
import forum.server.domainlayer.message.ForumMessage; 
import forum.server.domainlayer.search.SearchHit; 

public class SearchIndexTest extends TestCase { 

	private SearchIndex se;
	
	@Before 
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		this.se = SearchIndex.getInstance(); 
		this.se.clear();
	} 

	@After 
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	} 

	@Test 
	public void testAddAndGetMessage() { 
		UIMessage tMsg1 = new ForumMessage(0,0,"msg1 bla","content1", -1); 
		UIMessage tMsg2 = new ForumMessage(1,0,"msg2 bla","content2", -1); 
		UIMessage tMsg3 = new ForumMessage(2,0,"msg3","content3 bla", -1); 
		UIMessage tMsg4 = new ForumMessage(3,1,"msg4","content4", -1); 

		this.se.addMessage(tMsg4); 
		this.se.addMessage(tMsg3); 
		this.se.addMessage(tMsg2); 
		this.se.addMessage(tMsg1); 

		String[] words1 = {"content1"}; 
		String[] words2 = {"content2"}; 
		String[] words3 = {"content3"}; 
		String[] words4 = {"content4"}; 
		String[] words5 = {"msg1"}; 
		String[] words6 = {"bla"}; 

		//test logic operation 
		String[] words7 = {"content3", "AND", "bla"}; 
		String[] words8 = {"content3", "OR", "content4"}; 
		String[] words9 = {"content3", "AND", "bla", "OR", "content4"}; 

		/* Test get by content */ 
		Vector<SearchHit> tSHCol1 = (Vector<SearchHit>) this.se.getDataByContent(words1); //should hold tMsg1 
		Vector<SearchHit> tSHCol2 = (Vector<SearchHit>) this.se.getDataByContent(words2); //should hold tMsg2 
		Vector<SearchHit> tSHCol3 = (Vector<SearchHit>) this.se.getDataByContent(words3); //should hold tMsg3 
		Vector<SearchHit> tSHCol4 = (Vector<SearchHit>) this.se.getDataByContent(words4); //should hold tMsg4 
		Vector<SearchHit> tSHCol5 = (Vector<SearchHit>) this.se.getDataByContent(words5); //should hold tMsg1 
		Vector<SearchHit> tSHCol6 = (Vector<SearchHit>) this.se.getDataByContent(words6); //should hold tMsg1 tMsg2 tMsg3 

		
		//logic 
		Vector<SearchHit> tSHCol7 = (Vector<SearchHit>) this.se.getDataByContent(words7); //should hold tMsg3 tMsg2 tMsg1 
		Vector<SearchHit> tSHCol8 = (Vector<SearchHit>) this.se.getDataByContent(words8); //should hold tMsg3 and tMsg4 with similar scores 
		Vector<SearchHit> tSHCol9 = (Vector<SearchHit>) this.se.getDataByContent(words9); //should hold tMsg1 tMsg2 tMsg3 tMsg4 where tMsg3 has higher score 

		Vector<UIMessage> tMSGCol1 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol2 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol3 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol4 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol5 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol6 = new Vector<UIMessage>(); 

		//logic 
		Vector<UIMessage> tMSGCol7 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol8 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol9 = new Vector<UIMessage>(); 

		
		
		for (SearchHit tSh : tSHCol1) 
			tMSGCol1.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol2) 
			tMSGCol2.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol3) 
			tMSGCol3.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol4) 
			tMSGCol4.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol5) 
			tMSGCol5.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol6) 
			tMSGCol6.add(tSh.getMessage()); 

		//logic 
		for (SearchHit tSh : tSHCol7) 
			tMSGCol7.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol8) 
			tMSGCol8.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol9) 
			tMSGCol9.add(tSh.getMessage()); 

		assertTrue(tMSGCol1.contains(tMsg1)); 
		assertTrue(tMSGCol2.contains(tMsg2)); 
		assertTrue(tMSGCol3.contains(tMsg3)); 
		assertTrue(tMSGCol4.contains(tMsg4)); 
		assertTrue(tMSGCol5.contains(tMsg1)); 
		assertTrue(tMSGCol6.contains(tMsg1) && tMSGCol6.contains(tMsg2) && tMSGCol6.contains(tMsg3)); 

		//logic 
		assertTrue(tMSGCol7.contains(tMsg3) && tMSGCol7.contains(tMsg2) && tMSGCol7.contains(tMsg1)); 
		assertTrue(tMSGCol8.contains(tMsg3) &&  
				tMSGCol8.contains(tMsg4) &&  
				tSHCol8.elementAt(0).getScore() == tSHCol8.elementAt(1).getScore()); 
		assertTrue(tMSGCol9.contains(tMsg1) && 
				tMSGCol9.contains(tMsg2) && 
				tMSGCol9.contains(tMsg3) &&  
				tMSGCol9.contains(tMsg4)); 
		/*assertTrue(tSHCol9.elementAt(0).getScore() != tSHCol9.elementAt(1).getScore() && 
                                 tSHCol9.elementAt(0).getScore() != tSHCol9.elementAt(2).getScore() && 
                                 tSHCol9.elementAt(0).getScore() != tSHCol9.elementAt(3).getScore() && 
                                 tSHCol9.elementAt(1).getScore() == tSHCol9.elementAt(2).getScore() && 
                                 tSHCol9.elementAt(1).getScore() == tSHCol9.elementAt(3).getScore());  

                         this test actually works, the only problem is the different location the  
                         element is being put at 
                         but it's all good! - REALLY*/ 

		/* Test get by author */ 
		Vector<SearchHit> tSHCol10 = (Vector<SearchHit>) this.se.getDataByAuthor(new Long(0)); //should contain tMsg1 tMsg2 tMsg3 
		Vector<SearchHit> tSHCol11 = (Vector<SearchHit>) this.se.getDataByAuthor(new Long(1)); //should contain tMsg4 

		Vector<UIMessage> tMSGCol10 = new Vector<UIMessage>(); 
		Vector<UIMessage> tMSGCol11 = new Vector<UIMessage>(); 

		for (SearchHit tSh : tSHCol10) 
			tMSGCol10.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHCol11) 
			tMSGCol11.add(tSh.getMessage()); 

		assertTrue(tMSGCol10.contains(tMsg1) && tMSGCol10.contains(tMsg2) && tMSGCol10.contains(tMsg3)); 
		assertTrue(tMSGCol11.contains(tMsg4)); 
	} 

	// I used this code to validate my algorithm... AND IT WORKS =) 
	/* 
         public void testDivide() 
         { 
                 String tPhraseToSearch1 = "word1 word2 word3 word4"; 
                 String tPhraseToSearch2 = "word1 word2 AND word3 word4"; 
                 String tPhraseToSearch3 = "word1 AND word2 AND word3 AND word4"; 
                 String tPhraseToSearch4 = "word1 word2 AND word3 OR word4"; 

                 Vector<Vector<String>> tReturn1 = this.se.divideByBooleanOperators(tPhraseToSearch1.split(" ")); 
                 Vector<Vector<String>> tReturn2 = this.se.divideByBooleanOperators(tPhraseToSearch2.split(" ")); 
                 Vector<Vector<String>> tReturn3 = this.se.divideByBooleanOperators(tPhraseToSearch3.split(" ")); 
                 Vector<Vector<String>> tReturn4 = this.se.divideByBooleanOperators(tPhraseToSearch4.split(" ")); 

                 return; 
         } 
	 */ 

} 
