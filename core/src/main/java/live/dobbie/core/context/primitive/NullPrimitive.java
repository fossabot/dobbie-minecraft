package live.dobbie.core.context.primitive;

public class NullPrimitive implements Primitive {
    public static final NullPrimitive INSTANCE = new NullPrimitive();

    @Override
    public Object getValue() {
        return null;
    }
}
