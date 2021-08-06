package com.bdtcr.comparisons.testtenderer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.bdtcr.dao.ImportInfoTableDao;
import com.bdtcr.dao.MethodInfoTableDao;
import com.bdtcr.dao.TestInfoTableDao;
import com.bdtcr.models.*;
import com.bdtcr.utils.DBUtil;
import com.bdtcr.utils.FileUtil;
import com.bdtcr.utils.JacksonUtil;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  * @Author sunweisong
  * @Date 2020/4/23 1:04 AM
  */
public class TestTenderer {
//    final private static String QUERY_SUBJECT_PATH = "/Users/sunweisong/Desktop/Test Case Recommendation/experiment/query_subject_new";
//    final private static String QUERY_SUBJECT_PATH = "/Users/sunweisong/Desktop/Test Case Recommendation/experiment/query_subject_with_change";
    final private static String QUERY_SUBJECT_PATH = "/Users/sunweisong/Desktop/Test Case Recommendation/experiment/query_subject_without_tc";

    private static String experimentDataDirectoryPath = "/Users/sunweisong/Desktop/projects_from_github/expriment_data";

    final private static int k = 10; // top k

    private static TestInfoTableDao testInfoTableDao = new TestInfoTableDao();
    private static ImportInfoTableDao importInfoTableDao = new ImportInfoTableDao();
    private static MethodInfoTableDao methodInfoTableDao = new MethodInfoTableDao();

    private static List<TestTendererNeedMethodModel> testTendererNeedMethodModelList = new ArrayList<>();

    public static void main(String[] args) {
        TestTenderer testTenderer = new TestTenderer();
        testTenderer.run();
        testTenderer = null;
//
//        try {
//            testTenderer.run();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        testTendererNeedMethodModelList = null;
        testInfoTableDao = null;
        methodInfoTableDao = null;
        importInfoTableDao = null;
        DBUtil.closeConnection();
    }

