package dedep.blanus.gui;

import dedep.blanus.condition.Condition;
import dedep.blanus.step.Operator;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class OperatorsTableModel extends AbstractTableModel {

    private java.util.List<Operator> operatorsContainer = new ArrayList<>();

    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Name";
            case 1: return "Preconditions";
            case 2: return "Effects";
            default: throw new NoSuchElementException();
        }
    }

    public int getRowCount() {
        return operatorsContainer.size();
    }

    public Object getValueAt(int row, int col) {
        Operator o = operatorsContainer.get(row);

        switch (col) {
            case 0: return o.getName();
            case 1: return o.getPreconditions().stream().map(Condition::toString).collect(Collectors.joining(";"));
            case 2: return o.getEffects().stream().map(Condition::toString).collect(Collectors.joining(";"));
            default: throw new NoSuchElementException();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Operator oldOperator = operatorsContainer.get(rowIndex);
        Operator newOperator;

        switch (columnIndex) {
            case 0:
                newOperator = new Operator(oldOperator.getPreconditions(), oldOperator.getEffects(), (String)aValue);
                break;
            case 1:
                newOperator = new Operator(Condition.parseToList((String)aValue), oldOperator.getEffects(), oldOperator.getName());
                break;
            case 2:
                newOperator = new Operator(oldOperator.getPreconditions(), Condition.parseToList((String)aValue), oldOperator.getName());
                break;
            default: throw new NoSuchElementException();
        }

        operatorsContainer.set(rowIndex, newOperator);
    }

    public void addRow(Operator o) {
        operatorsContainer.add(o);
        fireTableDataChanged();
    }

    public void removeRow(int rowIndex) {
        operatorsContainer.remove(rowIndex);
        fireTableDataChanged();
    }

    public List<Operator> getOperators() {
        return Collections.unmodifiableList(operatorsContainer);
    }
}
