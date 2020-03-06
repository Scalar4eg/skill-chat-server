import java.util.concurrent.atomic.AtomicLong;

class IdGenerator {
    private static final AtomicLong counter = new AtomicLong(10); // Easily overflowed in a long run
    static long getNextId() {
        return counter.incrementAndGet();
    }
}
