package unipassau.categories.experiment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ParaphraseExperiment {

    public static final int NOT_DEFINED = -1;

    public String paraphrased;

    public String expected;

    public int position = NOT_DEFINED;

    public int total = NOT_DEFINED;

    public double score = NOT_DEFINED;

    public double maxScore = NOT_DEFINED;

    public double minScore = NOT_DEFINED;

    public long queryExecutionTime = NOT_DEFINED;

    public Map<String, Double> topResults = new LinkedHashMap<>();

    public String info;

    public ParaphraseExperiment(String paraphrased, String expected) {
        this.paraphrased = paraphrased;
        this.expected = expected;
        this.info = paraphrased + " -> " + expected;
    }

    @Override
    public String toString() {

        StringBuffer sbuffer = new StringBuffer(info + "\n'" + paraphrased + "' - looking for -> '" + expected + "'\n");
        sbuffer.append("total: " + (total == NOT_DEFINED ? "NOT_DEFINED" : total) + "\n");
        sbuffer.append("position: " + (position == NOT_DEFINED ? "NOT_DEFINED" : position) + "\n");
        sbuffer.append("score: " + (score == NOT_DEFINED ? "NOT_DEFINED" : score) + "\n");
        sbuffer.append("maxScore: " + (maxScore == NOT_DEFINED ? "NOT_DEFINED" : maxScore) + "\n");
        sbuffer.append("minScore: " + (minScore == NOT_DEFINED ? "NOT_DEFINED" : minScore) + "\n");
        sbuffer.append("queryExecutionTime: "
                + (queryExecutionTime == NOT_DEFINED ? "NOT_DEFINED" : queryExecutionTime) + "\n");

        sbuffer.append("--- TOP Results ---\n");
        int i = 1;
        for (Entry<String, Double> entry : topResults.entrySet())
            sbuffer.append(i++ + "    " + entry.getKey() + ": " + entry.getValue() + "\n");

        return sbuffer.toString();
    }
}
