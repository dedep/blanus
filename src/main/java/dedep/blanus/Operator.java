package dedep.blanus;

import dedep.blanus.param.Parameter;
import dedep.blanus.param.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Operator {
    private List<Condition> preconditions;
    private List<Condition> effects;
    private String name;

    public Operator(List<Condition> preconditions, List<Condition> effects, String name) {
        this.preconditions = preconditions;
        this.effects = effects;
        this.name = name;
    }

    public static List<Operator> generateOperatorsFromConditionsIntersection(List<ConditionTemplate> preconditionTemplate,
                                                                             List<ConditionTemplate> effectTemplate,
                                                                             String operatorName) {
        return generateOperatorsFromConditionsIntersection(Arrays.asList(preconditionTemplate),
                Arrays.asList(effectTemplate),
                Arrays.asList(operatorName));
    }

    public static List<Operator> generateOperatorsFromConditionsIntersection(List<List<ConditionTemplate>> preconditionTemplates,
                                                                             List<List<ConditionTemplate>> effectTemplates,
                                                                             List<String> operatorName) {
        Set<Parameter> allParams = preconditionTemplates.stream()
                .flatMap(l -> l.stream()).flatMap(p -> p.getParams().stream()).collect(Collectors.toSet());

        if (effectTemplates.stream().flatMap(l -> l.stream())
                .flatMap(e -> e.getParams().stream()).filter(p -> p instanceof Variable).anyMatch(p -> !allParams.contains(p))) {
            throw new IllegalArgumentException("Effect templates cannot refer to variable conditions not defined in preconditions templates.");
        }

        if (preconditionTemplates.size() != effectTemplates.size()) {
            throw new IllegalStateException("Sizes of templates arrays must be equal.");
        }

        if (allParams.stream().allMatch(p -> !(p instanceof Variable))) {
            return convertTemplatesToOperators(preconditionTemplates, effectTemplates, operatorName);
        }

        Variable firstVariable = (Variable) allParams.stream()
                .filter(param -> param instanceof Variable)
                .collect(Collectors.toList())
                .get(0);

        List<List<ConditionTemplate>> newPreconditionTemplates = partiallyApplyConditionTemplates(preconditionTemplates, firstVariable);
        List<List<ConditionTemplate>> newEffectTemplates = partiallyApplyConditionTemplates(effectTemplates, firstVariable);
        List<String> newNames = operatorName.stream()
                .flatMap(n -> firstVariable.getPossibleValues().stream().map(v -> n.replace(firstVariable.getName(), v)))
                .collect(Collectors.toList());

        return generateOperatorsFromConditionsIntersection(newPreconditionTemplates, newEffectTemplates, newNames);
    }

    private static List<Operator> convertTemplatesToOperators(List<List<ConditionTemplate>> preconditionTemplates,
                                                             List<List<ConditionTemplate>> effectTemplates,
                                                             List<String> operatorName) {

        List<List<Condition>> preconditions = preconditionTemplates.stream()
                .map(l -> l.stream().map(ct -> ct.toCondition()).collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<List<Condition>> effects = effectTemplates.stream()
                .map(l -> l.stream().map(ct -> ct.toCondition()).collect(Collectors.toList()))
                .collect(Collectors.toList());

        return IntStream.range(0, preconditions.size())
                .mapToObj(i -> new Operator(preconditions.get(i), effects.get(i), operatorName.get(i)))
                .collect(Collectors.toList());
    }

    private static List<List<ConditionTemplate>> partiallyApplyConditionTemplates(List<List<ConditionTemplate>> templates, Variable variableToApply) {
       return templates.stream()
                .flatMap(l -> variableToApply.getPossibleValues().stream().map(vv -> {
                    return l.stream().map(ct -> ct.replaceVariableWithValue(variableToApply, vv)).collect(Collectors.toList());
                }))
                .collect(Collectors.toList());
    }

    public List<Condition> getPreconditions() {
        return new ArrayList<>(preconditions);
    }

    public List<Condition> getEffects() {
        return new ArrayList<>(effects);
    }

    public String getName() {
        return name;
    }
}
