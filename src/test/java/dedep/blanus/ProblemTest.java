package dedep.blanus;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class ProblemTest {

    @Test
    public void testSimpleProblemNormallyComplete() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new Step(Collections.emptyList(), Arrays.asList(rightLeg, leftLeg), "Init step", 0);
        Step goalStep = new Step(Arrays.asList(rightLeg, leftLeg), Collections.emptyList(), "Goal step", 1);

        Plan initPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem legsProblem = new Problem(Collections.emptyList(), initPlan);

        Optional<Plan> completePlan = legsProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
        Assert.assertEquals(initPlan, completePlan.get());
    }

    @Test
    public void testUnsolvableProblem() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        MovementOperator addLeftLeg = new MovementOperator(
                Arrays.asList(rightLeg),
                Arrays.asList(rightLeg, leftLeg),
                "Add left leg"
        );

        Step initStep = new InitStep(Arrays.asList(rightLeg), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(rightLeg, leftLeg), "Goal step", 1);

        Plan initPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem legsProblem = new Problem(Collections.singletonList(addLeftLeg), initPlan);

        Optional<Plan> completePlan = legsProblem.createCompletePlan();

        Assert.assertFalse(completePlan.isPresent());
    }

    @Test
    public void testProblemWithTwoStepsOnTheSameLevel() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new Step(Collections.emptyList(), Collections.emptyList(), "Init step", 0);
        Step step1 = new Step(Collections.emptyList(), Arrays.asList(rightLeg), "step 1", 1);
        Step step2 = new Step(Collections.emptyList(), Arrays.asList(leftLeg), "step 2", 2);
        Step goalStep = new Step(Arrays.asList(rightLeg, leftLeg), Collections.emptyList(), "Goal step", 3);

        Relationship r1 = new Relationship(step1, goalStep, rightLeg);
        Relationship r2 = new Relationship(step2, goalStep, leftLeg);
        Relationship r3 = new Relationship(initStep, step1);
        Relationship r4 = new Relationship(initStep, step2);

        Plan plan = new Plan(
                Arrays.asList(
                        Collections.singletonList(initStep),
                        Arrays.asList(step1, step2),
                        Collections.singletonList(goalStep)
                ),
                Arrays.asList(r1, r2, r3, r4)
        );

        Problem legsProblem = new Problem(Collections.emptyList(), plan);
        Optional<Plan> resultPlan = legsProblem.createCompletePlan();

        Assert.assertTrue(resultPlan.isPresent());
        Assert.assertEquals(resultPlan.get(), plan);
    }

    @Test
    public void shouldChooseSubgoalThatIsClosestToGoal() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new InitStep(Collections.emptyList(), "Init step", 0);
        Step step1 = new Step(Collections.emptyList(), Arrays.asList(rightLeg), "step 1", 1);
        Step step2 = new Step(Collections.emptyList(), Arrays.asList(leftLeg), "step 2", 2);
        Step goalStep = new GoalStep(Arrays.asList(rightLeg), "Goal step", 3);

        Plan plan = new Plan(
                Arrays.asList(
                        Collections.singletonList(initStep),
                        Arrays.asList(step1, step2),
                        Collections.singletonList(goalStep)
                ),
                Collections.emptyList()
        );

        Problem legsProblem = new Problem(Collections.emptyList(), plan);
        Optional<Subgoal> subgoal = legsProblem.getCurrentPlan().chooseSubgoal();

        Assert.assertTrue(subgoal.isPresent());
        Assert.assertEquals(subgoal.get(), new Subgoal(goalStep, Optional.of(rightLeg)));
    }

    @Test
    public void shouldSolveProblemInTwoSteps() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Step initStep = new InitStep(Collections.emptyList(), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(leftLeg), "Goal step", 3);

        Plan plan = Plan.createMinimalPlan(initStep, goalStep);

        MovementOperator mv1 = new MovementOperator(Collections.emptyList(), Arrays.asList(rightLeg), "pull right leg");
        MovementOperator mv2 = new MovementOperator(Collections.singletonList(rightLeg), Arrays.asList(leftLeg), "pull left leg");

        Problem legsProblem = new Problem(Arrays.asList(mv1, mv2), plan);
        Optional<Plan> resultPlan = legsProblem.createCompletePlan();

        Assert.assertTrue(resultPlan.isPresent());
        Assert.assertEquals(resultPlan.get().getRelationships().size(), 2);
        Assert.assertEquals(resultPlan.get().getSteps().size(), 4);

        Assert.assertEquals(resultPlan.get().getSteps().get(0).get(0).getName(), "Init step");
        Assert.assertEquals(resultPlan.get().getSteps().get(1).get(0).getName(), "pull right leg");
        Assert.assertEquals(resultPlan.get().getSteps().get(2).get(0).getName(), "pull left leg");
        Assert.assertEquals(resultPlan.get().getSteps().get(3).get(0).getName(), "Goal step");
    }
}
