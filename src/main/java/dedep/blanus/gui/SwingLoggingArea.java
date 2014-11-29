package dedep.blanus.gui;

import dedep.blanus.report.ReportStrategy;

import javax.swing.*;

public class SwingLoggingArea extends JTextArea implements ReportStrategy {

    public static SwingLoggingArea instance;

    private SwingLoggingArea() {

    }

    public static SwingLoggingArea getInstance() {
        if (instance == null) {
            instance = new SwingLoggingArea();
        }

        return instance;
    }

    @Override
    public void report(String toReport) {
        append(toReport);
        setCaretPosition(getDocument().getLength());
    }
}
