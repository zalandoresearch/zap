package zalando.analytics.corpus;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import zalando.analytics.base.Sentence;

import java.io.*;
import java.util.List;

/**
 *
 * Class for reading a conll-u file into a list of Sentence objects.
 *
 * Created by Alan Akbik on 8/29/17.
 */
public class ConllFileReader {

    // Internal reader
    private BufferedReader reader;

    // If set to true, split only by tabs instead of tabs + whitespaces.
    private boolean tabsOnly = false;

    /*
     Constructor is private, only use readFromClasspathFile() to construct
     */
    private ConllFileReader() {
    }

    /**
     * Constructor method.
     *
     * @param fileOnClasspath Provide path to file in conll-u format
     * @return Itself for chaining constructor
     */
    public static ConllFileReader readFromClasspathFile(String fileOnClasspath) {

        ConllFileReader conllFileReader = new ConllFileReader();
        try {
            conllFileReader.reader = new BufferedReader(new InputStreamReader(
                    conllFileReader.getClass().getClassLoader().getResourceAsStream(
                            fileOnClasspath), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return conllFileReader;
    }

    public static ConllFileReader readFromFile(String file) {

        ConllFileReader conllFileReader = new ConllFileReader();
        try {
            conllFileReader.reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return conllFileReader;
    }

    /**
     * Call this function to split only using tabs.
     *
     * @return Itself for chaining constructor
     */
    public ConllFileReader splitByTabsOnly() {
        this.tabsOnly = true;
        return this;
    }

    /**
     * Read next Sentence in file
     * @return Next Sentence in file
     */
    public Sentence readNext() {
        try {
            String line;

            List<String> conllLines = Lists.newArrayList();
            while ((line = reader.readLine()) != null) {
                // in conll-u, metadata lines start with #
                if (line.trim().equals("")) break;
                else conllLines.add(line);
            }

            if (conllLines.size() == 0) return null;

            String join = Joiner.on("\n").join(conllLines);

            Sentence sentence = null;
            if (tabsOnly)
                sentence = Sentence.fromConllU_Tab(join);
            else
                sentence = Sentence.fromConllU(join);

            return sentence;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read the next noSentences sentences.
     * @param noSentences Number of sentences to read.
     * @return List of read Sentences
     */
    public List<Sentence> readSentences(int noSentences) {

        List<Sentence> sentences = Lists.newArrayList();

        for (int i = 0; i < noSentences; i++) {
            Sentence sentence = this.readNext();
            if (sentence == null) break;
            sentences.add(sentence);
        }
        return sentences;
    }
}
