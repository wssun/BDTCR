package com.bdtcr.tsearcher;


import com.bdtcr.tsearcher.SynonymPair;
import com.bdtcr.models.TokenModel;
import com.bdtcr.utils.NLPUtil;
import com.bdtcr.utils.WordNet;
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
 * @Date 2020/3/2 4:51 PM
 */
public class CommentAnalysis {

    final static String REGEX_SINGLE_LINE_COMMENT = "\\s*(//)[\\d\\D]*";

    final static String REGEX_MULTI_LINE_COMMENT = "\\s*(/\\*)([\\d\\D]*)(\\*/)";

    final static String REGEX_DOC_COMMENT = "\\s*(/\\*\\*)([\\d\\D]*)(\\*/)";

    final static String REGEX_CHINESE_AND_JAPANESE = "[\\u3040-\\u30ff\\u3400-\\u4dbf\\u4e00-\\u9fff\\uf900-\\ufaff\\uff66-\\uff9f]";


    void extractKeywords(String commentStr1) {
        // commentStr1 直接从数据库中的读 method_comment 注释
        // 第一步， 提取注释内容，比如 text = 'xxx'
        String commentDescription1 = extractCommentDescription(commentStr1);
        // 第二步 每个 token: <token,pos,lemma>;<token,pos,lemma>;<token,pos,lemma>
        List<TokenModel> tokenModelList1  = NLPUtil.commentNLPProcessing(commentDescription1);
        // 第三步，把所有 TokenModel 串起来写入数据库
    }

    /**
     * Measure the distance between two comments.
     * @param mutComment
     * @param testTargetComment
     * @return double
     * @date 2020/4/22 1:36 PM
     * @author sunweisong
     */
    public static double measureDistanceBetweenTwoComments(String mutComment
            , String testTargetComment) {
        // Process the javadoc comment 'mutComment'
//        String commentDescription = extractDescriptionFromJavaDoc(mutComment);

        String commentDescription = mutComment;
        if (commentDescription == null) {
            return -1;
        }
        List<TokenModel> tokenModelList1  = NLPUtil.commentNLPProcessing(commentDescription);

        // Process the keywords of testTargetComment
        String[] keywordArray = testTargetComment.split(";");
        List<TokenModel> tokenModelList2 = new ArrayList<>(keywordArray.length);
        for (String keyword : keywordArray) {
            int left = keyword.indexOf("{");
            int right = keyword.lastIndexOf("}");
            keyword = keyword.substring(left + 1, right);
            String[] elementArray = keyword.split(",");
            String token = elementArray[0];
            left = token.indexOf("'");
            right = token.lastIndexOf("'");
            token = token.substring(left + 1, right);
            String pos = elementArray[1];
            left = pos.indexOf("'");
            right = pos.lastIndexOf("'");
            pos = pos.substring(left + 1, right);
            String lemma = elementArray[2];
            left = lemma.indexOf("'");
            right = lemma.lastIndexOf("'");
            lemma = lemma.substring(left + 1, right);
            tokenModelList2.add(new TokenModel(token, pos, lemma));
        }
        // Normalize the pos of tokens.
        Map<String, String> keywordMap1 = NLPUtil.getNormalizedToken(tokenModelList1);
        Map<String, String> keywordMap2 =  NLPUtil.getNormalizedToken(tokenModelList2);
        // Calculate the distance.
        double distance = NLPUtil.calculateJDTwoKeyWordMaps(keywordMap1, keywordMap2);
        return distance;
    }

    /**
     * Measure the distance between two comments.
     * @param commentStr1 是新的待测方法的注释
     * @param commentStr2 = keywords：<token,pos,lemma>;<token,pos,lemma>;<token,pos,lemma>
     * @return double
     * @date 2020/3/3 9:22 PM
     * @author sunweisong
     */
    public static double measureCommentDistance(String commentStr1, String commentStr2) {
        String commentDescription1 = extractCommentDescription(commentStr1);
        System.out.println("comment1: " + commentDescription1);
        String commentDescription2 = extractCommentDescription(commentStr2);
        System.out.println("comment2: " + commentDescription2);
        List<TokenModel> tokenModelList1  = NLPUtil.commentNLPProcessing(commentDescription1);
        List<TokenModel> tokenModelList2  = NLPUtil.commentNLPProcessing(commentDescription2);

        // 统一化词性
        Map<String, String> keywordMap1 = NLPUtil.getNormalizedToken(tokenModelList1);
        Map<String, String> keywordMap2 =  NLPUtil.getNormalizedToken(tokenModelList2);


        double distance = NLPUtil.calculateJDTwoKeyWordMaps(keywordMap1, keywordMap2);
        return distance;
    }

