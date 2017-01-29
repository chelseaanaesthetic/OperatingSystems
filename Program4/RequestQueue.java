/**
 * ICS 462-1 Operating Systems
 * Homework Problem 4
 * 
 * .java
 * Purpose: This class provides a structure for the queued  
 *          requests and the methods to interact with the queue 
 *          and determine which request to fulfill first.
 * 
 * @author Chelsea Hanson
 * @version 1.0 12/1/16
 */
 
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;

public class RequestQueue {
    // maintains a list of  requests
    private LinkedList<Integer> requests;
    
    // used for all but fcfs
    private int[] sortedRequests;
    
    private int startIndex;
    private int nextHigher;
    private int nextLower;    
    private int maxIndex;
    private int remaining;
    
    /**
     * This constructor initializes a list with no starting values
     */
    public RequestQueue() {
        requests = new LinkedList<Integer>();
    }
    
    /**
     * This constructor initializes a list with the values from the given array
     * @param req - the array of disk requests to initialize the disk request queue with
     */
    public RequestQueue (int[] req, int start, int max) {
        requests = new LinkedList<Integer>();
        for (int i : req) {
            requests.addLast(i);
        }
        sortRequests(req, start, max);
    }
    
    /**
     * This method sorts the requests in ascending order and sets the pointers for access.
     * @param req - the array of requests to be sorted
     *              start - the initial head position on the disk
     *              max - the largest posible head position     
     */
    private void sortRequests(int[] req, int start, int max) {
        startIndex = start;
        maxIndex = max;
        sortedRequests = req.clone();
        Arrays.sort(sortedRequests);
         
        // find where the initial head position fits in the request array
        for (int i = 0; (sortedRequests[i] < start) && (i < sortedRequests.length - 1); i++) {
            if (sortedRequests[i + 1] > start) {
                nextHigher = i + 1;
                nextLower = i;
                return;
            }
        }
        // if the initial head position is higher than than any value
        nextHigher = sortedRequests.length;
        nextLower = sortedRequests.length - 1;
    }
    
    /**
     * This method gets the number of requests in the disk request queue.
     * @return the number of requests in the disk request queue
     */
    public int size() {
        return requests.size();
    }
    
    /**
     * This method determines if there are more disk requests waiting in the queue.
     * @return true if there are more requests in the disk request queue
     */
    public boolean hasMoreRequests() {
        return (requests.size() > 0);
    }
    
    /**
     * This method returns the next disk request from the queue to be executed.
     * @return the first request in the disk queue
     */
    public int nextRequest() {
        return requests.pollFirst();
    }
    
    /** 
     * This method returns the next increasing request from the initial position.
     * @return the next increasing request or the limit if no requests left in that direction 
     */
    public int nextUp() {
        // if no requests left, return the maximum possible request
        if (nextHigher > sortedRequests.length - 1) {
            return maxIndex;
        }
        // remove request, return the value, and increase the pointer
        requests.removeFirstOccurrence(sortedRequests[nextHigher]);
        return sortedRequests[nextHigher++];
    }
    
    /**
     * This method wraps the request queue into a cirular array.
     * @return the new index after wrapping around the circular array
     */
    public int wrap() {
        if (nextHigher == maxIndex) {
            nextHigher = 0;
            return nextHigher;
        }
        else if (nextLower == -1) {
            nextLower = maxIndex - 1;
            return nextLower;
        }
        else return -2;
    }
    
    /** 
     * This method is similar to nextUp() but it goes from 0 to initial position 
     *                                                                (instead of initial position to max).
     * @return the next increasing request or the limit if no higher requests left
     */
    public int next() {
        if (nextHigher > sortedRequests.length - 1) {
            nextHigher = 0;
            return maxIndex;
        }
        else {
            // remove request, return the value, and increase the pointer
            requests.removeFirstOccurrence(sortedRequests[nextHigher]);
            return sortedRequests[nextHigher++];
        }
    }
    
    /**
     * This method returns the next decreasing request from the initial position.
     * @return the next decreasing request or 0 if there are no more requests left in that direction
     */
    public int nextDown() {
        // if no requests left, return the lower limit: 0
        if (nextLower < 0) {
            return -1;
        }
        // remove request, return the value, and decrease the pointer
        requests.removeFirstOccurrence(sortedRequests[nextLower]);
        return sortedRequests[nextLower--];
    }
    
    /** 
     * This method is similar to nextDown() but it goes from max value to initial position 
     *                                                                (instead of initial position to 0).
     * @return the next increasing request or the limit if no higher requests left
     */
    public int previous() {
        // wrap from 0 to max
        if (nextLower == -1) {
            nextLower = maxIndex - 1;
            return previous();
        }
        
        // remove request, return the value, and decrease the pointer
        requests.removeFirstOccurrence(sortedRequests[nextLower]);
        return sortedRequests[nextLower--];
    }
}