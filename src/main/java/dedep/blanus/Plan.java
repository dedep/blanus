package dedep.blanus;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Plan {
    private List<List<Step>> orderedSteps;
    private List<Relationship> relationships;

    private Predicate<ImmutablePair<Condition, Step>> existsRelationshipBindingEntry = entry ->
            relationships.stream().anyMatch(relation ->
                    entry.getKey().equals(relation.getRelationshipCondition()) && entry.getValue().equals(relation.getRelationship().getRight())
    );

    public Plan(List<List<Step>> orderedSteps, List<Relationship> relationships) {
        this.orderedSteps = orderedSteps;
        this.relationships = relationships;
    }

    public boolean isComplete() {
        return getStepPreconditionsMap().stream().allMatch(existsRelationshipBindingEntry);
    }

    public Optional<ImmutablePair<Condition, Step>> chooseSubGoal() {
        return getStepPreconditionsMap().stream()
                .filter(existsRelationshipBindingEntry.negate())
                .findFirst();
    }

    private List<ImmutablePair<Condition, Step>> getStepPreconditionsMap() {
        Function<Step, List<ImmutablePair<Condition, Step>>> createStepConditionMap = step ->
                step.getPreconditions()
                        .stream()
                        .map(c -> ImmutablePair.of(c, step))
                        .collect(Collectors.toList());

        List<Step> flattenedSteps = orderedSteps.stream().flatMap((steps) -> steps.stream()).collect(Collectors.toList());

        return flattenedSteps.stream().map(createStepConditionMap).reduce(new ArrayList<>(), (o, o2) -> {
            List<ImmutablePair<Condition, Step>> ret = new ArrayList<>(o);
            ret.addAll(o2);
            return ret;
        });
    }

    public static Plan createMinimalPlan(Step init, Step goal) {
        return new Plan(Arrays.asList(Collections.singletonList(init), Collections.singletonList(goal)), bindSteps(init, goal));
    }

    private static List<Relationship> bindSteps(Step initStep, Step goalStep) {
        ImmutablePair<Step, Step> pair = ImmutablePair.of(initStep, goalStep);
        List<Condition> effects = initStep.getEffects();
        List<Condition> preconditions = goalStep.getPreconditions();

        return effects.stream()
                .filter(preconditions::contains)
                .map(c -> new Relationship(pair, c))
                .collect(Collectors.toList());
    }
}