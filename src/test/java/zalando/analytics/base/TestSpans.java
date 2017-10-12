package zalando.analytics.base;

import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 10/12/17.
 */
public class TestSpans {

    @Test
    public void testBuildBIOSentence(){

        Sentence sentence = Sentence.fromSpanAnnotated("<START:NamedPerson> Shailene Woodley <END> posed for some photos at a screening of her new film , Divergent , in <START:NamedLocation> Thousand Oaks <END> , <START:NamedLocation> California <END> .");
        System.out.println(sentence.toSpanConll());

    }
}
