package live.dobbie.core.script.js.converter;


public interface FromJSConverter {
    <V> V fromJs(Object object, Class<V> expectedType);
}
