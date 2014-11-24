package dedep.blanus;

import dedep.blanus.report.LoggingStrategy;
import dedep.blanus.report.ReportContext;

public class App {

    private static ReportContext context = null;

    public static void main(String[] args) {
    }

    public static ReportContext getReportContext() {
        if (context == null) {
            context = new ReportContext(new LoggingStrategy());
        }

        return context;
    }
}
