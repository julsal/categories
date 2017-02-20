package unipassau.categories.experiment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Runner {

    protected static final String VERSION = "0.0.1";

    protected Criteria[] criterias = new Criteria[]{new TopCriteria(10), new TopCriteria(20), new TopCriteria(50)};

    protected Collection<FullResult> resultsBack = Collections.synchronizedList(new LinkedList<>());

    protected Map<String, List<String>> allParaphrases;

    protected Set<String> fullCategoriesSet;

    protected Map<Integer, List<String>> fullCategoriesBlock;

    protected int nrThreads;

    protected int topResults;

    protected String outputFile;

    protected String lang;

    public Runner(String fullCategories, String paraphrases, String lang, int nrThreads, int topResults) {
        this.allParaphrases = Importer.importCategories(new File(paraphrases));
        this.fullCategoriesSet = Importer.importDataset(new File(fullCategories));
        this.lang = lang;
        this.nrThreads = nrThreads;
        this.topResults = topResults;
        this.outputFile = lang + ".out";

        this.fullCategoriesBlock = new HashMap<>();
        int blockId = 0;
        final int BLOCK_SIZE = 1000;

        int counter = BLOCK_SIZE;
        List<String> block = new LinkedList<>();
        for (String cat : fullCategoriesSet) {

            if (counter-- > 0) {
                block.add(cat);
            } else {
                fullCategoriesBlock.put(blockId++, block);
                block = new LinkedList<>();
                counter = BLOCK_SIZE;
            }
        }

        if (!block.isEmpty()) {
            fullCategoriesBlock.put(blockId++, block);
        }

        System.out.println("    - generated " + fullCategoriesBlock.size() + " blocks of " + BLOCK_SIZE + " categories.");

    }

    protected Collection<Runnable> createRunnables() {
        Collection<Runnable> runnables = allParaphrases.entrySet().stream().map(
                entry -> createRunnable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return runnables;
    }

    public RunVectorComposition createRunnable(String category, List<String> paraphrases) {
        DistributionalCompositor compositor = new DistributionalCompositor(lang, "W2V");
        compositor.fullCategoriesBlock = this.fullCategoriesBlock;
        compositor.fullCategoriesSet = this.fullCategoriesSet;

        return new RunVectorComposition(category, paraphrases, compositor, resultsBack, criterias, topResults);
    }

    public void run() {
        ExecutorService tpool = Executors.newFixedThreadPool(nrThreads);
        Collection<Runnable> runnings = createRunnables();

        for (Runnable running : runnings)
            tpool.execute(running);

        tpool.shutdown();

        try {
            while (!tpool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            ObjectOutputStream oio = new ObjectOutputStream(new FileOutputStream(new File(outputFile)));
            oio.writeObject(resultsBack);
            oio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Runner v." + VERSION + " - " + this.lang;
    }

    public static void main(String[] args) {

        if (args != null && args.length == 5) {
            String fullCategoriesFile = args[0];
            String paraphrasesFile = args[1];
            String lang = args[2];
            int nrThreads = Integer.parseInt(args[3]);
            int topResults = Integer.parseInt(args[4]);

            Runner runner = new Runner(fullCategoriesFile, paraphrasesFile, lang, nrThreads, topResults);
            System.out.println(runner.toString());

            runner.run();

        } else

        {
            System.out.println("ERROR - correct param order: fullCategoriesFile paraphrasesFile lang nrThreads topResults");
        }
    }
}
