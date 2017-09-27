package zalando.analytics.parser.wrappers;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import zalando.analytics.base.Language;

import java.util.List;
import java.util.Properties;

/**
 * Simple tokenizer wrapper for cases where full parsing is not necessary. Language must be passed during construction.
 * <p>
 * Created by Alan Akbik on 9/1/17.
 */
public class TokenizerWrapper {

    // private field for stanford pipeline object
    private StanfordCoreNLP pipeline = null;

    /**
     * Constructor for this class, requires language to be passed.
     *
     * @param language Language for which tokenization is performed.
     */
    public TokenizerWrapper(Language language) {

        // English is initalized differently
        if (language.equals(Language.ENGLISH)) {
            // init Stanford NLP for English
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit");
            props.setProperty("ssplit.isOneSentence", "true");
            pipeline = new StanfordCoreNLP(props);
        }
        // all other languages are initalized using language-specific properties
        else {
            String languageString = language.toString().substring(0, 1).toUpperCase() + language.toString().substring(1).toLowerCase();
            Properties props = StringUtils.argsToProperties(
                    "-props", "StanfordCoreNLP-" + languageString.toLowerCase() + ".properties");
            props.setProperty("annotators", "tokenize, ssplit");
            props.setProperty("ssplit.isOneSentence", "true");
            pipeline = new StanfordCoreNLP(props);
        }
    }

    /**
     * Tokenize this string, return as string in which whitespaces indicate tokens.
     * @param text String to be tokenized
     * @return String with whitespace separated tokens
     */
    public String tokenize(String text) {

        if (text.trim().equals("")) {
            return "";
        }

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        try {
            pipeline.annotate(document);
        } catch (Exception e) {
            System.out.println("text = '" + text + "'");
            e.printStackTrace();
            return "";
        }

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {

            StringBuilder whitespaceTokenized = new StringBuilder();

            // traversing the tokens in the current sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                whitespaceTokenized.append(token.get(CoreAnnotations.TextAnnotation.class)).append(" ");
            }
            return whitespaceTokenized.toString().trim();
        }

        return "";
    }
}
