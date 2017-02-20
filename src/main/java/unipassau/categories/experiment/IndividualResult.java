package unipassau.categories.experiment;

import java.io.Serializable;

public class IndividualResult implements Serializable {

	public double precision;

	public double recall;

	public double reciprocalRank;

	public double queryExecutionTime;

	@Override
	public String toString() {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append("\n    Precision: " + precision);
		sbuilder.append("\n    Recall: " + recall);
		sbuilder.append("\n    Reciprocal Rank: " + reciprocalRank);
		sbuilder.append("\n    Execution Time: " + ((double) queryExecutionTime) / 1000 + "s");
		sbuilder.append("\n");

		return sbuilder.toString();
	}
}
