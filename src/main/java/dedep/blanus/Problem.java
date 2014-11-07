package dedep.blanus;

import java.util.List;
import java.util.Optional;

public class Problem {
    private List<MovementOperator> operators;
    private Plan plan;

    private static final int ITERATIONS_UPPER_THRESHOLD = 100;

    public Problem(List<MovementOperator> operators, Plan plan) {
        this.operators = operators;
        this.plan = plan;
    }

    public Optional<Plan> createCompletePlan() {
        return createCompletePlan(0);
    }

    public Optional<Plan> createCompletePlan(int iterationsAcc) {
        if (plan.isComplete()) {
            return Optional.of(plan);
        } else if (iterationsAcc >= ITERATIONS_UPPER_THRESHOLD) {
            return Optional.empty();
        }

        Subgoal subgoal = plan.chooseSubgoal().get();
        Plan newPlan = plan.solveSubgoal(subgoal, operators);
        return new Problem(operators, newPlan).createCompletePlan(iterationsAcc + 1);
    }

    public Plan getCurrentPlan() {
        return plan;
    }
}
