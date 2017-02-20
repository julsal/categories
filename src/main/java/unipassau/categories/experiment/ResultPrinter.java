package unipassau.categories.experiment;


import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ResultPrinter {

    public static void print(FullResult fullResult) {
        String[] relevant = fullResult.description.split("\n")[0].split(" -> ");
        String paraphrase = relevant[0];
        String original = relevant[1];

        String[] topResults = fullResult.description.split("--- TOP Results ---\n")[1].split("\n");

        List<String> catResults = new LinkedList<>();
        for (String cad : topResults) {
            String category = cad.split("    ")[1].split(": ")[0];
            //String category = cad;
            catResults.add(category);
        }

        System.out.println("------------------------------------");
        System.out.println("Query: " + paraphrase);
        System.out.println("---------     Results     ----------");
        catResults.forEach(System.out::println);
        System.out.println("####################################\n");
    }

    public static void printNumbers(List<String> scenarios, Collection<FullResult> fullResult) {

        for (String scenario : scenarios) {
            System.out.println(scenario + "\n" + ResultEvaluation.getSummarizedResult(scenario, fullResult));

        }

    }

    public static void printAll(Collection<FullResult> results) {
        for (FullResult fr : results)
            print(fr);
    }

    public static void main(String[] args) {
        String dir = "/home/juliano/Documents/phd/linse/";
        String[] files = {"de.out", "pt.out", "en.out"};
        //String[] files = {"pt.out"};

        boolean summary = true;

        for (String file : files) {
            System.out.println(file);
            Collection<FullResult> results = Importer.importResults(new File(dir, file));

            if(summary) {
                Criteria[] criterias = new Criteria[]{new TopCriteria(10), new TopCriteria(20), new TopCriteria(50)};
                printNumbers(Arrays.stream(criterias).map(Object::toString).collect(Collectors.toList()), results);

            } else {
                printAll(results);
            }
            System.out.println("--------- size: " + results.size() + " ------------------");
        }
    }
}
