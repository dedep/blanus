package dedep.blanus.condition;

import dedep.blanus.param.Constant;
import dedep.blanus.param.Parameter;
import dedep.blanus.param.Variable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConditionTemplate {

    private String templateName;

    private List<Parameter> params;

    public ConditionTemplate(String name, Parameter... parameters) {
        templateName = name;
        params = Arrays.asList(parameters);
    }

    public List<Parameter> getParams() {
        return Collections.unmodifiableList(params);
    }

    public String getTemplateName() {
        return templateName;
    }

    public ConditionTemplate replaceVariableWithValue(Variable v, String value) {
        List<ConditionTemplate> templatesList = replaceVariableWithValues(v, value);

        if (templatesList.isEmpty()) {
            return this;
        } else {
            return templatesList.get(0);
        }
    }

    public List<ConditionTemplate> replaceVariableWithValues(Variable v, String... values) {
        if (!params.contains(v)) {
            return Arrays.asList(this);
        }

        Variable var = (Variable) params.stream().filter(p -> p.equals(v)).findFirst().get();
        int varIndex = params.indexOf(var);

        return Arrays.asList(values).stream().map(value -> {
            ArrayList<Parameter> newParams = new ArrayList<>(params);
            newParams.remove(var);
            newParams.add(varIndex, new Constant(value));

            return new ConditionTemplate(this.templateName, newParams.toArray(new Parameter[newParams.size()]));
        }).collect(Collectors.toList());
    }

    public Condition toCondition() {
        String value = templateName + "(" + params.stream()
                .map(Parameter::getName)
                .collect(Collectors.joining(", ")) + ")";

        return new Condition(value);
    }

    public static List<ConditionTemplate> parseList(String input, List<Parameter> params) {
        return Stream.of(input.split(";")).map(String::trim).map(s -> parse(s, params)).collect(Collectors.toList());
    }

    private static ConditionTemplate parse(String input, List<Parameter> params) {
        if ("".equals(input)) {
            throw new IllegalArgumentException("Cannot parse ConditionTemplate from empty string");
        }

        String[] parsed = input.split("\\)|\\(");
        String name = parsed[0].trim();
        Parameter[] arguments = new Parameter[0];

        if (parsed.length > 1) {
            arguments = Stream.of(parsed[1].split(",")).map(String::trim).map(s -> {
                if (s.startsWith("$")) {
                    Optional<Parameter> param = params.stream().filter(p -> p.getName().equals(s)).findFirst();
                    if (param.isPresent()) {
                        return param.get();
                    } else {
                        throw new IllegalArgumentException("Unknown variable: " + s);
                    }
                } else {
                    return new Constant(s);
                }
            }).toArray(Parameter[]::new);
        }

        return new ConditionTemplate(name, arguments);
    }
}
