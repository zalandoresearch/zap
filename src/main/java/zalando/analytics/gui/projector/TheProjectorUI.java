package zalando.analytics.gui.projector;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import zalando.analytics.annotation.transfer.TranslationRetriever;
import zalando.analytics.base.Language;
import zalando.analytics.corpus.ConllFileReader;

/**
 * Created by Alan Akbik on 4/21/17.
 * <p>
 * Class that starts the web server for the Projector UI
 */
public class TheProjectorUI {

    boolean goldSourceSet = false;

    boolean goldTargetSet = false;

    /**
     * TheProjectorUI method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

//        TheProjectorUI.instance()
//                .setGoldSourceCorpus("/home/aakbik/Documents/Code/release/zap/src/main/resources/corpora/annotation-sample.conllu")
//                .setTranslations("/home/aakbik/Documents/Code/release/zap/src/main/resources/translations/annotation-sample.translations")
//                .startDebugServerAtPort(9000);

        TheProjectorUI.instance()
//                .setGoldTargetCorpus("/home/aakbik/Documents/Code/release/zap/src/main/resources/corpora/de-ud-dev.conllu", Language.GERMAN)
//                .setTranslations("/home/aakbik/Documents/Code/release/zap/src/main/resources/translations/de-ud.translations")
                .startDebugServerAtPort(9000);
    }

    public void startServerAtPort(int port){
        ResourceConfig rc = new ClassNamesResourceConfig(ClasspathHandler.class, ProjectorHandler.class);

        String uri = "http://9.1.71.21:" + port + "/";
        try {
            HttpServer server = HttpServerFactory.create(uri, rc);
            server.start();

            String extra = "";
            if (goldSourceSet) extra = "?input=gold-sl";
            if (goldTargetSet) extra = "?input=gold-tl";

            System.out.println(String.format("Demo available at http://localhost:"
                    + port + "/index.html" + extra));
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TheProjectorUI instance(){
        return new TheProjectorUI();
    }

    public TheProjectorUI setTranslations(String pathToTranslations){
        LanguageDAO.getInstance().translations =  TranslationRetriever.loadTranslationsFromFile(pathToTranslations);
        return this;
    }

    public TheProjectorUI setGoldTargetCorpus(String pathToCorpus, Language language){
        goldTargetSet = true;
        LanguageDAO.getInstance().goldTargetCorpus =  new Corpus(ConllFileReader.readFromFile(pathToCorpus).readSentences(100), language);
        return this;
    }

    public TheProjectorUI setGoldSourceCorpus(String pathToCorpus){
        goldSourceSet = true;
        LanguageDAO.getInstance().goldSourceCorpus =  new Corpus(ConllFileReader.readFromFile(pathToCorpus).readSentences(100), Language.ENGLISH);
        return this;
    }

    private void startDebugServerAtPort(int port){
        ResourceConfig rc = new ClassNamesResourceConfig(LocalHandler.class, ProjectorHandler.class);

        String uri = "http://9.1.71.21:" + port + "/";
        try {
            HttpServer server = HttpServerFactory.create(uri, rc);
            server.start();

            System.out.println(String.format("Demo available at http://localhost:"
                    + port));
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

