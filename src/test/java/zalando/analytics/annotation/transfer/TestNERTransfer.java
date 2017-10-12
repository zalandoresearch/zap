package zalando.analytics.annotation.transfer;

import org.junit.Test;
import zalando.analytics.base.Language;
import zalando.analytics.base.Sentence;

/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 10/12/17.
 */
public class TestNERTransfer {

    @Test
    public void testTransferNER(){

        Sentence sl = Sentence.fromSpanAnnotated("<START:NamedPerson> Shailene Woodley <END> posed for some photos at a screening of her new film , Divergent , in <START:NamedLocation> Thousand Oaks <END> , <START:NamedLocation> California <END> .");

        System.out.println(" --- INPUT ---");
        System.out.println(sl.toSentence());
        System.out.println(sl.toSpanConll());

        Sentence tl = Sentence.fromTokenized("Shailene Woodley posierte für einige Fotos bei einer Vorführung ihres neuen Films Divergent in Thousand Oaks , Kalifornien .");

        BiSentence biSentence = new BiSentence(sl, tl);

        biSentence.align(HeuristicAligner.getInstance(Language.GERMAN));

        System.out.println("\n --- Alignment ---");
        System.out.println(biSentence);

        new AnnotationTransfer().transferSpans(biSentence);

        System.out.println("\n --- OUTPUT ---");
        System.out.println(tl.toSentence());
        System.out.println(tl.toSpanConll());

    }
}
