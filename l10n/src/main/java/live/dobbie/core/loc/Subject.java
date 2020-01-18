package live.dobbie.core.loc;

import lombok.NonNull;

/**
 * Human/living subject that can be referred to in the translations.
 */
public interface Subject {
    @NonNull String getName();

    @NonNull Gender getGender();
}
