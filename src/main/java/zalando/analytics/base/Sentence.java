package zalando.analytics.base;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import zalando.analytics.helpers.StringHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Basic object that holds one sentence with different levels of NLP information. Consists of a list of tokens (words)
 * in the sentence, a list of metadata lines, as well as a list of contractions.
 * <p>
 * Created by Alan Akbik on 8/28/17.
 */
public class Sentence {

    // List of all tokens (words) in this sentence.
    private List<Token> tokens = Lists.newArrayList();

    // List of metadata strings as prefixed by '#' in conll-u format.
    private List<String> metadata = Lists.newArrayList();

    // List of contractions in this sentence prior to tokenization.
    private Map<Integer, Contraction> contractions = Maps.newHashMap();


    // ------------------------------------------------------------------------
    // Methods below are used to create a new Sentence object
    // ------------------------------------------------------------------------

    /**
     * Creates a new Sentence object from a whitespace tokenized string.
     *
     * @param text Whitespace tokenized string
     * @return Sentence object with basic tokens, but no lemmas, PoS etc.
     */
    public static Sentence fromTokenized(String text) {

        Sentence sentence = new Sentence();
        // split conll along new lines
        for (String word : text.split(" ")) {
            Token token = sentence.newToken();
            token.setText(word);
        }
        return sentence;
    }

    public static Sentence fromSpanAnnotated(String text) {

        Sentence sentence = new Sentence();
        String nerType = null;
        for (String word : text.split(" ")) {

            if (word.matches("<.*>")) {
                nerType = word.replaceAll("<START:", "").replaceAll(">", "");
                if (word.matches("<END>")) nerType = null;
            } else {
                Token token = sentence.newToken();
                token.setText(word);
                if (nerType != null) token.setNer(nerType);
            }
        }
        return sentence;
    }

    /**
     * Creates a new Sentence object from a conll-u string that is either whitespace or tab separated
     *
     * @param conllu sentence in conll-u format
     * @return Sentence object with all NLP information in conll-u string
     */
    public static Sentence fromConllU(String conllu) {
        return Sentence.fromConllU(conllu, "[ \\t]+");
    }

    /**
     * Creates a new Sentence object from a conll-u string that is tab separated. Use this method if fields in conll-u
     * may contain whitespaces.
     *
     * @param conllu sentence in conll-u format
     * @return Sentence object with all NLP information in conll-u string
     */
    public static Sentence fromConllU_Tab(String conllu) {
        return Sentence.fromConllU(conllu, "[\\t]+");
    }

    /**
     * Creates a new Sentence object from a conll-u string with separator supplied as regexp
     *
     * @param conllx    sentence in conll-u format
     * @param separator regular expression for separator (typically whitespace or tab)
     * @return Sentence object with all NLP information in conll-u string
     */
    private static Sentence fromConllU(String conllx, String separator) {

        Sentence sentence = new Sentence();

        // split conll along new lines
        String[] lines = conllx.split("\\n");

        for (String line : lines) {

            // lines that start with # are metadata lines in conll-u
            if (line.startsWith("#")) sentence.getMetadata().add(line);

                // other lines are either tokens or contractions
            else {

                String[] fields = line.split(separator);

                // if first field of line contains '-', it is a contraction
                if (fields[0].contains("-")) {
                    sentence.contractions.put(Integer.valueOf(fields[0].split("-")[0]), new Contraction(fields[0], fields[1]));
                    continue;
                }

                // create new token for all non-contraction fields
                Token token = sentence.newToken()
                        .setText(fields[1])
                        .setLemma(fields[2])
                        .setPosUniversal(fields[3])
                        .setPos(fields[4])
                        .setMorph(fields[5])
                        .setHeadId(Integer.parseInt(fields[6]))
                        .setDeprel(fields[7])
                        .setMisc(fields[9]);

                // add SRL frame if exist
                if (fields.length > 10 && fields[10].equals("Y")) {
                    token.addNewFrame(fields[11]);
                }
            }
        }

        // if there are frames, check for roles
        List<Frame> frames = sentence.getFrames();
        int tokenId = 0;
        for (String line : lines) {
            // and line that is not metadata may be a role
            if (!line.startsWith("#")) {
                tokenId++;
                String[] fields = line.split(separator);
                for (int i = 12; i < fields.length; i++) {
                    if (!fields[i].equals("_")) {
                        frames.get(i - 12).addRole(new Role(fields[i], sentence.getToken(tokenId)));
                    }
                }
            }
        }

        return sentence;
    }


