package live.dobbie.core.misc.primitive.compcond;

import live.dobbie.core.context.value.VarCmpCondType;
import org.junit.jupiter.api.Test;

import static live.dobbie.core.misc.primitive.Primitive.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarCmpCondTypeTest {

    @Test
    void lessTest() {
        assertFalse(VarCmpCondType.LESS.satisfies(of(1), of(1)));
        assertFalse(VarCmpCondType.LESS.satisfies(of(1), of(0)));
        assertTrue(VarCmpCondType.LESS.satisfies(of(0), of(1)));
    }

    @Test
    void lessOrEqualTest() {
        assertTrue(VarCmpCondType.LESS_OR_EQUAL.satisfies(of(1), of(1)));
        assertFalse(VarCmpCondType.LESS_OR_EQUAL.satisfies(of(1), of(0)));
        assertTrue(VarCmpCondType.LESS_OR_EQUAL.satisfies(of(0), of(1)));
    }

    @Test
    void equalTest() {
        assertTrue(VarCmpCondType.EQUAL.satisfies(of(1), of(1)));
        assertFalse(VarCmpCondType.EQUAL.satisfies(of(1), of(0)));
        assertFalse(VarCmpCondType.EQUAL.satisfies(of(0), of(1)));
    }

    @Test
    void greaterOrEqualTest() {
        assertTrue(VarCmpCondType.GREATER_OR_EQUAL.satisfies(of(1), of(1)));
        assertTrue(VarCmpCondType.GREATER_OR_EQUAL.satisfies(of(1), of(0)));
        assertFalse(VarCmpCondType.GREATER_OR_EQUAL.satisfies(of(0), of(1)));
    }

    @Test
    void greaterTest() {
        assertFalse(VarCmpCondType.GREATER.satisfies(of(1), of(1)));
        assertTrue(VarCmpCondType.GREATER.satisfies(of(1), of(0)));
        assertFalse(VarCmpCondType.GREATER.satisfies(of(0), of(1)));
    }

}