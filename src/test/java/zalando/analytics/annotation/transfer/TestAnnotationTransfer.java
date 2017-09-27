package zalando.analytics.annotation.transfer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import zalando.analytics.base.Constituent;
import zalando.analytics.base.Language;
import zalando.analytics.base.Sentence;
import zalando.analytics.base.Token;

/**
 * Created by Alan Akbik on 8/29/17.
 * <p>
 * Test class for AnnotationTransfer
 */
public class TestAnnotationTransfer {

    // Aligner used in tests, initialized before each test
    HeuristicAligner aligner = HeuristicAligner.emptyDictionary();

    /**
     * Annotation transfer test 1. This test shows how annotation transfer is normally executed with a heuristic aligner.
     */
    @Test
    public void testTransferEnglishGerman_1() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU("1    The        the        DET     DT    _   3   det     _   _   _   _        _     \n" +
                "2    fat        fat        ADJ     JJ    _   3   amod    _   _   _   _        _     \n" +
                "3    man        man        NOUN    NN    _   4   nsubj   _   _   _   _        A0    \n" +
                "4    ate        eat        VERB    VBD   _   0   root    _   _   Y   eat.01   _     \n" +
                "5    a          a          DET     DT    _   6   det     _   _   _   _        _     \n" +
                "6    sandwich   sandwich   NOUN    NN    _   4   dobj    _   _   _   _        A1    \n" +
                "7    .          .          PUNCT   .     _   4   punct   _   _   _   _        _\n");

