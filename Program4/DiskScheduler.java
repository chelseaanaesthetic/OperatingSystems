/**
 * ICS 462-1 Operating Systems
 * Homework Problem 4
 * 
 * DiskScheduler.java
 * Purpose: This class contains disk scheduling algorithms for a 
 *          disk scheduler, as well as the driver to test them out.
 * 
 * @author Chelsea Hanson
 * @version 1.0 12/1/16
 */

import java.lang.*;
 
public class DiskScheduler {
    // an object that simulates a disk request queue
    private static RequestQueue diskRequestQueue;
    // the list of disk requests generated
    private static int[] referenceString;
    // the number of disk requests to process
    private static int testSize;
    // the number of cylinders on the disk
    private static int diskSize;
    // remembers the initial head position on the disk
    private static int initialPosition; 
    // tracks the current head position on the disk
    private static int currentPosition;
    // tracks the position of the next increasing request
    private static int nextUp;
    // tracks the position of the next decreasing request
    private static int nextDown;
    // holds the cylinder position the head moves to
    private static int moveToPosition;
    // keeps track of how many head movements have elapsed
    private static int headMovements;
    
    /**
     * This method is essentially the driver to the whole project.
     * It creates a string of disk requests and tests the efficiency 
     * of different scheduling algorithms.
     */
    public static void main(String[] args) {
        initialize();
        
        // run each algorithm on the string of disk requests
        fcfs();   // First-Come, First-Serve
        sstf();   // Shortest Seek-Time First
        scan();   // SCAN
        cscan();  // C-SCAN
        look();   // LOOK
        clook();  // C-LOOK
    }
    
    /**
     * This method sets up the initial data to be simulated by the disk scheduler.
     * Variables here can be scaled for testing purposes.
     */
    public static void initialize() {
        testSize = 1000;
        diskSize = 5000;
        initialPosition = 2400;
        
        // generate an array of 1000 random integers between 0 and 4999
        referenceString = new int[testSize];
        for (int i = 0; i < testSize; i++) {
            referenceString[i] = (int)(Math.random() * diskSize);
        }
    }
    
    /**
     * This method is used to clear out any leftover data from the previous 
     * algorithm simulation and to return values to their initial state.
     */
    private static void resetValues() {
        currentPosition = initialPosition;
        headMovements = 0;
        diskRequestQueue = new RequestQueue(referenceString, initialPosition, diskSize);
        
        nextUp = diskRequestQueue.nextUp();
        nextDown = diskRequestQueue.nextDown();
    }
    
    /**
     * This method runs through a simulation of the 
     * First-Come, First-Serve scheduling algorithm.
     */
    private static void fcfs() {
        resetValues();
        
        while (diskRequestQueue.hasMoreRequests()) {
            moveToPosition = diskRequestQueue.nextRequest();
            moveHead();
        }
        
        printResult("First-Come First-Serve");
    }
    
    /**
     * This method runs through a simulation of the 
     * Shortest Seek-Time First scheduling algorithm.
     */
    private static void sstf() {
        resetValues();
        while(diskRequestQueue.hasMoreRequests()) {
            // as long as we haven't hit the upper limit...
            // if the next higher request is closer or there are no more lower requests...
            if ((nextUp != diskSize) && (((nextUp - currentPosition) < (currentPosition - nextDown)) || (nextDown == -1))) {
                moveToPosition = nextUp;
                nextUp = diskRequestQueue.nextUp();
            }
            
            // if the next lower request is closer or there are no more higher requests...
            else {
                moveToPosition = nextDown;
                nextDown = diskRequestQueue.nextDown();
            }
            
            moveHead();
        }
        
        printResult("Shortest Seek-Time First");
    }
    
    /**
     * This method runs through a simulation of the 
     * SCAN scheduling algorithm.
     */
    private static void scan() {
        resetValues();
        while (diskRequestQueue.hasMoreRequests()) {
            // if there are more higher requests, 
            if (nextUp < diskSize - 1) {
                moveToPosition = nextUp;
                nextUp = diskRequestQueue.nextUp();
                moveHead();
                // if there are no more higher requests, go to the highest value and turn around
                if (nextUp == diskSize) {
                    moveToPosition = diskSize - 1;
                    moveHead();
                }
            }
            
            //
            else {
                moveToPosition = nextDown;
                nextDown = diskRequestQueue.nextDown();
                moveHead();
            }
        }
        
        printResult("SCAN");
    }
    
    /**
     * This method runs through a simulation of the 
     * C-SCAN scheduling algorithm.
     */
    private static void cscan() {
        resetValues();
        while (diskRequestQueue.hasMoreRequests()) {  
            // if there are more higher requests,
            if (nextUp < diskSize - 1) {
                moveToPosition = nextUp;
                nextUp = diskRequestQueue.next();
                moveHead();
            }
            // wrap through the rest of the higher cylinders and back to 0 and continue up
            else {
                moveToPosition = diskSize - 1;
                moveHead();
                currentPosition = 0;
                headMovements++;
                nextUp = diskRequestQueue.next();
            }
        }
        
        printResult("C-SCAN");
    }
    
    /**
     * This method runs through a simulation of the 
     * LOOK scheduling algorithm.
     */
    private static void look() {
        resetValues();
        
        while (diskRequestQueue.hasMoreRequests()) {
            // if there are more higher requests, 
            if (nextUp != diskSize) {
                moveToPosition = nextUp;
                nextUp = diskRequestQueue.nextUp();
                // if there are no more higher requests, turn around
                if (nextUp == diskSize) {
                    moveToPosition = nextDown;                    
                }
                moveHead();
            }
            // continue processing lower requests
            else {
                moveToPosition = nextDown;
                nextDown = diskRequestQueue.nextDown();
                moveHead();
            }
        }
        
        printResult("LOOK");
    }
    
    /**
     * This method runs through a simulation of the 
     * C-LOOK scheduling algorithm.
     */
    private static void clook() {
        resetValues();
        
        while (diskRequestQueue.hasMoreRequests()) {  
            // if there are more higher requests,
            if (nextUp < diskSize - 1) {
                moveToPosition = nextUp;
                nextUp = diskRequestQueue.next();
                moveHead();
            }
            // wrap through the rest of the higher cylinders and back to 0 and continue up
            else {
                moveToPosition = diskSize - 1;
                moveHead();
                currentPosition = 0;
                headMovements++;
                nextUp = diskRequestQueue.next();
            }
        }
        
        printResult("C-LOOK");
    }
    
    /** 
     * This method moves the disk head to the next request and increases the head movements count.
     */
    private static void moveHead() {
        headMovements += Math.abs(currentPosition - moveToPosition);
        currentPosition = moveToPosition;
    }
    
    /**
     * This method displays to the user the list of disk requests to be simulated.
     * @param refString - the list of disk requests to simulate
     */
    public static void printDiskRequestString() {
        System.out.print("\nDisk Request String: \n" + referenceString[0]);
        for (int i = 1; i < referenceString.length; i++) {
            System.out.print("," + referenceString[i]);
        }
        System.out.println("\n");
    } 
    
    /**
     * This method takes raw data of the number of head movements required
     * to service all disk requests with the given scheduling algorithm,
     * and displays it nicely for the user to compare algorithms.
     * @param name - the name of the algorithm that got the results
     */
    public static void printResult(String name) {
        System.out.println(name + " Scheduling Algorithm resulted in " 
                           + headMovements + " head movements.");
    }
}