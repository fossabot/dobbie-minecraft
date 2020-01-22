package live.dobbie.core.misc.primitive.storage;


import java.util.Hashtable;

public class ThreadsafePrimitiveStorage extends PrimitiveMap {
    public ThreadsafePrimitiveStorage() {
        super(new Hashtable<>());
    }
}
