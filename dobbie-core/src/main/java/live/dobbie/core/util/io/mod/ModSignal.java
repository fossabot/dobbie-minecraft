package live.dobbie.core.util.io.mod;

/**
 * ModSignal is used to indicate if the same source was changed during some time.
 * Intended use case:<br/>
 * <ol>
 * <li> Request {@link ModSignal} from one source and store it somewhere<br/>
 * <li> We need to know if source was changed, just request {@link ModSignal} again from the same source<br/>
 * <li> Compare these two using {@link #equals(Object)}
 * </ol>
 * Two {@link ModSignal}s from two different sources must be equal if they point to the same content.
 */
public interface ModSignal {
}
