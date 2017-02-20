package unipassau.categories.experiment;

public class TopCriteria implements Criteria {

	private int max;

	public TopCriteria(int max) {
		this.max = max;
	}

	@Override
	public float getPrecision(ParaphraseExperiment exp) {

		if (getRecall(exp) > 0) {
			return 1f / Math.min(max, exp.total);
		}

		return 0;
	}

	@Override
	public float getRecall(ParaphraseExperiment exp) {
		if (exp.position != ParaphraseExperiment.NOT_DEFINED && exp.position <= max)
			return 1;

		return 0;
	}

	@Override
	public float getReciprocalRank(ParaphraseExperiment exp) {

		if (getRecall(exp) > 0)
			return (1f / (float) exp.position);

		return 0;
	}

	@Override
	public String toString() {
		return "TOP" + max;
	}
}
