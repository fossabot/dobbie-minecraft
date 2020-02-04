package live.dobbie.core.trigger.authored;

import live.dobbie.core.loc.Subject;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import lombok.NonNull;


public interface Author extends Subject {
    @NonNull String getName();

    class NameConverter implements PrimitiveConverter<Author, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull Author value) {
            return new StringPrimitive(value.getName());
        }
    }
}
