package zalando.analytics.base;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * Main class for holding a token (word) with different levels of NLP information. Each Token is assigned a Sentence.
 * Achtung: Constructor is protected, i.e. make new Token object using static creator in Sentence object.
 * <p>
 * Created by Alan Akbik on 8/28/17.
 */
public class Token implements Comparable<Token> {

    // Positional index of Token in Sentence.
    private int id;

    // Surface form text of this Token.
    private String text = UNSET;

    // Lemma of this Token.
    private String lemma = UNSET;

    // Morphological information of this Token.
    private String morph = UNSET;

    // Universal PoS tag of this Token.
    private String posUniversal = UNSET;

    // Language-specific PoS tag of this Token.
    private String pos = UNSET;

    // Id of syntactic head of this token (0 is root).
    private int headId = 0;

    // Dependency relation to syntactic head.
    private String deprel = UNSET;

    // Field for miscellaneous extra information, such as whether the next Token is whitespace separated.
    private String misc = UNSET;

    // If this Token evokes a semantic Frame, this field is set.
    private Frame frame = null;

    // Sentence object to which this token belongs.
    protected Sentence sentence;

    // default string for unset fields
    private static String UNSET = "_";

    /**
     * Constructor for new Token. Called from Sentence object, i.e. use Sentence.addToken() to create new Token.
     *
     * @param sentence
     */
    protected Token(Sentence sentence) {
        this.sentence = sentence;
    }

    /**
     * Make this Token into a Frame
     *
     * @param frame label of frame evoked by this token
     * @return generated frame.
     */
    public Frame addNewFrame(String frame) {
        this.frame = new Frame(frame);
        return this.frame;
    }

    /**
     * Return true if this Token evokes a Frame
     *
     * @return true if this Token evokes a Frame, else false
     */
    public boolean evokesFrame() {
        return this.frame != null;
    }


    // ------------------------------------------------------------------------
    // Dependency tree methods to create Constituents or paths in tree
    // ------------------------------------------------------------------------

    /**
     * Get the syntactic head as Token object
     *
     * @return Syntactic head as Token object
     */
    public Token getHead() {
        if (this.getHeadId() < 1) return null;

        for (Token token : sentence.getTokens()) {
            if (token.getId() == this.getHeadId()) return token;
        }
        return null;
    }

    /**
     * Get all direct children of this Token.
     *
     * @return All direct children of this Token.
     */
    public List<Token> getChildren() {
        List<Token> children = Lists.newArrayList();
        for (Token token : this.sentence.getTokens()) {
            if (token.getHeadId() == this.getId()) children.add(token);
        }
        return children;
    }

    /**
     * Get entire subtree of this token as list of children
     *
     * @return All children of this token as list
     */
    public List<Token> getAllChildren() {
        List<Token> children = Lists.newArrayList();
        getChildrenRecursive(this, children);
        return children;
    }

    /**
     * internal recursive method for getting all children
     */
    private void getChildrenRecursive(Token head, List<Token> children) {
        for (Token child : head.getChildren()) {
            // dont fo into predicate
            children.add(child);
            getChildrenRecursive(child, children);
        }
    }

