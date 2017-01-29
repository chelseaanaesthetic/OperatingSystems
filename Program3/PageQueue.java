/**
 * ICS 462-1 Operating Systems
 * Homework Problem 3
 * 
 * PageQueue.java
 * Purpose: This class provides a structure for the queued page 
 *          requests and the methods to interact with the queue 
 *          and determine which request to bring into memory.
 * 
 * @author Chelsea Hanson
 * @version 1.0 11/30/16
 */

import java.util.LinkedList;
import java.util.Iterator;

public class PageQueue{
    // maintains a list of page requests
    private LinkedList<Integer> requests;
    
    /**
     * This constructor initializes a list with no starting values
     */
    public PageQueue() {
        requests = new LinkedList<Integer>();
    }
    
    /**
     * This constructor initializes a list with the values from the given array
     * @param req - the array of page requests to initialize the page queue with
     */
    public PageQueue (int[] req) {
        requests = new LinkedList<Integer>();
        for (int i : req) {
            requests.addLast(i);
        }
    }
    
    /**
     * This method gets the number of requests in the page queue.
     * @return the number of requests in the page queue
     */
    public int size() {
        return requests.size();
    }
    
    /**
     * This method determines if there are more page requests waiting in the queue.
     * @return true if there are more requests in the page queue
     */
    public boolean hasMoreRequests() {
        return (requests.size() > 0);
    }
    
    /**
     * This method returns the next page request from the queue to be executed.
     * @return the first request in the page queue
     */
    public int nextRequest() {
        return requests.pollFirst();
    }
    
    /**
     * This method determines which page in memory is least likely to be used
     * in the near future.
     * @param frames - an array of the page numbers currently in memory
     * @return the page number of the page in memory that is not needed for the 
     *         longest amount of time (or page in lowest memory location for tie)
     */
    public int findLeastLikely(int[] frames) {
        // the number of options for pages that can be swapped out
        int choices = frames.length;
        Iterator<Integer> iterator = requests.iterator();
        
        while (choices > 1 && iterator.hasNext()) {
            int temp = iterator.next();
            // if page in memory is needed soon, mark with 10 (not a valid page number)
            for (int i = 0; i < frames.length; i++){
                // if the upcoming request is already in memory, don't swap it out
                if (frames[i] == temp) {
                    frames[i] = 10;
                    choices--;
                }
            }
        }
        // after pruning all but one choice or no more requests to check...
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] < 10) {
                // return the only choice, or the first (lowest in memory) page not needed
                return frames[i];
            }
        }
        
        // if somehow there is an error
        return -1;
    }
}