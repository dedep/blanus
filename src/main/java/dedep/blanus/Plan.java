package dedep.blanus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Plan {
    private List<List<Step>> orderedSteps;
    private List<Relationship> relationships;

    public Plan(List<List<Step>> orderedSteps, List<Relationship> relationships) {
        this.orderedSteps = orderedSteps;
        this.relationships = relationships;
    }

    public boolean isComplete() {
        return getSubgoals().stream().allMatch(planFulfillsSubgoal);
    }

    public Optional<Subgoal> chooseSubgoal() {
        return getSubgoals().stream()
                .filter(planFulfillsSubgoal.negate())
                .findFirst();
    }

    public Plan solveSubgoal(Subgoal subgoal, List<MovementOperator> operators) {
        if (subgoal.getCondition().isPresent()) {

            return operators.stream()
                    .filter(o -> o.getEffects().contains(subgoal.getCondition().get()))
                    .findFirst()
                    .map(operator -> {
                        Step newStep = new Step(operator.getPreconditions(), operator.getEffects(), operator.getName(), new Random().nextInt());
                        return addStepToPlan(newStep, subgoal.getStep());
                    })
                    .orElse(this);
        }
        return this;
    }

    private Plan addStepToPlan(Step stepToAdd, Step goalStep) {
        List<List<Step>> newOrderedStepsList = new ArrayList<>();
        newOrderedStepsList.addAll(orderedSteps);

        int subgoalIndex = orderedSteps.indexOf(orderedSteps.stream().filter(os -> os.contains(goalStep)).findFirst().get());
        List<Step> newStepsList = new ArrayList<>();
        newStepsList.add(stepToAdd);

        if (orderedSteps.get(subgoalIndex - 1).stream().anyMatch(step -> step instanceof InitStep)) {
            newOrderedStepsList.add(subgoalIndex, newStepsList);
        } else {
            newStepsList.addAll(orderedSteps.get(subgoalIndex - 1));
            newOrderedStepsList.remove(subgoalIndex - 1);
            newOrderedStepsList.add(subgoalIndex - 1, newStepsList);
        }

        List<Relationship> newRelationships = new ArrayList<>(relationships);
        newRelationships.addAll(bindSteps(stepToAdd, goalStep));

        return new Plan(newOrderedStepsList, newRelationships);
    }

    private Predicate<Subgoal> planFulfillsSubgoal = entry ->
            relationships.stream().anyMatch(relation ->
                            entry.getCondition().equals(relation.getRelationshipCondition()) && entry.getStep().equals(relation.getRelationship().getRight())
            );

    private List<Subgoal> getSubgoals() {
        Function<Step, List<Subgoal>> createStepConditionMap = step ->
                step.getPreconditions()
                        .stream()
                        .map(c -> new Subgoal(step, Optional.of(c)))
                        .collect(Collectors.toList());

        List<Step> flattenedSteps = orderedSteps.stream().flatMap((steps) -> steps.stream()).collect(Collectors.toList());

        return flattenedSteps.stream().map(createStepConditionMap).reduce(new ArrayList<>(), (o, o2) -> {
            List<Subgoal> ret = new ArrayList<>(o);
            ret.addAll(o2);
            return ret;
        });
    }

    public static Plan createMinimalPlan(Step init, Step goal) {
        return new Plan(Arrays.asList(Collections.singletonList(init), Collections.singletonList(goal)), bindSteps(init, goal));
    }

    private static List<Relationship> bindSteps(Step initStep, Step goalStep) {
        List<Condition> effects = initStep.getEffects();
        List<Condition> preconditions = goalStep.getPreconditions();

        return effects.stream()
                .filter(preconditions::contains)
                .map(c -> new Relationship(initStep, goalStep, c))
                .collect(Collectors.toList());
    }

    public List<List<Step>> getSteps() {
        return Collections.unmodifiableList(orderedSteps);
    }

    public List<Relationship> getRelationships() {
        return Collections.unmodifiableList(relationships);
    }
}