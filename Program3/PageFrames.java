/**
 * ICS 462-1 Operating Systems
 * Homework Problem 3
 * 
 * PageFrames.java
 * Purpose: This class provides a structure for the frames in memory.
 *          It simulates swapping in and out different page requests.
 * 
 * @author Chelsea Hanson
 * @version 1.0 11/30/16
 */

import java.util.LinkedList;
import java.util.Iterator;

public class PageFrames {
    // maintains a list of frames in memory
    private LinkedList<Integer> frames;
    // the maximum number of frames in memory
    private int maxSize;
    // the number of free frames
    private int size;
    
    /**
     * This constructor initializes a list with a predefined maximum size.
     * param max - the user/driver specified maximum number of frames in memory
     */
    public PageFrames(int max) {
        frames = new LinkedList<Integer>();
        maxSize = max;
        size = max;
    }
    
    /**
     * This method checks if a given page is already in memory.
     * @param page - the user/driver specified page number to look for in memory
     * @return true if the page is already in memory
     *         false if the page is not
     */
    public boolean contains(int page) {
        for (int i : frames) {
            if (page == i) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method checks if all the frames in memory are filled.
     * @return true if there are no empty frames in memory 
     *         false if there is an empty frame
     */
    public boolean isFull() {
        if (size > 0) {
            return false;
        }
        return true;
    }
    
    /**
     * This method simulates bringing a page into memory.
     * @param page - the page number to put into memory
     */
    public void loadPage(int page) {
        if (! isFull()) {
            // adds page to the end of the list of frames in memory
            frames.addLast(page);
            // one less available frame in memory
            size--;
        }
    }
    
    /**
     * This method simulates swapping the next page out of memory.
     */
    public void swapOutPage() {
        // oldest in is the first of the list
        frames.removeFirst();
        size++;
    }
    
    /**
     * This method simulates swapping a specific page out of memory.
     * (For use with the Optimal Replacement Algorithm)
     * @param page - the page to swap out of memory so a new page can be loaded
     */
    public void swapOut(int page) {
        for (int i : frames) {
            if (page == i) {
                // find and remove the page from memory
                frames.removeFirstOccurrence(page);
                size++;
                break;
            }
        }
    }
    
    /**
     * This method is used to show that a page in memory is accessed again,
     * and is not growing old and stale. 
     * (important for least-recently-used algorithm)
     * @param page - the page in memory that is accessed again
     */
    public void updateUse(int page) {
        // remove page from the middle of the list
        swapOut(page);
        // and place it at the end instead
        loadPage(page);
    }
    
    /**
     * This method gets an instantaneous look of which pages are in memory
     * @return an array of the pages in memory
     */
    public int[] snapshot() {
        int[] arr = new int[maxSize];
        int i = 0;
        
        // iterate over the frames in memory to see which page is in them
        Iterator<Integer> iterator = frames.iterator();
        while (iterator.hasNext() && i < maxSize) {
            arr[i] = iterator.next();
        }
        
        return arr;
    }
}