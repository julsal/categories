package unipassau.categories.eval;

public class CategoryEvalCriteria {

    protected int top;

    public CategoryEvalCriteria(int top) {
        this.top = top;
    }

    public float getPrecision(CategoryEval cat) {
        if (!cat.relevantTargets.containsKey(cat.target)) {
            cat.relevantTargets.put(cat.target, Integer.MAX_VALUE);
        }

        int truePositive = 0;

        for (String rel : cat.relevantTargets.keySet()) {
            if (cat.relevantTargets.get(rel) <= top)
                truePositive++;
        }

        return ((float) truePositive) / (float) top;
    }

    public float getRecall(CategoryEval cat) {
        if (!cat.relevantTargets.containsKey(cat.target)) {
            cat.relevantTargets.put(cat.target, Integer.MAX_VALUE);
        }

        int truePositive = 0;

        for (String rel : cat.relevantTargets.keySet()) {
            if (cat.relevantTargets.get(rel) <= top)
                truePositive++;
        }

        return ((float) truePositive) / (float) cat.relevantTargets.size();
    }

    public float getReciprocalRank(CategoryEval cat) {
        if (!cat.relevantTargets.containsKey(cat.target)) {
            cat.relevantTargets.put(cat.target, Integer.MAX_VALUE);
        }

        int bestPosition = Integer.MAX_VALUE;

        for (String rel : cat.relevantTargets.keySet()) {
            int pos = cat.relevantTargets.get(rel);
            if (pos <= top && pos < bestPosition)
                bestPosition = pos;
        }

        if (bestPosition <= top)
            return (1f / (float) bestPosition);

        return 0;
    }
}
