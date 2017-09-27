package zalando.analytics.annotation.transfer;

import org.junit.Assert;
import org.junit.Test;
import zalando.analytics.base.Language;
import zalando.analytics.base.Sentence;
import zalando.analytics.base.Token;

/**
 * Created by Alan Akbik on 9/3/17.
 *
 * Test class for HeuristicAligner
 */
public class TestHeuristicAligner {

    /**
     * Test alignmend of English-German sentence pair
     */
    @Test
    public void testGermanHeuristicAlignment(){

        Sentence sentenceSL = Sentence.fromConllU("1    The        the        DET     DT    _   3   det     _   _   _   _        _     \n" +
                "2    fat        fat        ADJ     JJ    _   3   amod    _   _   _   _        _     \n" +
                "3    man        man        NOUN    NN    _   4   nsubj   _   _   _   _        A0    \n" +
                "4    ate        eat        VERB    VBD   _   0   root    _   _   Y   eat.01   _     \n" +
                "5    a          a          DET     DT    _   6   det     _   _   _   _        _     \n" +
                "6    sandwich   sandwich   NOUN    NN    _   4   dobj    _   _   _   _        A1    \n" +
                "7    .          .          PUNCT   .     _   4   punct   _   _   _   _        _\n");

        Sentence sentenceTL = Sentence.fromConllU("1    Der          der          _      ART     _   3   det     _   _   _   _  \n" +
                "2    dicke        dick         _      ADJA    _   3   amod    _   _   _   _  \n" +
                "3    Mann         mann         NOUN   NN      _   4   nsubj   _   _   _   _  \n" +
                "4    verschlang   verschlang   VERB   VVFIN   _   0   root    _   _   _   _  \n" +
                "5    ein          ein          _      ART     _   6   det     _   _   _   _  \n" +
                "6    Sandwich     sandwich     NOUN   NN      _   4   dobj    _   _   _   _  \n" +
                "7    .            --           _      $.      _   4   punct   _   _   _   _\n");

        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL);

        biSentence.align(HeuristicAligner.getInstance(Language.GERMAN));

        // check if "fat" and "dicke" are aligned
        Token fat = biSentence.getSentenceSL().getToken(2);
        Token dicke = biSentence.getSentenceTL().getToken(2);

        Assert.assertTrue(biSentence.getAligned(fat).equals(dicke));
        Assert.assertTrue(biSentence.getAligned(dicke).equals(fat));

        // check if "ate" and "verschlang" are aligned
        Token ate = biSentence.getSentenceSL().getToken(4);
        Token verschlang = biSentence.getSentenceTL().getToken(4);

        Assert.assertTrue(biSentence.getAligned(ate).equals(verschlang));
        Assert.assertTrue(biSentence.getAligned(verschlang).equals(ate));

        // check if "sandwich" and "Sandwich" are aligned
        Token sandwich = biSentence.getSentenceSL().getToken(6);
        Token Sandwich = biSentence.getSentenceTL().getToken(6);

        Assert.assertTrue(biSentence.getAligned(sandwich).equals(Sandwich));
        Assert.assertTrue(biSentence.getAligned(Sandwich).equals(sandwich));

