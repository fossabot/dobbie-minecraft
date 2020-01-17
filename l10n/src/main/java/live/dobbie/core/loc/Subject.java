package live.dobbie.core.loc;

import lombok.NonNull;

public interface Subject {
    @NonNull String getName();

    @NonNull Gender getGender();
}
