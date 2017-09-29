package zalando.analytics.annotation.transfer;

import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Alan Akbik on 8/31/17.
 * <p>
 * Simple class that holds a map of precomputed sentence translations. Use it to retrieve translations
 */
public class TranslationRetriever {

    // Internal translation map
    private Map<String, String> translationMap = Maps.newHashMap();

    /**
     * Load translations from classpath file or file list
     *
     * @param paths File or file list on classpath of tab-separated translations (source \t translation)
     * @return TranslationRetriever object holding translations
     */
    public static TranslationRetriever loadTranslations(String... paths) {
        System.out.println(".. init translator");
        TranslationRetriever instance = new TranslationRetriever();
        for (String path : paths) {
            InputStream inputStream = TranslationRetriever.class.getClassLoader().getResourceAsStream(path);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                instance.translationMap.put(line.split("\\t")[0], line.split("\\t")[1]);
            }
        }
        return instance;
    }

    /**
     * Load translations from classpath file or file list
     *
     * @param paths File or file list on classpath of tab-separated translations (source \t translation)
     * @return TranslationRetriever object holding translations
     */
    public static TranslationRetriever loadTranslationsFromFile(String... paths) {
        System.out.println(".. init translator");
        TranslationRetriever instance = new TranslationRetriever();
        for (String path : paths) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(path));

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    instance.translationMap.put(line.split("\\t")[0], line.split("\\t")[1]);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    /**
     * Translation retrieval method, return translation for given source sentence
     *
     * @param textToTranslate Text for which translation is requested
     * @return Translated text
     */
    public String translate(String textToTranslate) {
        if (this.translationMap.containsKey(textToTranslate)) return translationMap.get(textToTranslate);
        return "";
    }

//    /**
//     * Init English-French translations
//     * @return TranslationRetriever for English-French
//     */
//    public static TranslationRetriever getFrenchTranslations(){
//        return loadTranslations("translations/fr-ud.translations");
//    }
//
//    /**
//     * Init English-German translations
//     * @return TranslationRetriever for English-German
//     */
//    public static TranslationRetriever getGermanTranslations(){
//        return loadTranslations("translations/de-ud.translations", "translations/annotation-sample.translations");
//    }
//
//    /**
//     * Init English-Spanish translations
//     * @return TranslationRetriever for English-Spanish
//     */
//    public static TranslationRetriever getSpanishTranslations(){
//        return loadTranslations("translations/es-ud.translations");
//    }


}
