package live.dobbie.core.util.io.mod;

import live.dobbie.core.util.io.URLSupplier;

/**
 * This signal is used when we can't say if source was changed or not.
 *
 * @see URLSupplier#getModSignal()
 */
public class UnknownModSignal implements ModSignal {
    // I'm not going to break equals() and hashCode() functionality,
    // because users required to create new instances at every call,
    // so no two instances will equal anyway
}
