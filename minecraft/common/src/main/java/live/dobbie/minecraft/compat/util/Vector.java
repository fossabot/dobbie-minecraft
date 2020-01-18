package live.dobbie.minecraft.compat.util;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class Vector {
    private double x, y, z;

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector multiply(@NonNull Vector vector) {
        return setX(getX() * vector.getX()).setY(getY() * vector.getY()).setZ(getZ() * vector.getZ());
    }

    public Vector multiply(double x, double y, double z) {
        return multiply(new Vector(x, y, z));
    }

    public Vector copy() {
        return new Vector(getX(), getY(), getZ());
    }

    public static Vector unit() {
        return new Vector(1., 1., 1.);
    }
}
