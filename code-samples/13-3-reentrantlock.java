/**
 * ReentrantLock: 
 *  1. 等待可中断
 *  2. 公平锁
 *  3. 可绑定多个条件
 * 本代码示例了绑定多个条件的特色
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerExample {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition(); // 缓冲区不满时的条件
    private final Condition notEmpty = lock.newCondition(); // 缓冲区不空时的条件

    public ProducerConsumerExample(int capacity) {
        this.capacity = capacity;
    }

    // 生产者添加元素
    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) { // 如果缓冲区满了，生产者等待
                notFull.await(); // 等待缓冲区有空间
            }
            queue.add(value); // 添加元素
            System.out.println("Produced: " + value + ", Queue size: " + queue.size());
            notEmpty.signal(); // 通知消费者缓冲区不为空
        } finally {
            lock.unlock();
        }
    }

    // 消费者取出元素
    public int get() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) { // 如果缓冲区为空，消费者等待
                notEmpty.await(); // 等待缓冲区有元素
            }
            int value = queue.poll(); // 取出元素
            System.out.println("Consumed: " + value + ", Queue size: " + queue.size());
            notFull.signal(); // 通知生产者缓冲区有空间
            return value;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ProducerConsumerExample example = new ProducerConsumerExample(5);

        // 创建生产者线程
        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    example.put(i);
                    Thread.sleep((int) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 创建消费者线程
        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    example.get();
                    Thread.sleep((int) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}