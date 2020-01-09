package live.dobbie.core.trigger.authored;

import live.dobbie.core.context.primitive.StringPrimitive;
import live.dobbie.core.context.primitive.converter.PrimitiveConverter;
import lombok.NonNull;


public interface Author {
    @NonNull String getName();

    @NonNull
    default String getDisplayName() {
        return getName();
    }

    class NameConverter implements PrimitiveConverter<Author, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull Author value) {
            return new StringPrimitive(value.getName());
        }
    }

    class DisplayNameConverter implements PrimitiveConverter<Author, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull Author value) {
            return new StringPrimitive(value.getDisplayName());
        }
    }
}
