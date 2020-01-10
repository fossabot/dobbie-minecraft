package live.dobbie.core.util;

public interface Cleanable {
    void cleanup();

    static void cleanupAll(Cleanable... cl) {
        for (Cleanable cleanable : cl) {
            if (cleanable != null) {
                cleanable.cleanup();
            }
        }
    }
}
