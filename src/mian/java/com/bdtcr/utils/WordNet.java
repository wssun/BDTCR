package com.bdtcr.utils;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
  * @Author sunweisong
  * @Date 2020/3/5 12:20 PM
  */
public class WordNet {

    /**
     * the installation directory of the WordNet dictionary
     */
    private final static String WN_HOME_PATH = "/usr/local/WordNet-3.0";

    /**
     * Get synonyms of the word from the WordNet dictionary.
     * @param word
     * @param pos
     * @return Set<String>
     * @date 2020/3/5 2:02 PM
     * @author sunweisong
     */
    public static Set<String> getSynonymsFromWordNet(String word, String pos) {
        String path = WN_HOME_PATH + File.separator + "dict";
        URL url = null;
        try{
            // construct the URL to the Wordnet dictionary directory
            url = new URL("file", null, path);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }
        // construct the dictionary object and open it
        IDictionary dict = new Dictionary(url);
        IIndexWord idxWord = null;
        Set<String> synonymSet = new HashSet<>();
        try {
            dict.open();
            if ("NOUN".equals(pos)) {
                idxWord = dict.getIndexWord(word, POS.NOUN);
            } else if ("VERB".equals(pos)) {
                idxWord = dict.getIndexWord(word, POS.VERB);
            } else if ("ADJECTIVE".equals(pos)) {
                idxWord= dict.getIndexWord(word, POS.ADJECTIVE);
            } else {
                // others
            }
            if (idxWord == null || idxWord.getWordIDs() == null) {
                // no synonyms
                return null;
            }
            // 1st meaning
            List<IWordID> iWordIDList = idxWord.getWordIDs();
            for (IWordID wordID : iWordIDList) {
                IWord iWord = dict.getWord(wordID);
                ISynset iSynset = iWord.getSynset();
                for (IWord synonym : iSynset.getWords()) {
                    String lemma = synonym.getLemma();
                    // exclude itself and phrases
                    if (lemma.contains("_") || lemma.equals(word)) {
                        continue;
                    }
                    synonymSet.add(lemma);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dict != null) {
                dict.close();
            }
        }
        if (synonymSet.size() == 0) {
            synonymSet = null;
        }
        return  synonymSet;
    }

    /**
      *
      * @param wordMap:<word, pos>
      * @return Map<String, Set<String>>
      * @date 2020/3/5 2:04 PM
      * @author sunweisong
      */
    public static Map<String, Set<String>> getSynonymsForWords(Map<String, String> wordMap) {
        Map<String, Set<String>> synonymMap = new HashMap<>();
        Iterator<Map.Entry<String, String>> entries = wordMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, String> entry = entries.next();
            String word = entry.getKey();
            String pos = entry.getValue();
            Set<String> synonymSet = new HashSet<>();
            int commaIndex = pos.indexOf(",");
            if (commaIndex != -1) {
                // for ClassName and MethodName
                for (String tempPos : pos.split(",")) {
                    Set<String> tempSet = getSynonymsFromWordNet(word, tempPos);
                    if (tempSet == null) {
                        continue;
                    }
                    if (synonymSet == null) {
                        synonymSet = getSynonymsFromWordNet(word, tempPos);
                    } else {
                        synonymSet.addAll(tempSet);
                    }
                }
            } else {
                synonymSet = getSynonymsFromWordNet(word, pos);
            }
            synonymMap.put(word, synonymSet);
        }
        return synonymMap;
    }
}
