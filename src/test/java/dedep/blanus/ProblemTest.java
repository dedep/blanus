package dedep.blanus;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class ProblemTest {
    @Test
    public void testSimpleProblemNormallyComplete() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new Step(Collections.emptyList(), Arrays.asList(rightLeg, leftLeg), "Init step", 0);
        Step goalStep = new Step(Arrays.asList(rightLeg, leftLeg), Collections.emptyList(), "Goal step", 1);

        Plan initPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem legsProblem = new Problem(Collections.emptyList(), initPlan);

        Assert.assertTrue(initPlan.equals(legsProblem.createCompletePlan()));
    }

    @Test
    public void testSimpleProblemNotCompleted() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new Step(Collections.emptyList(), Arrays.asList(rightLeg), "Init step", 0);
        Step goalStep = new Step(Arrays.asList(rightLeg, leftLeg), Collections.emptyList(), "Goal step", 1);

        Plan initPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem legsProblem = new Problem(Collections.emptyList(), initPlan);

        Assert.assertNull(legsProblem.createCompletePlan());
    }

    @Test
    public void testProblemWithTwoStepsOnTheSameLevel() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new Step(Collections.emptyList(), Collections.emptyList(), "Init step", 0);
        Step step1 = new Step(Collections.emptyList(), Arrays.asList(rightLeg), "step 1", 1);
        Step step2 = new Step(Collections.singletonList(rightLeg), Arrays.asList(leftLeg), "step 2", 1);
        Step goalStep = new Step(Arrays.asList(rightLeg, leftLeg), Collections.emptyList(), "Goal step", 2);

        Relationship r1 = new Relationship(ImmutablePair.of(step1, goalStep), rightLeg);
        Relationship r2 = new Relationship(ImmutablePair.of(step2, goalStep), leftLeg);

        Plan plan = new Plan(
                Arrays.asList(
                        Collections.singletonList(initStep),
                        Arrays.asList(step1, step2),
                        Collections.singletonList(goalStep)
                ),
                Arrays.asList(r1, r2)
        );

        Problem legsProblem = new Problem(Collections.emptyList(), plan);

        Assert.assertNull(legsProblem.createCompletePlan());
    }
}
