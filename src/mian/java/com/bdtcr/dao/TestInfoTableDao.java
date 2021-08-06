package com.bdtcr.dao;

import com.bdtcr.models.MethodInfoTableModel;
import com.bdtcr.models.TestInfoTableModel;
import com.bdtcr.utils.DBUtil;

import java.sql.*;
import java.util.*;

/**
  * @Author sunweisong
  * @Date 2020/4/21 1:00 AM
  */
public class TestInfoTableDao {

    private PreparedStatement pst = null;
    private ResultSet rs = null;

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/13 23:01
      * @author sunweisong
      */
    public Map<String, Set<String>> searchTestTargetAndTestCaseId() {
        Map<String, Set<String>> testTargetTestCaseSetMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select test_case_id, test_targets from test_info_table";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String testCaseId = rs.getString("test_case_id");
                String testTargets = rs.getString("test_targets");
                testTargets = testTargets.substring(1, testTargets.length() - 1).trim();
                if (testTargets.indexOf(",") == -1) {
                    Set<String> testCaseIdSet = testTargetTestCaseSetMap.get(testTargets);
                    if (testCaseIdSet == null) {
                        testCaseIdSet = new HashSet<>();
                    }
                    testCaseIdSet.add(testCaseId);
                    testTargetTestCaseSetMap.put(testTargets, testCaseIdSet);
                    continue;
                }
                String[] testTargetArray = testTargets.split(",");
                for (String testTarget : testTargetArray) {
                    testTarget = testTarget.trim();
                    Set<String> testCaseIdSet = testTargetTestCaseSetMap.get(testTarget);
                    if (testCaseIdSet == null) {
                        testCaseIdSet = new HashSet<>();
                    }
                    testCaseIdSet.add(testCaseId);
                    testTargetTestCaseSetMap.put(testTarget, testCaseIdSet);
                }
                testTargetArray = null;
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testTargetTestCaseSetMap;
    }


    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/13 09:21
      * @author sunweisong
      */
    public Set<String> searchAllTestTargets() {
        Set<String> testTargetSet = new HashSet<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select test_targets from test_info_table";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String testTargets = rs.getString(1);
                testTargets = testTargets.substring(1, testTargets.length() - 1).trim();
                if (testTargets.indexOf(",") == -1) {
                    testTargetSet.add(testTargets);
                    continue;
                }
                String[] testTargetArray = testTargets.split(",");
                for (String testTarget : testTargetArray) {
                    testTargetSet.add(testTarget.trim());
                }
                testTargetArray = null;
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testTargetSet;
    }

