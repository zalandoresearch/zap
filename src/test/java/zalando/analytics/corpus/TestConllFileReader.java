package zalando.analytics.corpus;

import org.junit.Assert;
import org.junit.Test;
import zalando.analytics.base.Sentence;

import java.util.List;

/**
 * Created by Alan Akbik on 8/29/17.
 *
 * Test class for ConllFileReader
 */
public class TestConllFileReader {

    /**
     * Test load one sentence
     */
    @Test
    public void loadOneFromClasspath() {

        ConllFileReader conllFileReader = ConllFileReader.readFromClasspathFile("ud-examples.conllu");
        Sentence sentence = conllFileReader.readNext();

        Assert.assertEquals("The paint and wheels looked like glass and the interior looked new !", sentence.toSentence());

    }

    /**
     * Test load many sentences
     */
    @Test
    public void loadManyFromClasspath() {

        ConllFileReader conllFileReader = ConllFileReader.readFromClasspathFile("ud-examples.conllu");

        List<Sentence> sentences = conllFileReader.readSentences(10);

        Assert.assertEquals(2, sentences.size());

        for (Sentence sentence : sentences) {
            System.out.println(sentence);
        }
    }
}
