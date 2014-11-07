package dedep.blanus;

import java.util.Collections;
import java.util.List;

public class InitStep extends Step {
    public InitStep(List<Condition> effects, String name, int id) {
        super(Collections.<Condition>emptyList(), effects, name, id);
    }
}
