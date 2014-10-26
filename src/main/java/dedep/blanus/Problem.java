package dedep.blanus;

import java.util.List;

public class Problem {
    private List<MovementOperator> operators;
    private Plan plan;

    public Problem(List<MovementOperator> operators, Plan plan) {
        this.operators = operators;
        this.plan = plan;
    }

    public Plan createCompletePlan() {
        if (plan.isComplete()) return plan;
        return null;
    }
}
