package dedep.blanus.gui;

import dedep.blanus.param.Parameter;
import dedep.blanus.param.Variable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ParametersPanel extends JPanel {

    private java.util.List<Parameter> paramsContainer = new ArrayList<>();

    public ParametersPanel() {
        JList<String> myList = new JList<>();
        myList.setModel(new DefaultListModel<>());
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        TitledBorder titled = new TitledBorder("Variables");
        setBorder(titled);

        JPanel variablePanel = new JPanel(new FlowLayout());
        variablePanel.add(new JLabel("Name:"));
        JTextField variableNameValue = new JTextField(10);
        variablePanel.add(variableNameValue);
        JLabel variableValueLabel = new JLabel("Values:");
        JTextField variableValueValue = new JTextField(10);
        JButton variableAddButton = new JButton("Add");
        variableAddButton.addActionListener(e -> {
            if (!"".equals(variableNameValue.getText()) && !"".equals(variableValueValue.getText())) {
                Parameter newParam = new Variable(variableNameValue.getText(), variableValueValue.getText().split(";"));
                if (paramsContainer.stream().noneMatch(p -> p.getName().equals(newParam.getName()))) {
                    paramsContainer.add(0, newParam);
                    ((DefaultListModel<String>)myList.getModel()).add(0, newParam.getName());
                }
            }
        });

        JButton variableRemoveButton = new JButton("Remove");
        variableRemoveButton.addActionListener(e -> {
            if (myList.getSelectedIndex() >= 0) {
                paramsContainer.remove(myList.getSelectedIndex());
                ((DefaultListModel<String>)myList.getModel()).remove(myList.getSelectedIndex());
            }
        });

        variablePanel.add(variableValueLabel);
        variablePanel.add(variableValueValue);
        variablePanel.add(variableAddButton);
        variablePanel.add(variableRemoveButton);
        variablePanel.setAlignmentX(0);
        add(variablePanel);
        add(new JScrollPane(myList));

    }

    public List<Parameter> getParams() {
        return Collections.unmodifiableList(paramsContainer);
    }
}
