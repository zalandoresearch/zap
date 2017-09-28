package zalando.analytics.parser.wrappers;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import org.apache.commons.io.FileUtils;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.pipeline.Pipeline;
import zalando.analytics.base.*;
import zalando.analytics.base.Language;
import zalando.analytics.helpers.StringHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * Wrapper around several NLP libraries to support tokenization, lemmatization, PoS tagging, dependency parsing and
 * semantic role labeling for various languages. The Language needs to be passed to the constructor, then the pipeline
 * is automatically set up.
 * <p>
 * Created by Alan Akbik on 8/28/17.
 */
public class PipelineWrapper {

    // Language of this pipeline, defaults to English.
    private Language language = Language.ENGLISH;

    // Anna lemmatizer for this pipeline (note: some languages such as Chinese do not have lemmatizers)
    private Lemmatizer lemmatizer;

    // Stanford CoreNLP object for most NLP preprocessing
    private StanfordCoreNLP pipeline = null;

    // MATE semantic role labeler for SRL in English
    private SemanticRoleLabeler semanticRoleLabeler;

    /**
     * Constructor for pipeline object. Require language to be specified.
     *
     * @param language Target language for this pipeline.
     */
    public PipelineWrapper(Language language) {

        this.language = language;

        String languageString = language.toString().substring(0, 1).toUpperCase() + language.toString().substring(1).toLowerCase();

        //---------------------------------
        // StanfordNLP
        //---------------------------------
        Properties props = new Properties();

        // use NN parsers for non-English languages
        if (!language.equals(Language.ENGLISH)) {
            props = StringUtils.argsToProperties(
                    "-props", "StanfordCoreNLP-" + languageString.toLowerCase() + ".properties");
            props.setProperty("annotators", "tokenize, ssplit, pos, depparse");
            props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/UD_" + languageString + ".gz");
        } else {
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
            props.setProperty("ssplit.isOneSentence", "true");
        }

        // initialize STANFORD NLP
        pipeline = new StanfordCoreNLP(props);

        //---------------------------------
        // ANNA tools
        //---------------------------------
        // use ANNA lemmatizers for non-English and non-Chinese languages
        if (!language.equals(Language.ENGLISH) && !language.equals(Language.CHINESE)) {

            InputStream cpResource = this.getClass().getClassLoader().getResourceAsStream("models/lemma-" + languageString.toLowerCase() + "-3.6.model");
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("file", "temp");
                FileUtils.copyInputStreamToFile(cpResource, tmpFile); // FileUtils from apache-io
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert tmpFile != null;
            try {
                String absolutePath = tmpFile.getAbsolutePath();
                System.out.println("absolutePath = " + absolutePath);
                lemmatizer = new Lemmatizer(absolutePath, false);
            } finally {
                boolean delete = tmpFile.delete();

            }
        }

        //---------------------------------
        // MATE tools
        //---------------------------------
        // use MATE semantic role labeler for English
        if (language.equals(Language.ENGLISH)) {

            se.lth.cs.srl.languages.Language.setLanguage(se.lth.cs.srl.languages.Language.L.eng);

            // init MateSRL for English
            ZipFile zipFile = null;
            try {
                String srlModel = "srl-english.model";
                InputStream cpResource = this.getClass().getClassLoader().getResourceAsStream("models/" + srlModel);
                System.out.println("cpResource = " + cpResource);
                if (cpResource != null) {
                    File tmpFile = null;
                    try {
                        tmpFile = File.createTempFile("file", "temp");
                        FileUtils.copyInputStreamToFile(cpResource, tmpFile); // FileUtils from apache-io
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (tmpFile != null)
                        try {
                            String absolutePath = tmpFile.getAbsolutePath();
                            zipFile = new ZipFile(absolutePath);
                            semanticRoleLabeler = Pipeline.fromZipFile(zipFile);
                            zipFile.close();
                        } finally {
                            boolean delete = tmpFile.delete();
                        }
                } else {
                    // print warning
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to parse a plain text sentence with the full NLP pipeline.
     *
     * @param text Plain text sentence.
     * @return Parsed sentence.
     */
    public Sentence parse(String text) {

        if (text.trim().equals("")) {
            return new Sentence();
        }

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {

            Sentence parse = new Sentence();

            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            int i = 0;
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {

                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);

                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                Token newtoken = parse.newToken().setText(word).setPos(pos);

                // this is the lemma of the token
                if (language.equals(Language.ENGLISH)) {
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    newtoken.setLemma(lemma);

                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    if (!ne.equals("O"))
                        newtoken.setMisc("Ner=" + StringHelper.capitalizeFirst(ne));
                }
            }

            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);

            for (SemanticGraphEdge semanticGraphEdge : dependencies.edgeIterable()) {

                parse.getToken(semanticGraphEdge.getDependent().index()).setDeprel(semanticGraphEdge.getRelation().getShortName());
                parse.getToken(semanticGraphEdge.getDependent().index()).setHeadId(semanticGraphEdge.getGovernor().index());
            }

            // Determine universal dependencies from tagset
            toUniversalDependencies(parse);

            for (Token token : parse.getTokens()) {
                if (token.getHeadId() == 0) token.setDeprel("root");
            }

            // use MATE lemmatizer for non-English
            if (!language.equals(Language.ENGLISH) && !language.equals(Language.CHINESE)) {

                SentenceData09 sentenceData09 = new SentenceData09();
                sentenceData09.init(("<root> <root> " + Joiner.on(" ").join(parse.getTexts())).split(" "));
                sentenceData09 = lemmatizer.apply(sentenceData09);

                for (int k = 1; k < sentenceData09.length(); k++) {
                    parse.getToken(k)
                            .setLemma(sentenceData09.plemmas[k]);
                }
            }

            // Only English currently has SRL
            if (language.equals(Language.ENGLISH) && semanticRoleLabeler != null) {
                se.lth.cs.srl.corpus.Sentence s = new se.lth.cs.srl.corpus.Sentence(
                        prepareFields(parse.getTexts()),
                        prepareFields(parse.getLemmas()),
                        prepareFields(parse.getPos()),
                        prepareFields(parse.getMorph()));

                int size = parse.getHeadIds().size();
                int[] heads = new int[size];
                Integer[] temp = parse.getHeadIds().toArray(new Integer[size]);
                for (int n = 0; n < size; ++n) {
                    heads[n] = temp[n];
                }
                s.setHeadsAndDeprels(heads, parse.getDeprels().toArray(new String[parse.getDeprels().size()]));
                semanticRoleLabeler.parseSentence(s);

                for (Predicate p : s.getPredicates()) {
                    Frame frame = parse.getToken(p.getIdx()).addNewFrame(p.getSense());
                    for (Word arg : p.getArgMap().keySet()) {
                        frame.addRole(new Role(p.getArgumentTag(arg), parse.getToken(arg.getIdx())));
                    }
                }
            }
            return parse;
        }
        return null;
    }

    /**
     * Helper method to format sentence for SRL (MATE SRL somehow needs the root field as textual string in array).
     *
     * @return Fields as string with extra root field.
     */
    private String[] prepareFields(List<String> fields) {

        List<String> input = Lists.newArrayList("<root>");
        input.addAll(fields);

        int size = input.size();
        String[] out = new String[size];
        String[] temp = input.toArray(new String[size]);
        System.arraycopy(temp, 0, out, 0, size);

        return out;
    }

    /**
     * Add universal dependencies to a sentence from language-specific dependencies.
     *
     * @param sentence Sentence for which universal dependencies are produced
     * @return True if sentence fully converted.
     */
    private static boolean toUniversalDependencies(Sentence sentence) {

        for (Token token : sentence.getTokens()) {

            if (token.getPos().startsWith("v")) token.setPosUniversal("VERB");
            if (token.getPos().startsWith("n")) token.setPosUniversal("NOUN");
            if (token.getPos().startsWith("pp")) token.setPosUniversal("PRON");
            if (token.getPos().startsWith("r")) token.setPosUniversal("ADV");
            if (token.getPos().startsWith("s")) token.setPosUniversal("PREP");
            if (token.getPos().startsWith("pr")) token.setPosUniversal("PREP");

            if (token.getPos().equals("NFP")) token.setPosUniversal("SYM");

            if (token.getPos().equals("#")) token.setPosUniversal("NOUN");

            if (token.getPos().startsWith("V")) token.setPosUniversal("VERB");

            if (token.getPos().startsWith("NNP")) token.setPosUniversal("PROPN");

            if (token.getPos().startsWith("DT")) token.setPosUniversal("DET");
            if (token.getPos().startsWith("IN")) token.setPosUniversal("ADP");
            if (token.getPos().equals("NN")) token.setPosUniversal("NOUN");
            if (token.getPos().equals("NNS")) token.setPosUniversal("NOUN");
            if (token.getPos().startsWith("JJ")) token.setPosUniversal("ADJ");
            if (token.getPos().startsWith("PRP")) token.setPosUniversal("PRON");
            if (token.getPos().equals("MD")) token.setPosUniversal("AUX");
            if (token.getPos().equals("CD")) token.setPosUniversal("NUM");
            if (token.getPos().equals("CC")) token.setPosUniversal("CCONJ");

            if (token.getPos().equals("TO")) token.setPosUniversal("ADP");
            if (token.getPos().equals("TO") && token.getDeprel().equals("mark")) token.setPosUniversal("PART");
            if (token.getPos().equals("TO") && token.getDeprel().equals("xcomp")) token.setPosUniversal("PART");
            if (token.getPos().equals("TO") && token.getDeprel().equals("ccomp")) token.setPosUniversal("PART");
            if (token.getPos().equals("TO") && token.getDeprel().equals("advcl")) token.setPosUniversal("PART");
            if (token.getPos().equals("TO") && token.getDeprel().equals("case")) token.setPosUniversal("ADP");
            if (token.getPos().equals("TO") && token.getDeprel().equals("dep")) token.setPosUniversal("ADP");
            if (token.getPos().equals("TO") && token.getDeprel().equals("mwe")) token.setPosUniversal("ADP");
            if (token.getPos().equals("TO") && token.getDeprel().equals("nsubj")) token.setPosUniversal("ADP");
            if (token.getPos().equals("TO") && token.getDeprel().equals("conj")) token.setPosUniversal("ADP");

            if (token.getPos().equals("RB")) token.setPosUniversal("ADV");
            if (token.getPos().equals("RB") && token.getLemma().equals("not")) token.setPosUniversal("PART");

            if (token.getPos().equals("POS")) token.setPosUniversal("PART");
            if (token.getPos().startsWith("WP")) token.setPosUniversal("PRON");
            if (token.getPos().equals("WRB")) token.setPosUniversal("ADV");
            if (token.getPos().equals("WDT")) token.setPosUniversal("PRON");
            if (token.getPos().equals("EX")) token.setPosUniversal("PRON");
            if (token.getPos().equals("PDT")) token.setPosUniversal("DET");
            if (token.getPos().equals("RBS")) token.setPosUniversal("ADV");
            if (token.getPos().equals("RBR")) token.setPosUniversal("ADV");
            if (token.getPos().equals("RP")) token.setPosUniversal("ADP");
            if (token.getPos().equals("UH")) token.setPosUniversal("INTJ");
            if (token.getPos().equals("AFX")) token.setPosUniversal("X");
            if (token.getPos().equals("FW")) token.setPosUniversal("X");
            if (token.getPos().equals("LS")) token.setPosUniversal("X");
            if (token.getPos().equals("ADD")) token.setPosUniversal("X");
            if (token.getPos().equals("XX")) token.setPosUniversal("X");

            if (token.getPos().equals(".")) token.setPosUniversal("PUNCT");
            if (token.getPos().equals(",")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("?")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("!")) token.setPosUniversal("PUNCT");
            if (token.getText().equals(";")) token.setPosUniversal("PUNCT");
            if (token.getPos().equals(":")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("``")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("''")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("--")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("-")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("`")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("'")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("...")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("..")) token.setPosUniversal("PUNCT");
            if (token.getPos().matches("[\\.']+")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("\"")) token.setPosUniversal("PUNCT");
            if (token.getPos().equals("-LRB-")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("-LCB-")) token.setPosUniversal("PUNCT");
            if (token.getPos().equals("-RRB-")) token.setPosUniversal("PUNCT");
            if (token.getText().equals("-RCB-")) token.setPosUniversal("PUNCT");
            if (token.getPos().equals("HYPH")) token.setPosUniversal("PUNCT");

            if (token.getPos().equals("PUNCT")) token.setDeprel("punct");

            if (token.getPos().equals("$")) token.setPosUniversal("SYM");

            if (token.getPosUniversal().trim().equals("")) {
                System.out.println("token = " + token);
                token.setPosUniversal(token.getPos());
            }
        }
        return false;
    }


}
