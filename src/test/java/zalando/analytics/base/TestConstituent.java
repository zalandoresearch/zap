package zalando.analytics.base;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Alan Akbik on 8/30/17.
 *
 * Test class for finding and rendering Constituent in Sentence
 */
public class TestConstituent {

    @Test
    public void renderConstituent() {

        Sentence sentence = Sentence.fromConllU("1    One         one        NUM     CD    _   8    nsubj    _   _   _   _          A1    _     _     \n" +
                "2    of          of         ADP     IN    _   5    case     _   _   _   _          _     _     _     \n" +
                "3    the         the        DET     DT    _   5    det      _   _   _   _          _     _     _     \n" +
                "4    big         big        ADJ     JJ    _   5    amod     _   _   _   _          _     _     _     \n" +
                "5    questions   question   NOUN    NNS   _   1    nmod     _   _   _   _          _     _     _     \n" +
                "6    about       about      ADP     IN    _   7    case     _   _   _   _          _     _     _     \n" +
                "7    Mars        Mars       PROPN   NNP   _   5    nmod     _   _   _   _          _     _     _     \n" +
                "8    is          be         VERB    VBZ   _   0    root     _   _   Y   be.01      _     _     _     \n" +
                "9    when        when       ADV     WRB   _   16   advmod   _   _   _   _          _     AM-TMP_     \n" +
                "10   or          or         CCONJ   CC    _   9    cc       _   _   _   _          _     _     _     \n" +
                "11   whether     whether    ADP     IN    _   9    dep      _   _   _   _          _     _     _     \n" +
                "12   humans      human      NOUN    NNS   _   16   nsubj    _   _   _   _          _     A1    A0    \n" +
                "13   will        will       AUX     MD    _   16   aux      _   _   _   _          _     AM-MOD_     \n" +
                "14   ever        ever       ADV     RB    _   16   advmod   _   _   _   _          _     AM-TMP_     \n" +
                "15   be          be         VERB    VB    _   16   cop      _   _   Y   be.01      _     _     _     \n" +
                "16   able        able       ADJ     JJ    _   8    advcl    _   _   _   _          A2    A2    _     \n" +
                "17   to          to         ADP     TO    _   18   mark     _   _   _   _          _     _     _     \n" +
                "18   visit       visit      VERB    VB    _   16   xcomp    _   _   Y   visit.01   _     _     _     \n" +
                "19   .           .          PUNCT   .     _   0    punct    _   _   _   _          _     _     _\n");

        Constituent constituent = sentence.getToken(5).getConstituent(sentence.getToken(8));
        Assert.assertEquals(constituent.toString(), "of the big questions about Mars");

        constituent = sentence.getToken(9).getConstituent(sentence.getToken(16));
        Assert.assertEquals(constituent.toString(), "when or whether");
    }


    @Test
    public void renderRelativeClause(){

        Sentence sentence = Sentence.fromConllU("1    The    the    DT    DET     _   2   det     _   _   \n" +
                "2    man    man    NN    NOUN    _   0   root    _   _   \n" +
                "3    who    who    WP    PRON    _   4   nsubj   _   _   \n" +
                "4    sang   sing   VBD   VERB    _   2   ccomp   _   _   \n" +
                "5    a      a      DT    DET     _   6   det     _   _   \n" +
                "6    song   song   NN    NOUN    _   4   dobj    _   _   \n" +
                "7    .      .      .     PUNCT   _   2   punct   _   _\n");

        Constituent constituent = sentence.getToken(2).getConstituent(sentence.getToken(4));
        Assert.assertEquals(constituent.toString(), "The man");
        constituent = sentence.getToken(2).getConstituent(sentence.getToken(7));
        Assert.assertEquals(constituent.toString(), "The man who sang a song");
    }