    // ------------------------------------------------------------------------
    // Methods below are used to render Sentence object as string
    // ------------------------------------------------------------------------

    /**
     * Render this sentence as a whitespace-tokenized string.
     *
     * @return sentence as a whitespace-tokenized string
     */
    public String toSentence() {
        return Joiner.on(" ").join(getTexts());
    }

    /**
     * Render this sentence in conll-u format.
     *
     * @return sentence in conll-u format
     */
    public String toConllU() {

        StringBuilder conllX = new StringBuilder();
        for (String metadatum : metadata) {
            conllX.append(metadatum + "\n");
        }

        for (int i = 0; i < this.tokens.size(); ++i) {

            if (contractions.containsKey(i)) {

                Contraction contraction = contractions.get(i);

                conllX.append(StringHelper.addWhitespaces(contraction.contractedIds, 5));
                conllX.append(StringHelper.addWhitespaces(contraction.text, StringHelper.lengthOfLongestInList(this.getTexts()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getLemmas()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getPosUniversal()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getPos()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getMorph()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInt(this.getHeadIds()) + 3));
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getDeprels()) + 3));
                conllX.append("_   ");
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getMisc()) + 3));

                conllX.append("\n");
            }

            Token token = this.tokens.get(i);

            conllX.append(StringHelper.addWhitespaces(String.valueOf(token.getId()), 5));
            conllX.append(StringHelper.addWhitespaces(token.getText(), StringHelper.lengthOfLongestInList(this.getTexts()) + 3));
            conllX.append(StringHelper.addWhitespaces(token.getLemma(), StringHelper.lengthOfLongestInList(this.getLemmas()) + 3));
            conllX.append(StringHelper.addWhitespaces(token.getPosUniversal(), StringHelper.lengthOfLongestInList(this.getPosUniversal()) + 3));
            conllX.append(StringHelper.addWhitespaces(token.getPos(), StringHelper.lengthOfLongestInList(this.getPos()) + 3));
            conllX.append(StringHelper.addWhitespaces(token.getMorph(), StringHelper.lengthOfLongestInList(this.getMorph()) + 3));
            conllX.append(StringHelper.addWhitespaces(String.valueOf(token.getHeadId()), StringHelper.lengthOfLongestInt(this.getHeadIds()) + 3));
            conllX.append(StringHelper.addWhitespaces(token.getDeprel(), StringHelper.lengthOfLongestInList(this.getDeprels()) + 3));
            conllX.append("_   ");
            conllX.append(StringHelper.addWhitespaces(token.getMisc(), StringHelper.lengthOfLongestInList(this.getMisc()) + 3));

            if (token.evokesFrame()) {
                conllX.append("Y   ");
                conllX.append(StringHelper.addWhitespaces(token.getFrame().getLabel(), StringHelper.lengthOfLongestInList(this.getFrameNames()) + 3));
            } else {
                conllX.append("_   ");
                conllX.append(StringHelper.addWhitespaces("_", StringHelper.lengthOfLongestInList(this.getFrameNames()) + 3));
            }

            for (Frame frame : this.getFrames()) {
                if (frame.hasTokenRole(token)) {
                    conllX.append(StringHelper.addWhitespaces(frame.getTokenRole(token), 6));
                } else
                    conllX.append("_     ");
            }

            conllX.append("\n");
        }
        return conllX.toString().trim();
    }

    @Override
    public String toString() {

        String texts = "\n-----------------------------------------\n" + this.toSentence() + "\n---\n";

        texts += toConllU();

        texts += "\n-----------------------------------------\n";
        return texts;
    }


    public List<Constituent> getNER() {

        List<Constituent> ners = Lists.newArrayList();
        Collections.sort(tokens);

        List<Token> nerTokens = Lists.newArrayList();

        String lastNerType = "";
        for (Token token : this.getTokens()) {

            // if this token is a NE ...
            if (!token.getNer().equals("_")) {

                // it is either a NEW ner
                if (!token.getNer().equals(lastNerType)) {

                    if (nerTokens.size() > 0) {
                        Constituent ner = new Constituent(Lists.newArrayList(nerTokens)).setType(lastNerType);
                        ners.add(ner);
                    }
                    nerTokens = Lists.newArrayList(token);
                }
                // or a continuation of a previous NER
                else {
                    nerTokens.add(token);
                }
                lastNerType = token.getNer();


            }
            // if not, add new NER if last token was NER
            else {
                if (nerTokens.size() > 0) {
                    Constituent ner = new Constituent(Lists.newArrayList(nerTokens)).setType(lastNerType);
                    ners.add(ner);
                }
                nerTokens = Lists.newArrayList();
                lastNerType = "";
            }
        }

        return ners;
    }


    public List<Constituent> getVerbalConstituents() {
        List<Constituent> constituents = Lists.newArrayList();
        for (Token token : this.getTokens()) {

            if (token.getPosUniversal().equals("VERB")) {
                Token predicate = token;
                if (token.getDeprel().equals("cop")) {
                    predicate = token.getHead();
                    constituents.add(predicate.getConstituent(token));
                }
                for (Token child : predicate.getChildren()) {
                    constituents.add(child.getConstituent(token));
                }
            }
        }
        return constituents;
    }


    // ------------------------------------------------------------------------
    // Methods to create or access Tokens in Sentence
    // ------------------------------------------------------------------------

    /**
     * Create a new empty Token in this sentence. Used for chaining constructors.
     *
     * @return New Token in this sentence
     */
    public Token newToken() {
        Token token = new Token(this);
        int id = tokens.size() + 1;
        token.setId(id);
        this.tokens.add(token);
        return token;
    }

    /**
     * Get Token by its index.
     *
     * @param i position index of Token.
     * @return Token at this position in Sentence.
     */
    public Token getToken(int i) {
        return this.tokens.get(i - 1);
    }

    /**
     * Get all Tokens as a List
     *
     * @return all Tokens as a List
     */
    public List<Token> getTokens() {
        return tokens;
    }


    // ------------------------------------------------------------------------
    // Helper methods to get lists of strings of Token fields
    // ------------------------------------------------------------------------

    /**
     * Get all Token surface texts as a list.
     *
     * @return all Token surface texts as a list.
     */
    public List<String> getTexts() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getText());
        }
        return texts;
    }

    /**
     * Get all Token lemmas as a list.
     *
     * @return all Token lemmas as a list.
     */
    public List<String> getLemmas() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getLemma());
        }
        return texts;
    }

    /**
     * Get all Token universal PoS tags as a list.
     *
     * @return all Token universal PoS tags as a list.
     */
    public List<String> getPosUniversal() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getPosUniversal());
        }
        return texts;
    }

    /**
     * Get all Token PoS tags as a list.
     *
     * @return all Token PoS tags as a list.
     */
    public List<String> getPos() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getPos());
        }
        return texts;
    }

    /**
     * Get all Token morphological info as a list.
     *
     * @return all Token morphological info as a list.
     */
    public List<String> getMorph() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getMorph());
        }
        return texts;
    }

    /**
     * Get all Token head ids as a list.
     *
     * @return all Token head ids as a list.
     */
    public List<Integer> getHeadIds() {
        List<Integer> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getHeadId());
        }
        return texts;
    }

    /**
     * Get all Token deprels as a list.
     *
     * @return all Token deprels as a list.
     */
    public List<String> getDeprels() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getDeprel() + "");
        }
        return texts;
    }

    /**
     * Get all Token misc field as a list.
     *
     * @return all Token misc field as a list.
     */
    public List<String> getMisc() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            texts.add(token.getMisc());
        }
        return texts;
    }

    /**
     * Get all Token frame names as a list.
     *
     * @return all Token frame names as a list.
     */
    public List<String> getFrameNames() {
        List<String> texts = Lists.newArrayList();
        for (Token token : tokens) {
            if (token.evokesFrame())
                texts.add(token.getFrame().getLabel());
        }
        return texts;
    }

    /**
     * Get all Frames in this Sentence.
     *
     * @return all Frames in this Sentence.
     */
    public List<Frame> getFrames() {
        List<Frame> frames = Lists.newArrayList();
        for (Token token : tokens) {
            if (token.evokesFrame()) frames.add(token.getFrame());
        }
        return frames;
    }


    /**
     * Get all metadata in this sentence
     *
     * @return All metadata in this sentence
     */
    public List<String> getMetadata() {
        return metadata;
    }
}
