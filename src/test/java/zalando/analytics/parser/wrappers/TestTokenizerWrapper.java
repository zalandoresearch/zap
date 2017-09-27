package zalando.analytics.parser.wrappers;

import org.junit.Test;
import zalando.analytics.base.Language;
import zalando.analytics.corpus.LineReader;

import java.io.*;

/**
 * Created by aakbik on 9/1/17.
 */
public class TestTokenizerWrapper {

    @Test
    public void tokenizeEnglish() throws Exception {
        TokenizerWrapper tokenizerWrapper = new TokenizerWrapper(Language.ENGLISH);
        String tokenized = tokenizerWrapper.tokenize("Where have all the flowers gone?");
        System.out.println("tokenized = " + tokenized);
    }

    @Test
    public void tokenizeGerman() throws Exception {
        TokenizerWrapper tokenizerWrapper = new TokenizerWrapper(Language.GERMAN);
        String tokenized = tokenizerWrapper.tokenize("Wo sind all die Blumen hin?");
        System.out.println("tokenized = " + tokenized);
    }

    @Test
    public void tokenizeFrench() throws Exception {
        TokenizerWrapper tokenizerWrapper = new TokenizerWrapper(Language.FRENCH);
        String tokenized = tokenizerWrapper.tokenize("Pourquoi on n'assume pas le meilleur?");
        System.out.println("tokenized = " + tokenized);
    }

    @Test
    public void tokenizeSpanish() throws Exception {
        TokenizerWrapper tokenizerWrapper = new TokenizerWrapper(Language.SPANISH);
        String tokenized = tokenizerWrapper.tokenize("Yo quiero Taco Bell?");
        System.out.println("tokenized = " + tokenized);
    }

//    @Test
//    public void tokenizeCorpus() throws Exception {
//
//        TokenizerWrapper tokenizer = new TokenizerWrapper(Language.ENGLISH);
//
//        FileWriter writer = new FileWriter(new File("/home/aakbik/Downloads/en-es.txt/OpenSubtitles2016.tok.en-es.en"));
//
//        LineReader reader = LineReader.readFromFile("/home/aakbik/Downloads/en-es.txt/OpenSubtitles2016.en-es.en");
//        String line;
//        while ((line = reader.getNextLine()) != null) {
//
//            String tokenize = tokenizer.tokenize(line);
//            if (tokenize.contains("\n")) System.out.println("tokenize = " + tokenize);
//
//            writer.append(tokenize);
//            writer.append("\n");
//        }
//        writer.close();
//    }
}
