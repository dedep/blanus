package dedep.blanus;

import java.util.List;
import java.util.Optional;

public class Problem {
    private List<Operator> operators;
    private Plan plan;

    private static final int ITERATIONS_UPPER_THRESHOLD = 90000;

    public Problem(List<Operator> operators, Plan plan) {
        this.operators = operators;
        this.plan = plan;
    }

    public Optional<Plan> createCompletePlan() {
        int iterationAcc = 0;

        while (iterationAcc <= ITERATIONS_UPPER_THRESHOLD) {
            if (plan.isComplete()) {
                return Optional.of(plan);
            }

            this.plan = plan.chooseSubgoal()
                    .map(s -> plan.solveSubgoal(s, operators))
                    .orElse(plan);

            iterationAcc++;
        }

        return Optional.empty();
    }

    public Plan getCurrentPlan() {
        return plan;
    }
}
