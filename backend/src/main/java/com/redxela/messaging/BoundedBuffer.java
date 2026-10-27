package com.redxela.messaging;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Bounded buffer for the producer-consumer problem, built from the classic
 * three primitives: a counting semaphore for free positions, another for
 * filled positions, and a mutex around the queue itself. Messages come out in
 * the order they went in (FIFO), a producer that runs ahead of the consumer
 * sleeps on the free-positions semaphore instead of losing data or spinning,
 * and a consumer with nothing to read sleeps on the filled-positions one.
 */
public class BoundedBuffer<T> {

    private final int capacity;
    private final Queue<T> items = new ArrayDeque<>();
    private final Semaphore freePositions;
    private final Semaphore filledPositions = new Semaphore(0, true);
    private final ReentrantLock mutex = new ReentrantLock();

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.freePositions = new Semaphore(capacity, true);
    }

    public void put(T item) throws InterruptedException {
        freePositions.acquire();
        mutex.lock();
        try {
            items.add(item);
        } finally {
            mutex.unlock();
        }
        filledPositions.release();
    }

    public T take() throws InterruptedException {
        filledPositions.acquire();
        T item;
        mutex.lock();
        try {
            item = items.remove();
        } finally {
            mutex.unlock();
        }
        freePositions.release();
        return item;
    }

    public int capacity() {
        return capacity;
    }

    public List<T> contents() {
        mutex.lock();
        try {
            return List.copyOf(items);
        } finally {
            mutex.unlock();
        }
    }
}