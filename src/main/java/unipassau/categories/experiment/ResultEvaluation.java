package unipassau.categories.experiment;

import unipassau.categories.eval.CategoryEval;
import unipassau.categories.eval.CategoryEvalCriteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ResultEvaluation {

	public static IndividualResult getResult(ParaphraseExperiment exp, Criteria criteria) {

		IndividualResult result = new IndividualResult();
		result.precision = criteria.getPrecision(exp);
		result.recall = criteria.getRecall(exp);
		result.reciprocalRank = criteria.getReciprocalRank(exp);
		result.queryExecutionTime = exp.queryExecutionTime;

		return result;
	}

	public static IndividualResult getResult(CategoryEval cat, CategoryEvalCriteria criteria) {

		IndividualResult result = new IndividualResult();
		result.precision = criteria.getPrecision(cat);
		result.recall = criteria.getRecall(cat);
		result.reciprocalRank = criteria.getReciprocalRank(cat);

		return result;
	}

	public static IndividualResult getSummarizedResult(String name, Collection<FullResult> fullResults) {

		Set<IndividualResult> summarized = new HashSet<>();

		for (FullResult fr : fullResults) {
            summarized.add(fr.results.get(name));
        }


		IndividualResult avg = new IndividualResult();
		avg.precision = 0;
		avg.recall = 0;
		avg.reciprocalRank = 0;
		avg.queryExecutionTime = 0;

		if (!summarized.isEmpty()) {
			for (IndividualResult r : summarized) {
                avg.precision += r.precision;
				avg.recall += r.recall;
				avg.reciprocalRank += r.reciprocalRank;
				avg.queryExecutionTime += r.queryExecutionTime;
			}

			avg.precision /= summarized.size();
			avg.recall /= summarized.size();
			avg.reciprocalRank /= summarized.size();
			avg.queryExecutionTime /= summarized.size();
		}

		return avg;
	}
}
