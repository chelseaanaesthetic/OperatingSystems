/**
 * ICS 462-1 Operating Systems
 * Homework Problem 3
 * 
 * MemoryManager.java
 * Purpose: This class contains page replacement algorithms for 
 *          a memory manager, as well as the driver to test them out.
 * 
 * @author Chelsea Hanson
 * @version 1.0 11/30/16
 */

public class MemoryManager {
    // an object that simulates a page request queue
    private static PageQueue pageQueue;
    // an object that simulates the frames in memory
    private static PageFrames pageFrames;
    // the current page request the memory manager is dealing with
    private static int active;
    
    private static int[] givenRefString;
    private static int[] customRefString;
    private static int[] workingString;
    
    // keeps track of how many frames the manager is testing algorithms on
    private static int numOfFrames;
    // keeps track of how many page faults are generated via each algorithm
    private static int pageFaults;
    
    /**
     * This method is essentially the driver to the whole project.
     * It creates strings of page requests and tests the efficiency 
     * of different replacement algorithms.
     * 
     */
    public static void main(String args[]) {
        initialize();
        
        // print to the screen the string of page requests to be simulated
        printPageReferenceString(customRefString, "Random");
        
        // run each algorithm on the string of page requests
        firstInFirstOutAlgorithm(customRefString);
        leastRecentlyUsedAlgorithm(customRefString);
        optimalAlgorithm(customRefString);
        
        // repeat with the given string of requests
        printPageReferenceString(givenRefString, "Given");
        
        firstInFirstOutAlgorithm(givenRefString);
        leastRecentlyUsedAlgorithm(givenRefString);
        optimalAlgorithm(givenRefString);
    }
    
    /**
     * This method sets up the initial data to be simulated by the memory manager.
     */
    public static void initialize() {
        // part of the assignment description
        givenRefString = new int[]
                {0,7,0,1,2,0,8,9,0,3,0,4,5,6,7,0,8,9,1,2,3,4,5,9,7,8,5,6,0,3};
        
        // generate an array of 30 random integers between 0 and 9
        customRefString = new int[30];
        for (int i = 0; i < 30; i++) {
            customRefString[i] = (int)(Math.random() * ((9) + 1));
        }
    }
    
    /**
     * This method is used to clear out any leftover data from the previous 
     * algorithm simulation and to return values to their initial state.
     * @param i - the number of frames to use in the upcoming simulation
              arr - the array of page requests to simulate
     */
    private static void resetValues(int i, int[] arr) {
        // no active page request yet
        active = -1;
        // reset page fault counter to 0
        pageFaults = 0;
        // set the number of frames in memory to simulate
        numOfFrames = i;
        pageFrames = new PageFrames(i);
        // preserve the original array by creating a copy and sending that for simulation
        workingString = arr.clone();
        pageQueue = new PageQueue(workingString);
    }
    
    /**
     * This method simulates the first-in, first-out page replacement algorithm to 
     * handle the list of page requests.
     * @param refString - the list of page requests to simulate
     */
    public static void firstInFirstOutAlgorithm(int[] refString) {
        // run through each successive number of frames in memory to compare them
        for (int i = 1; i < 8; i++) {
            resetValues(i, refString);
            // if theres another page request, get it
            while (pageQueue.hasMoreRequests()) {
                active = pageQueue.nextRequest();
                // if the page is not already in memory, add a fault
                if (! (pageFrames.contains(active))) {
                    pageFaults++;
                    // if there is no available frame to load the page into...
                    if (pageFrames.isFull()) {
                        // swap one out
                        pageFrames.swapOutPage();
                    }
                    // and load the page into memory
                    pageFrames.loadPage(active);
                }
            }
            
            // display algorithm name and results to the console for comparison
            printResult("First In First Out");
        }
        System.out.println("");
    }
    
    /**
     * This method simulates the least recently used page replacement algorithm
     * to handle the list of page requests.
     * @param refString - the list of page requests to simulate
     */
    public static void leastRecentlyUsedAlgorithm(int[] refString) {
        // mostly the same as first-in, first-out except for...
        for (int i = 1; i < 8; i++) {
            resetValues(i, refString);
            
            while (pageQueue.hasMoreRequests()) {
                active = pageQueue.nextRequest();
                // if the page is already in memory, mark that it was called again
                if (pageFrames.contains(active)) {
                    pageFrames.updateUse(active);
                } 
                else {
                    pageFaults++;
                    if (pageFrames.isFull()) {
                        pageFrames.swapOutPage();
                    }
                    pageFrames.loadPage(active);
                }
            }
            
            // display algorithm name and results to the console for comparison
            printResult("Least Recently Used");
        }
        System.out.println("");
    }
    
    /**
     * This method simulates the optimal replacement algorithm to handle the list 
     * of page requests.
     * @param refString - the list of page requests to simulate
     */
    public static void optimalAlgorithm(int[] refString) {
        int leastLikely;
        
        // mostly the same as first-in, first-out except for...
        for (int i = 1; i < 8; i++) {
            resetValues(i, refString);
           
            while (pageQueue.hasMoreRequests()) {
                active = pageQueue.nextRequest();
                if (! (pageFrames.contains(active))) {
                    pageFaults++;
                    // instead of swapping out the oldest page, 
                    // get the least likely page to be used in the future to swap out
                    if (pageFrames.isFull()) {
                        leastLikely = pageQueue.findLeastLikely(pageFrames.snapshot());
                        pageFrames.swapOut(leastLikely);
                    }
                    pageFrames.loadPage(active);
                }
            }
            
            // display algorithm name and results to the console for comparison
            printResult("Optimal Page Replacement");
        }
        System.out.println("");
    }
    
    /**
     * This method displays to the user the list of page requests to be simulated.
     * @param refString - the list of page requests to simulate
     *        type - a string representation of the list of page requests
     *               (value of: Random or Given to differentiate which is which)
     */
    public static void printPageReferenceString(int[] refString, String type) {
        System.out.print("\n" + type + " Page Reference String: \n" + refString[0]);
        for (int i = 1; i < refString.length; i++) {
            System.out.print("," + refString[i]);
        }
        System.out.println("\n");
    }
    
    /**
     * This method takes raw data of the number of frames and page faults
     * and displays it nicely for the user to compare algorithms.
     * @param name - the name of the algorithm that got the results
     */
    public static void printResult(String name) {
        System.out.println(name + " with " + numOfFrames + 
        " page frames results in " + pageFaults + " page faults.");
    }
}