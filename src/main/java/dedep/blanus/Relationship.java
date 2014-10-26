package dedep.blanus;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Relationship {
    private ImmutablePair<Step, Step> relationship;
    private Condition relationshipCondition;

    public Relationship(ImmutablePair<Step, Step> relationship, Condition relationshipCondition) {
        //todo: sprawdzić czy stepy-y posiadają ten warunek
        this.relationship = relationship;
        this.relationshipCondition = relationshipCondition;
    }

    public ImmutablePair<Step, Step> getRelationship() {
        return relationship;
    }

    public Condition getRelationshipCondition() {
        return relationshipCondition;
    }
}
