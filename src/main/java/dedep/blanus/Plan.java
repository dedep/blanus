package dedep.blanus;

import dedep.blanus.util.Either;

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
        return getSubgoals().stream().allMatch(planFulfillsSubgoal) && getConflicts().isEmpty();
    }

    public Optional<Subgoal> chooseSubgoal() {
        return getSubgoals().stream()
                .filter(planFulfillsSubgoal.negate())
                .findFirst();
    }

    public Plan solveSubgoal(Subgoal subgoal, List<Operator> operators) {
        return solveSubgoal(subgoal, operators, true);
    }

    public Plan solveSubgoal(Subgoal subgoal, List<Operator> operators, boolean tryBindingToExistingStep) {
        if (subgoal.getCondition().isPresent()) {
            Plan planWithConflicts;

            if (tryBindingToExistingStep) {
                planWithConflicts = solveByBindingToExistingStep(subgoal)
                        .orElse(solveByAddingNewStep(subgoal, operators)
                                .orElse(this));
            } else {
                planWithConflicts = solveByAddingNewStep(subgoal, operators).orElse(this);
            }

            return planWithConflicts
                    .resolveConflicts()
                    .map(plan -> plan, conflict -> planWithConflicts.makeReturn(conflict, operators));
        }

        return this;
    }

    public Either<Plan, Conflict> resolveConflicts() {
        List<Conflict> conflicts = getConflicts();

        if (conflicts.isEmpty()) {
            return Either.left(this);
        }

        Conflict conflict = conflicts.get(0);

        Optional<Plan> resolvedConflictPlan =
                Optional.ofNullable(promoteConflict(conflict)
                .orElse(degradeConflict(conflict).orElse(null)));

        if (!resolvedConflictPlan.isPresent()) {
            return Either.right(conflict);
        }

        return resolvedConflictPlan.get().resolveConflicts();
    }

    private List<Conflict> getConflicts() {
        return relationships.stream()
                .filter(r -> r.getRelationshipCondition().isPresent())
                .flatMap(r -> {
                    Condition conflictingCondition = r.getRelationshipCondition().get().negate();

                    int leftIndex = getStepIndex(r.getRelationship().getLeft()).get();
                    int rightIndex = getStepIndex(r.getRelationship().getRight()).get();

                    List<Step> potentiallyThreateningSteps =
                            flattenSteps(orderedSteps.subList(Math.min(leftIndex, rightIndex), Math.max(leftIndex, rightIndex) + 1));

                    List<Step> conflicts = potentiallyThreateningSteps.stream()
                            .filter(s -> s.getEffects().contains(conflictingCondition))
                            .filter(s -> !r.containsStep(s))
                            .collect(Collectors.toList());

                    return conflicts.stream().map(s -> new Conflict(s, r));
                }).collect(Collectors.toList());
    }

    private Optional<Plan> promoteConflict(Conflict conflict) {
        List<List<Step>> newOrderedSteps = removeStep(conflict.getStep(), orderedSteps);

        int leftIndex = getStepIndex(conflict.getRelationship().getRelationship().getLeft(), newOrderedSteps).get();
        int rightIndex = getStepIndex(conflict.getRelationship().getRelationship().getRight(), newOrderedSteps).get();

        if (Math.max(leftIndex, rightIndex) == newOrderedSteps.size() - 1) {
            return Optional.empty();
        }

        int insertIndex = Math.max(leftIndex, rightIndex) + 1;

        int lowestDependentStepIndex = relationships.stream()
                .filter(r -> r.getRelationship().getLeft().equals(conflict.getStep()))
                .mapToInt(r -> getStepIndex(r.getRelationship().getRight()).get())
                .min()
                .orElse(Integer.MAX_VALUE);

        if (newOrderedSteps.get(insertIndex).stream().anyMatch(s -> s instanceof GoalStep) || insertIndex >= lowestDependentStepIndex) {
            newOrderedSteps.add(insertIndex, Arrays.asList(conflict.getStep()));
        } else {
            newOrderedSteps.get(insertIndex).add(conflict.getStep());
        }

        return Optional.of(new Plan(newOrderedSteps, relationships));
    }

    private Optional<Plan> degradeConflict(Conflict conflict) {
        List<List<Step>> newOrderedSteps = removeStep(conflict.getStep(), orderedSteps);

        int leftIndex = getStepIndex(conflict.getRelationship().getRelationship().getLeft(), newOrderedSteps).get();
        int rightIndex = getStepIndex(conflict.getRelationship().getRelationship().getRight(), newOrderedSteps).get();

        if (Math.min(leftIndex, rightIndex) == 0) {
            return Optional.empty();
        }

        int insertIndex = Math.min(leftIndex, rightIndex) - 1;

        int highestDependentStepIndex = relationships.stream()
                .filter(r -> r.getRelationship().getRight().equals(conflict.getStep()))
                .mapToInt(r -> getStepIndex(r.getRelationship().getLeft()).get())
                .max()
                .orElse(0);

        if (newOrderedSteps.get(insertIndex).stream().anyMatch(s -> s instanceof InitStep) || insertIndex <= highestDependentStepIndex) {
            newOrderedSteps.add(insertIndex, Arrays.asList(conflict.getStep()));
        } else {
            newOrderedSteps.get(insertIndex).add(conflict.getStep());
        }

        return Optional.of(new Plan(newOrderedSteps, relationships));
    }

    public Plan makeReturn(Conflict conflict, List<Operator> operators) {
        List<Relationship> newRelationships = new ArrayList<>(this.relationships);
        newRelationships.remove(conflict.getRelationship());

        Plan newPlan = new Plan(orderedSteps, newRelationships);
        Subgoal newSubgoal = new Subgoal(conflict.getRelationship().getRelationship().getRight(), conflict.getRelationship().getRelationshipCondition());

        return newPlan.solveSubgoal(newSubgoal, operators, false);
    }

    private List<List<Step>> removeStep(Step step, List<List<Step>> source) {
       return source.stream()
                .filter(l -> l.size() >= 2 || !l.contains(step))
                .map(l -> l.stream().filter(s -> !s.equals(step)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private Optional<Integer> getStepIndex(Step step) {
        return getStepIndex(step, orderedSteps);
    }

    private Optional<Integer> getStepIndex(Step step, List<List<Step>> stepsList) {
        Optional<List<Step>> levelList = stepsList.stream().filter(f -> f.contains(step)).findFirst();

        return levelList.map(stepsList::indexOf);
    }

    private List<Step> flattenSteps(List<List<Step>> toFlatten) {
        return toFlatten.stream().flatMap((steps) -> steps.stream()).collect(Collectors.toList());
    }

    private Optional<Plan> solveByBindingToExistingStep(Subgoal subgoal) {
        Step subgoalStep = subgoal.getStep();
        int subgoalStepIndex = orderedSteps.indexOf(orderedSteps.stream().filter(f -> f.contains(subgoalStep)).findFirst().get());
        List<List<Step>> stepsBeforeSubgoal = orderedSteps.subList(0, subgoalStepIndex);
        List<Step> flattenedSteps = flattenSteps(stepsBeforeSubgoal);

        Optional<Step> stepFullfilingSubgoal = flattenedSteps.stream().filter(s -> s.getEffects().contains(subgoal.getCondition().get())).findFirst();
        return stepFullfilingSubgoal.map(step -> {
            List<Relationship> relationships = new ArrayList<>(this.relationships);
            relationships.add(new Relationship(step, subgoal.getStep(), subgoal.getCondition()));

            return new Plan(orderedSteps, relationships);
        });
    }

    private Optional<Plan> solveByAddingNewStep(Subgoal subgoal, List<Operator> operators) {
        return operators.stream()
                .filter(o -> o.getEffects().contains(subgoal.getCondition().get()))
                .findFirst()
                .map(operator -> {
                    Step newStep = new Step(operator.getPreconditions(), operator.getEffects(), operator.getName(), new Random().nextInt());
                    return addStepToPlan(newStep, subgoal);
                });
    }

    private Plan addStepToPlan(Step stepToAdd, Subgoal subgoal) {
        List<List<Step>> newOrderedStepsList = new ArrayList<>();
        newOrderedStepsList.addAll(orderedSteps);

        int subgoalIndex = orderedSteps.indexOf(orderedSteps.stream().filter(os -> os.contains(subgoal.getStep())).findFirst().get());
        List<Step> newStepsList = new ArrayList<>();
        newStepsList.add(stepToAdd);

        if (orderedSteps.get(subgoalIndex - 1).stream().anyMatch(step -> step instanceof InitStep)) {
            newOrderedStepsList.add(subgoalIndex, newStepsList);
        } else {
            newStepsList.addAll(orderedSteps.get(subgoalIndex - 1));
            newOrderedStepsList.remove(subgoalIndex - 1);
            newOrderedStepsList.add(subgoalIndex - 1, newStepsList);
        }

        List<Relationship> relationships = new ArrayList<>(this.relationships);
        relationships.add(new Relationship(stepToAdd, subgoal.getStep(), subgoal.getCondition()));

        return new Plan(newOrderedStepsList, relationships);
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