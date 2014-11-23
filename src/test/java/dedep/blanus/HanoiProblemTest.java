package dedep.blanus;

import dedep.blanus.param.Variable;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HanoiProblemTest {

    public List<Operator> getHanoiOperators(int n) {
        String[] discs = IntStream.rangeClosed(1, n).mapToObj(Integer::toString).toArray(String[]::new);
        String[] elements = ArrayUtils.addAll(discs, "A", "B", "C");

        Variable disc = new Variable("disc", discs);
        Variable source = new Variable("source", elements);
        Variable dest = new Variable("dest", elements);

        List<Operator> operators = Operator.generateOperatorsFromConditionsIntersection(
                Arrays.asList(
                        new ConditionTemplate("Wolny", disc),
                        new ConditionTemplate("Wolny", dest),
                        new ConditionTemplate("Mniejszy", disc, dest),
                        new ConditionTemplate("Na", source, disc)
                ),
                Arrays.asList(
                        new ConditionTemplate("Na", dest, disc),
                        new ConditionTemplate("Wolny", source),
                        new ConditionTemplate("Wolny", disc),
                        new ConditionTemplate("!Na", source, disc),
                        new ConditionTemplate("!Wolny", dest)
                ),
                "Move($disc, $source, $dest)"
        );

        return filterProperOperators(operators, elements);
    }

    private List<Operator> filterProperOperators(List<Operator> toFilter, String[] elements) {
        return toFilter.stream()
                .filter(o -> Stream.of(elements).noneMatch(e -> o.getName().replaceFirst(e, "").contains(e)))
                .filter(o -> {
                    String[] operatorArgs = o.getName().split("\\)|\\(")[1].split(",");
                    try {
                        return Integer.parseInt(operatorArgs[0].trim()) < Integer.parseInt(operatorArgs[1].trim());
                    } catch (NumberFormatException e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    private InitStep getInitStep(int n) {
        List<Condition> initStepEffects = new ArrayList<>();

        initStepEffects.addAll(Arrays.asList(new Condition("Wolny(B)"), new Condition("Wolny(C)"), new Condition("Wolny(1)")));

        initStepEffects.addAll(IntStream.rangeClosed(1, n).mapToObj(i -> new Integer(i)).flatMap(i -> {
            List<String> biggerElements = IntStream.rangeClosed(i + 1, n).mapToObj(Integer::toString).collect(Collectors.toList());
            biggerElements.add("A");
            biggerElements.add("B");
            biggerElements.add("C");

            return biggerElements.stream().map(e -> new Condition("Mniejszy(" + i + ", " + e + ")"));
        }).collect(Collectors.toList()));

        initStepEffects.addAll(IntStream.rangeClosed(2, n)
                .mapToObj(i -> new Condition("Na(" + i + ", " + (i - 1) + ")")).collect(Collectors.toList()));

        initStepEffects.add(new Condition("Na(A, " + n + ")"));

        return new InitStep(initStepEffects, "Init step", 1);
    }

    private GoalStep getGoalStep(int n) {
        List<Condition> goalStepPreconds = new ArrayList<>();

        goalStepPreconds.addAll(IntStream.rangeClosed(2, n)
                .mapToObj(i -> new Condition("Na(" + i + ", " + (i - 1) + ")")).collect(Collectors.toList()));

        goalStepPreconds.add(new Condition("Na(C, " + n + ")"));

        return new GoalStep(goalStepPreconds, "Goal step", 2);
    }

    @Test
    public void hanoi2ProblemTest() {
        InitStep initStep = getInitStep(2);
        GoalStep goalStep = getGoalStep(2);

        Plan hanoiPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem hanoiProblem = new Problem(getHanoiOperators(2), hanoiPlan);

        Optional<Plan> completePlan = hanoiProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
    }

    @Test
    public void hanoi3ProblemTest() {
        InitStep initStep = getInitStep(3);
        GoalStep goalStep = getGoalStep(3);

        Plan hanoiPlan = Plan.createMinimalPlan(initStep, goalStep);
        Problem hanoiProblem = new Problem(getHanoiOperators(3), hanoiPlan);

        Optional<Plan> completePlan = hanoiProblem.createCompletePlan();

        Assert.assertTrue(completePlan.isPresent());
    }
}
