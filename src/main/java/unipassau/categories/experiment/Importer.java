package unipassau.categories.experiment;

import java.io.*;
import java.util.*;

public class Importer {

    public static Map<String, List<String>> importCategories(File file) {
        System.out.println("    - importing file '" + file + "'...");
        Map<String, List<String>> categories = new HashMap<>();

        int paraphrses = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String[] parts = line.split(";");

                    List<String> list = new LinkedList<>();
                    categories.put(parts[0], list);

                    for (int i = 1; i < parts.length; i++) {
                        String paraphrase = parts[i].trim();
                        if (paraphrase.length() > 0 && !paraphrase.startsWith("#"))
                            list.add(parts[i]);
                    }

                    paraphrses += list.size();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("    - " + categories.size() + " categories were imported representing " + paraphrses + " paraphrses.");
        return categories;
    }

    public static Set<String> importDataset(File file) {
        System.out.println("    - importing dataset from '" + file + "'...");
        Set<String> categories = new HashSet<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("---"))
                    categories.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("    - " + categories.size() + " categories were imported!");

        return categories;
    }

    public static Collection<FullResult> importResults(File file) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            Object object = input.readObject();

            return (Collection<FullResult>) object;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
