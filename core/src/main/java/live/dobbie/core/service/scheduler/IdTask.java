package live.dobbie.core.service.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class IdTask {
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (name == null) return super.equals(o);
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdTask idTask = (IdTask) o;
        return Objects.equals(name, idTask.name);
    }

    @Override
    public int hashCode() {
        return name == null ? super.hashCode() : Objects.hash(name);
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : "IdTask{name=" + name + "}";
    }

    public static IdTask unique() {
        return new IdTask(null);
    }

    public static IdTask name(@NonNull String name) {
        return new IdTask(name);
    }
}
