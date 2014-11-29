package dedep.blanus.gui;

import dedep.blanus.condition.Condition;
import dedep.blanus.condition.ConditionTemplate;
import dedep.blanus.param.Parameter;
import dedep.blanus.step.Operator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class OperatorsPanel extends JPanel {

    private JTable table;

    public OperatorsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TitledBorder titled = new TitledBorder("Operators");
        setBorder(titled);

        table = new JTable(new OperatorsTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel headerPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Remove");
        headerPanel.add(addBtn);
        headerPanel.add(delBtn);

        addBtn.addActionListener(e -> {
            ((OperatorsTableModel)table.getModel()).addRow(
                    new Operator(Collections.emptyList(), Collections.emptyList(), "")
            );
        });

        delBtn.addActionListener(e -> {
            if (table.getSelectedRow() >= 0) {
                ((OperatorsTableModel)table.getModel()).removeRow(table.getSelectedRow());
            }
        });

        add(headerPanel);
        JScrollPane pane = new JScrollPane(table);
        add(pane);
    }

    public java.util.List<Operator> getOperators(java.util.List<Parameter> params) {
        java.util.List<Operator> operatorTemplates = ((OperatorsTableModel)table.getModel()).getOperators();

        return operatorTemplates.stream().flatMap(o -> {
            String name = o.getName();
            String preconds = o.getPreconditions().stream().map(Condition::toString).collect(Collectors.joining(";"));
            String effects = o.getEffects().stream().map(Condition::toString).collect(Collectors.joining(";"));

            return Operator.generateOperatorsFromConditionsIntersection(
                    ConditionTemplate.parseList(preconds, params),
                    ConditionTemplate.parseList(effects, params),
                    constants -> true,
                    name).stream();
        }).collect(Collectors.toList());
    }
}
