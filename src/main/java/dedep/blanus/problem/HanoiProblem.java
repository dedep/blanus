package dedep.blanus.problem;

import dedep.blanus.condition.Condition;
import dedep.blanus.condition.ConditionTemplate;
import dedep.blanus.param.Variable;
import dedep.blanus.plan.Plan;
import dedep.blanus.step.GoalStep;
import dedep.blanus.step.InitStep;
import dedep.blanus.step.Operator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HanoiProblem extends Problem {

    private int n;

    public HanoiProblem(int n) {
        this.n = n;

        setOperators(getHanoiOperators());
        setPlan(Plan.createMinimalPlan(getInitStep(), getGoalStep()));
    }

    private List<Operator> getHanoiOperators() {
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

    private InitStep getInitStep() {
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

    private GoalStep getGoalStep() {
        List<Condition> goalStepPreconds = new ArrayList<>();

        goalStepPreconds.addAll(IntStream.rangeClosed(2, n)
                .mapToObj(i -> new Condition("Na(" + i + ", " + (i - 1) + ")")).collect(Collectors.toList()));

        goalStepPreconds.add(new Condition("Na(C, " + n + ")"));

        return new GoalStep(goalStepPreconds, "Goal step", 2);
    }
}
