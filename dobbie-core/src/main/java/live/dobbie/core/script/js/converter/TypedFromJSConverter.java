package live.dobbie.core.script.js.converter;


public interface TypedFromJSConverter<V> {
    V typedFromJs(Object object);
}
