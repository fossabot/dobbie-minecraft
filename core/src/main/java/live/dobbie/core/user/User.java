package live.dobbie.core.user;

import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import lombok.NonNull;


public interface User {
    @NonNull String getName();

    void sendMessage(@NonNull String message);

    void sendErrorMessage(@NonNull String message);

    void disconnect(@NonNull String message);

    default void disconnectLoc(@NonNull LocString ls) {
        disconnect(ls.build());
    }

    default void sendLocMessage(@NonNull LocString ls) {
        sendMessage(ls.build());
    }

    default void sendErrorLocMessage(@NonNull LocString ls) {
        sendErrorMessage(ls.build());
    }

    interface Factory<N> {
        @NonNull User create(@NonNull N nativeUser);
    }

    class Name implements PrimitiveConverter<User, StringPrimitive> {
        @NonNull
        @Override
        public StringPrimitive parse(@NonNull User value) {
            return new StringPrimitive(value.getName());
        }
    }
}
