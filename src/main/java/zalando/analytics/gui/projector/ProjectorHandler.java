package zalando.analytics.gui.projector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zalando.analytics.annotation.transfer.AnnotationTransfer;
import zalando.analytics.base.*;
import zalando.analytics.annotation.transfer.BiSentence;
import zalando.analytics.parser.wrappers.PipelineWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Alan Akbik on 4/21/17.
 *
 * Handler for all projection and retrival operations in Projector GUI.
 */
@Path("/transfer")
public class ProjectorHandler {

    // Main DAO for all required objects.
    private LanguageDAO languageDAO = LanguageDAO.getInstance();

    // Logger for this class
    private Logger logger = LogManager.getLogger(ProjectorHandler.class);

    public ProjectorHandler() {
    }

    /**
     * Retrieves a sentence from a corpus and its translation
     * @param corpusName Name of the corpus
     * @param no Number of sentence in corpus
     * @param tl Target language
     * @return Object containing translated sentence pair
     */
    @GET
    @Path("retrieve/{corpus}/{no}/{tl}")
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieve(@PathParam("corpus") String corpusName, @PathParam("no") String no, @PathParam("tl") String tl) {

        logger.debug("corpusName = " + corpusName);
        logger.debug("no = " + no);
        logger.debug("tl = " + tl);

        Corpus corpus = null;
        if (corpusName.equals("gold-sl")) corpus = languageDAO.goldSourceCorpus;
        if (corpusName.equals("gold-tl")) corpus = languageDAO.goldTargetCorpus;

        // Get the sentence from the corpus
//        Corpus corpus = languageDAO.getCorpus(corpusName);
        String corpusSentence = corpus.getSentences().get(Integer.parseInt(no)).toSentence();

        ProcessedSentence processedSentence = new ProcessedSentence();

        // If the corpus language is English, corpus is source
        if (corpus.getCorpusLanguage().equals(Language.ENGLISH)) {
            processedSentence.sourceSentence = corpusSentence;
            processedSentence.targetSentence = languageDAO.translations.translate(corpusSentence);
            processedSentence.targetLanguage = Language.get(tl).toString().toLowerCase();
        } else {
            processedSentence.sourceSentence = languageDAO.translations.translate(corpusSentence);
            processedSentence.targetSentence = corpusSentence;
            processedSentence.targetLanguage = corpus.getCorpusLanguage().toString().toLowerCase();
        }

        String s = new Gson().toJson(processedSentence);
        logger.debug("s = " + s);

        return s;
    }

    /**
     * Executes annotation projection for a sentence from a parsed corpus. If the corpus is in English, the target
     * language sentence is parsed. If the corpus is in another language, the source language sentence is parsed.
     *
     * @param corpusName Name of the corpus
     * @param no Number of sentence in corpus
     * @param tl Target language
     * @param sourceSentence English source sentence
     * @param targetSentence Target language sentence
     * @return Object containing all information on the annotation transfer for display in UI
     */
    @GET
    @Path("corpus/{corpusName}/{no}/{tl}/{sourceSentence}/{targetSentence}")
    @Produces({MediaType.APPLICATION_JSON})
    public String transferCorpus(@PathParam("corpusName") String corpusName,
                           @PathParam("no") String no,
                           @PathParam("tl") String tl,
                           @PathParam("sourceSentence") String sourceSentence,
                           @PathParam("targetSentence") String targetSentence) {

        logger.debug("corpusName = " + corpusName);
        logger.debug("sourceSentence = " + sourceSentence);
        logger.debug("targetSentence = " + targetSentence);

        Corpus corpus = null;
        if (corpusName.equals("gold-sl")) corpus = languageDAO.goldSourceCorpus;
        if (corpusName.equals("gold-tl")) corpus = languageDAO.goldTargetCorpus;

        Sentence corpusSentence = corpus.getSentences().get(Integer.parseInt(no));

        ProcessedSentence processedSentence;

        // is the corpus in English or the target language?
        if (corpus.getCorpusLanguage().equals(Language.ENGLISH)) {
            Sentence translatedCorpusSentence = languageDAO.getPipeline(Language.get(tl)).parse(targetSentence);
            processedSentence = alignAndProject(corpusSentence, translatedCorpusSentence, Language.get(tl));
        }
        else {
            Sentence translatedCorpusSentence = languageDAO.getPipeline(Language.ENGLISH).parse(sourceSentence);
            processedSentence = alignAndProject(translatedCorpusSentence, corpusSentence, Language.get(tl));
        }

        String json = new Gson().toJson(processedSentence);
        logger.debug("json = " + json);
        return json;
    }


