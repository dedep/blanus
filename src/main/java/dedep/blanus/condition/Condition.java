package dedep.blanus.condition;

public class Condition {
    private String value;

    public Condition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Condition negate() {
        if (value.startsWith("!")) {
            return new Condition(value.substring(1));
        } else {
            return new Condition("!".concat(value));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;

        if (!value.equals(condition.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
