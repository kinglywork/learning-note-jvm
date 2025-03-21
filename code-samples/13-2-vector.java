// although the get, add, remove, size is thread safe (with sychronized block), it's not thread safe in below code,
// as the code including size and remove or code including size and get are not atomic, concurrent running will lead to unexpected result.
private static Vector<Integer> vector = new Vector<Integer>();

public static void main(String[] args) {
    while (true) {
        for (int i = 0; i < 10; i++) {
            vector.add(i);
        }

        Thread removeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < vector.size(); i++) {
                    vector.remove(i);
                }
            }
        });

        Thread printThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < vector.size(); i++) {
                    System.out.println(vector.get(i));
                }
            }
        });

        removeThread.start();
        printThread.start();

        while (Thread.activeCount() > 20);
    }
}