    @Test
    public void renderCopular(){

        Sentence sentence = Sentence.fromConllU("1    The     the     DT    DET     _   2   det     _   _   \n" +
                "2    man     man     NN    NOUN    _   4   nsubj   _   _   \n" +
                "3    was     be      VBD   VERB    _   4   cop     _   _   \n" +
                "4    right   right   JJ    ADJ     _   0   root    _   _   \n" +
                "5    about   about   IN    ADP     _   6   case    _   _   \n" +
                "6    this    this    DT    DET     _   4   nmod    _   _   \n" +
                "7    .       .       .     PUNCT   _   4   punct   _   _\n");

        Constituent constituent = sentence.getToken(2).getConstituent(sentence.getToken(3));
        Assert.assertEquals(constituent.toString(), "The man");

        sentence = Sentence.fromConllU("1    Mars           mars           _      NE       _   3    nsubj       _   _   _   _            A1    _     \n" +
                "2    ist            sein           VERB   VAFIN    _   3    cop         _   _   Y   be.01        _     _     \n" +
                "3    Kohlendioxid   kohlendioxid   NOUN   NN       _   0    root        _   _   _   _            _     _     \n" +
                "4    ,              --             _      $,       _   3    punct       _   _   _   _            _     _     \n" +
                "5    also           also           _      ADV      _   10   advmod      _   _   _   _            _     _     \n" +
                "6    können         können         VERB   VMFIN    _   10   aux         _   _   _   _            _     AM-MOD\n" +
                "7    wir            wir            _      PPER     _   10   nsubj       _   _   _   _            _     A0    \n" +
                "8    dort           dort           _      ADV      _   10   advmod      _   _   _   _            _     AM-LOC\n" +
                "9    nicht          nicht          _      PTKNEG   _   10   neg         _   _   _   _            _     AM-NEG\n" +
                "10   atmen          atmen          VERB   VVINF    _   3    parataxis   _   _   Y   breathe.01   _     _     \n" +
                "11   .              --             _      $.       _   3    punct       _   _   _   _            _     _\n");
        constituent = sentence.getToken(3).getConstituent(sentence.getToken(2));
        System.out.println("constituent = " + constituent);
    }

    @Test
    public void renderClause(){

        Sentence sentence = Sentence.fromConllU("1    I          I          PRON    PRP   _   2   nsubj    _   _   _   _            A0    _     \n" +
                "2    received   receive    VERB    VBD   _   0   root     _   _   Y   receive.01   _     _     \n" +
                "3    the        the        DET     DT    _   5   det      _   _   _   _            _     _     \n" +
                "4    ordered    order      VERB    VBN   _   5   amod     _   _   Y   order.01     _     _     \n" +
                "5    items      item       NOUN    NNS   _   2   dobj     _   _   _   _            A1    A1    \n" +
                "6    promptly   promptly   ADV     RB    _   2   advmod   _   _   _   _            AM-TMP_     \n" +
                "7    .          .          PUNCT   .     _   2   punct    _   _   _   _            _     _\n");

        Constituent constituent = sentence.getToken(5).getConstituent(sentence.getToken(4));
        Assert.assertEquals(constituent.toString(), "items");

    }

    @Test
    public void restRenderPartialParse(){

        Sentence sentence = Sentence.fromConllU("1    Flüssig-Sauerstoff   _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "2    wäre                 _   VERB    VB    _   5    cop          _   _   Y   be.01      _     _     \n" +
                "3    viel                 _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "4    zu                   _   ADV     RB    _   5    advmod       _   _   _   _          _     _     \n" +
                "5    schwer               _   ADJ     JJ    _   0    _            _   _   _   _          _     _     \n" +
                "6    ,                    _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "7    um                   _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "8    den                  _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "9    ganzen               _   DET     PDT   _   10   det:predet   _   _   _   _          _     _     \n" +
                "10   Weg                  _   NOUN    NN    _   14   nmod         _   _   _   _          _     AM-DIR\n" +
                "11   zum                  _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "12   Mars                 _   PROPN   NNP   _   10   nmod         _   _   _   _          _     _     \n" +
                "13   zu                   _   _       _     _   0    _            _   _   _   _          _     _     \n" +
                "14   transportieren       _   VERB    VB    _   5    ccomp        _   _   Y   carry.01   A2    _     \n" +
                "15   .                    _   _       _     _   0    _            _   _   _   _          _     _\n");

        Constituent constituent = sentence.getToken(14).getConstituent(sentence.getToken(2));
        Assert.assertEquals(constituent.toString(), "ganzen Weg Mars transportieren");
        Assert.assertEquals(constituent.toStringFull(), "ganzen Weg zum Mars zu transportieren");
    }

}
