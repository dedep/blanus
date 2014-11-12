package dedep.blanus.param;

import java.util.Arrays;
import java.util.HashSet;

public class Variable extends Parameter {

    public Variable(String name, String... domain) {

        if (name.startsWith("$")) {
            this.name = name;
        } else {
            this.name = "$".concat(name);
        }

        possibleValues = new HashSet<>();
        possibleValues.addAll(Arrays.asList(domain));
    }
}
