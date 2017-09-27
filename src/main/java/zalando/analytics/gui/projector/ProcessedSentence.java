package zalando.analytics.gui.projector;

import java.util.List;

/**
 * Created by Alan Akbik on 4/21/17.
 *
 * Object used for communication with javascript. Contains source and target sentence, alignments and all levels of
 * supported linguistic annotation.
 */
public class ProcessedSentence {

    // Source sentence as plain string
    public String sourceSentence;

    // Target sentence as plain string
    public String targetSentence;

    // Target language as string
    public String targetLanguage;

    // token alignments between source and target sentence
    public List<List<String>> alignments;

    // List of tokens in source sentence, with token level information
    public List<List<Object>> tokensSL;

    // List of tokens in projected target sentence, with token level information
    public List<List<Object>> tokensTL_predicted;

    // List of tokens in predicted target sentence, with token level information
    public List<List<Object>> tokensTL_projected;

    // dependency arcs in source sentence
    public List<List<String>> arcsSL;

    // dependency arcs in target sentence, predicted
    public List<List<String>> arcsTL_predicted;

    // dependency arcs in source sentence, projected
    public List<List<String>> arcsTL_projected;

    // NER in source sentence
    public List<List<Object>> nerSL;

    // NER in target sentence, projected
    public List<List<Object>> nerTL_projected;
//    public List<List<Object>> nerTL_predicted;

    // SRL in source sentence
    public List<List<Object>> framesSL;

    // SRL in target sentence, using predicted dependencies
    public List<List<Object>> framesTL_predicted;

    // SRL in target sentence, using projected dependencies
    public List<List<Object>> framesTL_projected;
}
