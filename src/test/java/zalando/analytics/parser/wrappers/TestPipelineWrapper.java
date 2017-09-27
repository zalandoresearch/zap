package zalando.analytics.parser.wrappers;

import org.junit.Ignore;
import org.junit.Test;
import zalando.analytics.base.Language;
import zalando.analytics.base.Sentence;

/**
 * Created by Alan Akbik on 8/28/17.
 *
 * Simple class to test the PipelineWrapper to parse sentences in all supported languages.
 */
public class TestPipelineWrapper {

    @Test
    public void testParseEnglish() {

        PipelineWrapper parser = new PipelineWrapper(Language.ENGLISH);
        Sentence parse = parser.parse("Ferddy Fergunson had the opportunity to attend one of his seminars in Essen.");
        System.out.println("parse = " + parse);
    }

    @Test
    public void testParseGerman() {

        PipelineWrapper parser = new PipelineWrapper(Language.GERMAN);
        Sentence parse = parser.parse("Der dicke Mann verschlang ein Sandwich .");
        System.out.println("parse = " + parse);
    }

    @Test
    @Ignore
    public void testParseFrench() {

        PipelineWrapper parser = new PipelineWrapper(Language.FRENCH);
        Sentence parse = parser.parse("Je ne veux pas comprendre pourquoi il chante .");
        System.out.println("parse = " + parse);
    }

    @Test
    @Ignore
    public void testParseSpanish() {

        PipelineWrapper parser = new PipelineWrapper(Language.SPANISH);
        Sentence parse = parser.parse("Yo quiero Taco Bell .");
        System.out.println("parse = " + parse);
    }


    @Test
    @Ignore
    public void testParseChinese() {

        PipelineWrapper parser = new PipelineWrapper(Language.CHINESE);
        Sentence parse = parser.parse("克林顿说，华盛顿将逐步落实对韩国的经济援助。");
        System.out.println("parse = " + parse);
    }


}
