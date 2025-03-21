/**
 * 被注释掉的race定义和race++不能保证线程安全，即使用valotile修饰也不行。因为race++翻译成机器指令后是多个操作，无法保证原子性
 */

public class AtomicTest {
    // public static int race = 0;
    public static AtomicInteger race = new AtomicInteger(0);

    public static void increase() {
        // race++;
        race.incrementAndGet();
    }

    private static final int THREADS_COUNT = 20;

    public static void main(String[] args) throws Exception {
        Thread[] threads = new Thread[THREADS_COUNT];

        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        increase();
                    }
                }
            });

            threads[i].start();
        }

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }

        System.out.println(race);
    }
}

/// // implementation of incrementAndGet
/**
 * Atomically increment by one the current value.
 * @return the updated value
 */
// public final int incrementAndGet() {
//     for (;;) {
//         int current = get();
//         int nexxt = current + 1;
//         if (compareAndSet(current, next)) {
//             return next;
//         }
//     }
// }