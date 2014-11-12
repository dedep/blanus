package dedep.blanus;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public class Relationship {
    private ImmutablePair<Step, Step> relationship;
    private Optional<Condition> relationshipCondition;

    public Relationship(Step fromStep, Step toStep, Optional<Condition> relationshipCondition) {
        this.relationship = ImmutablePair.of(fromStep, toStep);
        this.relationshipCondition = relationshipCondition;
    }

    public Relationship(Step fromStep, Step toStep, Condition relationshipCondition) {
        this(fromStep, toStep, Optional.ofNullable(relationshipCondition));
    }

    public Relationship(Step fromStep, Step toStep) {
        this(fromStep, toStep, Optional.empty());
    }

    public ImmutablePair<Step, Step> getRelationship() {
        return relationship;
    }

    public Optional<Condition> getRelationshipCondition() {
        return relationshipCondition;
    }

    public boolean containsStep(Step step) {
        return relationship.left.equals(step) || relationship.right.equals(step);
    }
}
