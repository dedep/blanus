package dedep.blanus.gui;

import dedep.blanus.plan.Plan;
import dedep.blanus.problem.Problem;

import javax.swing.*;
import java.util.Optional;

public class ProblemWorker extends SwingWorker<Optional<Plan>, Void> {

    private Problem problem;

    public ProblemWorker(Problem problem) {
        this.problem = problem;
    }

    @Override
    protected Optional<Plan> doInBackground() throws Exception {
        return problem.createCompletePlan();
    }
}
