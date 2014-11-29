package dedep.blanus;

import dedep.blanus.gui.GUIMain;
import dedep.blanus.gui.SwingLoggingArea;
import dedep.blanus.report.ReportContext;
import dedep.blanus.report.ReportStrategy;

public class App {

    private static ReportContext context = null;

    private static ReportStrategy strategy = SwingLoggingArea.getInstance();

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(GUIMain::createAndShowGUI);
    }

    public static ReportContext getReportContext() {
        if (context == null) {
            context = new ReportContext(strategy);
        }

        return context;
    }

    public static ReportStrategy getStrategy() {
        return strategy;
    }
}
