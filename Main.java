package com.company;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static int finalSum = 0;

    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    public static void main(String[] args) throws Exception {
        int arrSize, numTreads;
        int[] data;

        System.out.println("Enter number of the array elements: ");
        arrSize = scanner.nextInt();
        System.out.println("Enter number of threads: ");
        numTreads = scanner.nextInt();

        data = new int[arrSize];

        for (int i = 0; i < arrSize; i++) {
            data[i] = random.nextInt(10) + 1;
            System.out.println(i + ". " + data[i]);
        }
        getSumInThreads(arrSize, numTreads, data);

        System.out.println("The End.");
    }

    static void getSumInThreads(int arrSize, int numTreads, int[] data) {
        int sum = 0;
        Lock mutex = new ReentrantLock();
        Thread[] threads = new Thread[numTreads];
        int numElementsInArr = arrSize / numTreads;
        int rem = arrSize % numTreads;
        SumThread[] arrSum = new SumThread[numElementsInArr];

        for (int i = 0; i < numTreads; i++) {
            int start = i * numElementsInArr;
            int end = start + numElementsInArr;
            SumThread sumThread = new SumThread(data, start, end, mutex);
            arrSum[i] = sumThread;
            threads[i] = new Thread(arrSum[i]);
            System.out.println(threads[i].getId() + " запущен");
            threads[i].start();
        }
        for (int i = 0; i < rem; i++) {
            int index = arrSize - 1 - i;
            sum = sum + data[index];
        }
        for (int i = 0; i < numTreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finalSum = finalSum + sum;
        System.out.println("sum = " + finalSum);
    }
}

class SumThread implements Runnable {
    private int[] data;
    private int start, end, sum;
    private Lock mutex;


    public SumThread(int[] data, int start, int end, Lock mutex) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.mutex = mutex;
        this.sum = 0;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            sum = sum + data[i];
        }
        mutex.lock();
        try {
            Main.finalSum = Main.finalSum + sum;
            System.out.println(Thread.currentThread().getId() + ": my sum " + sum);
        } finally {
            mutex.unlock();
        }
    }

}
