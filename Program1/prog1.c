/*************************************************
 * ICS 462-1 Operating Systems
 * Homework Problem 1
 * 
 * prog1.c
 * Purpose: Creates child processes and runs multiple child 
 *        processes at the same time.
 * 
 * @author Chelsea Hanson
 * @version 1.0 9/27/16
 *
 ************************************************/

#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

/**
    Splits a process into multiple processes.
*/
void forkProcess();

/** 
    Contains the loop to run each child process.
    @param child - the number of the child to be run.
*/
void specialProcess(int child);

// Controls operation of the program.
int main() {
    int pid;
    
    // create child process
    pid = fork();
    
    // checks for an error in forking
    if (pid < 0) {
        fprintf(stderr, "A fork has failed");
        return 1;
    }
    
    // runs the fork 
    else if (pid == 0) {
        forkProcess();
    }
    
    // runs the original
    else {
        wait(1);
        printf("Child Processes Complete\n");
    }
    
    return 0;
}

// Splits a process into multiple processes.
void forkProcess() {
    int pid;
    
    // splitting the child process into 2
    pid = fork();
    
    // checks for an error forking child
    if (pid < 0) {
        fprintf(stderr, "A fork has failed");
        exit(1);
    }
    
    // runs fork of child process
    else if (pid == 0) {
        specialProcess(2);
    }
    
    // runs original child process
    else {
        specialProcess(1);
    }
}

// Contains the loop to run each child process.
void specialProcess(int child) {
    int i;
    for (i = 0; i < 10; i++){
        sleep(1);
        printf("Child Process %d: Run %d\n", child, i+1);
    }
}