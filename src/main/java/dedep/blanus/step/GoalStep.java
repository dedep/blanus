package dedep.blanus.step;

import dedep.blanus.condition.Condition;

import java.util.Collections;
import java.util.List;

public class GoalStep extends Step {
    public GoalStep(List<Condition> preconditions, String name, int id) {
        super(preconditions, Collections.<Condition>emptyList(), name, id);
    }
}