    /**
     * Executes annotation projection for a manually entered sentence pair. Because no corpus is used, parsers are used
     * to parse both source and target language sentence.
     *
     * @param sourceSentence English source sentence
     * @param tl Target language
     * @param targetSentence Target language sentence
     * @return Object containing all information on the annotation transfer for display in UI
     */
    @GET
    @Path("manual/{sentence}/{tl}/{translation}")
    @Produces({MediaType.APPLICATION_JSON})
    public String manual(@PathParam("sentence") String sourceSentence, @PathParam("tl") String tl, @PathParam("translation") String targetSentence) {

        logger.debug("target language: " + tl + "\t" + Language.get(tl));

        PipelineWrapper slPipeline = languageDAO.getPipeline(Language.ENGLISH);
        PipelineWrapper tlPipeline = languageDAO.getPipeline(Language.get(tl));

        Sentence parsedSL = slPipeline.parse(sourceSentence.trim());
        logger.debug(parsedSL.toConllU());

        Sentence parsedTL = tlPipeline.parse(targetSentence.trim());
        logger.debug(parsedTL.toConllU());

        ProcessedSentence processedSentence = alignAndProject(parsedSL, parsedTL, Language.get(tl));

        String json = new Gson().toJson(processedSentence);
        logger.debug("json = " + json);
        return json;
    }


    /**
     * Internal method that executes annotation projection for a sentence pair and target language
     *
     * @param parsedSL Parsed English source sentence
     * @param parsedTL Parsed target language sentence
     * @param targetLanguage Target language
     * @return Object containing all information on the annotation transfer for display in UI
     */
    private ProcessedSentence alignAndProject(Sentence parsedSL, Sentence parsedTL, Language targetLanguage) {

        logger.debug(parsedSL);
        logger.debug(parsedTL);

        AnnotationTransfer transfer = new AnnotationTransfer();

        BiSentence parallelSentence_srlonly = new BiSentence(parsedSL, parsedTL);
        parallelSentence_srlonly.align(languageDAO.getAligner(targetLanguage));
        transfer.transferShallowSemantics(parallelSentence_srlonly);

        logger.debug(parallelSentence_srlonly.toString());
        logger.debug(parsedTL);

        BiSentence parallelSentence_projected = new BiSentence(parsedSL, Sentence.fromTokenized(parsedTL.toSentence()));
        parallelSentence_projected.copyAlignments(parallelSentence_srlonly);
        transfer.transfer(parallelSentence_projected);

        logger.debug(parallelSentence_projected.toString());
        logger.debug(parallelSentence_projected.getSentenceTL());

        return getProcessedSentence(parallelSentence_projected, parsedTL);
    }


