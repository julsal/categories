package unipassau.categories.experiment;

public interface Criteria {

	public float getPrecision(ParaphraseExperiment exp);

	public float getRecall(ParaphraseExperiment exp);

	public float getReciprocalRank(ParaphraseExperiment exp);
}
