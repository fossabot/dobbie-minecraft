package live.dobbie.core.settings.source.jackson;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface JacksonParseable {
    String[] value();
}
