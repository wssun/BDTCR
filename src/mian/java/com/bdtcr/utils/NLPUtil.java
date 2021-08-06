package com.bdtcr.utils;

import com.bdtcr.tsearcher.SynonymPair;
import com.bdtcr.models.TokenModel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author sunweisong
 * @Date 2020/3/18 8:19 PM
 */
public class NLPUtil {

    // https://stackoverflow.com/questions/43418812/check-whether-a-string-contains-japanese-chinese-characters
    final static String REGEX_CHINESE_AND_JAPANESE = "[\\u3040-\\u30ff\\u3400-\\u4dbf\\u4e00-\\u9fff\\uf900-\\ufaff\\uff66-\\uff9f\\u0400-\\u04FF]";
    final static List<String> posAbbrList = Arrays.asList("NN", "NNS", "NNP", "NNPS"
            , "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"
            , "JJ", "JJR", "JJS");

    static Properties properties = new Properties();
    static StanfordCoreNLP stanfordCoreNLP;

    static {
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        properties.setProperty("tokenize.options", "ptb3Escaping=false");
        stanfordCoreNLP = new StanfordCoreNLP(properties);
    }

    /**
     * Preprocessing the comments including tokenize, pos and lemma
     * @param commentStr
     * @return List<TokenModel>
     * @date 2020/3/3 7:48 PM
     * @author sunweisong
     */
    public static List<TokenModel> commentNLPProcessing(String commentStr) {
        List<TokenModel> tokenModelList = new ArrayList<>();
//        Properties props = new Properties();
//        // 设置相应的properties
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//        props.setProperty("tokenize.options", "ptb3Escaping=false");
        // 获得StanfordCoreNLP 对象
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation document = new Annotation(commentStr);
        stanfordCoreNLP.annotate(document);
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        for (CoreLabel tempToken : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            // this is the text of the token
            String token = tempToken.get(CoreAnnotations.TextAnnotation.class);
            // this is the POS tag of the token
            String pos = tempToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            // this is the lemma of the token
            String lemma = tempToken.get(CoreAnnotations.LemmaAnnotation.class);
            if (posAbbrList.contains(pos)) {
                tokenModelList.add(new TokenModel(token, pos, lemma));
            }
        }
        return tokenModelList;
    }


    public static void main(String[] args) {
        String commentStr = "<P> Sets the name of the {@link LuceneIndex} as identified in the {@link GemFireCache}. </p>";
        List<TokenModel> tokenModelList = commentNLPProcessing(commentStr);
        for (TokenModel tokenModel : tokenModelList) {
            System.out.println(tokenModel.toString());
        }
//        System.out.println("Before: " + commentStr);
//        System.out.println("After: " + tokensOfStr);

        String commentStr1 = "@param indexName {@link String} containing the name of the {@link LuceneIndex}.";
        tokenModelList = commentNLPProcessing(commentStr1);
        System.out.println("Before: " + commentStr1);
        for (TokenModel tokenModel : tokenModelList) {
            System.out.println(tokenModel.toString());
        }
//        System.out.println("After: " + tokensOfStr);

        String commentStr2 = "@see #setBeanName(String). ";
        tokenModelList = commentNLPProcessing(commentStr2);
        for (TokenModel tokenModel : tokenModelList) {
            System.out.println(tokenModel.toString());
        }
        System.out.println("Before: " + commentStr2);
//        System.out.println("After: " + tokensOfStr);
    }

