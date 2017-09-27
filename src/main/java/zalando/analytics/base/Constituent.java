package zalando.analytics.base;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;

/**
 * Class for holding a syntactic constituent, which is a list of tokens. Constituents can determine their syntactic heads
 * and their Jaccard similarity to other constituents.
 * <p>
 * Created by Alan Akbik on 8/30/17.
 */
public class Constituent {

    // List of Tokens that make up this Constituent
    private List<Token> tokens;

    // Constituent type
    private String type = "Constituent";

    /**
     * Create a new Constituent by providing all Tokens in constituent
     *
     * @param tokens List of Tokens in Constituent
     */
    public Constituent(List<Token> tokens) {
        Collections.sort(tokens);
        this.tokens = tokens;
    }

    /**
     * Get all Tokens in this Constituent
     *
     * @return all Tokens in this Constituent
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Get the syntactic head of this constituent
     *
     * @return Syntactic head of this constituent
     */
    public Token getHead() {

        if (this.getTokens().size() == 0) return null;

        // for each token, for up in tree while in constituent
        Token head = this.getTokens().get(0);
        while (true) {
            if (this.getTokens().contains(head.getHead())) head = head.getHead();
            else break;
        }
        return head;
    }

    /**
     * Determine and return the Jaccard similarity between this and another Constituent. Similarity is higher the more
     * Tokens both Constituents share, i.e. 1 if all Tokens the same and 0 if all Tokens different.
     *
     * @param otherConstituent The Constituent to which the similarity is computed.
     * @return Jaccard similarity
     */
    public double jaccardSimilarity(Constituent otherConstituent) {

        int overlap = Sets.intersection(Sets.newHashSet(this.getTokens()), Sets.newHashSet(otherConstituent.getTokens())).size();
        int union = Sets.union(Sets.newHashSet(this.getTokens()), Sets.newHashSet(otherConstituent.getTokens())).size();

        return (double) overlap / union;
    }

    // ------------------------------------------------------------------------
    // ToString methods
    // ------------------------------------------------------------------------

    public String toString() {
        Collections.sort(this.getTokens());
        String rendering = "";
        for (Token tokenInConstituent : tokens) {
            rendering += tokenInConstituent.getText() + " ";
        }
        return rendering.trim();
    }

    /**
     * Variant of toString which returns a span as given by first and last token in constituent
     *
     * @return String span as given by first and last token in constituent
     */
    public String toStringFull() {

        Collections.sort(this.getTokens());
        Token first = this.getTokens().get(0);
        Token last = this.getTokens().get(this.getTokens().size() - 1);

        String rendering = "";
        for (int i = first.getId(); i <= last.getId(); i++) {
            rendering += first.sentence.getToken(i).getText() + " ";
        }

        return rendering.trim();
    }

    public Constituent setType(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }
}