        // German translation
        Sentence sentenceTL = Sentence.fromTokenized("Der dicke Mann ass ein Sandwich .");

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // initialize bisentence and word-align
        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL).align(HeuristicAligner.getInstance(Language.GERMAN));

        // transfer all annotations
        new AnnotationTransfer().transfer(biSentence);

        // test target language tokens and their projected annotations
        Token dicke = biSentence.getSentenceTL().getToken(2);
        Assert.assertTrue(dicke.getPosUniversal().equals("ADJ"));
        Assert.assertTrue(dicke.getDeprel().equals("amod"));

        Token ass = biSentence.getSentenceTL().getToken(4);
        Assert.assertTrue(ass.getPosUniversal().equals("VERB"));
        Assert.assertTrue(ass.getFrame().getLabel().equals("eat.01"));

        Token sandwich = biSentence.getSentenceTL().getToken(6);
        Assert.assertTrue(sandwich.getPosUniversal().equals("NOUN"));
        Assert.assertTrue(sandwich.getDeprel().equals("dobj"));

        System.out.println("\n after transfer: \n" + biSentence.getSentenceTL().toConllU());

    }

    /**
     * Annotation transfer test 2
     */
    @Test
    public void testTransferEnglishGerman_2() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU("1\tIt\tit\tPRON\tPRP\t_\t4\tnsubj\t_\t_\t_\t_\tA1\t_\n" +
                "2\t's\tbe\tVERB\tVBZ\t_\t4\tcop\t_\t_\tY\tbe.01\t_\t_\n" +
                "3\tan\ta\tDET\tDT\t_\t4\tdet\t_\t_\t_\t_\t_\t_\n" +
                "4\tidea\tidea\tNOUN\tNN\t_\t0\troot\t_\t_\t_\t_\tA2\tA1\n" +
                "5\tplagued\tplague\tVERB\tVBN\t_\t4\tacl\t_\t_\tY\tplague.01\t_\t_\n" +
                "6\twith\twith\tADP\tIN\t_\t8\tcase\t_\t_\t_\t_\t_\t_\n" +
                "7\tunsolved\tunsolved\tADJ\tJJ\t_\t8\tamod\t_\t_\t_\t_\t_\t_\n" +
                "8\tproblems\tproblem\tNOUN\tNNS\t_\t5\tnmod\t_\t_\t_\t_\t_\tA2\n" +
                "9\t.\t.\tPUNCT\t.\t_\t0\tpunct\t_\t_\t_\t_\t_\t_");

        // German translation
        Sentence sentenceTL = Sentence.fromTokenized("Es ist eine Idee , die mit ungelösten Problemen geplagt ist .");

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // initialize bisentence and word-align
        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL).align(aligner);

        // transfer all annotations
        new AnnotationTransfer().transfer(biSentence);

        // test target language tokens and their projected annotations
        Token es = biSentence.getSentenceTL().getToken(1);
        Assert.assertTrue(es.getPosUniversal().equals("PRON"));
        Assert.assertTrue(es.getDeprel().equals("nsubj"));

        Token ist = biSentence.getSentenceTL().getToken(2);
        Assert.assertTrue(ist.getPosUniversal().equals("VERB"));
        Assert.assertTrue(ist.getFrame().getLabel().equals("be.01"));

        Token idee = biSentence.getSentenceTL().getToken(4);
        Assert.assertTrue(idee.getPosUniversal().equals("NOUN"));

        Assert.assertTrue(ist.getFrame().getTokenRole(idee).equals("A2"));

        System.out.println("\n after transfer: \n" + biSentence.getSentenceTL().toConllU());
    }

    /**
     * Annotation transfer test 3. In this test, the target language sentence is already parsed.
     */
    @Test
    public void testTransferEnglishGerman_3() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU("1    After      after      ADP     IN     _   3    case        _   _   _   _         _     \n" +
                "2    a          a          DET     DT     _   3    det         _   _   _   _         _     \n" +
                "3    quarter    quarter    NOUN    NN     _   8    nmod        _   _   _   _         AM-TMP\n" +
                "4    of         of         ADP     IN     _   6    case        _   _   _   _         _     \n" +
                "5    a          a          DET     DT     _   6    det         _   _   _   _         _     \n" +
                "6    year       year       NOUN    NN     _   3    nmod        _   _   _   _         _     \n" +
                "7    I          I          PRON    PRP    _   8    nsubj       _   _   _   _         A0    \n" +
                "8    held       hold       VERB    VBD    _   0    root        _   _   Y   hold.01   _     \n" +
                "9    a          a          DET     DT     _   11   det         _   _   _   _         _     \n" +
                "10   fragrant   fragrant   ADJ     JJ     _   11   amod        _   _   _   _         _     \n" +
                "11   miracle    miracle    NOUN    NN     _   8    dobj        _   _   _   _         A1    \n" +
                "12   in         in         ADP     IN     _   14   case        _   _   _   _         _     \n" +
                "13   my         my         PRON    PRP$   _   14   nmod:poss   _   _   _   _         _     \n" +
                "14   hands      hand       NOUN    NNS    _   8    nmod        _   _   _   _         AM-LOC\n" +
                "15   .          .          PUNCT   .      _   8    punct       _   _   _   _         _\n");

        // German translation with gold annotations
        Sentence sentenceTL = Sentence.fromConllU("1    Nach        nach      ADP     APPR    _                                                                 4    case     _   _               _   _  \n" +
                "2    einem       ein       DET     ART     Case=Dat|Definite=Ind|Gender=Masc,Neut|Number=Sing|PronType=Art   4    det      _   _               _   _  \n" +
                "3    viertel     Viertel   NUM     ADJA    NumType=Card                                                      4    nummod   _   _               _   _  \n" +
                "4    Jahr        Jahr      NOUN    NN      Case=Dat|Gender=Masc,Neut|Number=Sing                             5    obl      _   _               _   _  \n" +
                "5    hielt       halten    VERB    VVFIN   Number=Sing|Person=1|VerbForm=Fin                                 0    root     _   _               _   _  \n" +
                "6    ich         ich       PRON    PPER    Case=Nom|Number=Sing|Person=1|PronType=Prs                        5    nsubj    _   _               _   _  \n" +
                "7    ein         ein       DET     ART     Case=Acc|Definite=Ind|Gender=Masc,Neut|Number=Sing|PronType=Art   9    det      _   _               _   _  \n" +
                "8    duftendes   duftend   ADJ     ADJA    Case=Acc|Degree=Pos|Gender=Masc,Neut|Number=Sing                  9    amod     _   _               _   _  \n" +
                "9    Wunder      Wunder    NOUN    NN      Case=Acc|Gender=Masc,Neut|Number=Sing                             5    obj      _   _               _   _  \n" +
                "10   in          in        ADP     APPR    _                                                                 12   case     _   _               _   _  \n" +
                "11   den         der       DET     ART     Case=Dat|Definite=Def|Number=Plur|PronType=Art                    12   det      _   _               _   _  \n" +
                "12   Händen      Hand      NOUN    NN      Case=Dat|Number=Plur                                              5    obl      _   SpaceAfter=No   _   _  \n" +
                "13   .           .         PUNCT   $.      _                                                                 5    punct    _   _               _   _\n");

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // initialize bisentence and word-align
        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL).align(aligner);

        // transfer all annotations
        new AnnotationTransfer().transfer(biSentence);

        Sentence projected = biSentence.getSentenceTL();

        // test target language tokens and their projected annotations
        Constituent inHand = projected.getToken(12).getConstituent(projected.getToken(5));
        Assert.assertTrue(inHand.toString().equals("in den Händen"));

        Constituent viertelJahr = projected.getToken(4).getConstituent(projected.getToken(5));
        Assert.assertTrue(viertelJahr.toStringFull().equals("Nach einem viertel Jahr"));

        Constituent duftendesWunder = projected.getToken(9).getConstituent(projected.getToken(5));
        Assert.assertTrue(duftendesWunder.toString().equals("ein duftendes Wunder"));

        System.out.println("\n after transfer: \n" + biSentence.getSentenceTL().toConllU());
    }

    /**
     * Annotation transfer test 4
     */
    @Test
    public void testTransferEnglishGerman_4() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU(
                "1    Mars      Mars      PROPN   NNP   _                4    nsubj      _   _   _   _            A1    _     \n" +
                        "2    is        be        VERB    VBZ   sg:be-v.1        4    cop        _   _   Y   be.01        _     _     \n" +
                        "3    carbon    carbon    NOUN    NN    _                4    compound   _   _   _   _            _     _     \n" +
                        "4    dioxide   dioxide   NOUN    NN    _                0    root       _   _   _   _            A2    _     \n" +
                        "5    ,         ,         PUNCT   ,     _                0    punct      _   _   _   _            _     _     \n" +
                        "6    so        so        CCONJ   CC    _                4    cc         _   _   _   _            _     _     \n" +
                        "7    we        we        PRON    PRP   _                10   nsubj      _   _   _   _            _     A0    \n" +
                        "8    ca        can       AUX     MD    _                10   aux        _   _   _   _            _     AM-MOD\n" +
                        "9    n't       not       ADV     RB    _                10   neg        _   _   _   _            _     AM-NEG\n" +
                        "10   breathe   breathe   VERB    VB    sg:breathe-v.1   4    conj       _   _   Y   breathe.01   _     _     \n" +
                        "11   there     there     ADV     RB    _                10   advmod     _   _   _   _            _     AM-LOC\n" +
                        "12   .         .         PUNCT   .     _                0    punct      _   _   _   _            _     _\n");

        // German translation with gold annotations
        Sentence sentenceTL = Sentence.fromConllU(
                "1    Mars           mars           _      NE       _   3    nsubj       _   _   _   _  \n" +
                        "2    ist            sein           VERB   VAFIN    _   3    cop         _   _   _   _  \n" +
                        "3    Kohlendioxid   kohlendioxid   NOUN   NN       _   0    root        _   _   _   _  \n" +
                        "4    ,              --             _      $,       _   3    punct       _   _   _   _  \n" +
                        "5    also           also           _      ADV      _   10   advmod      _   _   _   _  \n" +
                        "6    können         können         VERB   VMFIN    _   10   aux         _   _   _   _  \n" +
                        "7    wir            wir            _      PPER     _   10   nsubj       _   _   _   _  \n" +
                        "8    dort           dort           _      ADV      _   10   advmod      _   _   _   _  \n" +
                        "9    nicht          nicht          _      PTKNEG   _   10   neg         _   _   _   _  \n" +
                        "10   atmen          atmen          VERB   VVINF    _   3    parataxis   _   _   _   _  \n" +
                        "11   .              --             _      $.       _   3    punct       _   _   _   _\n");

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // initialize bisentence and word-align
        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL).align(aligner);

        // transfer all annotations
        new AnnotationTransfer().transfer(biSentence);

        // test target language tokens and their projected annotations
        Token mars = biSentence.getSentenceTL().getToken(1);
        Assert.assertTrue(mars.getPosUniversal().equals("PROPN"));
        Assert.assertTrue(mars.getDeprel().equals("nsubj"));

        Token kohlendioxid = biSentence.getSentenceTL().getToken(3);
        Assert.assertTrue(kohlendioxid.getPosUniversal().equals("NOUN"));

        Token ist = biSentence.getSentenceTL().getToken(2);
        Assert.assertTrue(ist.getFrame().getLabel().equals("be.01"));
        Assert.assertTrue(ist.getFrame().getTokenRole(kohlendioxid).equals("A2"));

        System.out.println("\n after transfer: \n" + biSentence.getSentenceTL().toConllU());
    }

    /**
     * Annotation transfer test 5. Here we make a distinction between annotating everything and annotating only semantics.
     */
    @Test
    public void testTransferEnglishGerman_5() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU("1    So          so          ADV     RB    _   4    advmod   _   _   _   _              AM-DIS_     \n" +
                "2    this        this        DET     DT    _   3    det      _   _   _   _              _     _     \n" +
                "3    baker       baker       NOUN    NN    _   4    nsubj    _   _   _   _              A0    _     \n" +
                "4    has         have        VERB    VBZ   _   12   ccomp    _   _   Y   have.03        _     AM-ADV\n" +
                "5    really      really      ADV     RB    _   6    advmod   _   _   _   _              _     _     \n" +
                "6    great       great       ADJ     JJ    _   7    amod     _   _   _   _              _     _     \n" +
                "7    rolls       roll        NOUN    NNS   _   4    dobj     _   _   _   _              A1    _     \n" +
                "8    ,           ,           PUNCT   ,     _   12   punct    _   _   _   _              _     _     \n" +
                "9    you         you         PRON    PRP   _   12   nsubj    _   _   _   _              _     A0    \n" +
                "10   can         can         AUX     MD    _   12   aux      _   _   _   _              _     AM-MOD\n" +
                "11   only        only        ADV     RB    _   12   advmod   _   _   _   _              _     AM-ADV\n" +
                "12   recommend   recommend   VERB    VB    _   0    root     _   _   Y   recommend.01   _     _     \n" +
                "13   them        they        PRON    PRP   _   12   dobj     _   _   _   _              _     A1    \n" +
                "14   to          to          ADP     TO    _   15   case     _   _   _   _              _     _     \n" +
                "15   others      other       NOUN    NNS   _   12   nmod     _   _   _   _              _     A2    \n" +
                "16   .           .           PUNCT   .     _   12   punct    _   _   _   _              _     _\n");

        // German translation with gold annotations
        Sentence sentenceTL = Sentence.fromConllU("# sent_id = dev-s6\n" +
                "# text = Also dieser Bäcker hat würklich super Bröchten, die kann man einfach nur weiter Empfelen.\n" +
                "1    Also       also       ADV     ADV     _                                                       4    advmod      _   _               _   _         AM-DIS\n" +
                "2    dieser     dies       PRON    PDAT    Case=Nom|Gender=Masc|Number=Sing|PronType=Dem           3    det         _   _               _   _         _     \n" +
                "3    Bäcker     Bäcker     NOUN    NN      Case=Nom|Gender=Masc|Number=Sing                        4    nsubj       _   _               _   _         A0    \n" +
                "4    hat        haben      VERB    VAFIN   Mood=Ind|Number=Sing|Person=3|Tense=Pres|VerbForm=Fin   0    root        _   _               Y   have.03   _     \n" +
                "5    würklich   würklich   ADV     ADJD    _                                                       4    advmod      _   _               _   _         _     \n" +
                "6    super      super      ADJ     ADJD    Case=Acc|Degree=Pos|Number=Sing                         7    amod        _   _               _   _         _     \n" +
                "7    Bröchten   Bröchten   NOUN    NN      Case=Acc|Number=Sing                                    4    obj         _   SpaceAfter=No   _   _         A1    \n" +
                "8    ,          ,          PUNCT   $,      _                                                       4    punct       _   _               _   _         _     \n" +
                "9    die        der        PRON    PRELS   Case=Acc|PronType=Rel                                   15   obj         _   _               _   _         _     \n" +
                "10   kann       können     AUX     VMFIN   Mood=Ind|Person=3|Tense=Pres|VerbForm=Fin               15   aux         _   _               _   _         _     \n" +
                "11   man        man        PRON    PIS     Case=Nom|PronType=Ind                                   15   nsubj       _   _               _   _         _     \n" +
                "12   einfach    einfach    ADV     ADJD    _                                                       15   advmod      _   _               _   _         _     \n" +
                "13   nur        nur        ADV     ADV     _                                                       12   advmod      _   _               _   _         _     \n" +
                "14   weiter     weiter     ADV     ADV     _                                                       15   advmod      _   _               _   _         _     \n" +
                "15   Empfelen   Empfelen   VERB    NN      _                                                       4    parataxis   _   SpaceAfter=No   _   _         _     \n" +
                "16   .          .          PUNCT   $.      _                                                       4    punct       _   _               _   _         _\n");

        AnnotationTransfer transfer = new AnnotationTransfer();

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // Transfer only SRL and keep German gold annotations
        BiSentence biSentence_srlonly = new BiSentence(sentenceSL, sentenceTL).align(aligner);
        transfer.transferShallowSemantics(biSentence_srlonly);

        // Transfer everything
        BiSentence biSentence_projected = new BiSentence(sentenceSL, Sentence.fromTokenized(sentenceTL.toSentence()));
        biSentence_projected.copyAlignments(biSentence_srlonly);
        transfer.transfer(biSentence_projected);


        Token baecker1 = biSentence_srlonly.getSentenceTL().getToken(3);
        Assert.assertTrue(baecker1.getPosUniversal().equals("NOUN"));
        Assert.assertTrue(baecker1.getDeprel().equals("nsubj"));

        Token baecker2 = biSentence_projected.getSentenceTL().getToken(3);
        Assert.assertTrue(baecker2.getPosUniversal().equals("NOUN"));
        Assert.assertTrue(baecker2.getDeprel().equals("nsubj"));

        System.out.println("project only SRL: \n" + biSentence_srlonly.getSentenceTL());
        System.out.println("project everything: \n" + biSentence_projected.getSentenceTL());

    }

    /**
     * Annotation transfer test 5. Here we make a distinction between annotating everything and annotating only semantics.
     */
    @Test
    public void testTransferEnglishGerman_6() {

        // English sentence with Gold annotations
        Sentence sentenceSL = Sentence.fromConllU("1    Ferddy        Ferddy        PROPN   NNP    _   2    compound    _   Ner=Person     _   _           _     _     \n" +
                "2    Fergunson     Fergunson     PROPN   NNP    _   3    nsubj       _   Ner=Person     _   _           A0    _     \n" +
                "3    had           have          VERB    VBD    _   0    root        _   _              Y   have.03     _     _     \n" +
                "4    the           the           DET     DT     _   5    det         _   _              _   _           _     _     \n" +
                "5    opportunity   opportunity   NOUN    NN     _   3    dobj        _   _              _   _           A1    _     \n" +
                "6    to            to            PART    TO     _   7    mark        _   _              _   _           _     _     \n" +
                "7    attend        attend        VERB    VB     _   5    acl         _   _              Y   attend.01   _     _     \n" +
                "8    one           one           NUM     CD     _   7    dobj        _   Ner=Number     _   _           _     A1    \n" +
                "9    of            of            ADP     IN     _   11   case        _   _              _   _           _     _     \n" +
                "10   his           he            PRON    PRP$   _   11   nmod:poss   _   _              _   _           _     _     \n" +
                "11   seminars      seminar       NOUN    NNS    _   8    nmod        _   _              _   _           _     _     \n" +
                "12   in            in            ADP     IN     _   13   case        _   _              _   _           _     _     \n" +
                "13   Essen         Essen         PROPN   NNP    _   7    nmod        _   Ner=Location   _   _           _     AM-LOC\n" +
                "14   .             .             PUNCT   .      _   3    punct       _   _              _   _           _     _\n");

        // German translation with gold annotations
        Sentence sentenceTL = Sentence.fromTokenized("Ferddy Fergunson hatte Gelegenheit , eines seiner Seminare in Essen zu besuchen .");

        AnnotationTransfer transfer = new AnnotationTransfer();

        System.out.println("before transfer: \n" + sentenceTL.toConllU());

        // Transfer only SRL and keep German gold annotations
        BiSentence biSentence = new BiSentence(sentenceSL, sentenceTL).align(aligner);
        transfer.transfer(biSentence);

        System.out.println("project only SRL: \n" + biSentence.getSentenceTL());

    }


    @Before
    public void initDictionary() {

        aligner.addTranslation("it", "es");
        aligner.addTranslation("'s", "ist");
        aligner.addTranslation("with", "ist", 0.5);
        aligner.addTranslation("plagued", "geplagt");
        aligner.addTranslation("idea", "idee");
        aligner.addTranslation("with", "mit");
        aligner.addTranslation("problems", "problemen");


        aligner.addTranslation("held", "hielt");
        aligner.addTranslation("i", "ich");
        aligner.addTranslation("year", "jahr", 0.5);
        aligner.addTranslation("miracle", "wunder");
        aligner.addTranslation("hands", "händen");
        aligner.addTranslation("in", "in");
        aligner.addTranslation("quarter", "viertel");


        aligner.addTranslation("whether", "ob");
        aligner.addTranslation("decoration", "dekoration");
        aligner.addTranslation("or", "oder", 0.5);
        aligner.addTranslation("fireworks", "feuerwerk");
        aligner.addTranslation("i", "ich");
        aligner.addTranslation("am", "bin");
        aligner.addTranslation("thrilled", "begeistert");
        aligner.addTranslation("by", "von");
        aligner.addTranslation("the", "der");
        aligner.addTranslation("selection", "und");
        aligner.addTranslation("quality", "qualität");

        aligner.addTranslation("mars", "mars");
        aligner.addTranslation("is", "ist");
        aligner.addTranslation("dioxide", "kohlendioxid", 0.5);

        aligner.addTranslation("has", "hat");
        aligner.addTranslation("great", "super");
        aligner.addTranslation("dioxide", "kohlendioxid", 0.5);
        aligner.addTranslation("rolls", "bröchten");
        aligner.addTranslation("baker", "bäcker");

        aligner.addTranslation("attend", "besuchen");
    }
}