    /**
     * For this token, generate the full syntactic constituent with regards to a predicate.
     *
     * @param predicate Predicate to which constituent relates.
     * @return Constituent object
     */
    public Constituent getConstituent(Token predicate) {

        // if this constituent is the target of a copular, special rules apply
        if (predicate.getDeprel().equals("cop") && this.equals(predicate.getHead())) {

            List<Token> copular = Lists.newArrayList(this);
            for (Token token : this.getChildren()) {
                if (token.getDeprel().equals("amod")) copular.add(token);
                if (token.getDeprel().equals("det")) copular.add(token);
                if (token.getDeprel().equals("compound")) copular.add(token);
                if (token.getDeprel().equals("nmod")) copular.add(token);
            }
            Collections.sort(copular);
            return new Constituent(copular);
        }

        List<Token> allChildren = getAllChildren();

        // if constituent does not contain the predicate, return
        if (!allChildren.contains(predicate)) {
            allChildren.add(this);
            return new Constituent(allChildren);

        }
        // if constituent contains predicate: (1) filter tree branch with predicate and (2) identify group that contains
        // head word
        else {
            // (1) filter tree branch that contains predicate
            List<Token> filteredConstituent = Lists.newArrayList();
            for (Token allChild : allChildren) {
                if (allChild.getPathToHead().contains(predicate)) System.out.println("allChild = " + allChild);
                else filteredConstituent.add(allChild);
            }
            filteredConstituent.add(this);

            // (2) filtering branch may introduce gaps in constituent. Therefore, identify group that contains head
            // word as main part of constituent
            Collections.sort(filteredConstituent);
            List<Token> group = Lists.newArrayList();

            // identify gap in constituent
            for (Token token : filteredConstituent) {

                if (group.size() > 0 && token.getId() != group.get(group.size() - 1).getId() + 1) {

                    // if group contains head word, return
                    if (group.contains(this)) return new Constituent(group);
                    group = Lists.newArrayList();
                }
                group.add(token);
            }
            return new Constituent(group);
        }
    }

    /**
     * Get the sequence of syntactic heads up to root from this token as list.
     *
     * @return sequence of syntactic heads up to root from this token as list.
     */
    private List<Token> getPathToHead() {
        List<Token> headPath = Lists.newArrayList();
        Token head = this;
        while (true) {
            headPath.add(head);
            if (head.getHeadId() > 0) head = head.getHead();
            else break;
        }
        return headPath;
    }

    // ------------------------------------------------------------------------
    // Getters and setters for all fields in this object
    // ------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public Token setText(String text) {
        this.text = text;
        return this;
    }

    public String getLemma() {
        return lemma;
    }

    public Token setLemma(String lemma) {
        this.lemma = lemma;
        return this;
    }

    public String getMorph() {
        return morph;
    }

    public Token setMorph(String morph) {
        this.morph = morph;
        return this;
    }

    public String getPosUniversal() {
        return posUniversal;
    }

    public Token setPosUniversal(String posUniversal) {
        this.posUniversal = posUniversal;
        return this;
    }

    public String getPos() {
        return pos;
    }

    public Token setPos(String pos) {
        this.pos = pos;
        return this;
    }

    public int getHeadId() {
        return headId;
    }

    public Token setHeadId(int headId) {
        this.headId = headId;
        return this;
    }

    public String getDeprel() {
        return deprel;
    }

    public Token setDeprel(String deprel) {
        this.deprel = deprel;
        return this;
    }

    public String getMisc() {
        return misc;
    }

    public Token setMisc(String misc) {
        this.misc = misc;
        return this;
    }

    public Frame getFrame() {
        return frame;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.valueOf(this.getId()) + "\t" + this.getText();
    }

    @Override
    public int compareTo(Token o) {
        return this.getId() - o.getId();
    }

    public boolean hasLowAlignmentExpectation() {

        if (this.getLemma().equals("--")) return true;
        if (this.getPosUniversal().equals("PUNCT")) return true;
//        if (this.getDeprel().equals("det") && !this.getPos().startsWith("N")) return true;
        if (this.getDeprel().equals("case")) return true;

        return false;
    }

    public String getNer() {
        if (this.misc.contains("Ner=")) {
            return misc.replaceFirst("Ner=", "");
        }
        return UNSET;
    }

    public Token addAnnotation(String type, String annotation) {
        if (annotation.equals("_"))
            return this;

        if (this.misc.equals("_")) this.misc = type + "=" + annotation;
        else this.misc += " " + type + "=" + annotation;

        return this;
    }

    public String getAnnotation(String type){
        for (String field : this.misc.split(" ")) {
            if (field.startsWith(type + "=")) return field.replaceFirst(type + "=", "");
        }
        return UNSET;
    }

    public Token setNer(String ner) {

        if (ner.equals("_"))
            return this;

        if (this.misc.equals("_")) this.misc = "Ner=" + ner;
        else if (!this.misc.contains("Ner")) this.misc += "Ner=" + ner;
        else this.misc += " Ner=" + ner;

        return this;
    }
}
