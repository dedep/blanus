package dedep.blanus.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStrategy implements ReportStrategy {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void report(String toReport) {
        log.info(toReport);
    }
}
