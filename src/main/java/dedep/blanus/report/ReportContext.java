package dedep.blanus.report;

public class ReportContext {

    private ReportStrategy strategy;

    public ReportContext(ReportStrategy strategy) {
        this.strategy = strategy;
    }

    public void report(String toReport) {
        strategy.report(toReport);
    }
}
