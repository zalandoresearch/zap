package zalando.analytics.corpus;

import org.junit.Assert;
import org.junit.Test;
import zalando.analytics.base.Sentence;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 9/27/17.
 */
public class TestLineReader {

    /**
     * Test iterate
     */
    @Test
    public void loadFromClasspath() {

        LineReader lineReader = LineReader.readFromClasspathFile("ud-examples.conllu");

        int count = 0;
        for (String line : lineReader) count++;

        Assert.assertEquals(30, count);

    }
}
