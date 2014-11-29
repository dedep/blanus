package dedep.blanus.problem;

import dedep.blanus.App;
import dedep.blanus.step.Operator;
import dedep.blanus.plan.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Problem {
    private List<Operator> operators;
    private Plan plan;

    private static final int ITERATIONS_UPPER_THRESHOLD = 200000;

    public Problem(List<Operator> operators, Plan plan) {
        this.operators = operators;
        this.plan = plan;
    }

    protected Problem() {
        operators = new ArrayList<>();
    }

    public Optional<Plan> createCompletePlan() {
        int iterationAcc = 0;

        while (iterationAcc <= ITERATIONS_UPPER_THRESHOLD) {
            if (plan.isComplete()) {
                App.getReportContext().report("\nProblem Solved! Final plan: " + plan.toString());
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

    protected void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    protected void setPlan(Plan plan) {
        this.plan = plan;
    }
}
