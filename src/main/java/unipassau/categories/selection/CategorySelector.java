package unipassau.categories.selection;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CategorySelector {

    public static final String RDF_SCHEMA_TYPE = "http://www.w3.org/2000/01/rdf-schema#label";

    public static final String PURL_SUBJECT = "http://purl.org/dc/terms/subject";

    public static final String WIKI_PREFIX = "http://pt.dbpedia.org/resource/";

    protected LinkedHashMap<String, String> categLabel = new LinkedHashMap<>();

    protected Map<String, Integer> descriptionSize = new HashMap<>();

    protected Map<String, Set<String>> categPopularity = new HashMap<>();

    public void process(File categLabelFile, File artCategFile) throws IOException {
        //reading the labels
        FileInputStream is = new FileInputStream(categLabelFile);
        NxParser nxp = new NxParser();
        nxp.parse(is);

        for (Node[] nx : nxp) {
            String type = nx[1].getLabel();
            if (type.equals(RDF_SCHEMA_TYPE)) {
                String categId = nx[0].getLabel().replace(WIKI_PREFIX, "");
                String desc = nx[2].getLabel();

                categLabel.put(categId, desc);
                descriptionSize.put(categId, desc.length());
            } else {
                System.out.println("UNEXPECTED TYPE: " + "");
            }
        }
        is.close();

        //counting the popularity.
        is = new FileInputStream(artCategFile);
        nxp = new NxParser();
        nxp.parse(is);

        for (Node[] nx : nxp) {
            String type = nx[1].getLabel();
            if (type.equals(PURL_SUBJECT)) {
                String instance = nx[0].getLabel().replace(WIKI_PREFIX, "");
                String category = nx[2].getLabel().replace(WIKI_PREFIX, "");

                categPopularity.putIfAbsent(category, new HashSet<>());
                Set<String> instances = categPopularity.get(category);
                instances.add(instance);

            } else {
                System.out.println("UNEXPECTED TYPE: " + "");
            }
        }
    }

    public Set<String> getCategories(boolean filter) {

        int max = 0;
        int min = Integer.MAX_VALUE;

        for (String categ : this.descriptionSize.keySet()) {
            max = Integer.max(max, this.descriptionSize.get(categ));
            min = Integer.min(min, this.descriptionSize.get(categ));
        }

        int numBuckets = 10;
        List<String>[] buckets = new List[numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            buckets[i] = new LinkedList<>();
        }

        int interval = (max - min) / numBuckets;
        System.out.println("max: " + max + " | min: " + min);


        int superior = min + interval;
        int inferior = min;
        int bucket = 0;
        while (superior < max + interval) {
            //System.out.println("interval: " + interval + " | bucket: " + bucket);

            for (String categ : this.descriptionSize.keySet()) {
                int size = this.descriptionSize.get(categ);
                if (size >= inferior && size < superior) {
                    buckets[Math.min(bucket, numBuckets - 1)].add(this.categLabel.get(categ));
                }
            }

            bucket++;
            inferior = superior;
            superior += interval;
        }

        System.out.println("Sorting...");
        PopularitySorter sorter = new PopularitySorter(this.categPopularity);
        Set<String> categories = new HashSet<>();

        final int TOTAL = 100;

        for (int i = 0; i < numBuckets; i++) {
            buckets[i].sort(sorter);
            int previousSize = categories.size();
            for (int j = 0; j < buckets[i].size() && categories.size() < previousSize + 10; j++) {

                String currentCateg = buckets[i].get(j);
                if ((!filter) || !currentCateg.matches(".*[a-zA-Z]+.*")) {
                    categories.add(currentCateg);
                }

            }
        }

        System.out.println("Completing... [" + categories.size() + "]");

        if (categories.size() < TOTAL) {
            String[] labelsArray = this.categLabel.values().toArray(new String[0]);

            while (categories.size() < TOTAL) {
                int index = (int) (labelsArray.length * Math.random());
                String currentCateg = labelsArray[index];

                if ((!filter) || !currentCateg.matches(".*[a-zA-Z]+.*")) {
                    categories.add(currentCateg);
                }
            }
        }

        System.out.println("Done!");
        return categories;
    }

    public Collection<String> getTargetDataset() {
        return this.categLabel.values();
    }


    public static void writeInFile(Collection<String> categs, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String categ : categs) {
            writer.write(categ);
            writer.write('\n');
        }

        writer.close();
    }

    public static void main(String[] args) throws Exception {
        String basedir = "/home/juliano/Documents/phd/dbpedia";

        String[] targetLangs = new String[]{"zh", "fr", "es", "it", "ar", "sv", "nl", "ru", "ja", "ko", "fa"};
        Set<String> filter = Arrays.stream(new String[]{"zh", "ar", "ru", "ja", "ko", "fa",}).collect(Collectors.toSet());

        for (String lang : targetLangs) {
            System.out.println("Processing lang " + lang + "...");

            String categLabel = "category_labels_LANG.tql".replace("LANG", lang);
            String artCateg = "article_categories_LANG.tql".replace("LANG", lang);

            String selectedCategsOut = "out/selected_categories_LANG.txt".replace("LANG", lang);
            String targetDatasetOut = "out/target_dataset_LANG.txt".replace("LANG", lang);

            CategorySelector selector = new CategorySelector();
            selector.process(new File(basedir, categLabel), new File(basedir, artCateg));
            Collection<String> selectedCategs = selector.getCategories(filter.contains(lang));
            Collection<String> targetDataset = selector.getTargetDataset();


            writeInFile(selectedCategs, new File(basedir, selectedCategsOut));
            writeInFile(targetDataset, new File(basedir, targetDatasetOut));

            System.out.println("Categs: " + selectedCategs.size() + " | target: " + targetDataset.size());
            System.out.println("Done [" + lang + "]!");
        }
    }
}

class PopularitySorter implements Comparator<String> {

    protected Map<String, Integer> popularity = new HashMap<>();

    public PopularitySorter(Map<String, Set<String>> popularity) {
        for (String categ : popularity.keySet()) {
            this.popularity.put(categ, popularity.get(categ).size());
        }
    }

    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(popularity.getOrDefault(o2, 0), popularity.getOrDefault(o1, 0));
    }
}
