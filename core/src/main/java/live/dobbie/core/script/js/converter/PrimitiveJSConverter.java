package live.dobbie.core.script.js.converter;

import live.dobbie.core.misc.primitive.DateTimePrimitive;
import live.dobbie.core.misc.primitive.Primitive;
import lombok.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class PrimitiveJSConverter extends TypedJSValueConverter<Primitive> {
    private final @NonNull JSValueConverter delegateConverter;

    public PrimitiveJSConverter(@NonNull JSValueConverter delegateConverter) {
        super(Primitive.class);
        this.delegateConverter = delegateConverter;
    }

    @Override

    public Primitive typedFromJs(Object object) {
        return Primitive.of(delegateConverter.fromJs(object, Object.class));
    }

    @Override
    public Object typedToJs(Primitive object, @NonNull Scriptable scope, @NonNull Context context) {
        if (object instanceof DateTimePrimitive) {
            long millis = ((DateTimePrimitive) object).getValue().toEpochMilli();
            Function dateFunction = (Function) ScriptableObject.getProperty(scope, "Date");
            return dateFunction.construct(context, scope, new Object[]{
                    delegateConverter.toJs(millis, scope, context)
            });
        }
        return delegateConverter.toJs(Primitive.toObject(object), scope, context);
    }
}
