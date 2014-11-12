package dedep.blanus;

import java.util.ArrayList;
import java.util.List;

public class Operator {
    private List<Condition> preconditions;
    private List<Condition> effects;
    private String name;

    public Operator(List<Condition> preconditions, List<Condition> effects, String name) {
        this.preconditions = preconditions;
        this.effects = effects;
        this.name = name;
    }

    public List<Condition> getPreconditions() {
        return new ArrayList<>(preconditions);
    }

    public List<Condition> getEffects() {
        return new ArrayList<>(effects);
    }

    public String getName() {
        return name;
    }
}