        System.out.println(biSentence);
    }

    /**
     * Test alignmend of English-French sentence pair
     */
    @Test
    public void testFrenchHeuristicAlignment(){

        Sentence sentenceSL = Sentence.fromConllU("1    The        the        DET     DT    _   3   det     _   _   _   _        _     \n" +
                "2    fat        fat        ADJ     JJ    _   3   amod    _   _   _   _        _     \n" +
                "3    man        man        NOUN    NN    _   4   nsubj   _   _   _   _        A0    \n" +
                "4    ate        eat        VERB    VBD   _   0   root    _   _   Y   eat.01   _     \n" +
                "5    a          a          DET     DT    _   6   det     _   _   _   _        _     \n" +
                "6    sandwich   sandwich   NOUN    NN    _   4   dobj    _   _   _   _        A1    \n" +
                "7    .          .          PUNCT   .     _   4   punct   _   _   _   _        _\n");

        Sentence sentenceTL = Sentence.fromConllU("1    Le         _   _      _     _   0   _       _   _   _   _        _     \n" +
                "2    gros       _   ADJ    JJ    _   3   amod    _   _   _   _        _     \n" +
                "3    homme      _   NOUN   NN    _   5   nsubj   _   _   _   _        A0    \n" +
                "4    a          _   _      _     _   0   _       _   _   _   _        _     \n" +
                "5    mangé      _   VERB   VBD   _   0   _       _   _   Y   eat.01   _     \n" +
                "6    un         _   _      _     _   0   _       _   _   _   _        _     \n" +
                "7    sandwich   _   NOUN   NN    _   5   dobj    _   _   _   _        A1    \n" +
                "8    .          _   _      _     _   0   _       _   _   _   _        _\n");

        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL);

        biSentence.align(HeuristicAligner.getInstance(Language.FRENCH));

        // check if "fat" and "dicke" are aligned
        Token fat = biSentence.getSentenceSL().getToken(2);
        Token gros = biSentence.getSentenceTL().getToken(2);

        Assert.assertTrue(biSentence.getAligned(fat).equals(gros));
        Assert.assertTrue(biSentence.getAligned(gros).equals(fat));

        // check if "ate" and "verschlang" are aligned
        Token ate = biSentence.getSentenceSL().getToken(4);
        Token mange = biSentence.getSentenceTL().getToken(5);

        Assert.assertTrue(biSentence.getAligned(ate).equals(mange));
        Assert.assertTrue(biSentence.getAligned(mange).equals(ate));

        // check if "sandwich" and "Sandwich" are aligned
        Token sandwich = biSentence.getSentenceSL().getToken(6);
        Token Sandwich = biSentence.getSentenceTL().getToken(7);

        Assert.assertTrue(biSentence.getAligned(sandwich).equals(Sandwich));
        Assert.assertTrue(biSentence.getAligned(Sandwich).equals(sandwich));

        System.out.println(biSentence);
    }

    @Test
    public void testFashionAlign(){

        Sentence english = Sentence.fromConllU("1    Hearst           Hearst           PROPN   NNP   _   2    compound   _   Ner=NamedOrganizationOther   _   _           _     _     _     \n" +
                "2    Magazines        magazine         NOUN    NNS   _   5    nsubj      _   Ner=NamedOrganizationOther   _   _           _     A0    _     \n" +
                "3    does             do               VERB    VBZ   _   5    aux        _   _                            Y   do.01       _     _     _     \n" +
                "4    not              not              PART    RB    _   5    neg        _   _                            _   _           _     AM-NEG__     \n" +
                "5    accept           accept           VERB    VB    _   0    root       _   _                            Y   accept.01   _     _     _     \n" +
                "6    any              any              DET     DT    _   7    det        _   _                            _   _           _     _     _     \n" +
                "7    responsibility   responsibility   NOUN    NN    _   5    dobj       _   _                            _   _           _     A1    _     \n" +
                "8    for              for              ADP     IN    _   12   case       _   _                            _   _           _     _     _     \n" +
                "9    late             late             ADJ     JJ    _   12   amod       _   _                            _   _           _     _     _     \n" +
                "10   or               or               CCONJ   CC    _   9    cc         _   _                            _   _           _     _     _     \n" +
                "11   lost             lose             VERB    VBN   _   9    conj       _   _                            Y   lose.02     _     _     _     \n" +
                "12   entries          entry            NOUN    NNS   _   7    nmod       _   _                            _   _           _     _     A1    \n" +
                "13   .                .                PUNCT   .     _   5    punct      _   _                            _   _           _     _     _");

        Sentence german = Sentence.fromConllU("1    Hearst         hearst       _      NE      _   2    det     _   _   _   _       _     \n" +
                "2    Magazines      magazine     _      NE      _   3    nsubj   _   _   _   _       _     \n" +
                "3    übernimmt      übernehmen   VERB   VVFIN   _   0    root    _   _   Y   do.01   _     \n" +
                "4    keine          kein         _      PIAT    _   5    neg     _   _   _   _       _     \n" +
                "5    Haftung        haftung      NOUN   NN      _   3    dobj    _   _   _   _       _     \n" +
                "6    für            für          _      APPR    _   7    case    _   _   _   _       _     \n" +
                "7    verspätete     verspäten    _      ADJA    _   5    nmod    _   _   _   _       _     \n" +
                "8    oder           oder         _      KON     _   7    cc      _   _   _   _       _     \n" +
                "9    verloren       verlieren    VERB   VVFIN   _   11   case    _   _   _   _       _     \n" +
                "10   gegangene      gegangen     _      ADJA    _   11   amod    _   _   _   _       _     \n" +
                "11   Einsendungen   einsendung   NOUN   NN      _   7    conj    _   _   _   _       _     \n" +
                "12   .              --           _      $.      _   3    punct   _   _   _   _       _");

        BiSentence biSentence = new BiSentence(english, german);

        biSentence.align(HeuristicAligner.getInstance(Language.GERMAN));

        System.out.println(biSentence);

    }

}
