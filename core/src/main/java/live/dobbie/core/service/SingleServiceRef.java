package live.dobbie.core.service;

import lombok.NonNull;

public class SingleServiceRef<S extends Service> extends AbstractServiceRef<S> {

    public SingleServiceRef(@NonNull String name, @NonNull S service, @NonNull ServiceRefProvider provider) {
        super(name, provider);
        fireUpdate(service, null);
    }
}
