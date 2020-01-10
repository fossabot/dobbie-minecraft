package live.dobbie.core.script.js;

import live.dobbie.core.script.Script;
import lombok.*;

@Value
@EqualsAndHashCode(of = "sourceObject")
public class JSScript implements Script<JSScriptContext> {
    @NonNull Object sourceObject;
    @NonNull
    @Getter(value = AccessLevel.PACKAGE)
    org.mozilla.javascript.Script script;
}
