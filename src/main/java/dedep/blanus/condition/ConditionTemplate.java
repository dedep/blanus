package dedep.blanus.condition;

import dedep.blanus.param.Constant;
import dedep.blanus.param.Parameter;
import dedep.blanus.param.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
}
