package unipassau.categories.experiment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FullResult implements Serializable {

    public Map<String, IndividualResult> results = new HashMap<>();

    public String description;

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder(description);
        for (String key : results.keySet()) {
            sbuilder.append("\n   - " + key + "\n    - " + results.get(key));
        }

        return sbuilder.toString();
    }
}
