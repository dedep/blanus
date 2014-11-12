package dedep.blanus;

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
}
