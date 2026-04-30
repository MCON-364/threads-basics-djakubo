package edu.touro.mcon364.concurrency.lesson1.exercises;

/**
 * Exercise 1:
 * Make this class thread-safe so that concurrent increments do not lose updates.
 */
public class ThreadSafeTaskCounter {

    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
