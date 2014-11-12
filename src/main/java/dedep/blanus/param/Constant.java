package dedep.blanus.param;

import java.util.HashSet;

public class Constant extends Parameter {
    public Constant(String value) {
        this.name = value;
        possibleValues = new HashSet<>();
        possibleValues.add(value);
    }
}
