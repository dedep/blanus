package dedep.blanus.plan;

import dedep.blanus.step.Step;

public class Conflict {
    private Step step;
    private Relationship relationship;

    public Conflict(Step step, Relationship relationship) {
        this.step = step;
        this.relationship = relationship;
    }

    public Step getStep() {
        return step;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public String toString() {
        return "Conflict{" +
                "stepID=" + step.getId() +
                ", relationship=" + relationship +
                '}';
    }
}
