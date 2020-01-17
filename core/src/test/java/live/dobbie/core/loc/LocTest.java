package live.dobbie.core.loc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocTest {

    @Test
    void basicTest() {
        Loc loc = new Loc();
        assertEquals("hello, world!", loc.withKey("hello, world!").build());
    }

    @Test
    void icuPluralTest() {
        Loc loc = new Loc();
        assertEquals("You have one item.", loc.withKey("You have {itemCount, plural,\n" +
                "    =0 {no items}\n" +
                "    one {one item}\n" +
                "    other {{itemCount} items}\n" +
                "}.").set("itemCount", 1).build());
    }

    @Test
    void icuGenderTest() {
        Loc loc = new Loc();
        assertEquals("Kirti est allée à Paris.", loc.withKey("{a} est " +
                "{a_gender, select, female {allée} other {allé}} à Paris.")
                .set("a", "Kirti")
                .set("a_gender", Gender.FEMALE)
                .build());
    }

}