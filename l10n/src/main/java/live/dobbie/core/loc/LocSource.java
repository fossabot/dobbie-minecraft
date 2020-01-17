package live.dobbie.core.loc;

import lombok.NonNull;


public interface LocSource {
    String getTranslation(@NonNull String key);
}