    public static void test1() {
        String commentStr = "Sets the name of the {@link LuceneIndex} as identified in the {@link GemFireCache}.";
        String tokensOfStr = tokenizeCommentStr(commentStr);
        System.out.println("Before: " + commentStr);
        System.out.println("After: " + tokensOfStr);

        String commentStr1 = "@param indexName {@link String} containing the name of the {@link LuceneIndex}.";
        tokensOfStr = tokenizeCommentStr(commentStr1);
        System.out.println("Before: " + commentStr1);
        System.out.println("After: " + tokensOfStr);

        String commentStr2 = "@see #setBeanName(String). ";
        tokensOfStr = tokenizeCommentStr(commentStr2);
        System.out.println("Before: " + commentStr2);
        System.out.println("After: " + tokensOfStr);
    }


    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/8/14 15:45
     * @author sunweisong
     */
    public static String tokenizeCommentStr(String commentStr) {
        StringBuffer stringBuffer = new StringBuffer();
        Properties props = new Properties();
        // 设置相应的properties
        props.setProperty("annotators", "tokenize, ssplit");
        props.setProperty("tokenize.options", "ptb3Escaping=false");
        // 获得StanfordCoreNLP 对象
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(commentStr);
        pipeline.annotate(document);
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        for (CoreLabel tempToken : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            // this is the text of the token
            String token = tempToken.get(CoreAnnotations.TextAnnotation.class);
            stringBuffer.append(token + " ");
        }
        return stringBuffer.toString();
    }


    /**
     * Normalize the tokens.
     * @param tokenModelList
     * @return Map<String, String>:<token, pos>
     * @throws
     * @date 2020/3/5 2:18 PM
     * @author sunweisong
     */
    public static Map<String, String> getNormalizedToken(List<TokenModel> tokenModelList) {
        Map<String, String> keywordMap = new HashMap<>();
        for (TokenModel tokenModel : tokenModelList) {
            String lemma = tokenModel.getLemma();
            String pos = tokenModel.getPos();
            if (pos.startsWith("N")) {
                keywordMap.put(lemma, "NOUN");
            } else if (pos.startsWith("V")) {
                keywordMap.put(lemma, "VERB");
            } else if (pos.startsWith("J")) {
                keywordMap.put(lemma, "ADJECTIVE");
            } else {
                // others
            }
        }
        return keywordMap;
    }

