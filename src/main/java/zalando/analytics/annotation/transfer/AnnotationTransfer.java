package zalando.analytics.annotation.transfer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zalando.analytics.base.Constituent;
import zalando.analytics.base.Frame;
import zalando.analytics.base.Role;
import zalando.analytics.base.Token;

import java.util.List;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 8/28/17.
 * <p>
 * Main class that executes annotation transfer for a BiSentence. Distinguishes between transfer of shallow syntax,
 * deep syntax and shallow semantics.
 */
public class AnnotationTransfer {

    private static final Logger logger = LogManager.getLogger(AnnotationTransfer.class);

    /**
     * Method that transfers PoS tags and universal PoS tags in a word-aligned BiSentence
     *
     * @param biSentence Word-aligned BiSentence
     */
    private void transferShallowSyntax(BiSentence biSentence) {

        // Iterate through each token in source sentence
        for (Token tokenSL : biSentence.getSentenceSL().getTokens()) {

            // If we can align the source token, project PoS
            Token aligned = biSentence.getAligned(tokenSL);
            if (aligned != null) {
                aligned.setPosUniversal(tokenSL.getPosUniversal());
                aligned.setPos(tokenSL.getPos());

//                if (!tokenSL.getNer().equals("_"))
//                    aligned.setNer(tokenSL.getNer());
            }
        }
    }

    public void transferSpans(BiSentence biSentence){

        // Iterate through each token in source sentence
        for (Token tokenSL : biSentence.getSentenceSL().getTokens()) {

            // If we can align the source token, project PoS
            Token aligned = biSentence.getAligned(tokenSL);
            if (aligned != null) {
                aligned.setPosUniversal(tokenSL.getPosUniversal());
                aligned.setPos(tokenSL.getPos());

                if (!tokenSL.getMisc().equals("_"))
                    aligned.setMisc(tokenSL.getMisc());
            }
        }
    }

    /**
     * Method that transfers typed dependencies in a word-aligned BiSentence
     *
     * @param biSentence Word-aligned BiSentence
     */
    private void transferDeepSyntax(BiSentence biSentence) {

        // Iterate through each token in source sentence
        for (Token tokenSL : biSentence.getSentenceSL().getTokens()) {

            // Check if we can align both the source token, and its head
            Token aligned = biSentence.getAligned(tokenSL);
            if (aligned != null) {
                Token alignedHead = biSentence.getAligned(tokenSL.getHead());

                // If so, project arc onto aligned target token and head
                if (alignedHead != null) {
                    aligned.setHeadId(alignedHead.getId());
                    aligned.setDeprel(tokenSL.getDeprel());
                }
            }
        }
    }

    /**
     * Method that transfers SRL (frames and roles in a BiSentence)
     *
     * @param biSentence Word-aligned BiSentence
     */
    public void transferShallowSemantics(BiSentence biSentence) {

        // get all verbal constituents out of TL parse tree
        List<Constituent> constituentsTL = biSentence.getSentenceTL().getVerbalConstituents();

        // Log all detected target language constituents for debug
        logger.debug("TL constituents: " + Joiner.on("  ||  ").join(constituentsTL));

        // Iterate through each token in source sentence
        for (Token tokenSL : biSentence.getSentenceSL().getTokens()) {

            // Check if we can align token AND token evokes a frame
            Token aligned = biSentence.getAligned(tokenSL);
            if (aligned != null) {
                if (tokenSL.evokesFrame()) {

                    // If so, add this frame to the target token
                    Frame frame = aligned.addNewFrame(tokenSL.getFrame().getLabel());

                    // If we can project the frame, we must check if we can project the roles
                    for (Role role : tokenSL.getFrame().getRoles()) {

                        Constituent constituentSL = role.getRoleHead().getConstituent(tokenSL);
                        logger.debug("Source Constituent: " + constituentSL);
                        Constituent constituentAligned = new Constituent(Lists.newArrayList());
                        for (Token token : constituentSL.getTokens()) {
                            if (biSentence.getAligned(token) != null)
                                constituentAligned.getTokens().add(biSentence.getAligned(token));
                        }
                        logger.debug("   ---   projected as: " + constituentAligned);

                        Constituent bestTLcontituent = null;
                        double highestSimilarity = 0.;
                        for (Constituent constituent : constituentsTL) {
                            double jaccardSimilarity = constituentAligned.jaccardSimilarity(constituent);
                            if (jaccardSimilarity > highestSimilarity) {
                                highestSimilarity = jaccardSimilarity;
                                bestTLcontituent = constituent;
                            }
                        }
                        logger.debug("   ---   best matching TL constituent: " + highestSimilarity + "\t" + bestTLcontituent);
                        if (bestTLcontituent != null) {
                            frame.addRole(new Role(role.getRoleLabel(), bestTLcontituent.getHead()));
                        }
                    }
                }
            }
        }
    }


    /**
     * Transfer all annotations (PoS, dependencies, SRL) from source to target in BiSentence
     *
     * @param biSentence Word-aligned BiSentence
     */
    public void transfer(BiSentence biSentence) {
        transferShallowSyntax(biSentence);
        transferDeepSyntax(biSentence);
        transferShallowSemantics(biSentence);
    }


}
