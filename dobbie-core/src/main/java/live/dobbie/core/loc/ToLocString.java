package live.dobbie.core.loc;

import lombok.NonNull;

public interface ToLocString {
    @NonNull LocString toLocString(@NonNull Loc loc);
}
