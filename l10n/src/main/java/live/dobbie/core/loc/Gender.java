package live.dobbie.core.loc;

// As described in http://site.icu-project.org/design/formatting/select
public enum Gender {
    MALE,
    FEMALE,
    MIXED,
    UNKNOWN;

    // key to use in MessageFormat, e.g.
    // "{*key*, select, female {allée} other {allé}}"
    public String formatValue() {
        return name().toLowerCase();
    }
}
