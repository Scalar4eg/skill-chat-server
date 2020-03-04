import java.util.concurrent.atomic.AtomicInteger;

class IdGenerator {
    private static final AtomicInteger counter = new AtomicInteger(10); // Easily overflowed in a long run
    static int getNextId() {
        return counter.incrementAndGet();
    }
}