    /**
     * Extract the description from javaDoc
     * @param
     * @return
     * @throws
     * @date 2020/4/22 4:40 PM
     * @author sunweisong
     */
    public static String extractDescriptionFromJavaDoc(String javaDoc) {
        if (isContainOtherLanguage(javaDoc)) {
            Matcher m = Pattern.compile(REGEX_CHINESE_AND_JAPANESE).matcher(javaDoc);
            while (m.find()) {
                String find = m.group();
                javaDoc = javaDoc.replace(find, " ");
            }
        }
        int start = javaDoc.indexOf("JavadocDescription");
        if (start == -1) {
            /*
            2020.04.29 添加
             */
            return null;
        }
        int end = javaDoc.lastIndexOf("blockTags");
        javaDoc = javaDoc.substring(start, end).trim();
        start = javaDoc.indexOf("[");
        end = javaDoc.lastIndexOf("]");
        javaDoc = javaDoc.substring(start + 1, end);
        javaDoc = javaDoc.replaceAll("\n", " ");
        start = javaDoc.indexOf("{");
        StringBuffer stringBuffer = new StringBuffer();
        while (start != -1) {
            end = javaDoc.indexOf("'}");
            String block = javaDoc.substring(0, end + 2);
            if (!judgeQuoteMatch(block)) {
                // JavadocSnippet{text='} class.'}
                javaDoc = javaDoc.substring(end + 2);
                end = javaDoc.indexOf("'}");
                if (end != -1) {
                    block = block + javaDoc.substring(0, end + 2);
                }
            }
            if (block.charAt(0) == ',') {
                block = block.substring(1).trim();
            }
            int left = block.indexOf("{");
            int right = block.lastIndexOf("'}");
            String propertyName = block.substring(0, left).trim();
            block = block.substring(left + 1, right + 1);
            if ("JavadocSnippet".equals(propertyName)) {
                left = block.indexOf("\'");
                right = block.lastIndexOf("\'");
                String text = block.substring(left + 1, right).trim();
                stringBuffer.append(text + " ");
            } else if ("JavadocInlineTag".equals(propertyName)) {
                left = block.indexOf("content=");
                block = block.substring(left);
                left = block.indexOf("\'");
                right = block.lastIndexOf("\'");
                String content = block.substring(left + 1, right).trim();
                stringBuffer.append(content + " ");
            }
            if (end != -1) {
                javaDoc = javaDoc.substring(end + 2);
            }
            start = javaDoc.indexOf("{");
        }
        String description = stringBuffer.toString();
        stringBuffer = null;
        if (description.contains("<p>")) {
            description = description.replace("<p>", "");
            description = description.replace("</p>", "");
        }
        if (description.contains("<em>")) {
            description = description.replace("<em>", "");
            description = description.replace("</em>", "");
        }
        if (description.contains("<b>")) {
            description = description.replace("<b>", "");
            description = description.replace("</b>", "");
        }
        return description;
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

    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/4/24 12:48 AM
     * @author sunweisong
     */
    private static boolean judgeQuoteMatch(String block) {
        int count = 0;
        for (int i = 0; i < block.length(); i++) {
            char c = block.charAt(i);
            if (c != '\'') {
                continue;
            }
            if (block.charAt(i + 1) == 's' && count != 0) {
                // 'It's a shortcut for `Future.VOID.delayed(delay, scheduler)`'
                // {text='setter UrlMetaData'}
                continue;
            }
            if (block.charAt(i + 1) == 't' && count != 0) {
                // 'We don't try and retry as this might cause other unwanted issues.'
                continue;
            }
            count++;
        }
        if (count % 2 == 0) {
            return true;
        }
        return false;
    }

    /**
     * Extract the description of the javadoc comment.
     * @param javaDocComment
     * @return String
     * @date 2020/4/22 1:29 PM
     * @author sunweisong
     */
    private static String extractDescriptionFromJavaDocComment(String javaDocComment) {
        String regex = "text='.*?'";
        Matcher matcher = Pattern.compile(regex).matcher(javaDocComment);
        StringBuffer textBuffer = new StringBuffer();
        while (matcher.find()) {
            String text = matcher.group();
            System.out.println(text);
            int start = text.indexOf("'");
            int end = text.lastIndexOf("'");
            text = text.substring(start + 1, end).trim();
            char lastChar = text.charAt(text.length() - 1);
            if (!".".equals(lastChar)) {
                textBuffer.append(text + ". ");
            } else {
                textBuffer.append(text + " ");
            }
        }
        if (textBuffer.length() == 0) {
            textBuffer = null;
            return null;
        }
        return textBuffer.toString().trim();
    }


    /**
     * Extract the comment description from the comment string.
     * @param commentStr
     * @return String
     * @date 2020/3/3 9:12 PM
     * @author sunweisong
     */
    public static String extractCommentDescription(String commentStr) {
        String commentDescription = "";
        if (commentStr.matches(REGEX_DOC_COMMENT)) {
            System.out.println("DOC_COMMENT");
            String[] lineArray = commentStr.split("\n");
            for (String line : lineArray) {
                line = line.trim();
                if (line.contains("/**")) {
                    line = line.substring(3).trim();
                } else {
                    line = line.substring(1).trim();
                }
                if ("".equals(line)) {
                    continue;
                }
                commentDescription = line;
                break;
            }
        } else if (commentStr.matches(REGEX_MULTI_LINE_COMMENT)) {
            System.out.println("MULTI_LINE_COMMENT");
            int start = commentStr.indexOf("/*");
            int end = commentStr.lastIndexOf("*/");
            commentStr = commentStr.substring(start + 2, end).trim();
            commentDescription = commentStr.replaceAll("\n", " ");
        } else if (commentStr.matches(REGEX_SINGLE_LINE_COMMENT)) {
            System.out.println("SINGLE_LINE_COMMENT");
            int start = commentStr.indexOf("//");
            commentDescription = commentStr.substring(start + 2).trim();
        } else {
            System.err.println("未识别的注释！");
        }
        return commentDescription;
    }
}
