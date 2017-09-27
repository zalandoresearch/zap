package zalando.analytics.annotation.transfer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zalando.analytics.base.Language;
import zalando.analytics.corpus.LineReader;
import zalando.analytics.helpers.CollectionHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Alan Akbik on 4/26/17.
 *
 * Class that is used to word-align a BiSentence on the fly. Contains a table of source/target token similarities that
 * needs to be precomputed (for instance) from aligned corpora. Pre-computed similarities are packaged for some
 * language pairs in src/main/resources/alignment.
 */
public class HeuristicAligner {

    // Table for source/target token similarities
    Table<String, String, Double> similarities = HashBasedTable.create();

    // Logger
    private static final Logger logger = LogManager.getLogger(HeuristicAligner.class);

    /**
     * Get instance of pre-computed heuristic aligner for language. Currently, source language is always English.
     *
     * @param language Target language for alignment probabilities
     * @return Heuristic aligner for English to target language
     */
    public static HeuristicAligner getInstance(Language language) {

        logger.info("... loading heuristic aligner for " + language);

        HeuristicAligner dictionary = new HeuristicAligner();

        // Pre-computed translation probabilities are packaged in src/main/resources/alignment and always follow the
        // pattern "language"-hmm.dict
        LineReader lineReader = LineReader.readFromClasspathFile("alignment/" + language.toString().toLowerCase() + "-hmm.dict");

        // Read dictionary into memory.
        while (true) {
            String line = lineReader.getNextLine();
            if (line == null) break;

            String[] fields = line.trim().split("\\t");
            if (fields.length == 3) {
                Double prob = Double.parseDouble(fields[2]);
                dictionary.similarities.put(fields[0], fields[1], prob);
            }
        }

        return dictionary;
    }

    /**
     * Get similarity (currently translation probability) between source and target language string.
     * Higher means more similar.
     *
     * @param source Source word (English) as string
     * @param target Target word as string
     * @return Similarity
     */
    public double getSimilarity(String source, String target) {
        if (!this.similarities.contains(source, target)) return 0;
        return this.similarities.get(source, target);
    }


    // ------------------------------------------------------------------------
    // Methods for test initialization of HeuristicAligner (mostly for unit tests)
    // ------------------------------------------------------------------------

    /**
     * Returns an empty dictionary (currently mostly used for unit test purposes
     *
     * @return Empty dictionary
     */
    public static HeuristicAligner emptyDictionary() {
        return new HeuristicAligner();
    }

    /**
     * Add translation to dictionary (similarity set to 1.)
     *
     * @param source Source word (English) as string
     * @param target Target word as string
     */
    public void addTranslation(String source, String target) {
        this.similarities.put(source, target, 1.);
    }

    /**
     * Add translation to dictionary)
     *
     * @param source Source word (English) as string
     * @param target Target word as string
     * @param similarity Similarity between both (higher is more similar)
     */
    public void addTranslation(String source, String target, double similarity) {
        this.similarities.put(source, target, similarity);
    }

    // ------------------------------------------------------------------------
    // Methods for preparing the heuristic aligner for new languages
    // ------------------------------------------------------------------------

    /**
     * Helper method that takes the translation probabilities as output by the BerkeleyAligner and computes
     * the alignment file
     *
     * @param basepath Folder containing the translation probabilities as output by the BerkeleyAligner
     * @param pathToTSV Path for the produced alignment file
     */
    public static void prepareDictionary(String basepath, String pathToTSV) {

        double minSimilarity = 0.01;
        String stage = "2";

        try {
            FileWriter writer = new FileWriter(new File(pathToTSV));

            HeuristicAligner dictionary = new HeuristicAligner();

            LineReader lineReader = LineReader.readFromFile(basepath + "/" + "stage" + stage + ".1.params.txt");
            while (true) {
                if (lineReader.getNextLine().trim().equals("# Translation probabilities")) break;
            }

            String currentWord = "";
            while (true) {
                String line = lineReader.getNextLine();
                if (line == null) break;

                if (!line.startsWith(" ")) currentWord = line.split("\\t")[0];
                else {
                    String[] fields = line.trim().split(": ");
                    if (fields.length == 2) {
                        String translation = fields[0];
                        Double prob = Double.parseDouble(fields[1]);

                        if (prob > minSimilarity)
                            dictionary.similarities.put(currentWord, translation, prob);
                    }
                }
            }

            lineReader = LineReader.readFromFile(basepath + "/" + "stage" + stage + ".2.params.txt");
            while (true) {
                if (lineReader.getNextLine().trim().equals("# Translation probabilities")) break;
            }

            currentWord = "";
            while (true) {
                String line = lineReader.getNextLine();
                if (line == null) break;

                if (!line.startsWith(" ")) currentWord = line.split("\\t")[0];
                else {
                    String[] fields = line.trim().split(": ");
                    if (fields.length == 2) {
                        String translation = fields[0];
                        Double prob = Double.parseDouble(fields[1]);

                        if (prob > minSimilarity)
                            if (dictionary.similarities.contains(translation, currentWord)) {
                                if (prob > dictionary.similarities.get(translation, currentWord))
                                    dictionary.similarities.put(translation, currentWord, prob);
                            } else
                                dictionary.similarities.put(translation, currentWord, prob);
                    }
                }
            }

            for (String source : dictionary.similarities.rowKeySet()) {
                for (Map.Entry<String, Double> sortedTranslation : CollectionHelper.sortMapByValueDesc(dictionary.similarities.row(source))) {
                    writer.append(source + "\t" + sortedTranslation.getKey() + "\t" + sortedTranslation.getValue() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
