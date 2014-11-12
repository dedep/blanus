package dedep.blanus.param;

import java.util.Collections;
import java.util.Set;

public abstract class Parameter {
    protected String name;
    protected Set<String> possibleValues;

    public Set<String> getPossibleValues() {
        return Collections.unmodifiableSet(possibleValues);
    }

    public String getName() {
        return name;
    }
}
