package unipassau.categories.eval;

import java.util.HashMap;

public class CategoryEval {

    public String category;

    public String target;

    public HashMap<String, Integer> relevantTargets = new HashMap<>();

    public CategoryEval(String category, String target) {
        this.category = category;
        this.target = target;

        if(this.target.length() == 0)
            throw new RuntimeException("deu merda");
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder(category + " -> " + target + " = ");
        this.relevantTargets.forEach((k, v) -> sbuilder.append(k + ":" + v + " | "));

        return sbuilder.toString();
    }
}
