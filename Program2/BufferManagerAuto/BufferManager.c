/*************************************************
 * ICS 462-1 Operating Systems
 * Homework Problem 2
 * 
 * BufferManager.c
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

#include "BufferManager.h"

// 0 for false
// 1 for true
int initialized = 0;
int tight = 0;

// array for the buffer pool that all buffers come from
long masterBuffer[5120];
// provides error values for returning pointers to
long errors[2];
// tracks how many available buffers of each size
int count[7];
    
// function declarations    
long * getBuffer(int request);
void returnBuffer(long *address);
void checkStatus();
void debug();

//helper functions
void initialize();
int nextSizeUp(int size);
int indexof(int size);
int findIndex(long *ptr);

// builds the master array that all the buffers come from
void initialize() {
    int i;
    for (i = 0; i < 10; i++) {
        masterBuffer[512 * i] = 512;
    }
    count[0] = 10;
    initialized = 1;
    errors[0] = -1;
    errors[1] = -2;
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

// finds the array index of the given pointer
int findIndex(long *ptr) {
    int index;

    for (index = 1; index < 5120; index += 8) {
        if (&masterBuffer[index] == ptr) {
            return index - 1;
        }
    }

    return -1;
}

// allocates a buffer of the requested size
// Splits larger buffers if needed
long * getBuffer(int request) {
    if (initialized == 0) {
        initialize();
    }

    if (request > 511) {
        // illegal request
        return &errors[1];
    }
    
    int i, j, size;

    size = nextSizeUp(request);
    printf("\nAllocating a buffer of size: %d\n", size - 1);


    for (j = size; j <= 512; j = j * 2) {
        // Searching for size j
        for (i = 0; i < 5120; i += j) {
            if (masterBuffer[i] == size ) {
                masterBuffer[i] = size - 1;
                count[indexof(size)] -= 1;
                checkStatus();
                printf("New buffer created at address %p\n", (void*) &masterBuffer[i + 1]);
                return &masterBuffer[i + 1];
            }
            if (masterBuffer[i] == j) {
                while (j > size) {
                    // Splitting j
                    // First Buddy
                    masterBuffer[i] = j/2;
                    count[indexof(j)] -= 1;
                    // Second Buddy
                    masterBuffer[i + j/2] = j/2;
                    count[indexof(j) + 1] += 2;
                    j = j / 2;
                }
                masterBuffer[i] = size - 1;
                count[indexof(size)] -= 1;
                checkStatus();
                printf("New buffer created at address %p\n", (void*) &masterBuffer[i + 1]);
                return &masterBuffer[i + 1];
            }

        }
    }
 
    // No buffer of that size is available
    return &errors[0];
}

// Takes an address for a buffer and returns it to the pool
// Merges up to provide larger buffer sizes
void returnBuffer(long *address) {
    int index;
    int size;
    int countIndex;

    index = findIndex(address);

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
            if (masterBuffer[index+size] == size) {
                // Merge buffers
                masterBuffer[index+size] = 0;
                count[countIndex] -= 2;
                masterBuffer[index] = (size * 2);
                count[countIndex-1] += 1;
            }
            else {
                // Buffers not merged!
                return;
            }
        }

        // if returned index is second buddy
        else if ( (index) % (size * 2) == size ) {
            // and the first buddy is same size and free
            if (masterBuffer[index-size] == size) {
                // Merge buffers
                masterBuffer[index] = 0;
                count[countIndex] -= 2;
                masterBuffer[index-size] = (size * 2);
                count[countIndex-1] += 1;

                index -= size;
            }
            else {
                // Buffers not merged!
                return;
            }
        }
        size *= 2;
    }
    checkStatus();
}

// If the buffer manager has been initialized, it checks if space is tight
void checkStatus() {
    if (initialized == 0) {
        printf("\nThe buffer manager hasn't been initialized, yet.\n");
    }
    else if (count[0] < 2) {
        tight = 1;
        printf("The buffer manager is getting tight!\n");
    }
    else if (count[0] > 1) {
        tight = 0;
    }
}

// Determines how many buffers of each size are available and prints the results
void debug() {
    if (initialized == 0) {
        printf("\nThe buffer manager hasn't been initialized, yet.\n");
    }
    else {
        printf("\n");
        if (tight == 0) {
            printf("not ");
        }
        printf("tight: %d 511 word buffers, %d 255 word buffers, %d 127 word buffers, \n%d 63 word buffers, %d 31 word buffers, %d 15 word buffers, and %d 7 word buffers\n", 
                count[0], count[1], count[2], count[3], count[4], count[5], count[6]);
    }
    return;
}