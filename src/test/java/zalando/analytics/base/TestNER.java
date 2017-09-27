package zalando.analytics.base;

import com.google.common.collect.Maps;
import org.junit.Test;
import zalando.analytics.corpus.ConllFileReader;
import zalando.analytics.corpus.LineReader;
import zalando.analytics.helpers.CollectionHelper;
import zalando.analytics.parser.wrappers.PipelineWrapper;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 9/12/17.
 */
public class TestNER {

    @Test
    public void testNer() {

        Sentence sentence = Sentence.fromConllU("1    Ferddy        Ferddy        PROPN   NNP    _   2    compound    _   Ner=Person     _   _           _     _     \n" +
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

        System.out.println("sentence = " + sentence);

        for (Constituent constituent : sentence.getNER()) {
            System.out.println(constituent.getType() + " = " + constituent);
        }

    }

    @Test
    public void testNer2() {

        Sentence sentence = Sentence.fromConllU("1    Workwear      Workwear      PROPN   NNP   _   2    compound    _   Ner=Category                    _   _           _     _     _     _     \n" +
                "2    Spring        Spring        PROPN   NNP   _   44   nsubj       _   Ner=FashionSeason               _   _           _     _     _     A1    \n" +
                "3    Essential     essential     ADJ     JJ    _   2    amod        _   _                               _   _           _     _     _     _     \n" +
                "4    #             #             NOUN    #     _   5    compound    _   _                               _   _           _     _     _     _     \n" +
                "5    4             4             NUM     CD    _   3    dep         _   _                               _   _           _     _     _     _     \n" +
                "6    :             :             PUNCT   :     _   5    punct       _   _                               _   _           _     _     _     _     \n" +
                "7    The           the           DET     DT    _   11   det         _   _                               _   _           _     _     _     _     \n" +
                "8    Linen         Linen         PROPN   NNP   _   11   compound    _   Ner=Textile                     _   _           _     _     _     _     \n" +
                "9    Blazer        Blazer        PROPN   NNP   _   11   compound    _   Ner=NominalProduct              _   _           _     _     _     _     \n" +
                "10   Linen         Linen         PROPN   NNP   _   11   compound    _   Ner=Textile                     _   _           _     _     _     _     \n" +
                "11   Blazer        Blazer        PROPN   NNP   _   5    dep         _   Ner=NominalProduct              _   _           _     _     _     _     \n" +
                "12   ,             ,             PUNCT   ,     _   11   punct       _   _                               _   _           _     _     _     _     \n" +
                "13   $             $             SYM     $     _   14   dep         _   _                               _   _           _     _     _     _     \n" +
                "14   68            68            NUM     CD    _   11   appos       _   _                               _   _           _     _     _     _     \n" +
                "15   ,             ,             PUNCT   ,     _   11   punct       _   _                               _   _           _     _     _     _     \n" +
                "16   ASOS          ASOS          PROPN   NNP   _   11   appos       _   Ner=NamedOrganizationRetailer   _   _           A0    _     _     _     \n" +
                "17   The           the           DET     DT    _   19   det         _   _                               _   _           _     _     _     _     \n" +
                "18   biggest       biggest       ADJ     JJS   _   19   amod        _   _                               _   _           _     _     _     _     \n" +
                "19   secret        secret        NOUN    NN    _   16   dep         _   _                               _   _           _     _     _     _     \n" +
                "20   to            to            PART    TO    _   21   mark        _   _                               _   _           _     _     _     _     \n" +
                "21   dressing      dress         VERB    VBG   _   16   acl         _   _                               Y   dress.01    _     _     _     _     \n" +
                "22   comfortably   comfortably   ADV     RB    _   21   advmod      _   _                               _   _           AM-MNR_     _     _     \n" +
                "23   for           for           ADP     IN    _   27   case        _   _                               _   _           _     _     _     _     \n" +
                "24   spring        spring        NOUN    NN    _   27   compound    _   _                               _   _           _     _     _     _     \n" +
                "25   and           and           CCONJ   CC    _   24   cc          _   _                               _   _           _     _     _     _     \n" +
                "26   summer        summer        NOUN    NN    _   24   conj        _   _                               _   _           _     _     _     _     \n" +
                "27   months        month         NOUN    NNS   _   22   nmod        _   _                               _   _           _     _     _     _     \n" +
                "28   isn           isn           NOUN    NN    _   21   dobj        _   _                               _   _           A1    _     _     _     \n" +
                "29   '             '             PUNCT   ''    _   21   punct       _   _                               _   _           _     _     _     _     \n" +
                "30   t             t             NOUN    NN    _   21   dobj        _   _                               _   _           A1    _     _     _     \n" +
                "31   always        always        ADV     RB    _   30   advmod      _   _                               _   _           _     _     _     _     \n" +
                "32   about         about         ADP     IN    _   33   mark        _   _                               _   _           _     _     _     _     \n" +
                "33   changing      change        VERB    VBG   _   21   advcl       _   _                               Y   change.01   AM-MNR_     _     _     \n" +
                "34   the           the           DET     DT    _   35   det         _   _                               _   _           _     _     _     _     \n" +
                "35   silhouette    silhouette    NOUN    NN    _   33   dobj        _   _                               _   _           _     A1    _     _     \n" +
                "36   --            --            PUNCT   :     _   33   punct       _   _                               _   _           _     _     _     _     \n" +
                "37   changing      change        VERB    VBG   _   33   parataxis   _   _                               Y   change.01   _     _     _     _     \n" +
                "38   the           the           DET     DT    _   39   det         _   _                               _   _           _     _     _     _     \n" +
                "39   fabric        fabric        NOUN    NN    _   37   dobj        _   _                               _   _           _     _     A1    _     \n" +
                "40   is            be            VERB    VBZ   _   44   cop         _   _                               Y   be.01       _     _     _     _     \n" +
                "41   often         often         ADV     RB    _   44   advmod      _   _                               _   _           _     _     _     AM-TMP\n" +
                "42   the           the           DET     DT    _   44   det         _   _                               _   _           _     _     _     _     \n" +
                "43   right         right         ADJ     JJ    _   44   amod        _   _                               _   _           _     _     _     _     \n" +
                "44   move          move          NOUN    NN    _   0    root        _   _                               _   _           _     _     _     A2    \n" +
                "45   .             .             PUNCT   .     _   44   punct       _   _                               _   _           _     _     _     _");

        for (Constituent constituent : sentence.getNER()) {
            System.out.println(constituent.getType() + " = " + constituent);
        }

    }

}
