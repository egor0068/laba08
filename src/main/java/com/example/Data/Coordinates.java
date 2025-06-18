package Data;

import Utilite.*;
import java.util.*;

public class Coordinates {

    private Integer x;
    private long y;

    public Coordinates(Integer x, long y) {
        if (x == null || x <= -419) {
            throw new IllegalArgumentException("Координата x должна быть больше -419 и не может быть null.");
        }
        if (y <= -942) {
            throw new IllegalArgumentException("Координата y должна быть больше -942.");
        }
        this.x = x;
        this.y = y;
    }

    public void setX(Integer x) {
        if (x == null || x <= -419) {
            throw new IllegalArgumentException("Координата x должна быть больше -419 и не может быть null.");
        }
        this.x = x;
    }

    public Integer getX() {
        return x;
    }

    public void setY(long y) {
        if (y <= -942) {
            throw new IllegalArgumentException("Координата y должна быть больше -942.");
        }
        this.y = y;
    }

    public long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
