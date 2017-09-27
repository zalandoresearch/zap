package zalando.analytics.gui.projector;

import com.google.common.collect.Lists;
import zalando.analytics.base.Language;
import zalando.analytics.base.Sentence;

import java.util.List;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 9/13/17.
 * <p>
 * Object that holds a corpus of parsed sentences for the Web UI.
 */
public class Corpus {

    // Sentences in this corpus
    List<Sentence> sentences = Lists.newArrayList();

    // Language of this corpus
    Language corpusLanguage;

    /**
     * Constructor
     *
     * @param sentences      Sentences in this corpus
     * @param corpusLanguage Language of this corpus
     */
    public Corpus(List<Sentence> sentences, Language corpusLanguage) {
        this.sentences = sentences;
        this.corpusLanguage = corpusLanguage;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public Language getCorpusLanguage() {
        return corpusLanguage;
    }
}
