/** 
  *  
  */ 
 package forum.server.domainlayer.search.basicsearch; 
  
 import java.util.Comparator; 

import forum.server.domainlayer.search.SearchHit;
  
 /** 
  * @author Royi Freifeld 
  * 
  */ 
 public class SearchHitComparator implements Comparator<SearchHit> { 
  
         @Override 
         public int compare(SearchHit o1, SearchHit o2)  
         { 
                 int toReturn = 0; 
                  
                 if (o1.getScore() < o2.getScore()) 
                         toReturn = -1; 
                 else if (o1.getScore() == o2.getScore()) 
                         toReturn = 0; 
                 else  
                         toReturn = 1; 
                  
                 return toReturn; 
         } 
  
 } 
