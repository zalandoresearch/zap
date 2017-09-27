package zalando.analytics.base;

/**
 * Simple class that holds a contraction. For instance, the German preposition "im" will be tokenized as "in dem".
 * This class is used to store the original form of the contraction and which tokens it pertains to.
 *
 * Created by Alan Akbik on 8/28/17.
 */
class Contraction {

    // IDs of Tokens in contraction.
    String contractedIds;

    // Original text of contraction.
    String text;

    /**
     * Constructor is package private (for now), since it is only called when reading conll-u format in Sentence object.
     *
     * @param contractedIds IDs of Tokens in contraction
     * @param text Original text of contraction
     */
    Contraction(String contractedIds, String text) {
        this.contractedIds = contractedIds;
        this.text = text;
    }
}
