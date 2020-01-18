package live.dobbie.core.loc;

/**
 * A subject's gender as described in <a href="http://site.icu-project.org/design/formatting/select">ICU docs</a>
 */
public enum Gender {
    MALE,
    FEMALE,
    MIXED,
    UNKNOWN;

    /**
     * A key in ICU pattern, e.g. {@code "{*key*, select, female {allée} other {allé}}"}
     */
    public String formatValue() {
        return name().toLowerCase();
    }
}