    /**
     * Calculate the Jaccard Distance between two keyword maps.
     * @param keywordMap1
     * @param keywordMap2
     * @return double
     * @date 2020/3/18 9:31 PM
     * @author sunweisong
     */
    public static double calculateJDTwoKeyWordMaps(Map<String, String> keywordMap1
            , Map<String, String> keywordMap2) {

        Set<String> keywordSet1 = keywordMap1.keySet();
        Set<String> keywordSet2 = keywordMap2.keySet();

        Set<String> tempSet = new HashSet<>();
        // intersection of keywordSet1 and keywordSet2
        Set<String> intersection = new HashSet<>();
        tempSet.addAll(keywordSet1);
        tempSet.retainAll(keywordSet2);
        intersection.addAll(tempSet);
        // union of keywordSet1 and keywordSet2
        Set<String> union = new HashSet<>();
        tempSet.addAll(keywordSet1);
        tempSet.addAll(keywordSet2);
        union.addAll(tempSet);
        // difference of keywordSet1 and intersection
        Set<String> differenceOfKeywordSet1AndIntersection = new HashSet<>();
        keywordSet1.removeAll(intersection);
        differenceOfKeywordSet1AndIntersection.addAll(keywordSet1);
        // difference of keywordSet2 and intersection
        Set<String> differenceOfKeywordSet2AndIntersection = new HashSet<>();
        keywordSet2.removeAll(intersection);
        differenceOfKeywordSet2AndIntersection.addAll(keywordSet2);
        // Get synonyms from WordNet
        int differenceOfKeywordSet1AndIntersectionSize = differenceOfKeywordSet1AndIntersection.size();
        int differenceOfKeywordSet2AndIntersectionSize = differenceOfKeywordSet2AndIntersection.size();
        Map<String, String> wordMap;
        Map<String, Set<String>> synonymMap;
        List<SynonymPair> synonymPairList;
        if (differenceOfKeywordSet1AndIntersectionSize <= differenceOfKeywordSet2AndIntersectionSize) {
            wordMap = new HashMap<>(differenceOfKeywordSet1AndIntersectionSize);
            for (String word : differenceOfKeywordSet1AndIntersection) {
                wordMap.put(word, keywordMap1.get(word));
            }
            synonymMap = WordNet.getSynonymsForWords(wordMap);
            synonymPairList = getSynonymPairs(synonymMap, differenceOfKeywordSet2AndIntersection);
            if (synonymPairList != null) {
                // analyze the pos of synonyms. some word may have pos of VERB or NOUN.
                Iterator<SynonymPair> iterator = synonymPairList.iterator();
                while(iterator.hasNext()){
                    SynonymPair synonymPair = iterator.next();
                    String word1Pos = keywordMap1.get(synonymPair.getWord1());
                    String word2Pos = keywordMap2.get(synonymPair.getWord2());
                    if (word1Pos.equals(word2Pos)) {
                        synonymPair.setPos(word1Pos);
                    } else {
                        iterator.remove();
                    }
                }
            }
        } else {
            wordMap = new HashMap<>(differenceOfKeywordSet2AndIntersectionSize);
            for (String word : differenceOfKeywordSet2AndIntersection) {
                wordMap.put(word, keywordMap2.get(word));
            }
            synonymMap = WordNet.getSynonymsForWords(wordMap);
            synonymPairList = getSynonymPairs(synonymMap, differenceOfKeywordSet1AndIntersection);
            if (synonymPairList != null) {
                Iterator<SynonymPair> iterator = synonymPairList.iterator();
                while(iterator.hasNext()){
                    SynonymPair synonymPair = iterator.next();
                    String word1Pos = keywordMap2.get(synonymPair.getWord1());
                    String word2Pos = keywordMap1.get(synonymPair.getWord2());
                    if (word1Pos.equals(word2Pos)) {
                        synonymPair.setPos(word1Pos);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        // calculate the comment distance.
        int intersectionSize = intersection.size();
        int unionSize = union.size();
        int synonymPairNumber = 0;
        if (synonymPairList != null) {
            synonymPairNumber = synonymPairList.size();
        }
//        System.out.println("intersectionSize: " + intersectionSize);
//        System.out.println("unionSize: " + unionSize);
//        System.out.println("synonymPairNumber:" + synonymPairNumber);
        double distance = 1.0 - (double) (intersectionSize + synonymPairNumber) / (unionSize - synonymPairNumber);
        return distance;
    }


    /**
     * Get the synonym pairs.
     * @param  synonymMap
     * @param  differenceOfKeywordSetAndIntersection
     * @return List<SynonymPair>
     * @date 2020/3/5 3:22 PM
     * @author sunweisong
     */
    private static List<SynonymPair> getSynonymPairs(Map<String,Set<String>> synonymMap
            , Set<String> differenceOfKeywordSetAndIntersection) {
        List<SynonymPair> synonymPairs = new ArrayList<>();
        Iterator<Map.Entry<String, Set<String>>> entries = synonymMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, Set<String>> entry = entries.next();
            String word = entry.getKey();
            Set<String> synonymSet = entry.getValue();
            if (synonymSet == null) {
                continue;
            }
            for (String keyword : differenceOfKeywordSetAndIntersection) {
                if (!synonymSet.contains(keyword)) {
                    continue;
                }
                synonymPairs.add(new SynonymPair(word, keyword));
                differenceOfKeywordSetAndIntersection.remove(keyword);
                break;
            }
        }
        if (synonymPairs.size() == 0) {
            synonymPairs = null;
        }
        return synonymPairs;
    }

    /**
     * Judge whether the string contains the Chinese or Japanese.
     * @param
     * @return
     * @throws
     * @date 2020/4/24 1:49 AM
     * @author sunweisong
     */
    public static boolean isContainOtherLanguage(String string) {
        Pattern p = Pattern.compile(REGEX_CHINESE_AND_JAPANESE);
        Matcher m = p.matcher(string);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
