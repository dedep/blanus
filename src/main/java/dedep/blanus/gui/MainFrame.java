package dedep.blanus.gui;

import dedep.blanus.plan.Plan;
import dedep.blanus.problem.HanoiProblem;
import dedep.blanus.problem.Problem;
import dedep.blanus.step.GoalStep;
import dedep.blanus.step.InitStep;
import dedep.blanus.step.Operator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel pane;
    private JPanel panel2;
    private JPanel panel3;

    private SwingWorker worker;

    public MainFrame() throws HeadlessException {
        createPage1();
        createPage2();
        createPage3();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Custom Problem", pane);
        tabbedPane.addTab("Hanoi Problem", panel2 );
        tabbedPane.addTab("Results", panel3 );
        getContentPane().add(tabbedPane);
    }

    public void createPage1() {
        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(2, 2));

        ParametersPanel paramsPanel = new ParametersPanel();
        panel1.add(paramsPanel);

        StepDefinitionPanel initStepDefPanel = new StepDefinitionPanel("Init step:");
        StepDefinitionPanel goalStepDefPanel = new StepDefinitionPanel("Goal step:");
        panel1.add(initStepDefPanel);
        panel1.add(goalStepDefPanel);

        OperatorsPanel operatorsPanel = new OperatorsPanel();
        panel1.add(operatorsPanel);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> {
            InitStep initStep = initStepDefPanel.getInitStep();
            GoalStep goalStep = goalStepDefPanel.getGoalStep();

            try {
                java.util.List<Operator> operators = operatorsPanel.getOperators(paramsPanel.getParams());
                Plan plan = Plan.createMinimalPlan(initStep, goalStep);
                Problem p = new Problem(operators, plan);
                fireProblemSolver(p);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel solvePanel = new JPanel(new FlowLayout());
        solvePanel.add(solveButton);
        pane.add(panel1);
        pane.add(solvePanel);
    }

    public void createPage2() {
        panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        JLabel label = new JLabel("Discs amount:");
        panel2.add(label);

        final JTextField field = new JTextField(10);
        panel2.add(field);

        JButton solve = new JButton("Solve");
        solve.addActionListener(e -> {
            try {
                int n = Integer.parseInt(field.getText());
                fireProblemSolver(new HanoiProblem(n));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(new JFrame(), "Illegal number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel2.add(solve);

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> stopProblemSolver());
        panel2.add(stop);
    }

    public void createPage3() {
        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());

        JTextArea area = SwingLoggingArea.getInstance();
        JScrollPane sp = new JScrollPane(area);
        panel3.add(sp);
    }

    private void fireProblemSolver(Problem problem) {
        if (worker != null) {
            worker.cancel(true);
        }
        worker = new ProblemWorker(problem);
        worker.execute();
    }

    private void stopProblemSolver() {
        worker.cancel(true);
        worker = null;
    }
}
