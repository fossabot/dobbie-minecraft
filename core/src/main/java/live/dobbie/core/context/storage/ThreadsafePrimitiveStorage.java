package live.dobbie.core.context.storage;


import java.util.Hashtable;

public class ThreadsafePrimitiveStorage extends PrimitiveMap {
    public ThreadsafePrimitiveStorage() {
        super(new Hashtable<>());
    }
}
