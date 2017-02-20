package unipassau.categories.eval;

import unipassau.categories.experiment.FullResult;
import unipassau.categories.experiment.IndividualResult;
import unipassau.categories.experiment.ResultEvaluation;

import java.util.*;
import java.util.stream.Collectors;

public class Agreement {

    public static Collection<CategoryEval> getFull(String file1, String file2, Map<String, String> paraphraseTargets) {

        Map<String, CategoryEval> set1 = ImportEval.importCats(file1, paraphraseTargets);
        Map<String, CategoryEval> set2 = ImportEval.importCats(file2, paraphraseTargets);

        System.out.println("set1 size: " + set1.size());
        System.out.println("set2 size: " + set2.size());

        Collection<CategoryEval> full = new LinkedList<>();

        for (CategoryEval cat : set1.values()) {
            //System.out.println(cat);
            HashMap<String, Integer> relevant = set2.get(cat.category).relevantTargets;

            for (String r : relevant.keySet()) {
                if (!cat.relevantTargets.containsKey(r)) {
                    cat.relevantTargets.put(r, relevant.get(r));
                }
            }

            full.add(cat);
        }

        return full;
    }

    public static Map<String, CategoryEval> getAgreement(String file1, String file2, Map<String, String> paraphraseTargets) {
        Map<String, CategoryEval> set1 = ImportEval.importCats(file1, paraphraseTargets);
        Map<String, CategoryEval> set2 = ImportEval.importCats(file2, paraphraseTargets);

        Map<String, CategoryEval> agreement = new HashMap<>();

        for (CategoryEval cat : set1.values()) {
            CategoryEval catAgreement = new CategoryEval(cat.category, cat.target);
            HashMap<String, Integer> relevant = set2.get(cat.category).relevantTargets;

            for (String r : relevant.keySet()) {
                if (cat.relevantTargets.containsKey(r)) {
                    catAgreement.relevantTargets.put(r, relevant.get(r));
                }
            }

            agreement.put(catAgreement.category, catAgreement);
        }

        return agreement;
    }

    public static double agreementIndex(Collection<CategoryEval> full, Map<String, CategoryEval> positiveAgree, int size) {

        List<Double> agreementIndex = new LinkedList<>();
        for (CategoryEval fc : full) {
            CategoryEval agreed = positiveAgree.get(fc.category);
            double index = size - (fc.relevantTargets.size() - agreed.relevantTargets.size());
            agreementIndex.add(index / size);
        }
        ;
        OptionalDouble total = agreementIndex.stream().mapToDouble(a -> a).average();

        return total.getAsDouble();
    }

    public static void main(String[] args) {
        String[] langLong = new String[]{"portuguese", "german", "english"};
        String[] langTwo = new String[]{"pt", "de", "en"};

        for (int i = 0; i < langLong.length; i++) {

            String set1 = "/home/juliano/Documents/phd/linse/evaluation/LANG-eval-1.csv".replace("LANG", langLong[i]);
            String set2 = "/home/juliano/Documents/phd/linse/evaluation/LANG-eval-2.csv".replace("LANG", langLong[i]);

            String paraphrses = "/home/juliano/Documents/phd/linse/paraphrases-LANG.csv".replace("LANG", langTwo[i]);

            Map<String, String> paraphraseTargets = ImportEval.getParaphrasesTargets(paraphrses);
            Collection<CategoryEval> full = getFull(set1, set2, paraphraseTargets);
            Map<String, CategoryEval> aggrement = getAgreement(set1, set2, paraphraseTargets);

            double index = agreementIndex(full, aggrement, 50);
            double favg = full.stream().map(c -> c.relevantTargets).collect(Collectors.averagingDouble(r -> r.size()));
            double gavg = aggrement.values().stream().map(c -> c.relevantTargets).collect(Collectors.averagingDouble(r -> r.size()));

            System.out.println(langLong[i] + ": " + index);
            System.out.println("full : " + favg);
            System.out.println("agree : " + gavg);

            for (CategoryEvalCriteria criteria : new CategoryEvalCriteria[]{new CategoryEvalCriteria(10), new CategoryEvalCriteria(20)}) {
                Collection<FullResult> fullResults = new LinkedList<>();

                for (CategoryEval ag : aggrement.values()) {
                    IndividualResult ind = ResultEvaluation.getResult(ag, criteria);

                    Map<String, IndividualResult> results = new HashMap<>();
                    results.put("TOP" + criteria.top, ind);

                    FullResult fr = new FullResult();
                    fr.results = results;

                    fullResults.add(fr);
                }


                IndividualResult intanceResult = ResultEvaluation.getSummarizedResult("TOP" + criteria.top, fullResults);
                System.out.println("TOP" + criteria.top);
                System.out.println(intanceResult);
            }


        }
    }
}

//set1 size: 229
//set2 size: 229
//portuguese: 0.9489956331877729
//full : 3.7205240174672487
//agree : 1.1703056768558953
//set1 size: 169
//set2 size: 169
//german: 0.996094674556213
//full : 1.816568047337278
//agree : 1.621301775147929
//set1 size: 45
//set2 size: 45
//english: 0.9075555555555554
//full : 6.377777777777778
//agree : 1.7555555555555555
