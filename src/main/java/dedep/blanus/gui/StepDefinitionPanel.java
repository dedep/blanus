package dedep.blanus.gui;

import dedep.blanus.condition.Condition;
import dedep.blanus.step.GoalStep;
import dedep.blanus.step.InitStep;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class StepDefinitionPanel extends JPanel {

    private java.util.List<Condition> conditionsContainer = new ArrayList<>();

    public StepDefinitionPanel(String name) {
        JList<String> myList = new JList<>();
        myList.setModel(new DefaultListModel<>());
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TitledBorder titled = new TitledBorder(name);
        setBorder(titled);

        JPanel addCndPanel = new JPanel(new FlowLayout());

        addCndPanel.add(new JLabel("Condition:"));

        JTextField variableValueValue = new JTextField(10);
        addCndPanel.add(variableValueValue);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            if (!"".equals(variableValueValue.getText()) &&
                    conditionsContainer.stream().noneMatch(c -> c.getValue().equals(variableValueValue.getText()))) {
                Condition newCondition = new Condition(variableValueValue.getText());
                conditionsContainer.add(newCondition);
                ((DefaultListModel<String>)myList.getModel()).add(0, newCondition.getValue());
            }
        });
        addCndPanel.add(addBtn);

        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> {
            if (myList.getSelectedIndex() >= 0) {
                conditionsContainer.remove(myList.getSelectedIndex());
                ((DefaultListModel<String>)myList.getModel()).remove(myList.getSelectedIndex());
            }
        });
        addCndPanel.add(removeBtn);
        add(addCndPanel);
        add(new JScrollPane(myList));

    }

    public InitStep getInitStep() {
        return new InitStep(conditionsContainer, "Init step", 0);
    }

    public GoalStep getGoalStep() {
        return new GoalStep(conditionsContainer, "Goal step", 999);
    }
}