    /**
     * Internal method that prepares all information for the JSON object that is passed to the UI. This method is messy
     * and may be removed in the future if we find a better UI solution.
     *
     * @param projection BiSentence with source and target sentences
     * @param parsedTL_predicted Predicted target sentence
     * @return Object containing all information on the annotation transfer for display in UI
     */
    private ProcessedSentence getProcessedSentence(BiSentence projection, Sentence parsedTL_predicted) {

        ProcessedSentence processedSentence = new ProcessedSentence();

        processedSentence.sourceSentence = projection.getSentenceSL().toSentence().replaceAll(" ", "   ");
        processedSentence.targetSentence = parsedTL_predicted.toSentence().replaceAll(" ", "   ");
        processedSentence.tokensSL = Lists.newArrayList();
        processedSentence.tokensTL_predicted = Lists.newArrayList();
        processedSentence.tokensTL_projected = Lists.newArrayList();
        processedSentence.framesSL = Lists.newArrayList();
        processedSentence.framesTL_predicted = Lists.newArrayList();
        processedSentence.framesTL_projected = Lists.newArrayList();
        processedSentence.nerSL = Lists.newArrayList();
        processedSentence.nerTL_projected = Lists.newArrayList();

        Map<Token, Integer> tokenOffsetMapSL = Maps.newHashMap();
        int offs = 0;
        for (Token token : projection.getSentenceSL().getTokens()) {
            tokenOffsetMapSL.put(token, offs);
            offs += token.getText().length() + 3;
        }

        Map<Integer, Integer> tokenOffsetMapTL_offset = Maps.newHashMap();
        offs = 0;
        for (Token token : parsedTL_predicted.getTokens()) {
            tokenOffsetMapTL_offset.put(token.getId(), offs);
            offs += token.getText().length() + 3;
        }

        for (Token token : projection.getSentenceSL().getTokens()) {
            Integer start = tokenOffsetMapSL.get(token);
            ArrayList<Object> entry = Lists.newArrayList();
            entry.add("SL" + token.getId());
            entry.add(Lists.newArrayList(start, start + token.getText().length()));
            entry.add(token.getPosUniversal().replaceAll("PUNCT", "P"));
            processedSentence.tokensSL.add(entry);
        }

        int level = 0;

        for (Constituent ner : projection.getSentenceSL().getNER()) {
            String description = ner.getType();
            Integer start = tokenOffsetMapSL.get(ner.getTokens().get(0));
            ArrayList<Object> con = Lists.newArrayList();
            con.add(description);
            con.add(Lists.newArrayList(start, start + ner.toStringFull().replaceAll(" ", "   ").length()));
            con.add(level);
            processedSentence.nerSL.add(con);
        }

        level = 1;
        for (Token token : projection.getSentenceSL().getTokens()) {

            if (token.evokesFrame() && !token.getFrame().getLabel().equals("be.03")) {
                Integer start = tokenOffsetMapSL.get(token);
                ArrayList<Object> entry = Lists.newArrayList();
                Frame frame = token.getFrame();
                entry.add(frame.getLabel());
                entry.add(Lists.newArrayList(start, start + token.getText().length()));
                entry.add(level);
                processedSentence.framesSL.add(entry);

                for (Role role : frame.getRoles()) {

                    Constituent constituent = role.getRoleHead().getConstituent(token);
                    String description = role.getRoleLabel().replaceAll("AM-", "");

                    start = tokenOffsetMapSL.get(constituent.getTokens().get(0));
                    ArrayList<Object> con = Lists.newArrayList();
                    con.add(description);
                    con.add(Lists.newArrayList(start, start + constituent.toStringFull().replaceAll(" ", "   ").length()));
                    con.add(level);
                    processedSentence.framesSL.add(con);
                }
                level++;
            }
        }

        level = 0;
        for (Constituent ner : parsedTL_predicted.getNER()) {
            String description = ner.getType();
            Integer start = tokenOffsetMapTL_offset.get(ner.getTokens().get(0).getId());
            ArrayList<Object> con = Lists.newArrayList();
            con.add(description);
            con.add(Lists.newArrayList(start, start + ner.toStringFull().replaceAll(" ", "   ").length()));
            con.add(level);
            processedSentence.nerTL_projected.add(con);
        }
        level = 1;
        for (Token token : parsedTL_predicted.getTokens()) {
            if (token.evokesFrame() && !token.getFrame().getLabel().equals("be.03")) {
                Integer start = tokenOffsetMapTL_offset.get(token.getId());
                ArrayList<Object> entry = Lists.newArrayList();
                Frame frame = token.getFrame();
                entry.add(frame.getLabel());
                entry.add(Lists.newArrayList(start, start + token.getText().length()));
                entry.add(level);
                processedSentence.framesTL_predicted.add(entry);

                for (Role role : frame.getRoles()) {

                    Constituent constituent = role.getRoleHead().getConstituent(token);
                    String description = role.getRoleLabel().replaceAll("AM-", "");
                    start = tokenOffsetMapTL_offset.get(constituent.getTokens().get(0).getId());
                    ArrayList<Object> con = Lists.newArrayList();
                    con.add(description);
                    con.add(Lists.newArrayList(start, start + constituent.toStringFull().replaceAll(" ", "   ").length()));
                    con.add(level);
                    processedSentence.framesTL_predicted.add(con);
                }
                level++;
            }
        }

        level = 0;
        for (Constituent ner : projection.getSentenceTL().getNER()) {
            String description = ner.getType();
            Integer start = tokenOffsetMapTL_offset.get(ner.getTokens().get(0).getId());
            ArrayList<Object> con = Lists.newArrayList();
            con.add(description);
            con.add(Lists.newArrayList(start, start + ner.toStringFull().replaceAll(" ", "   ").length()));
            con.add(level);
            processedSentence.nerTL_projected.add(con);
        }


        level = 1;
        for (Token token : projection.getSentenceTL().getTokens()) {
            if (token.evokesFrame() && !token.getFrame().getLabel().equals("be.03")) {
                Integer start = tokenOffsetMapTL_offset.get(token.getId());
                ArrayList<Object> entry = Lists.newArrayList();
                Frame frame = token.getFrame();
                entry.add(frame.getLabel());
                entry.add(Lists.newArrayList(start, start + token.getText().length()));
                entry.add(level);
                processedSentence.framesTL_projected.add(entry);

                for (Role role : frame.getRoles()) {

                    Constituent constituent = role.getRoleHead().getConstituent(token);
                    String description = role.getRoleLabel().replaceAll("AM-", "");
                    start = tokenOffsetMapTL_offset.get(constituent.getTokens().get(0).getId());
                    ArrayList<Object> con = Lists.newArrayList();
                    con.add(description);
                    con.add(Lists.newArrayList(start, start + constituent.toStringFull().replaceAll(" ", "   ").length()));
                    con.add(level);
                    processedSentence.framesTL_projected.add(con);
                }
                level++;
            }
        }

        for (Token token : parsedTL_predicted.getTokens()) {
            Integer start = tokenOffsetMapTL_offset.get(token.getId());
            ArrayList<Object> entry = Lists.newArrayList();
            entry.add("TL" + token.getId());
            entry.add(Lists.newArrayList(start, start + token.getText().length()));
            entry.add(token.getPosUniversal().replaceAll("PUNCT", "P"));
            processedSentence.tokensTL_predicted.add(entry);
        }

        for (Token token : projection.getSentenceTL().getTokens()) {
            Integer start = tokenOffsetMapTL_offset.get(token.getId());
            ArrayList<Object> entry = Lists.newArrayList();
            entry.add("TL" + token.getId());
            entry.add(Lists.newArrayList(start, start + token.getText().length()));
            entry.add(token.getPosUniversal().replaceAll("PUNCT", "P"));
            processedSentence.tokensTL_projected.add(entry);
        }

        processedSentence.alignments = Lists.newArrayList();

        int count = 1;
        for (Table.Cell<Token, Token, Double> alignment : projection.aligments.cellSet()) {
            processedSentence.alignments.add(Lists.newArrayList("A" + count++, "SL" + alignment.getRowKey().getId(), "TL" + alignment.getColumnKey().getId()));
        }

        processedSentence.arcsSL = Lists.newArrayList();
        count = 1;
        for (Token token : projection.getSentenceSL().getTokens()) {
            if (token.getHeadId() > 0) {
                if (token.getId() < token.getHeadId())
                    processedSentence.arcsSL.add(Lists.newArrayList("ASL" + count++, "SL" + token.getId(), "SL" + token.getHeadId(), token.getDeprel()));
                else
                    processedSentence.arcsSL.add(Lists.newArrayList("ASL" + count++, "SL" + token.getHeadId(), "SL" + token.getId(), token.getDeprel()));
            }
        }

        processedSentence.arcsTL_predicted = Lists.newArrayList();
        for (Token token : parsedTL_predicted.getTokens()) {
            if (token.getHeadId() > 0) {
                if (token.getId() < token.getHeadId())
                    processedSentence.arcsTL_predicted.add(Lists.newArrayList(token.getDeprel(), "TL" + token.getId(), "TL" + token.getHeadId(), token.getDeprel()));
                else
                    processedSentence.arcsTL_predicted.add(Lists.newArrayList(token.getDeprel(), "TL" + token.getHeadId(), "TL" + token.getId(), token.getDeprel()));
            }
        }

        processedSentence.arcsTL_projected = Lists.newArrayList();
        for (Token token : projection.getSentenceTL().getTokens()) {
            if (token.getHeadId() > 0 && !token.getDeprel().equals("?")) {
                if (token.getId() < token.getHeadId())
                    processedSentence.arcsTL_projected.add(Lists.newArrayList(token.getDeprel(), "TL" + token.getId(), "TL" + token.getHeadId(), token.getDeprel()));
                else
                    processedSentence.arcsTL_projected.add(Lists.newArrayList(token.getDeprel(), "TL" + token.getHeadId(), "TL" + token.getId(), token.getDeprel()));
            }
        }

        return processedSentence;
    }

}
