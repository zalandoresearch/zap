package zalando.analytics.base;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 9/12/17.
 *
 * Test class for Sentence object
 */
public class TestSentence {

    /**
     * This method shows how to create a new Sentence object by adding Tokens one at a time (probably rare usage)
     */
    @Test
    public void testCreateSentence() {

        /*
         Method 1: Create a Sentence token by token
          */
        Sentence sentence = new Sentence();

        Token tokenHello = sentence.newToken();
        tokenHello.setText("Hello");
        tokenHello.setLemma("hi");
        tokenHello.setPos("UH");
        tokenHello.setPosUniversal("XX");

        Token tokenWorld = sentence.newToken();
        tokenWorld.setText("World");
        tokenWorld.setLemma("world");
        tokenWorld.setPos("NN");
        tokenWorld.setPosUniversal("NOUN");

        System.out.println(sentence.toString());

        /*
         Method 2: Same as method 1, but compact
          */
        Sentence sentence2 = new Sentence();

        sentence2.newToken().setText("Hello").setLemma("hi").setPos("UH").setPosUniversal("XX");
        sentence2.newToken().setText("World").setLemma("world").setPos("NN").setPosUniversal("NOUN");

        System.out.println(sentence2.toString());

        // check that both methods give us the same sentence
        Assert.assertEquals(sentence.toConllU(), sentence2.toConllU());
    }

    /**
     * This method shows how to create a new Sentence object by reading Conll-u format. Probably default usage.
     */
    @Test
    public void testReadFromConllU() {

        String conllu = "# sent_id = dev-s6\n" +
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
                "16   .          .          PUNCT   $.      _                                                       4    punct       _   _               _   _         _";

        Sentence sentence = Sentence.fromConllU(conllu);

        // check that sentence can be written out to conllu string
        Assert.assertEquals(sentence.toConllU(), conllu);

        // check fields of Sentence
        Assert.assertTrue(sentence.getToken(3).getPosUniversal().equals("NOUN"));
        Assert.assertTrue(sentence.getToken(4).getPosUniversal().equals("VERB"));
        Assert.assertTrue(sentence.getToken(4).getFrame().getLabel().equals("have.03"));
    }

    /**
     * This method checks if we can navigate through dependency tree
     */
    @Test
    public void testNaviagetDependencyTree() {

        String conllu = "# sent_id = dev-s6\n" +
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
                "16   .          .          PUNCT   $.      _                                                       4    punct       _   _               _   _         _";

        Sentence sentence = Sentence.fromConllU(conllu);

        Token baecker = sentence.getToken(3);

        Assert.assertEquals("Bäcker", baecker.getText());
        Assert.assertEquals("hat", baecker.getHead().getText());
        Assert.assertEquals("dieser", baecker.getChildren().get(0).getText());

        System.out.println("baecker: " + baecker);
        System.out.println("baecker:  " + baecker.getHead());
        List<Token> children = baecker.getChildren();
        for (Token child : children) {
            System.out.println("child:  " + child);
        }


    }
}
