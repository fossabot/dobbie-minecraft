package live.dobbie.core.script;

import lombok.Data;

@Data
public class ScriptResult<T> {
    private final T object;

    public boolean isNull() {
        return object == null;
    }
}
