package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Exercise 5 — Returning values from concurrent tasks with {@link Callable} and
 * {@link Future}.
 *
 * Scenario: split a list of integers across several workers, have each worker
 * compute the sum of its slice, then combine the partial results into a total.
 *
 * Your tasks:
 *
 * (A) Implement {@link #parallelSum(List, int)}.
 *     - Create a fixed thread pool with {@code workers} threads.
 *     - Divide {@code numbers} into {@code workers} roughly-equal slices.
 *     - Submit each slice as a {@code Callable<Long>} that returns the sum of
 *       that slice.
 *     - Collect all {@link Future<Long>} objects BEFORE calling {@code get()} on
 *       any of them (to keep the work concurrent).
 *     - After all futures are collected, call {@code get()} on each and add
 *       partial results to a running total.
 *     - Shut down the pool and return the total.
 *
 * Note: calling {@code get()} immediately after each {@code submit()} turns
 * concurrent work back into sequential work — avoid that pattern here.
 */
public class ParallelSumCalculator {

    /**
     * Computes the sum of {@code numbers} by splitting the work across
     * {@code workers} {@link Callable} tasks submitted to a thread pool.
     *
     * @param numbers list of values to sum
     * @param workers number of pool threads / partitions
     * @return the total sum
     */
    public long parallelSum(List<Integer> numbers, int workers)
            throws InterruptedException, ExecutionException {

        ExecutorService pool = Executors.newFixedThreadPool(workers);
        List<Future<Long>> futures = new ArrayList<>();

        int chunkSize = (numbers.size()+ workers -1)/workers;
        IntStream.iterate(0, start -> start < numbers.size(), start -> start + chunkSize)
                .forEach(start -> {
                    List<Integer> slice = numbers.subList(start, Math.min(start + chunkSize, numbers.size()));
                    futures.add(pool.submit(
                            () -> slice.stream().mapToLong(Integer::longValue).sum()
                    ));
                });

        long total = 0;
        for (Future<Long> f : futures) {
            long partial = f.get();
            total += partial;
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        return total;
    }
}
