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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
