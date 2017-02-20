package unipassau.categories.eval;

import unipassau.categories.experiment.Importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImportEval {

    public static final String QUERY_PREFFIX = "Query: ";

    public static Map<String, String> getParaphrasesTargets(String file) {
        Map<String, List<String>> paraphases = Importer.importCategories(new File(file));

        Map<String, String> results = new HashMap<>();

        for (String target : paraphases.keySet()) {
            for (String para : paraphases.get(target)) {
                results.put(para, target);
            }
        }

        return results;
    }

    public static Map<String, CategoryEval> importCats(String file, Map<String, String> paraphrasesTarget) {
        Map<String, CategoryEval> categs = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            CategoryEval cateval = null;
            String line;
            int rank = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && !line.startsWith("#") && !line.startsWith("-")) {

                    if (line.startsWith(QUERY_PREFFIX)) {
                        String candidate = line.split(";")[0].replace(QUERY_PREFFIX, "").trim();
                        cateval = new CategoryEval(candidate, paraphrasesTarget.get(candidate));
                        categs.put(cateval.category, cateval);
                        rank = 0;
                    } else {
                        rank++;
                        String[] parts = line.split(";");
                        if (parts.length > 1 && parts[1].trim().length() > 0 && parts[1].trim().equals("1")) {
                            cateval.relevantTargets.put(parts[0].trim(), rank);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categs;
    }

    public static Set<String> countAll(String file) {
        Set<String> all = new HashSet<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && !line.startsWith("#") && !line.startsWith("-")) {

                    if (!line.startsWith(QUERY_PREFFIX)) {
                        //System.out.println(line);
                        String[] parts = line.split(";");
                        if (parts.length > 0)
                            all.add(parts[0].trim());
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return all;
    }


    public static void main(String[] args) {
        String file = "/home/juliano/Documents/phd/linse/evaluation/LANG-eval-1.csv";

        for (String lang : new String[]{"english", "portuguese", "german"}) {
        /*
        String paraphrses = "/home/juliano/Documents/phd/linse/paraphrases-en.csv";
        Map<String, String> paraphraseTargets = getParaphrasesTargets(paraphrses);

        Map<String, CategoryEval> cats = new ImportEval().importCats(file, paraphraseTargets);

        cats.forEach((k, v) -> System.out.println(v));
        System.out.println(cats.size());
        */

            Set<String> all = ImportEval.countAll(file.replace("LANG", lang));
            System.out.println(lang + " = " + all.size());

        }

    }
}