    static {
        String searchCorpusForTBoosterDirectoryPath = experimentDataDirectoryPath + File.separator + "search_corpus_for_TestTenderer";
        String methodSummaryFilePath = searchCorpusForTBoosterDirectoryPath + File.separator + "method_summary.json";
        File methodSummaryFile = new File(methodSummaryFilePath);
        String jsonString = FileUtil.readFileContentToString(methodSummaryFile);
        JacksonUtil jacksonUtil = new JacksonUtil();
        JavaType javaType = jacksonUtil.getCollectionType(List.class, TestTendererNeedMethodModel.class);
        try {
            testTendererNeedMethodModelList = jacksonUtil.getMapper().readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/26 00:42
      * @author sunweisong
      */
    public void run() {
        StringBuffer pathStringBuffer = new StringBuffer(experimentDataDirectoryPath);
        pathStringBuffer.append(File.separator + "search_queries");
        String searchQueryDirectoryPath = pathStringBuffer.toString();
        File searchQueryDirectory = new File(searchQueryDirectoryPath);
        File[] queryDirectoryArray = searchQueryDirectory.listFiles();
        Map<String, Long> queryCostTimeMap = new HashMap<>();
        pathStringBuffer.append(File.separator);
        for (File queryDirectory : queryDirectoryArray) {
            if (queryDirectory.isFile()) {
                continue;
            }
            String queryDirectoryName = queryDirectory.getName();
//            if (!"query_1".equals(queryDirectoryName)) {
//                continue;
//            }
            System.out.println("--------------- " + queryDirectoryName + " ---------------");
            File[] subFileArray = queryDirectory.listFiles();
            File mutFile = null;
            for (File subFile : subFileArray) {
                String subFileName = subFile.getName();
                if (!subFileName.startsWith("mut_")) {
                    continue;
                }
                mutFile = subFile;
                break;
            }

            String fileContent = FileUtil.readFileContentToString(mutFile);
            MethodInfoTableModel methodInfoTableModel = (new JacksonUtil()).json2Bean(fileContent, MethodInfoTableModel.class);
            String className = methodInfoTableModel.getClassName();
            String methodName = methodInfoTableModel.getMethodName();
            String signature = methodInfoTableModel.getSignature();
            String returnType = methodInfoTableModel.getReturnType();

            Map<String, Integer> methodIdRankMap = new HashMap<>();
            long startTime = System.currentTimeMillis();
            /*
            1) Search for exact match of the query.
            signature = className + methodName + parameterTypes  + returnType; --> base
             */
            int rank = 0;
            for (TestTendererNeedMethodModel testTendererNeedMethodModel : testTendererNeedMethodModelList) {
                String tempClassName = testTendererNeedMethodModel.getClassName();
                if (!tempClassName.equals(className)) {
                    continue;
                }
                String tempSignature = testTendererNeedMethodModel.getSignature();
                if (!tempSignature.equals(signature)) {
                    continue;
                }
                String tempReturnType = testTendererNeedMethodModel.getReturnType();
                if (!tempReturnType.equals(returnType)) {
                    continue;
                }

                String methodId = testTendererNeedMethodModel.getMethodId();
                if (methodIdRankMap.get(methodId) == null) {
                    rank++;
                    methodIdRankMap.put(methodId, rank);
                }
                if (methodIdRankMap.size() == k) {
                    break;
                }
            }

            /*
            2) Add wildcards to the method names.
            signature = className + "xxx" + parameterTypes  + returnType; --> relax 1
             */
            if (methodIdRankMap.size() < k) {
                for (TestTendererNeedMethodModel testTendererNeedMethodModel : testTendererNeedMethodModelList) {
                    String tempClassName = testTendererNeedMethodModel.getClassName();
                    if (!tempClassName.equals(className)) {
                        continue;
                    }
                    String tempReturnType = testTendererNeedMethodModel.getReturnType();
                    if (!tempReturnType.equals(returnType)) {
                        continue;
                    }
                    String tempSignature = testTendererNeedMethodModel.getSignature();
                    String tempMethodName = testTendererNeedMethodModel.getMethodName();
                    tempSignature = tempSignature.replaceFirst(tempMethodName, "");
                    if (!(signature.replaceFirst(methodName, "")).equals(tempSignature)) {
                        continue;
                    }
                    String methodId = testTendererNeedMethodModel.getMethodId();
                    if (methodIdRankMap.get(methodId) == null) {
                        rank++;
                        methodIdRankMap.put(methodId, rank);
                    }
                    if (methodIdRankMap.size() == k) {
                        break;
                    }
                }
            }
            /*
            3) Remove the methods and search only for the classname. --> relax 2
             */
            if (methodIdRankMap.size() < k) {
                for (TestTendererNeedMethodModel testTendererNeedMethodModel : testTendererNeedMethodModelList) {
                String tempClassName = testTendererNeedMethodModel.getClassName();
                if (!tempClassName.equals(className)) {
                    continue;
                }
                String methodId = testTendererNeedMethodModel.getMethodId();
                if (methodIdRankMap.get(methodId) == null) {
                    rank++;
                    methodIdRankMap.put(methodId, rank);
                }
                if (methodIdRankMap.size() == k) {
                    break;
                }
            }
            }

            /*
            4) Add wildcards to the classname.
            signature = "xxx" + methodName + parameterTypes  + returnType; --> relax 3
             */
            if (methodIdRankMap.size() < k) {
                for (TestTendererNeedMethodModel testTendererNeedMethodModel : testTendererNeedMethodModelList) {
                    String tempSignature = testTendererNeedMethodModel.getSignature();
                    if (!tempSignature.equals(signature)) {
                        continue;
                    }
                    String tempReturnType = testTendererNeedMethodModel.getReturnType();
                    if (!tempReturnType.equals(returnType)) {
                        continue;
                    }

                    String methodId = testTendererNeedMethodModel.getMethodId();
                    if (methodIdRankMap.get(methodId) == null) {
                        rank++;
                        methodIdRankMap.put(methodId, rank);
                    }
                    if (methodIdRankMap.size() == k) {
                        break;
                    }
                }
            }

            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            queryCostTimeMap.put(queryDirectoryName, costTime);
            if (methodIdRankMap.isEmpty()) {
                continue;
            }

            pathStringBuffer.append(queryDirectoryName);


            pathStringBuffer.append(File.separator + "test_cases_recommended_by_TestTenderer_relax_3");
            String recommendedTestCasesDirectoryPath = pathStringBuffer.toString();
            File recommendedTestCasesDirectory = new File(recommendedTestCasesDirectoryPath);
            if (!recommendedTestCasesDirectory.exists()) {
                recommendedTestCasesDirectory.mkdir();
            }

            Set<String> methodIdSet = methodIdRankMap.keySet();
            List<String> methodIdList = new ArrayList<>();
            methodIdList.addAll(methodIdSet);

            List<MethodInfoTableModel> similarMethodInfoTableModelList = methodInfoTableDao.searchMethodListByMethodIdList(methodIdList);
            pathStringBuffer.append(File.separator);
            for (MethodInfoTableModel similarMethodInfoTableModel : similarMethodInfoTableModelList) {
                String methodId = similarMethodInfoTableModel.getMethodId();
                int rankIndex = methodIdRankMap.get(methodId);
                String tcRankDirectoryName = "tc_rank_" + rankIndex;
                pathStringBuffer.append(tcRankDirectoryName);
                String tcRankDirectoryPath = pathStringBuffer.toString();
                File tcRankDirectory = new File(tcRankDirectoryPath);
                if (!tcRankDirectory.exists()) {
                    tcRankDirectory.mkdir();
                }
                pathStringBuffer.append(File.separator);

                String testTargetFileName = "tt_" + methodId + ".json";
                pathStringBuffer.append(testTargetFileName);
                String testTargetFilePath = pathStringBuffer.toString();
                String testTargetString = (new JacksonUtil()).bean2Json(similarMethodInfoTableModel);
                FileUtil.writeStringToTargetFile(testTargetString, testTargetFilePath);

                // 移除 tt_xxx.json 文件名
                int testTargetFileNameIndex = pathStringBuffer.indexOf(testTargetFileName);
                pathStringBuffer.replace(testTargetFileNameIndex, pathStringBuffer.length(), "");

                String testCaseIds = similarMethodInfoTableModel.getTestCaseIds();
                testCaseIds = testCaseIds.substring(1, testCaseIds.length() - 1).trim();
                Set<String> testCaseIdSet = new HashSet<>();
                if (testCaseIds.indexOf(",") != -1) {
                    String[] testCaseIdArray = testCaseIds.split(",");
                    for (String testCaseId : testCaseIdArray) {
                        testCaseIdSet.add(testCaseId.trim());
                    }
                } else {
                    testCaseIdSet.add(testCaseIds);
                }
                Map<String, TestInfoTableModel> testCaseIdAndDetailInfoMap = testInfoTableDao.searchTestCaseByTestCaseIdSet(testCaseIdSet);
                Iterator<Map.Entry<String, TestInfoTableModel>> testCaseIterator = testCaseIdAndDetailInfoMap.entrySet().iterator();
                while (testCaseIterator.hasNext()) {
                    Map.Entry<String, TestInfoTableModel> entry = testCaseIterator.next();
                    String testCaseId = entry.getKey();
                    TestInfoTableModel testInfoTableModel = entry.getValue();
                    String testCaseString = (new JacksonUtil()).bean2Json(testInfoTableModel);
                    String testCaseFileName = "tc_" + testCaseId + ".json";
                    pathStringBuffer.append(testCaseFileName);
                    String testCaseFilePath = pathStringBuffer.toString();
                    FileUtil.writeStringToTargetFile(testCaseString, testCaseFilePath);

                    // 移除 tc_xxx.json 文件名
                    int testCaseFileNameIndex = pathStringBuffer.indexOf(testCaseFileName);
                    pathStringBuffer.replace(testCaseFileNameIndex, pathStringBuffer.length(), "");
                }

                // 移除 tc_rank_xxx 目录名
                int tcRankDirectoryNameIndex = pathStringBuffer.indexOf(tcRankDirectoryName);
                pathStringBuffer.replace(tcRankDirectoryNameIndex, pathStringBuffer.length(), "");

                testCaseIdAndDetailInfoMap = null;
                testCaseIdSet = null;
                tcRankDirectory = null;
            }

            // 移除 query_xxx 目录名
            int queryDirectoryNameIndex = pathStringBuffer.indexOf(queryDirectoryName);
            pathStringBuffer.replace(queryDirectoryNameIndex, pathStringBuffer.length(), "");

            methodIdList = null;
            methodIdSet = null;
            methodIdRankMap = null;
            similarMethodInfoTableModelList = null;
            methodInfoTableModel = null;
        }

        String queryCostTimeMapJsonString = (new JacksonUtil()).bean2Json(queryCostTimeMap);
        pathStringBuffer.append("cost_time_by_TestTenderer_relax_3.json");
        String costTimeFilePath = pathStringBuffer.toString();
        FileUtil.writeStringToTargetFile(queryCostTimeMapJsonString, costTimeFilePath);

        costTimeFilePath = null;
        queryCostTimeMap = null;
        pathStringBuffer = null;
        queryCostTimeMapJsonString = null;
        queryDirectoryArray = null;
        searchQueryDirectoryPath = null;
        searchQueryDirectory = null;
    }

    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/4/23 1:37 PM
      * @author sunweisong
      */
    public void run_old() throws SQLException {
        File querySubjectDirectory = new File(QUERY_SUBJECT_PATH);
        File[] queryDirectoryArray = querySubjectDirectory.listFiles();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (File queryDirectory : queryDirectoryArray) {
            String queryDirectoryName = queryDirectory.getName();
            if (".DS_Store".equals(queryDirectoryName)) {
                continue;
            }
            System.out.println("=============================" + queryDirectoryName + "=============================");


            String numString = queryDirectoryName.split("_")[1];
            int num = Integer.parseInt(numString);
//            if (num != 5) {
//                continue;
//            }


            // Step1: prepare a query (the signature used in TestTenderer).
            String queryDirectoryPath = queryDirectory.getAbsolutePath();
            String mutFileDirectoryPath = queryDirectoryPath + File.separator + "mut";
            File mutFileDirectory = new File(mutFileDirectoryPath);
            if (!mutFileDirectory.exists()) {
                continue;
            }
            File[] mutFileArray = mutFileDirectory.listFiles();
            String mutFilePath = null;
            for (File mutFile : mutFileArray) {
                String mutFileName = mutFile.getName();
                if (".DS_Store".equals(mutFileName)) {
                    continue;
                }
                System.out.println("+++++++++++++++ " + mutFileName + " +++++++++++++++");
                mutFilePath = mutFile.getAbsolutePath();
            }
            if (mutFileDirectoryPath == null) {
                System.err.println("The mut_xxx.json file doesn't exist!");
                continue;
            }
            File mutFile = new File(mutFilePath);
            String mutJsonString = FileUtil.readFileContentToString(mutFile);
            TestTargetModel testTargetModel = (new JacksonUtil()).json2Bean(mutJsonString, TestTargetModel.class);
            /*
            the method_signature in 'mut.json' file is composed of：PN+CN+MN+PT+RT
            for example: eu.rekawek.coffeegb.gpu+ColorPalette+getPalette+(int)+void
            */
            String methodSignature = testTargetModel.getMut_signature();
            String[] elementArray = methodSignature.split("\\+");
            String className = elementArray[1];
            String methodName = elementArray[2];
            String parameterTypes = elementArray[3];
            String returnType = elementArray[4];
            /*
            the pure method signature is composed of：MN+PT
            for example: getPalette(int)
             */
            String pureSignature = methodName + parameterTypes;

            StringBuffer logBuffer = new StringBuffer();
            System.out.println("begin recommendation...");
            logBuffer.append("begin recommendation, start time: " + df.format(new Date()) + System.lineSeparator());
            long startTime = System.currentTimeMillis();

            // Step2: search for the top k test cases.
            List<RecommendedTestCaseModel> recommendedTestCaseModelList = new ArrayList<>();

            /*
            1) Search for exact match of the query.
            signature = className + methodName + parameterTypes  + returnType;
             */
            logBuffer.append("begin exact matching." + System.lineSeparator());
            long startTime1 = System.currentTimeMillis();
            long endTime1;
            List<String> methodIdList = new ArrayList<>();
            List<MethodInfoTableModel> methodInfoTableModelList = testInfoTableDao.searchTTByPureMethodSignature(pureSignature, null);
            if (methodInfoTableModelList != null) {
                for (MethodInfoTableModel testTarget : methodInfoTableModelList) {
                    String ttClassName = testTarget.getClassName();
                    String ttReturnType = testTarget.getReturnType();
                    if (ttClassName.equals(className) && ttReturnType.equals(returnType)) {
                        String testTargetId = testTarget.getMethodId();
                        if (methodIdList.size() != 0 && methodIdList.contains(testTargetId)) {
                            continue;
                        }
                        System.out.println("----- " + testTargetId + " -----");
                        logBuffer.append("----- " + testTargetId + " -----"  + System.lineSeparator());
                        methodIdList.add(testTargetId);
                        RecommendedTestCaseModel recommendedTestCaseModel = new RecommendedTestCaseModel();
                        recommendedTestCaseModel.setMut_id(testTargetId);
                        String testTargetCode = testTarget.getMethodCode();
                        recommendedTestCaseModel.setMut_code(testTargetCode);
                        fillTestCaseInformation(recommendedTestCaseModel, testTargetId);
                        recommendedTestCaseModelList.add(recommendedTestCaseModel);
                        if (recommendedTestCaseModelList.size() == k) {
                            break;
                        }
                    }
                }
                logBuffer.append("end exact matching." + System.lineSeparator());
                endTime1 = System.currentTimeMillis();
                logBuffer.append("cost time of exact matching: " + (endTime1 - startTime1) + "ms" + System.lineSeparator());
            }

            if (recommendedTestCaseModelList.size() < k) {
                logBuffer.append("begin matching with adding wildcards to method names." + System.lineSeparator());
                startTime1 = System.currentTimeMillis();
                /*
                2) Add wildcards to the method names.
                signature = className + "xxx" + parameterTypes  + returnType;
                 */
                List<String> wildcardsConditionList = null;
                wildcardsConditionList = new ArrayList<>();
                wildcardsConditionList.add("MN");
                methodInfoTableModelList = testInfoTableDao.searchTTByPureMethodSignature(pureSignature, wildcardsConditionList);
                if (methodInfoTableModelList != null) {
                    for (MethodInfoTableModel testTarget : methodInfoTableModelList) {
                        String ttClassName = testTarget.getClassName();
                        String ttReturnType = testTarget.getReturnType();
                        if (ttClassName.equals(className) && ttReturnType.equals(returnType)) {
                            String testTargetId = testTarget.getMethodId();
                            if (methodIdList.size() != 0 && methodIdList.contains(testTargetId)) {
                                continue;
                            }
                            System.out.println("----- " + testTargetId + " -----");
                            logBuffer.append("----- " + testTargetId + " -----"  + System.lineSeparator());
                            methodIdList.add(testTargetId);

                            RecommendedTestCaseModel recommendedTestCaseModel = new RecommendedTestCaseModel();
                            recommendedTestCaseModel.setMut_id(testTargetId);
                            String testTargetCode = testTarget.getMethodCode();
                            recommendedTestCaseModel.setMut_code(testTargetCode);
                            fillTestCaseInformation(recommendedTestCaseModel, testTargetId);
                            recommendedTestCaseModelList.add(recommendedTestCaseModel);
                            if (recommendedTestCaseModelList.size() == k) {
                                break;
                            }
                        }
                    }
                }
                endTime1 = System.currentTimeMillis();
                logBuffer.append("end matching with adding wildcards to method names." + System.lineSeparator());
                logBuffer.append("cost time of matching with adding wildcards to method names: " + (endTime1 - startTime1) + "ms" + System.lineSeparator());
            }

            if (recommendedTestCaseModelList.size() < k) {
                logBuffer.append("begin matching without methods" + System.lineSeparator());
                startTime1 = System.currentTimeMillis();
                int count = k - recommendedTestCaseModelList.size();
                /*
                3) Remove the methods and search only for the classname.
                 */
                methodInfoTableModelList = testInfoTableDao.searchTTByTTClassName(className, count);
                if (methodInfoTableModelList != null) {
                    RecommendedTestCaseModel recommendedTestCaseModel;
                    for (MethodInfoTableModel testTarget : methodInfoTableModelList) {
                        String testTargetId = testTarget.getMethodId();
                        if (methodIdList.size() != 0 && methodIdList.contains(testTargetId)) {
                            continue;
                        }
                        System.out.println("----- " + testTargetId + " -----");
                        logBuffer.append("----- " + testTargetId + " -----"  + System.lineSeparator());
                        methodIdList.add(testTargetId);

                        recommendedTestCaseModel = new RecommendedTestCaseModel();
                        recommendedTestCaseModel.setMut_id(testTargetId);
                        String testTargetCode = testTarget.getMethodCode();
                        recommendedTestCaseModel.setMut_code(testTargetCode);
                        fillTestCaseInformation(recommendedTestCaseModel, testTargetId);
                        recommendedTestCaseModelList.add(recommendedTestCaseModel);
                    }
                }
                logBuffer.append("end matching without methods" + System.lineSeparator());
                endTime1 = System.currentTimeMillis();
                logBuffer.append("cost time of matching without methods: " + (endTime1 - startTime1) + "ms" + System.lineSeparator());
            }
            if (recommendedTestCaseModelList.size() < k) {
                logBuffer.append("begin matching with adding wildcards to classname." + System.lineSeparator());
                startTime1 = System.currentTimeMillis();
                /*
                4) Add wildcards to the classname.
                signature = "xxx" + methodName + parameterTypes  + returnType;
                 */
                methodInfoTableModelList = testInfoTableDao.searchTTByPureMethodSignature(pureSignature, null);
                if (methodInfoTableModelList != null) {
                    RecommendedTestCaseModel recommendedTestCaseModel;
                    for (MethodInfoTableModel testTarget : methodInfoTableModelList) {
                        String ttReturnType = testTarget.getReturnType();
                        if (ttReturnType.equals(returnType)) {
                            String testTargetId = testTarget.getMethodId();
                            if (methodIdList.size() != 0 && methodIdList.contains(testTargetId)) {
                                continue;
                            }
                            System.out.println("----- " + testTargetId + " -----");
                            logBuffer.append("----- " + testTargetId + " -----"  + System.lineSeparator());
                            methodIdList.add(testTargetId);

                            recommendedTestCaseModel = new RecommendedTestCaseModel();
                            recommendedTestCaseModel.setMut_id(testTargetId);
                            String testTargetCode = testTarget.getMethodCode();
                            recommendedTestCaseModel.setMut_code(testTargetCode);
                            fillTestCaseInformation(recommendedTestCaseModel, testTargetId);
                            recommendedTestCaseModelList.add(recommendedTestCaseModel);
                            if (recommendedTestCaseModelList.size() == k) {
                                break;
                            }
                        }
                    }
                }
                logBuffer.append("end matching with adding wildcards to classname." + System.lineSeparator());
                endTime1 = System.currentTimeMillis();
                logBuffer.append("cost time of matching with adding wildcards to classname: " + (endTime1 - startTime1) + "ms" + System.lineSeparator());

            }

            logBuffer.append("recommendation finished, end time: " + df.format(new Date()) + System.lineSeparator());
            long endTime = System.currentTimeMillis(); //获取结束时间
            logBuffer.append("total cost time: " + (endTime - startTime) + "ms" + System.lineSeparator());


            // Step3: save the recommended test cases to files.
            if (recommendedTestCaseModelList.size() > 0) {
                String targetDirectoryPath = queryDirectoryPath + File.separator + "recommended_tc_by_TestTenderer";
                File targetDirectory = new File(targetDirectoryPath);
                if (!targetDirectory.exists()) {
                    targetDirectory.mkdirs();
                }
                logBuffer.append("begin writing recommended test cases into files." + System.lineSeparator());
                FileUtil.writeRecommendedTestCaseToFile(recommendedTestCaseModelList
                        , targetDirectoryPath, "TestTenderer");
                logBuffer.append("writing finished.");
                String log = logBuffer.toString();
                String logFilePath = targetDirectoryPath + File.separator + "log.txt";
                FileUtil.writeStringToTargetFile(log, logFilePath);
                System.out.println("recommend finished.");
            }
            logBuffer = null;
        }
        DBUtil.getConnection().close();
    }

    /**
      * Fill the information of the recommended test case.
      * @param
      * @return
      * @throws
      * @date 2020/4/23 12:20 PM
      * @author sunweisong
      */
    private void fillTestCaseInformation(RecommendedTestCaseModel recommendedTestCaseModel, String testTargetId) {
        TestInfoTableModel tc  = testInfoTableDao.searchTCByTestTargetId(testTargetId);
//        String testCaseCode = tc.getTestCaseCode();
        /*
        注释掉的是老的
         */
        String testCaseCode = tc.getTestMethodCode();

        recommendedTestCaseModel.setTc_code(testCaseCode);
        String importDependencies = tc.getImportDependencies();
        if (!"".equals(importDependencies.trim())) {
            List<String> importStringList = searchImportStrings(importDependencies);
            if (importStringList != null) {
                recommendedTestCaseModel.setThird_party_dependencies(importStringList);
            }
        }
        String methodDependencies = tc.getMethodDependencies();
        if (!"".equals(methodDependencies.trim())) {
            List<MethodInfoTableModel> methodList = searchMethodInfoTableModels(methodDependencies);
            if (methodList != null) {
                recommendedTestCaseModel.setExternal_method_dependencies(methodList);
            }
        }
        int testFramework = tc.getTestFramework();
        recommendedTestCaseModel.setTestFramework(testFramework);
        int junitVersion = tc.getJunitVersion();
        recommendedTestCaseModel.setJunitVersion(junitVersion);
        int assertFramework = tc.getAssertFramework();
        recommendedTestCaseModel.setAssertFramework(assertFramework);
    }

    /**
     * Search the methods.
     * @param methodDependencies
     * @return List<MethodInfoTableModel>
     * @date 2020/4/23 3:27 PM
     * @author sunweisong
     */
    private List<MethodInfoTableModel> searchMethodInfoTableModels(String methodDependencies) {
        List<MethodInfoTableModel> methodInfoTableModelList = null;
        if (methodDependencies.indexOf(",") != -1) {
            List<String> methodIdList = new ArrayList<>();
            String[] methodIdArray = methodDependencies.split(",");
            for (String methodId : methodIdArray) {
                if ("".equals(methodId)) {
                    continue;
                }
                methodIdList.add(methodId);
            }
            if (methodIdList.size() > 0) {
                methodInfoTableModelList = methodInfoTableDao.searchMethodListByMethodIdList(methodIdList);
                methodIdList = null;
            }
        } else {
            String methodId = methodDependencies.trim();
            if (!"".equals(methodId)) {
                MethodInfoTableModel methodInfoTableModel = methodInfoTableDao.searchMethodByMethodId(methodId);
                if (methodInfoTableModel != null) {
                    methodInfoTableModelList = new ArrayList<>(1);
                    methodInfoTableModelList.add(methodInfoTableModel);
                }
            }
        }
        return methodInfoTableModelList;
    }

    /**
     * Search the import strings.
     * @param importDependencies
     * @return List<String>
     * @date 2020/4/23 3:09 PM
     * @author sunweisong
     */
    private List<String> searchImportStrings(String importDependencies) {
        List<String> importStringList = null;
        if (importDependencies.indexOf(",") != -1) {
            List<String> importIdList = new ArrayList<>();
            String[] importIdArray = importDependencies.split(",");
            for (String importId : importIdArray) {
                if ("".equals(importId)) {
                    continue;
                }
                importIdList.add(importId);
            }
            if (importIdList.size() > 0) {
                importStringList = importInfoTableDao.searchImportStringListByImportIdList(importIdList);
                importIdList = null;
            }
        } else {
            String importId = importDependencies.trim();
            if (!"".equals(importId)) {
                String importString = importInfoTableDao.searchImportStringByImportId(importId);
                if (importString != null) {
                    importStringList = new ArrayList<>(1);
                    importStringList.add(importString);
                }
            }
        }
        return importStringList;
    }



}
