package dedep.blanus.plan;

import dedep.blanus.problem.HanoiProblem;
import dedep.blanus.problem.Problem;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import java.util.*;

public class HanoiProblemTest {

    @Test
    public void hanoi2ProblemTest() {
        Problem hanoiProblem = new HanoiProblem(2);

        Optional<Plan> completePlan = hanoiProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
    }

    @Ignore
    @Test
    public void hanoi3ProblemTest() {
        Problem hanoiProblem = new HanoiProblem(3);

        Optional<Plan> completePlan = hanoiProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
    }
}
