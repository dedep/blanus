package dedep.blanus;

import java.util.Optional;

public class Subgoal {
    private Optional<Condition> condition;
    private Step step;

    public Subgoal(Step step, Optional<Condition> condition) {
        this.condition = condition;
        this.step = step;
    }

    public Subgoal(Step step) {
        this(step, Optional.empty());
    }

    public Optional<Condition> getCondition() {
        return condition;
    }

    public Step getStep() {
        return step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subgoal subgoal = (Subgoal) o;

        if (condition != null ? !condition.equals(subgoal.condition) : subgoal.condition != null) return false;
        if (step != null ? !step.equals(subgoal.step) : subgoal.step != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + (step != null ? step.hashCode() : 0);
        return result;
    }
}
