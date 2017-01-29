/*************************************************
 * ICS 462-1 Operating Systems
 * Homework Problem 2
 * 
 * Program2.c
 * Purpose: Creates and manages a buffer pool.
 * 
 * @author Chelsea Hanson
 * @version 1.0 11/22/16
 *
 ************************************************/

#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

// 0 for false
// 1 for true
int initialized = 0;
int tight = 0;

// array for the buffer pool that all buffers come from
long masterBuffer[5120];
// tracks how many available buffers of each size
int count[7];
    
// function declarations    
int getBuffer(int size);
void returnBuffer(int index);
void checkStatus();
void debug();

//helper functions
void printMenu(); 
void initialize();
int nextSizeUp(int size);
int indexof(int size);
    
// runs the entire Command Line UI    
int main() {
    
    int menuChoice = 0;
    int bufferSize;
    int index;
    
    while (1 == 1) {
        printMenu();
        // get user choice
        scanf( "%d", &menuChoice );

        switch (menuChoice) {
            case 0:
                return 0;
                break;
            case 1:
                // get requested buffer size
                printf( "\nEnter the desired buffer size: " );
                scanf( "%d", &bufferSize );
            
                while ( bufferSize > 511 ) {
                    printf ( "That size is too big. Choose a size from 7 to 511\n" );
                    scanf( "%d", &bufferSize );
                }
                    
                index = getBuffer(bufferSize);
                printf ("\nNew Buffer Index: %d\n", index);
                break;
            case 2:
                // get returning index
                printf("Enter the index to be returned: ");
                scanf( "%d", &index);
                returnBuffer(index);
                break;
            case 3:
                checkStatus();
                if ( tight == 1) {
                    printf("Tight\n");
                }
                else
                    printf("Not Tight\n");
                break;
            case 4:
                debug();
                break;
            default:
                // print error message & loop again
                printf("That is not a valid menu option! \n");
                break;
        }
    }
    
    return 0;
}

// Displays the menu choices for interacting with the buffer
void printMenu() {
    printf("\n\nBuffer Manager:\n 0. Quit \n 1. Request Buffer \n 2. Return Buffer \n 3. Status \n 4. Debug \n\nChoose an option from the menu and press enter: ");
}

// builds the master array that all the buffers come from
void initialize() {
    int i;
    for (i = 0; i < 10; i++) {
        masterBuffer[512 * i] = 512;
    }
    count[0] = 10;
    initialized = 1;
}

// gets the index of the tracker for the given size buffer
int indexof(int size) {
    switch (size) {
        case 512:
            return 0;
        case 256:
            return 1;
        case 128:
            return 2;
        case 64:
            return 3;
        case 32:
            return 4;
        case 16:
            return 5;
        case 8:
            return 6;
        default:
            return -1;
    }
}

// determines the buffer size to create based on request size
int nextSizeUp(int size) {
    if (size > 511 || size < 0) {
        return -1;
    }
    else if (size > 255) {
        return 512;
    }
    
    else if (size > 127) {
        return 256;
    }
    
    else if (size > 63) {
        return 128;
    }
    
    else if (size > 31) {
        return 64;
    }
    
    else if (size > 15) {
        return 32;
    }
    
    else if (size > 7) {
        return 16;
    }
    
    else {
        return 8;
    }
}

// allocates a buffer of the requested size
int getBuffer(int request) {
    if (initialized == 0) {
        initialize();
    }

    if (request > 511) {
        // illegal request
        return -2;
    }
    
    int i, j, size;

    size = nextSizeUp(request);
    printf("\nAllocating a buffer of size: %d\n", size);

    // Search for the next available buffer to be used or split
    for (j = size; j <= 512; j = j * 2) {
        printf("\nSearching for size %d...\n", j);
        for (i = 0; i < 5120; i += j) {
            if (masterBuffer[i] == size ) {
                masterBuffer[i] = size - 1;
                count[indexof(size)] -= 1;
                checkStatus();
                return i;
            }
            if (masterBuffer[i] == j) {
                while (j > size) {
                    // Splitting a buffer into 2
                    printf("\nSplitting %d \n", j);
                    masterBuffer[i] = j/2;
                    // First Buddy
                    printf("Index: %d is now size %d.\n", i, j/2);
                    count[indexof(j)] -= 1;
                    // Second Buddy
                    masterBuffer[i + j/2] = j/2;
                    printf("Index: %d is now size %d.\n", (i + j)/2, j/2);
                    count[indexof(j) + 1] += 2;
                    j = j / 2;
                }
                masterBuffer[i] = size - 1;
                count[indexof(size)] -= 1;
                checkStatus();
                return i;
            }

        }
    }
 
    printf("\nNo buffer of that size is available: Returning -1\n");
    return -1;
}

// returns a buffer to the buffer pool
void returnBuffer(int index) {
    int size;
    int countIndex;

    size = masterBuffer[index];

    if (size != 511 && size != 255 && size != 127 && size != 63 && size != 31 && size != 15 && size != 7) {
        printf("Index not recognized. Buffer not returned.\n");
        return;
    }

    else if (size == 512 || size == 256 || size == 128 || size == 64 || size == 32 || size == 16 || size == 8) {
        printf("This index has already been returned.\n");
        return;
    }

    else {
        masterBuffer[index] += 1;
        size = masterBuffer[index];
        countIndex = indexof(size);
        count[countIndex] += 1;
    }
    
    while (size < 512) {
        countIndex = indexof(size);
        // if returned index is first buddy
        if ( (index) % (size * 2) == 0 ) {
            // and the second buddy is same size and free
            printf("\nComparing index %d with index %d...", index, (index + size));
            if (masterBuffer[index+size] == size) {
                printf("\nBuffers Merged!\n");
                // merge buffers
                masterBuffer[index+size] = 0;
                count[countIndex] -= 2;
                masterBuffer[index] = (size * 2);
                count[countIndex-1] += 1;
            }
            else {
                printf("\nBuffers Not Merged!");
                return;
            }
        }

        // if returned index is second buddy
        else if ( (index) % (size * 2) == size ) {
            // and the first buddy is same size and free
            printf("\nComparing index %d with index %d...", index, (index - size));
            if (masterBuffer[index-size] == size) {
                // merge buffers
                printf("\nBuffers Merged!\n");
                masterBuffer[index] = 0;
                count[countIndex] -= 2;
                masterBuffer[index-size] = (size * 2);
                count[countIndex-1] += 1;

                index -= size;
            }
            else {
                printf("\nBuffers Not Merged!");
                return;
            }
        }
        size *= 2;
    }
    checkStatus();
}

// check if space is tight, provided the buffer manager has been initialized
void checkStatus() {
    if (initialized == 0) {
        printf("The buffer manager hasn't been initialized, yet.\n\n");
    }
    else if (count[0] < 2) {
        tight = 1;
        printf("\nThe buffer manager is getting tight!\n");
    }
}

// determines how many buffers of each size are abailable and prints the results
void debug() {
    if (initialized == 0) {
        printf("The buffer manager hasn't been initialized, yet.\n\n");
    }
    else {
        printf("\n\n");
        if (tight == 0) {
            printf("not ");
        }
        printf("tight: %d 511 word buffers, %d 255 word buffers, %d 127 word buffers, \n%d 63 word buffers, %d 31 word buffers, %d 15 word buffers, and %d 7 word buffers\n", 
                count[0], count[1], count[2], count[3], count[4], count[5], count[6]);
    }
    return;
}