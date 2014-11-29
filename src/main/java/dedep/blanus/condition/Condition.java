package dedep.blanus.condition;

import dedep.blanus.param.Constant;
import dedep.blanus.param.Parameter;
import dedep.blanus.param.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<Condition> createConditionsFromParameters(String name, Parameter... parameters) {
        List<Parameter> params = Arrays.asList(parameters);

        if (params.stream().allMatch(p -> !(p instanceof Variable))) {
            String functionTemplate = name + "(" + params.stream()
                    .map(Parameter::getName)
                    .collect(Collectors.joining(", ")) + ")";

            return Arrays.asList(new Condition(functionTemplate));
        }

        Variable firstVariable = (Variable) params.stream()
                .filter(param -> param instanceof Variable)
                .collect(Collectors.toList())
                .get(0);

        return firstVariable
                .getPossibleValues().stream()
                .map(v -> {
                    List<Parameter> newParams = new ArrayList<>();
                    newParams.addAll(params);
                    newParams.set(newParams.indexOf(firstVariable), new Constant(v));

                    return createConditionsFromParameters(name, newParams.toArray(new Parameter[newParams.size()]));
                })
                .flatMap(conditions -> conditions.stream())
                .collect(Collectors.toList());
    }

    public static List<Condition> parseToList(String input) {
        return Stream.of(input.split(";")).map(String::trim).map(Condition::new).collect(Collectors.toList());
    }
}

