package zalando.analytics.annotation.transfer;

import com.google.common.collect.*;
import zalando.analytics.base.Sentence;
import zalando.analytics.base.Token;
import zalando.analytics.helpers.CollectionHelper;
import zalando.analytics.helpers.StringHelper;

import java.util.*;

/**
 * Created by Alan Akbik on 8/28/17.
 * <p>
 * Class that holds a bisentence, i.e. two sentences that are translations of each other. One of the sentences is the
 * source sentence, while the other is the target (for the purpose of annotation projection).
 */
public class BiSentence {

    // The source side of the bisentence
    Sentence sentenceSL;

    // Target side of the bisentence
    Sentence sentenceTL;

    // Word alignments between both sentences
    public Table<Token, Token, Double> aligments = HashBasedTable.create();

    /**
     * Constructor for the bisentence. Requires source and target sentences.
     *
     * @param sentenceSL Source sentence
     * @param sentenceTL Target sentence
     */
    public BiSentence(Sentence sentenceSL, Sentence sentenceTL) {
        this.sentenceSL = sentenceSL;
        this.sentenceTL = sentenceTL;
    }

    /**
     * Compute word alignments between source and target sentence using a HeuristicAligner
     *
     * @param aligner Aligner that contains source/target word similarities
     * @return Aligned sentence
     */
    public BiSentence align(HeuristicAligner aligner) {

        // this map records all possible alignments
        Map<Alignment, Double> pairDistance = Maps.newHashMap();

        // Go through all source and target language tokens
        for (Token targetToken : sentenceTL.getTokens()) {

            // skip tokens with los alignment expectation
            if (targetToken.hasLowAlignmentExpectation()) continue;

            for (Token sourceToken : sentenceSL.getTokens()) {

                if (sourceToken.hasLowAlignmentExpectation()) continue;

                // get token similarity, try combinations of lemmas and text
                double similarity = aligner.getSimilarity(sourceToken.getText().toLowerCase(), targetToken.getText().toLowerCase());
                double other = aligner.getSimilarity(sourceToken.getLemma().toLowerCase(), targetToken.getText().toLowerCase());
                if (other > similarity) similarity = other;
                other = aligner.getSimilarity(sourceToken.getText().toLowerCase(), targetToken.getLemma().toLowerCase());
                if (other > similarity) similarity = other;
                other = aligner.getSimilarity(sourceToken.getLemma().toLowerCase(), targetToken.getLemma().toLowerCase());
                if (other > similarity) similarity = other;
                if (targetToken.getText().equals(sourceToken.getText())) {
                    other = 0.5;
                    if (other > similarity) similarity = other;
                }

                // if similarity exists, add alignment
                if (similarity > 0.01) {

                    int tokenDistance = sourceToken.getId() - targetToken.getId();
                    if (targetToken.getId() > sourceToken.getId())
                        tokenDistance = targetToken.getId() - sourceToken.getId();

                    similarity -= (double) tokenDistance / 100;
                    pairDistance.put(new Alignment(sourceToken, targetToken), similarity);
                }
            }
        }

        Set<Token> mappedSource = Sets.newHashSet();
        Set<Token> mappedTarget = Sets.newHashSet();

        // go through all possible alignments, sorted by most probably alignment first
        for (Map.Entry<Alignment, Double> entry : CollectionHelper.sortMapByValueDesc(pairDistance)) {

            // add alignment if source and target token are not yet aligned
            if (entry.getValue() > 0 && !mappedSource.contains(entry.getKey().sl) && !mappedTarget.contains(entry.getKey().tl)) {
                this.aligments.put(entry.getKey().sl, entry.getKey().tl, 1.);
                mappedSource.add(entry.getKey().sl);
                mappedTarget.add(entry.getKey().tl);
            }
        }

        return this;
    }


    /**
     * Return aligned token if one exists, else returns null
     *
     * @param token Token to which alignment is sough
     * @return Aligned token if exists (null otherwise)
     */
    public Token getAligned(Token token) {

        if (aligments.rowKeySet().contains(token)) {
            Map<Token, Double> row = aligments.row(token);
            return CollectionHelper.sortMapByValueDesc(row).first().getKey();
        }
        if (aligments.columnKeySet().contains(token)) {
            Map<Token, Double> column = aligments.column(token);
            return CollectionHelper.sortMapByValueDesc(column).first().getKey();
        }

        return null;
    }


    /**
     * Copy alignments from other BiSentence
     *
     * @param biSentence BiSentence from which to copy alignments
     */
    public void copyAlignments(BiSentence biSentence) {

        for (Table.Cell<Token, Token, Double> alignment : biSentence.aligments.cellSet()) {

            Token source = this.getSentenceSL().getToken(alignment.getRowKey().getId());
            Token target = this.getSentenceTL().getToken(alignment.getColumnKey().getId());
            this.aligments.put(source, target, alignment.getValue());
        }
    }

    // ------------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------------
    public Sentence getSentenceSL() {
        return sentenceSL;
    }

    public Sentence getSentenceTL() {
        return sentenceTL;
    }


    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------
    public String toString() {

        // first determine longest token
        int longestSLToken = 0;
        for (Token token : this.sentenceSL.getTokens()) {
            int length = token.getText().length();
            if (length > longestSLToken) longestSLToken = length;
        }
        int longestTLToken = 0;
        for (Token token : this.sentenceTL.getTokens()) {
            int length = token.getText().length();
            if (length > longestTLToken) longestTLToken = length;
        }

        StringBuilder out = new StringBuilder("\n" + StringHelper.addWhitespaces("", longestTLToken));
        String separatir = " ";
        for (Token sl : this.sentenceSL.getTokens()) {
            out.append(separatir + StringHelper.addWhitespaces(sl.getText(), sl.getText().length() + 1));
        }
        out.append("\n");
        for (Token tl : this.sentenceTL.getTokens()) {
            out.append(StringHelper.addWhitespaces(tl.getText(), longestTLToken));
            for (Token sl : this.sentenceSL.getTokens()) {
                if (this.aligments.contains(sl, tl)) {
                    out.append(separatir + StringHelper.centerInWhitespaces("X", sl.getText().length()) + " ");
                } else out.append(separatir + StringHelper.addWhitespaces("", sl.getText().length() + 1));

            }
            out.append("\n");
        }
        return out.toString();

    }


    /**
     * private class for alignments
     */
    private class Alignment {
        public Alignment(Token sl, Token tl) {
            this.sl = sl;
            this.tl = tl;
        }

        Token sl;
        Token tl;

        @Override
        public String toString() {
            return sl.getText() + " -- " + tl.getText();
        }
    }

}
