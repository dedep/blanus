package dedep.blanus;

import java.util.List;

public class Step extends Operator {
    private int id;

    public Step(List<Condition> preconditions, List<Condition> effects, String name, int id) {
        super(preconditions, effects, name);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
