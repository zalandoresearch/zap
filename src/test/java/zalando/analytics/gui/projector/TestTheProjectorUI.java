package zalando.analytics.gui.projector;

import org.junit.Ignore;
import org.junit.Test;
import zalando.analytics.base.Language;


/**
 * Created by Alan Akbik (alan.akbik@zalando.de) on 9/14/17.
 */
public class TestTheProjectorUI {

    @Test
    @Ignore
    public void testStartServer() {

        TheProjectorUI.instance()
//                .setGoldTargetCorpus("/home/aakbik/Documents/Code/release/zap/src/main/resources/corpora/de-ud-dev.conllu", Language.GERMAN)
//                .setTranslations("/home/aakbik/Documents/Code/release/zap/src/main/resources/translations/de-ud.translations")
                .startServerAtPort(9000);

//        startServerAtPort(9001);

    }
}
