package unipassau.categories.experiment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RunVectorComposition implements Runnable {

    protected DistributionalCompositor distributionalCompositor;

    private String category;

    private Collection<String> paraphrases;

    private String message;

    private Collection<FullResult> resultsBack;

    private Criteria[] criterias;

    private int topResults;

    public RunVectorComposition(String category, List<String> paraphrases, DistributionalCompositor distributionalCompositor,
                                Collection<FullResult> resultsBack, Criteria[] criterias, int topResults) {
        this.category = category;
        this.paraphrases = paraphrases;
        this.distributionalCompositor = distributionalCompositor;
        this.resultsBack = resultsBack;
        this.criterias = criterias;
        this.topResults = topResults;
    }

    @Override
    public void run() {
        printMessage();

        if (distributionalCompositor.hasCategory(category)) {
            System.out.println("'" + category + "' is OK!");

            for (String paraphrase : paraphrases) {
                System.out.println("Processing candidate descriptor '" + paraphrase + "'... ");

                long initial = System.currentTimeMillis();
                Map<String, Double> results = distributionalCompositor.relatedness(paraphrase);
                results = Utils.reverse(results);
                long _final = System.currentTimeMillis();

                ParaphraseExperiment exp = new ParaphraseExperiment(paraphrase, category);
                exp.total = results.size();
                exp.maxScore = results.entrySet().iterator().next().getValue();
                exp.minScore = ParaphraseExperiment.NOT_DEFINED;
                exp.queryExecutionTime = _final - initial;

                int i = 1;
                for (Entry<String, Double> e : results.entrySet()) {
                    String sclass = e.getKey();
                    Double score = e.getValue();

                    if (i <= topResults)
                        exp.topResults.put(sclass, score);

                    if (exp.position == ParaphraseExperiment.NOT_DEFINED && category.equals(sclass.trim())) {
                        exp.position = i;
                        exp.score = score;
                    }

                    i++;


                    if (exp.position != ParaphraseExperiment.NOT_DEFINED && i > topResults) {
                        exp.topResults = Utils.reverse(exp.topResults);
                        break;
                    }
                }

                FullResult fr = new FullResult();
                for (Criteria criteria : this.criterias)
                    fr.results.put(criteria.toString(), ResultEvaluation.getResult(exp, criteria));

                fr.description = exp.toString();
                resultsBack.add(fr);

                System.out.println("'" + paraphrase + "' Done! POS: " + exp.position + " - time: ("
                        + ((float) exp.queryExecutionTime) / 1000 + "s)");
            }


        } else
            System.out.println("ERROR: Category not found (" + category + ")");
    }

    public void printMessage() {
        System.out.println(message);
    }

    public void setMessage(int id, int total) {
        this.message = "Processing target core '" + category + "' (" + id + " of " + total + ") ... ";
    }
}