    /**
      * Search test cases by the range of the records.
      * @param offset
      * @param number
      * @return List<TestInfoTableModel>
      * @date 2020/4/21 11:36 PM
      * @author sunweisong
      */
    public List<TestInfoTableModel> searchTCByRange(int offset, int number) {
        Connection conn = DBUtil.getConnection();
        String sql = "select * from test_info_table limit " + offset + "," + number;
        List<TestInfoTableModel> testInfoTableModelList = new ArrayList<>(number);
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            TestInfoTableModel testInfoTableModel;
            while (rs.next()) {
                int id = rs.getInt("id");
                String testMethodCode = rs.getString("test_method_code");
                String testTargetId = rs.getString("test_target_id");
                String importDependencies = rs.getString("import_dependencies");
                String methodDependencies = rs.getString("method_dependencies");
                int testFramework = rs.getInt("test_framework");
                int junitVersion = rs.getInt("junit_version");
                int assertFramework = rs.getInt("assert_framework");
                testInfoTableModel = new TestInfoTableModel();
                testInfoTableModel.setId(id);
                testInfoTableModel.setTestTargets(testTargetId);
                testInfoTableModel.setTestMethodCode(testMethodCode);
                testInfoTableModel.setImportDependencies(importDependencies);
                testInfoTableModel.setMethodDependencies(methodDependencies);
                testInfoTableModel.setTestFramework(testFramework);
                testInfoTableModel.setJunitVersion(junitVersion);
                testInfoTableModel.setAssertFramework(assertFramework);
                testInfoTableModelList.add(testInfoTableModel);
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testInfoTableModelList;
    }

    /**
      * Get the total number of the records in test_info_table.
      * @return int
      * @date 2020/4/21 11:23 PM
      * @author sunweisong
      */
    public int getTotalNumberOfRecords() {
        Connection conn = DBUtil.getConnection();
        int totalCount = 0;
        String countSql = "select count(*) from test_info_table";
        try {
            pst = conn.prepareStatement(countSql);
            rs = pst.executeQuery();
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return totalCount;
    }

    /**
      * Search test targets by the test target classname.
      * @param className
      * @param k
      * @return List<MethodInfoTableModel>
      * @date 2020/4/23 12:53 PM
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchTTByTTClassName(String className, int k) {
        Connection conn = DBUtil.getConnection();
        List<MethodInfoTableModel> methodInfoTableModelList = null;
        StringBuffer stringBuffer = new StringBuffer("select test_target_id from test_info_table ");
        String whereString = " where test_target_signature like '%+" + className + "+%'";
        String limitString = " limit 0," + k;
        stringBuffer.append(whereString);
        stringBuffer.append(limitString);
        String sql = stringBuffer.toString();
        List<String> methodIdList = new ArrayList<>();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String ttId = rs.getString("test_target_id");
                methodIdList.add(ttId);
            }
            if (methodIdList.size() > 0) {
                MethodInfoTableDao methodInfoTableDao = new MethodInfoTableDao();
                methodInfoTableModelList = methodInfoTableDao.searchMethodListByMethodIdList(methodIdList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
            stringBuffer = null;
            methodIdList = null;
        }
        return methodInfoTableModelList;
    }


    /**
      * Search test targets by the method signature.
      * @param pureMethodSignature
      * @param wildcardsConditionList
      * @return List<MethodInfoTableModel>
      * @date 2020/4/21 3:08 PM
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchTTByPureMethodSignature(String pureMethodSignature
            , List<String> wildcardsConditionList) {
        List<MethodInfoTableModel> testTargetList = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String countSql = "select count(*) from test_info_table";
        int totalCount = 0;
        int totalPageNumber;
        int pageSize = 1000;
        try {
            pst = conn.prepareStatement(countSql);
            rs = pst.executeQuery();
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            if (totalCount == 0) {
                return null;
            }
            if (totalCount % pageSize == 0) {
                totalPageNumber = totalCount / pageSize;
            } else {
                totalPageNumber = totalCount / pageSize + 1;
            }
            Map<Integer, String> idMap = new HashMap<>();
            String sql = "select id,test_target_signature,test_target_id from test_info_table";
            if (totalPageNumber == 0) {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                processResultSet(pureMethodSignature, rs, idMap, wildcardsConditionList);
            } else {
                for (int pageNumber = 0; pageNumber < totalPageNumber; pageNumber++) {
                    /**
                     * i = 0, offset = 0; 0 ... 999
                     * i = 1, offset = 999 + 1 = 1000; 1000 ... 1999
                     * i = 2, offset = 1999 + 1 = 2000; 2000 ... 2999
                     * i = 3, offset = 2999 + 1 = 3000; 3000 ... 3999
                     * i = n, offset = n * pageSize; (n * pageSize) ... (n * pageSize + 1000)
                     */
                    int offset = pageNumber * pageSize;
                    String limitSql = " limit " + offset + "," + pageSize;
                    pst = conn.prepareStatement((sql + limitSql));
                    rs = pst.executeQuery();
                    processResultSet(pureMethodSignature, rs, idMap, wildcardsConditionList);
                }
            }
            if (idMap.size() == 0) {
                return null;
            }
            MethodInfoTableDao methodInfoTableDao = new MethodInfoTableDao();
            Iterator<Map.Entry<Integer, String>> iterator = idMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                String methodId = entry.getValue();
                MethodInfoTableModel testTarget = methodInfoTableDao.searchMethodByMethodId(methodId);
                if (testTarget != null) {
                    testTargetList.add(testTarget);
                }
            }
            idMap = null;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        if (testTargetList.size() == 0) {
            testTargetList = null;
        }
        return testTargetList;
    }

    /**
      * Process the result set.
      * @param pureMethodSignature
      * @param rs
      * @param idMap
      * @return void
      * @date 2020/4/21 3:05 PM
      * @author sunweisong
      */
    private void processResultSet(String pureMethodSignature, ResultSet rs
            , Map<Integer, String> idMap, List<String> wildcardsConditionList) throws SQLException {
        /*
        the test_target_signature in 'test_info_table' or 'method_info_table' is composed of：PN+CN+MN+PT
        for example: eu.rekawek.coffeegb.gpu+ColorPalette+getPalette+(int)
        */
        if (wildcardsConditionList == null) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String testTargetSignature = rs.getString("test_target_signature");
                if (testTargetSignature == null || "".equals(testTargetSignature.trim())) {
                    /*
                    2020.04.29添加
                     */
                    continue;
                }
                String[] elementArray = testTargetSignature.split("\\+");
                String methodName = elementArray[2];
                String parameterTypes = elementArray[3];
                String methodSignature = methodName + parameterTypes;
                if (!methodSignature.equals(pureMethodSignature)) {
                    continue;
                }
                String testTargetId = rs.getString("test_target_id");
                idMap.put(id, testTargetId);
            }
            return;
        }
        if (wildcardsConditionList.contains("MN")) {
            int index = pureMethodSignature.indexOf("(");
            String ptInPureSignature = pureMethodSignature.substring(index);
            while (rs.next()) {
                int id = rs.getInt("id");
                String testTargetSignature = rs.getString("test_target_signature");
                if (testTargetSignature == null || "".equals(testTargetSignature.trim())) {
                    // 2020.04.29添加
                    continue;
                }
                String[] elementArray = testTargetSignature.split("\\+");
                if (!ptInPureSignature.equals(elementArray[3])) {
                    continue;
                }
                String testTargetId = rs.getString("test_target_id");
                idMap.put(id, testTargetId);
            }
        }

    }

    /**
      * Search the test case by the test target id.
      * @param targetId
      * @return TestInfoTableModel
      * @date 2020/4/21 5:18 PM
      * @author sunweisong
      */
    public TestInfoTableModel searchTCByTestTargetId(String targetId) {
        Connection conn = DBUtil.getConnection();
        TestInfoTableModel testInfoTableModel = null;
        String sql = "select * from test_info_table  where test_target_id = '" + targetId + "'";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String testMethodCode = rs.getString("test_case_code");
                String testTargetSignature = rs.getString("test_target_signature");
                String importDependencies = rs.getString("import_dependencies");
                String methodDependencies = rs.getString("method_dependencies");
                int testFramework = rs.getInt("test_framework");
                int junitVersion = rs.getInt("junit_version");
                int assertFramework = rs.getInt("assert_framework");
                testInfoTableModel = new TestInfoTableModel();
                testInfoTableModel.setId(id);
                testInfoTableModel.setTestMethodCode(testMethodCode);
//                testInfoTableModel.setTestTargetSignature(testTargetSignature);
                testInfoTableModel.setImportDependencies(importDependencies);
                testInfoTableModel.setMethodDependencies(methodDependencies);
                testInfoTableModel.setTestFramework(testFramework);
                testInfoTableModel.setJunitVersion(junitVersion);
                testInfoTableModel.setAssertFramework(assertFramework);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testInfoTableModel;
    }


    /**
     * Close the database resources.
     * @throws SQLException
     * @date 2020/4/21 3:07 PM
     * @author sunweisong
     */
    private void closeDBResources() {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pst != null) {
                pst.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/4 11:12 PM
      * @author sunweisong
      */
    public void saveTestInfoToDatabase(TestInfoTableModel testInfoTableModel) {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT IGNORE INTO test_info_table (test_case_id, extended_signature, package_name" +
                ", class_name, class_annotations, extends_classes, implements_interfaces" +
                ", test_method_name, test_method_code, test_targets" +
                ", before_class_method, before_method, after_method, after_class_method" +
                ", import_dependencies, variable_dependencies, initializer_dependencies, enum_dependencies" +
                ", method_dependencies, test_framework, junit_version, assert_framework, project_id, storage_time)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp storageTime = new Timestamp(System.currentTimeMillis());
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, testInfoTableModel.getTestCaseId());
            pst.setString(2, testInfoTableModel.getExtendedSignature());
            pst.setString(3, testInfoTableModel.getPackageName());
            pst.setString(4, testInfoTableModel.getClassName());
            pst.setString(5, testInfoTableModel.getClassAnnotations());
            pst.setString(6, testInfoTableModel.getExtendsClasses());
            pst.setString(7, testInfoTableModel.getImplementsInterfaces());
            pst.setString(8, testInfoTableModel.getTestMethodName());
            pst.setString(9, testInfoTableModel.getTestMethodCode());
            pst.setString(10, testInfoTableModel.getTestTargets());
            pst.setString(11, testInfoTableModel.getBeforeClassMethod());
            pst.setString(12, testInfoTableModel.getBeforeMethod());
            pst.setString(13, testInfoTableModel.getAfterMethod());
            pst.setString(14, testInfoTableModel.getAfterClassMethod());
            pst.setString(15, testInfoTableModel.getImportDependencies());
            pst.setString(16, testInfoTableModel.getVariableDependencies());
            pst.setString(17, testInfoTableModel.getInitializerDependencies());
            pst.setString(18, testInfoTableModel.getEnumDependencies());
            pst.setString(19, testInfoTableModel.getMethodDependencies());
            pst.setInt(20, testInfoTableModel.getTestFramework());
            pst.setInt(21, testInfoTableModel.getJunitVersion());
            pst.setInt(22, testInfoTableModel.getAssertFramework());
            pst.setString(23, testInfoTableModel.getProjectId());
            pst.setTimestamp(24, storageTime);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/17 12:15
      * @author sunweisong
      */
    public Map<String, String> searchTestCaseCodesByTestCaseIdSet(Set<String> testCaseIdSet) {
        StringBuffer whereSqlStringBuffer = new StringBuffer("where test_case_id in (");
        int testCaseIdSetSize = testCaseIdSet.size();
        int index = 0;
        for (String testCaseId : testCaseIdSet) {
            index++;
            if (index < testCaseIdSetSize) {
                whereSqlStringBuffer.append("'" + testCaseId + "'" + ", ");
            } else {
                whereSqlStringBuffer.append("'" + testCaseId + "'");
            }
        }
        whereSqlStringBuffer.append(")");
        String whereSqlString = whereSqlStringBuffer.toString();
        Map<String, String> testCaseIdAndCodeMap = new HashMap<>();
        String sql = "select test_case_id, test_method_code from test_info_table " + whereSqlString;
        Connection conn = DBUtil.getConnection();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String testCaseId = rs.getString("test_case_id");
                String testMethodCode = rs.getString("test_method_code");
                testCaseIdAndCodeMap.put(testCaseId, testMethodCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testCaseIdAndCodeMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/17 13:13
      * @author sunweisong
      */
    public Map<String, TestInfoTableModel> searchTestCaseByTestCaseIdSet(Set<String> testCaseIdSet) {
        StringBuffer whereSqlStringBuffer = new StringBuffer("where test_case_id in (");
        int testCaseIdSetSize = testCaseIdSet.size();
        int index = 0;
        for (String testCaseId : testCaseIdSet) {
            index++;
            if (index < testCaseIdSetSize) {
                whereSqlStringBuffer.append("'" + testCaseId + "'" + ", ");
            } else {
                whereSqlStringBuffer.append("'" + testCaseId + "'");
            }
        }
        whereSqlStringBuffer.append(")");
        String whereSqlString = whereSqlStringBuffer.toString();
        TestInfoTableModel testInfoTableModel = null;
        String sql = "select * from test_info_table " + whereSqlString;
        Map<String, TestInfoTableModel> testCaseIdAndDetailInfoMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String testCaseId = rs.getString("test_case_id");
                String extendedSignature = rs.getString("extended_signature");
                String packageName = rs.getString("package_name");
                String className = rs.getString("class_name");
                String classAnnotations = rs.getString("class_annotations");
                String extendsClasses = rs.getString("extends_classes");
                String implementsInterfaces = rs.getString("implements_interfaces");
                String testMethodName = rs.getString("test_method_name");
                String testMethodCode = rs.getString("test_method_code");
                String testTargets = rs.getString("test_targets");
                String beforeClassMethod = rs.getString("before_class_method");
                String beforeMethod = rs.getString("before_method");
                String afterMethod = rs.getString("after_method");
                String afterClassMethod = rs.getString("after_class_method");
                String importDependencies = rs.getString("import_dependencies");
                String variableDependencies = rs.getString("variable_dependencies");
                String initializerDependencies = rs.getString("initializer_dependencies");
                String enumDependencies = rs.getString("enum_dependencies");
                String methodDependencies = rs.getString("method_dependencies");
                int testFramework = rs.getInt("test_framework");
                int junitVersion = rs.getInt("junit_version");
                int assertFramework = rs.getInt("assert_framework");
                String projectId = rs.getString("project_id");
                Timestamp storageTime = rs.getTimestamp("storage_time");
                testInfoTableModel = new TestInfoTableModel(id, testCaseId, extendedSignature
                        , packageName, className, classAnnotations, extendsClasses, implementsInterfaces
                        , testMethodName, testMethodCode, testTargets
                        , beforeClassMethod, beforeMethod, afterMethod, afterClassMethod
                        , importDependencies, variableDependencies, initializerDependencies, enumDependencies, methodDependencies
                        , testFramework, junitVersion, assertFramework
                        , projectId, storageTime);
                testCaseIdAndDetailInfoMap.put(testCaseId, testInfoTableModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testCaseIdAndDetailInfoMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/16 16:12
      * @author sunweisong
      */
    public String getTestMethodCodeByTestCaseId(String testCaseId) {
        String testCaseCode = null;
        Connection conn = DBUtil.getConnection();
        String sql = "select test_method_code from test_info_table  where test_case_id = '" + testCaseId + "'";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                testCaseCode = rs.getString("test_method_code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testCaseCode;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/9/10 16:42
      * @author sunweisong
      */
    public Map<String,String> searchTCIdAndTestCode(int offset, int rows) {
        Map<String, String> tcIdAndTestCodeMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String limitSql = "limit " + offset + ", " + rows;
        String sql = "select test_case_id, test_method_code from test_info_table " + limitSql;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String testCaseId = rs.getString("test_case_id");
                String testMethodCode = rs.getString("test_method_code");
                tcIdAndTestCodeMap.put(testCaseId, testMethodCode);
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return tcIdAndTestCodeMap;
    }
}
