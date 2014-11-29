package dedep.blanus.plan;

import dedep.blanus.condition.Condition;
import dedep.blanus.condition.ConditionTemplate;
import dedep.blanus.param.Constant;
import dedep.blanus.param.Variable;
import dedep.blanus.plan.Conflict;
import dedep.blanus.plan.Plan;
import dedep.blanus.plan.Relationship;
import dedep.blanus.plan.Subgoal;
import dedep.blanus.problem.Problem;
import dedep.blanus.step.GoalStep;
import dedep.blanus.step.InitStep;
import dedep.blanus.step.Operator;
import dedep.blanus.step.Step;
import dedep.blanus.util.Either;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

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
    public void testBindingInProblemSolving() {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");

        Operator addLeftLeg = new Operator(
                Arrays.asList(rightLeg),
                Arrays.asList(rightLeg, leftLeg),
                "Add left leg"
        );

        Step initStep = new InitStep(Arrays.asList(rightLeg), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(rightLeg, leftLeg), "Goal step", 1);

        Plan initPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem legsProblem = new Problem(Collections.singletonList(addLeftLeg), initPlan);

        Optional<Plan> completePlan = legsProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
        Assert.assertEquals(completePlan.get().getRelationships().size(), 3);
        Assert.assertEquals(completePlan.get().getSteps().size(), 3);
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
        Step step1 = new Step(Arrays.asList(rightLeg), Arrays.asList(rightLeg), "step 1", 1);
        Step step2 = new Step(Arrays.asList(leftLeg), Arrays.asList(leftLeg), "step 2", 2);
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

        Operator mv1 = new Operator(Collections.emptyList(), Arrays.asList(rightLeg), "pull right leg");
        Operator mv2 = new Operator(Collections.singletonList(rightLeg), Arrays.asList(leftLeg), "pull left leg");

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

    @Test
    public void shouldResolveConflictByPromotion() {
        Condition a = new Condition("a");
        Condition b = new Condition("b");
        Condition c = new Condition("c");
        Condition d = new Condition("d");

        Step initStep = new InitStep(Collections.singletonList(b), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(c, d), "Goal step", 3);
        Step step1 = new Step(Arrays.asList(b), Arrays.asList(a), "step1", 1);
        Step step2 = new Step(Arrays.asList(a), Arrays.asList(d), "step 2", 2);
        Step step3 = new Step(Arrays.asList(b), Arrays.asList(c, a.negate()), "step 3", 4);

        List<Relationship> relationshipList = Arrays.asList(
            new Relationship(initStep, step1, b),
            new Relationship(step1, step2, a),
            new Relationship(step2, goalStep, d),
            new Relationship(initStep, step3, b),
            new Relationship(step3, goalStep, c)
        );

        List<List<Step>> steps = Arrays.asList(
                Arrays.asList(initStep),
                Arrays.asList(step1),
                Arrays.asList(step3),
                Arrays.asList(step2),
                Arrays.asList(goalStep)
        );

        Plan plan = new Plan(steps, relationshipList);
        Either<Plan, Conflict> newPlan = plan.resolveConflicts();

        Assert.assertTrue(newPlan.getLeft().isPresent());
        Assert.assertTrue(newPlan.getLeft().get().getSteps().get(3).contains(step3));
        Assert.assertEquals(newPlan.getLeft().get().getSteps().size(), 5);
        Assert.assertEquals(newPlan.getLeft().get().getRelationships(), relationshipList);
        Assert.assertTrue(newPlan.getLeft().get().isComplete());
    }

    @Test
    public void shouldResolveConflictByDegradation() {
        Condition a = new Condition("a");
        Condition b = new Condition("b");
        Condition c = new Condition("c");
        Condition d = new Condition("d");

        Step initStep = new InitStep(Collections.singletonList(b), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(c, d), "Goal step", 3);
        Step step1 = new Step(Arrays.asList(b), Arrays.asList(a), "step1", 1);
        Step step2 = new Step(Arrays.asList(a), Arrays.asList(d), "step 2", 2);
        Step step3 = new Step(Arrays.asList(b), Arrays.asList(c, d.negate()), "step 3", 4);

        List<Relationship> relationshipList = Arrays.asList(
                new Relationship(initStep, step1, b),
                new Relationship(step1, step2, a),
                new Relationship(step2, goalStep, d),
                new Relationship(initStep, step3, b),
                new Relationship(step3, goalStep, c)
        );

        List<List<Step>> steps = Arrays.asList(
                Arrays.asList(initStep),
                Arrays.asList(step1),
                Arrays.asList(step2),
                Arrays.asList(step3),
                Arrays.asList(goalStep)
        );

        Plan plan = new Plan(steps, relationshipList);
        Either<Plan, Conflict> newPlan = plan.resolveConflicts();

        Assert.assertTrue(newPlan.getLeft().isPresent());
        Assert.assertTrue(newPlan.getLeft().get().getSteps().get(1).contains(step3));
        Assert.assertEquals(newPlan.getLeft().get().getSteps().size(), 4);
        Assert.assertEquals(newPlan.getLeft().get().getRelationships(), relationshipList);
        Assert.assertTrue(newPlan.getLeft().get().isComplete());
    }

    @Test
    public void shouldSolveProblemWithConflicts() {
        Condition b = new Condition("Sprzedaje(SM, chleb)");
        Condition c = new Condition("Sprzedaje(SM, mleko)");
        Condition d = new Condition("Sprzedaje(SM, kwiaty)");
        Condition e = new Condition("Ma(chleb)");
        Condition f = new Condition("Ma(kwiaty)");
        Condition g = new Condition("Ma(mleko)");
        Condition h = new Condition("W(SM)");
        Condition j = new Condition("W(dom)");

        Step initStep = new InitStep(Arrays.asList(j, b, c, d), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(j, e, f, g), "Goal step", 99);

        Operator buyFlower = new Operator(Arrays.asList(h, d), Arrays.asList(f), "Kup(kwiaty)");
        Operator buyMilk = new Operator(Arrays.asList(h, c), Arrays.asList(g), "Kup(mleko)");
        Operator buyBread = new Operator(Arrays.asList(h, b), Arrays.asList(e), "Kup(chleb)");
        Operator goHomeToSM = new Operator(Arrays.asList(j), Arrays.asList(h, j.negate()), "Pójść(dom, SM)");
        Operator goSMToHome = new Operator(Arrays.asList(h), Arrays.asList(j, h.negate()), "Pójść(SM, dom)");

        Plan plan = Plan.createMinimalPlan(initStep, goalStep);
        Problem shopProblem = new Problem(Arrays.asList(
                goHomeToSM, goSMToHome, buyBread, buyFlower, buyMilk), plan);

        Optional<Plan> resultPlan = shopProblem.createCompletePlan();

        Assert.assertTrue(resultPlan.isPresent());
    }

    @Test
    public void shouldGenerateOperators() {
        Variable src = new Variable("src", "A", "B", "C");
        Variable dest = new Variable("dest", "B", "C", "D");

        ConditionTemplate ct1 = new ConditionTemplate("On", dest, src);
        ConditionTemplate ct2 = new ConditionTemplate("Occupied", dest, new Constant("1"));

        List<Operator> operators = Operator.generateOperatorsFromConditionsIntersection(
                Arrays.asList(ct1, ct2),
                Arrays.asList(ct1, ct2),
                constants -> !constants.get(0).getName().equals(constants.get(1).getName()),
                "Move($src, $dest)"
        );

        Assert.assertEquals(operators.size(), 7);
    }

    @Test
    public void shouldRemoveStepAndAllDependentSteps() {
        Condition a = new Condition("a");
        Condition b = new Condition("b");
        Condition c = new Condition("c");
        Condition d = new Condition("d");

        Step initStep = new InitStep(Collections.singletonList(b), "Init step", 0);
        Step goalStep = new GoalStep(Arrays.asList(c, d), "Goal step", 3);
        Step step1 = new Step(Arrays.asList(b), Arrays.asList(a), "step1", 1);
        Step step2 = new Step(Arrays.asList(a), Arrays.asList(d), "step 2", 2);
        Step step3 = new Step(Arrays.asList(b), Arrays.asList(c, d.negate()), "step 3", 4);

        List<Relationship> relationshipList = Arrays.asList(
                new Relationship(initStep, step1, b),
                new Relationship(step1, step2, a),
                new Relationship(initStep, step3, b),
                new Relationship(step3, goalStep, c)
        );

        List<List<Step>> steps = Arrays.asList(
                Arrays.asList(initStep),
                Arrays.asList(step1),
                Arrays.asList(step2),
                Arrays.asList(step3),
                Arrays.asList(goalStep)
        );

        Plan plan = new Plan(steps, relationshipList);
        Plan newPlan = plan.removeStep(step2);

        Assert.assertEquals(newPlan.getSteps().size(), 3);
        Assert.assertEquals(newPlan.getSteps().get(1).get(0), step3);
        Assert.assertEquals(newPlan.getRelationships().size(), 2);

    }

    @Test
    public void shouldGenerateAllCombinationOfVariablesValues() {
        Variable v1 = new Variable("src", "A", "B", "C");
        Variable v2 = new Variable("dest", "B", "C", "D");

        List<Condition> conditions = Condition.createConditionsFromParameters("testFN", v1, new Constant("2"), v2);

        Assert.assertEquals(conditions.size(), 9);
        Assert.assertTrue(conditions.contains(new Condition("testFN(B, 2, D)")));
        Assert.assertTrue(conditions.contains(new Condition("testFN(A, 2, D)")));
        Assert.assertTrue(conditions.contains(new Condition("testFN(C, 2, D)")));
        Assert.assertTrue(conditions.contains(new Condition("testFN(A, 2, B)")));
    }
}
