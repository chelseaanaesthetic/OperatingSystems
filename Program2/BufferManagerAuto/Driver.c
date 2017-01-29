/*************************************************
 * ICS 462-1 Operating Systems
 * Homework Problem 2
 * 
 * Driver.c
 * Purpose: Tests the functionality of the buffer manager
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

long * requestBuffer(int);

int main() {
    long *buffer;
    long *rentedBuffer[10];

    debug();

    buffer = requestBuffer(700);

    buffer = requestBuffer(7);

    debug();

    returnBuffer(buffer);

    debug();

    // request the max amount of largest buffers
    int i;
    for (i = 0; i < 10; i++) {
        buffer = requestBuffer(511);
        rentedBuffer[i] = buffer;
        debug();
    }
    
    buffer = requestBuffer(7);

    // return the buffers from the previous for loop
    for (i = 9; i >= 0; i--) {
        returnBuffer(rentedBuffer[i]);
        debug();
    }

    i = 0;
    rentedBuffer[i++] = requestBuffer(73);
    rentedBuffer[i++] = requestBuffer(12);
    rentedBuffer[i++] = requestBuffer(4);
    rentedBuffer[i++] = requestBuffer(14);
    rentedBuffer[i++] = requestBuffer(4);
    rentedBuffer[i++] = requestBuffer(510);
    rentedBuffer[i++] = requestBuffer(77);
    rentedBuffer[i++] = requestBuffer(4);
    rentedBuffer[i++] = requestBuffer(14);
    rentedBuffer[i++] = requestBuffer(7);
    
    for (i = 9; i >= 0; i--) {
        returnBuffer(rentedBuffer[i]);
        debug(); 
    }
    
    return 1;
}

// gets a buffer but checks for error message return values
long * requestBuffer(int size) {
    long *temp = getBuffer(size);
    if (*temp == -2) {
        printf("\nError: Illegal Request!\n");
        return NULL;
    }
    else if (*temp == -1) {
        printf("\nError: Not Enough Space!\n");
        return NULL;
    }

    return temp;
}