package com.bdtcr.dao;

import com.bdtcr.models.MethodInfoTableModel;
import com.bdtcr.models.TestTendererNeedMethodModel;
import com.bdtcr.tsearcher.CommentAnalysis;
import com.bdtcr.utils.DBUtil;
import com.bdtcr.utils.NLPUtil;

import java.sql.*;
import java.util.*;

/**
  * @Author sunweisong
  * @Date 2020/4/21 2:11 PM
  */
public class MethodInfoTableDao {
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/8/16 15:46
     * @author sunweisong
     */
    public Set<String> searchTestCaseIdSetByMethodId(String methodId) {
        Set<String> testCaseIdSet = new HashSet<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select test_case_ids from method_info_table where method_id = '" + methodId + "'";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                String testCaseIds = rs.getString("test_case_ids");
                testCaseIds = testCaseIds.substring(0, testCaseIds.length() - 1).trim();
                if (testCaseIds.indexOf(",") == -1) {
                    testCaseIdSet.add(testCaseIds);
                } else {
                    String[] testCaseIdArray = testCaseIds.split(",");
                    for (String testCaseId : testCaseIdArray) {
                        testCaseIdSet.add(testCaseId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testCaseIdSet;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/16 10:49
      * @author sunweisong
      */
    public void updateMethodCFGByMethodIdAndCFGMap(Map<String, String> methodIdAndCFGMap) {
        Connection conn = DBUtil.getConnection();
        String sql="update method_info_table set method_cfg=? where method_id=?";
        Iterator<Map.Entry<String, String>> iterator = methodIdAndCFGMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String methodId = entry.getKey();
            String cfgJsonString = entry.getValue();
            try {
                pst = conn.prepareStatement(sql);
                pst.setString(1, cfgJsonString);
                pst.setString(2, methodId);
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResources();
            }
        }
        iterator = null;
        methodIdAndCFGMap = null;
    }

    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/13 09:12
      * @author sunweisong
      */
    public void setTestCaseByTestTargetTestCaseMap(Map<String, Set<String>> testTargetTestCaseSetMap) {
        Connection conn = DBUtil.getConnection();
        String sql="update method_info_table set is_mut=?, test_case_ids=? where method_id=?";
        Iterator<Map.Entry<String, Set<String>>> iterator = testTargetTestCaseSetMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            String testTarget = entry.getKey();
            Set<String> testCaseSet = entry.getValue();
            try {
                pst = conn.prepareStatement(sql);
                pst.setInt(1, 1);
                pst.setString(2, testCaseSet.toString());
                pst.setString(3, testTarget);
                int returnValue = pst.executeUpdate();
                if (returnValue == 0) {
                    System.err.println("is_mut 更新失败，测试目标：" + testTarget);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResources();
            }
        }
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/14 11:10
      * @author sunweisong
      */
    public void updateMethodCommentByMethodId(Map<String, String> methodIdAndCommentMap) {
        Connection conn = DBUtil.getConnection();
        String sql="update method_info_table set method_comment_summary=? where method_id=?";
        Iterator<Map.Entry<String, String>> iterator = methodIdAndCommentMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String methodId = entry.getKey();
            String methodComment = entry.getValue();
            try {
                pst = conn.prepareStatement(sql);
                pst.setString(1, methodComment);
                pst.setString(2, methodId);
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResources();
            }
        }
    }

    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/8/14 11:10
     * @author sunweisong
     */
    public void updateMethodCommentKeywordsByMethodId(Map<String, String> methodIdAndCommentKeywordsMap) {
        Connection conn = DBUtil.getConnection();
        String sql="update method_info_table set method_comment_keywords=? where method_id=?";
        Iterator<Map.Entry<String, String>> iterator = methodIdAndCommentKeywordsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String methodId = entry.getKey();
            String methodCommentKeywords = entry.getValue();
            try {
                pst = conn.prepareStatement(sql);
                pst.setString(1, methodCommentKeywords);
                pst.setString(2, methodId);
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResources();
            }
        }
    }


    
    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/13 09:25
      * @author sunweisong
      */
    public void setIsMutByTestTargetSet(Set<String> testTargetSet) {
        Connection conn = DBUtil.getConnection();
        String sql="update method_info_table set is_mut = 1 where method_id = ?";
        for (String testTarget : testTargetSet) {
            try {
                pst=conn.prepareStatement(sql);
                pst.setString(1, testTarget);
                int returnValue = pst.executeUpdate();
                if (returnValue == 0) {
                    System.err.println("is_mut 更新失败，测试目标：" + testTarget);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeDBResources();
            }
        }
    }

    /**
      * Search methods that could be used as the queries.
      * @return List<MethodInfoTableModel>
      * @date 2020/4/23 9:41 PM
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchMethodAsQuery(int offset, int number) {
        Connection conn = DBUtil.getConnection();
        StringBuffer stringBuffer = new StringBuffer();
        String sql = "select * from method_info_table";
        // is_mut=0 是不带test注解的方法
        String whereSql = " where is_mut = 0 and method_comment like '%Optional[%]%' and method_id not in (select test_target_id from test_info_table)";
        String limitSql = " limit " + offset + "," + number;
        sql = sql + whereSql + limitSql;
        List<MethodInfoTableModel> methodList = new ArrayList<>();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            MethodInfoTableModel methodInfoTableModel = null;
            while (rs.next()) {
                int id = rs.getInt("id");
                String methodCode = rs.getString("method_code");
                String methodId = rs.getString("method_id");
                String methodSignature = rs.getString("method_signature");
                String methodComment = rs.getString("method_comment");
                String returnType = rs.getString("return_type");
                methodInfoTableModel = new MethodInfoTableModel();
                methodInfoTableModel.setId(id);
                methodInfoTableModel.setMethodId(methodId);
                methodInfoTableModel.setMethodCode(methodCode);
                methodInfoTableModel.setSignature(methodSignature);
                methodInfoTableModel.setReturnType(returnType);
                methodInfoTableModel.setMethodCommentSummary(methodComment);
                methodList.add(methodInfoTableModel);
                methodInfoTableModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodList;
    }

    /**
      * Search method list by the method ids' strings.
      * @param methodIdList
      * @return List<MethodInfoTableModel>
      * @date 2020/4/21 10:53 PM
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchMethodListByMethodIdList(List<String> methodIdList) {
        Connection conn = DBUtil.getConnection();
        StringBuffer stringBuffer = new StringBuffer();
        String sql = "select * from method_info_table";
        int size = methodIdList.size();
        for (int i = 0; i < size; i++) {
            stringBuffer.append("'" + methodIdList.get(i) + "'");
            if (i < size - 1) {
                stringBuffer.append(",");
            }
        }
        String whereSql = " where method_id in (" + stringBuffer.toString() + ")";
        sql = sql + whereSql;
        List<MethodInfoTableModel> methodList = new ArrayList<>();
        MethodInfoTableModel methodInfoTableModel;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                methodInfoTableModel = new MethodInfoTableModel();
                int id = rs.getInt("id");
                methodInfoTableModel.setId(id);
                String methodId = rs.getString("method_id");
                methodInfoTableModel.setMethodId(methodId);

                String methodExtendedSignature = rs.getString("extended_signature");
                methodInfoTableModel.setExtendedSignature(methodExtendedSignature);

                String packageName = rs.getString("package_name");
                methodInfoTableModel.setPackageName(packageName);

                String className = rs.getString("class_name");
                methodInfoTableModel.setClassName(className);
                String classAnnotations = rs.getString("class_annotations");
                methodInfoTableModel.setClassAnnotations(classAnnotations);
                String extendsClasses = rs.getString("extends_classes");
                methodInfoTableModel.setExtendsClasses(extendsClasses);
                String implementsInterfaces = rs.getString("implements_interfaces");
                methodInfoTableModel.setImplementsInterfaces(implementsInterfaces);

                String methodName = rs.getString("method_name");
                methodInfoTableModel.setMethodName(methodName);
                String parameterTypes = rs.getString("parameter_types");
                methodInfoTableModel.setParameterTypes(parameterTypes);
                String returnType = rs.getString("return_type");
                methodInfoTableModel.setReturnType(returnType);
                String modifiers = rs.getString("modifiers");
                methodInfoTableModel.setModifiers(modifiers);
                String signature = rs.getString("signature");
                methodInfoTableModel.setSignature(signature);

                String methodCommentSummary = rs.getString("method_comment_summary");
                methodInfoTableModel.setMethodCommentSummary(methodCommentSummary);
                String methodCommentKeywords = rs.getString("method_comment_keywords");
                methodInfoTableModel.setMethodCommentKeywords(methodCommentKeywords);

                String methodCode = rs.getString("method_code");
                methodInfoTableModel.setMethodCode(methodCode);
                String methodCFG = rs.getString("method_cfg");
                methodInfoTableModel.setMethodCFG(methodCFG);

                String importDependencies = rs.getString("import_dependencies");
                methodInfoTableModel.setImportDependencies(importDependencies);
                String variableDependencies = rs.getString("variable_dependencies");
                methodInfoTableModel.setVariableDependencies(variableDependencies);
                String initializerDependencies = rs.getString("initializer_dependencies");
                methodInfoTableModel.setInitializerDependencies(initializerDependencies);
                String methodDependencies = rs.getString("method_dependencies");
                methodInfoTableModel.setMethodDependencies(methodDependencies);

                int fromWhere = rs.getInt("from_where");
                methodInfoTableModel.setFromWhere(fromWhere);
                int isMut = rs.getInt("is_mut");
                methodInfoTableModel.setIsMut(isMut);

                String testCaseIds = rs.getString("test_case_ids");
                methodInfoTableModel.setTestCaseIds(testCaseIds);
                String projectId = rs.getString("project_id");
                methodInfoTableModel.setProjectId(projectId);

                Timestamp storageTime = rs.getTimestamp("storage_time");
                methodInfoTableModel.setStorageTime(storageTime);

                methodList.add(methodInfoTableModel);
                methodInfoTableModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        if (methodList.size() == 0) {
            methodList = null;
        }
        return methodList;
    }


    /**
     *
     * @param methodId
     * @return String
     * @throws
     * @date 2020/4/21 11:50
     * @author sunweisong
     */
    public String searchMethodCodeByMethodId(String methodId) {
        Connection conn = DBUtil.getConnection();
        String sql = "select method_code from method_info_table where method_id = '" + methodId + "'";
        String methodCode = "";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                methodCode = rs.getString("method_code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodCode;
    }

    /**
     *
     * @param methodId
     * @return String
     * @throws
     * @date 2020/4/21 11:00
     * @author sunweisong
     */
    public String searchTestCaseIdsByMethodId(String methodId) {
        Connection conn = DBUtil.getConnection();
        String sql = "select test_case_ids from method_info_table where method_id = '" + methodId + "'";
        String testCaseIds = "";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                testCaseIds = rs.getString("test_case_ids");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testCaseIds;
    }

    /**
      * Search method by methodId.
      * @param methodId
      * @return MethodInfoTableModel
      * @date 2020/4/21 2:49 PM
      * @author sunweisong
      */
    public MethodInfoTableModel searchMethodByMethodId(String methodId) {
        Connection conn = DBUtil.getConnection();
        String sql = "select * from method_info_table where method_id = '" + methodId + "'";
        MethodInfoTableModel methodInfoTableModel = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                methodInfoTableModel = new MethodInfoTableModel(methodId);
                int id = rs.getInt("id");
                methodInfoTableModel.setId(id);
                String methodExtendedSignature = rs.getString("extended_signature");
                methodInfoTableModel.setExtendedSignature(methodExtendedSignature);

                String packageName = rs.getString("package_name");
                methodInfoTableModel.setPackageName(packageName);

                String className = rs.getString("class_name");
                methodInfoTableModel.setClassName(className);
                String classAnnotations = rs.getString("class_annotations");
                methodInfoTableModel.setClassAnnotations(classAnnotations);
                String extendsClasses = rs.getString("extends_classes");
                methodInfoTableModel.setExtendsClasses(extendsClasses);
                String implementsInterfaces = rs.getString("implements_interfaces");
                methodInfoTableModel.setImplementsInterfaces(implementsInterfaces);

                String methodName = rs.getString("method_name");
                methodInfoTableModel.setMethodName(methodName);
                String parameterTypes = rs.getString("parameter_types");
                methodInfoTableModel.setParameterTypes(parameterTypes);
                String returnType = rs.getString("return_type");
                methodInfoTableModel.setReturnType(returnType);
                String modifiers = rs.getString("modifiers");
                methodInfoTableModel.setModifiers(modifiers);
                String signature = rs.getString("signature");
                methodInfoTableModel.setSignature(signature);

                String methodCommentSummary = rs.getString("method_comment_summary");
                methodInfoTableModel.setMethodCommentSummary(methodCommentSummary);
                String methodCommentKeywords = rs.getString("method_comment_keywords");
                methodInfoTableModel.setMethodCommentKeywords(methodCommentKeywords);

                String methodCode = rs.getString("method_code");
                methodInfoTableModel.setMethodCode(methodCode);
                String methodCFG = rs.getString("method_cfg");
                methodInfoTableModel.setMethodCFG(methodCFG);

                String importDependencies = rs.getString("import_dependencies");
                methodInfoTableModel.setImportDependencies(importDependencies);
                String variableDependencies = rs.getString("variable_dependencies");
                methodInfoTableModel.setVariableDependencies(variableDependencies);
                String initializerDependencies = rs.getString("initializer_dependencies");
                methodInfoTableModel.setInitializerDependencies(initializerDependencies);
                String methodDependencies = rs.getString("method_dependencies");
                methodInfoTableModel.setMethodDependencies(methodDependencies);

                int fromWhere = rs.getInt("from_where");
                methodInfoTableModel.setFromWhere(fromWhere);
                int isMut = rs.getInt("is_mut");
                methodInfoTableModel.setIsMut(isMut);

                String testCaseIds = rs.getString("test_case_ids");
                methodInfoTableModel.setTestCaseIds(testCaseIds);
                String projectId = rs.getString("project_id");
                methodInfoTableModel.setProjectId(projectId);

                Timestamp storageTime = rs.getTimestamp("storage_time");
                methodInfoTableModel.setStorageTime(storageTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodInfoTableModel;
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
            }
            if (pst != null) {
                pst.close();
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
      * @date 2020/8/3 3:06 PM
      * @author sunweisong
      */
    public void saveMethodInfoToDatabase(MethodInfoTableModel methodInfoTableModel) {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT IGNORE INTO method_info_table (method_id, extended_signature, package_name" +
                ", class_name, class_annotations, extends_classes, implements_interfaces" +
                ", method_name, parameter_types, return_type, modifiers, signature, method_comment_summary, method_code" +
                ", import_dependencies, variable_dependencies, initializer_dependencies, enum_dependencies" +
                ", method_dependencies, from_where, project_id, storage_time)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp storageTime = new Timestamp(System.currentTimeMillis());
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, methodInfoTableModel.getMethodId());
            pst.setString(2, methodInfoTableModel.getExtendedSignature());
            pst.setString(3, methodInfoTableModel.getPackageName());
            pst.setString(4, methodInfoTableModel.getClassName());
            pst.setString(5, methodInfoTableModel.getClassAnnotations());
            pst.setString(6, methodInfoTableModel.getExtendsClasses());
            pst.setString(7, methodInfoTableModel.getImplementsInterfaces());
            pst.setString(8, methodInfoTableModel.getMethodName());
            pst.setString(9, methodInfoTableModel.getParameterTypes());
            pst.setString(10, methodInfoTableModel.getReturnType());
            pst.setString(11, methodInfoTableModel.getModifiers());
            pst.setString(12, methodInfoTableModel.getSignature());
            pst.setString(13, methodInfoTableModel.getMethodCommentSummary());
            pst.setString(14, methodInfoTableModel.getMethodCode());
            pst.setString(15, methodInfoTableModel.getImportDependencies());
            pst.setString(16, methodInfoTableModel.getVariableDependencies());
            pst.setString(17, methodInfoTableModel.getInitializerDependencies());
            pst.setString(18, methodInfoTableModel.getEnumDependencies());
            pst.setString(19, methodInfoTableModel.getMethodDependencies());
            pst.setInt(20, methodInfoTableModel.getFromWhere());
            pst.setString(21, methodInfoTableModel.getProjectId());
            pst.setTimestamp(22, storageTime);
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
     * @date 2020/8/14 09:29
     * @author sunweisong
     */
    public Map<String, String> searchMethodIdAndCodeWithComment() {
        Map<String, String> methodIdAndCodeMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_code from method_info_table where method_comment_summary != ''";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCode = rs.getString("method_code");
                methodIdAndCodeMap.put(methodId, methodCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdAndCodeMap;
    }

    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/8/14 09:29
     * @author sunweisong
     */
    public Map<String, String> searchMethodIdWithCommentSummary() {
        Map<String, String> methodIdAndCommentSummaryMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_comment_summary from method_info_table where is_mut=1 and method_comment_summary !=''";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCommentSummary = rs.getString("method_comment_summary");
                if (NLPUtil.isContainOtherLanguage(methodCommentSummary)) {
                    // 中文、日文
                    continue;
                }
                if (methodCommentSummary.contains("@")) {
                    methodCommentSummary = methodCommentSummary.replace("@", "");
                }
                if (".".equals(methodCommentSummary)) {
                    continue;
                }
                methodIdAndCommentSummaryMap.put(methodId, methodCommentSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdAndCommentSummaryMap;
    }



    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/15 16:23
      * @author sunweisong
      */
    public Map<String, Set<String>> searchMethodCommentSummary() {
        CommentAnalysis ca = new CommentAnalysis();
        Map<String, Set<String>> methodCommentSummaryMethodIdSetMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_comment_summary from method_info_table where method_comment_summary != ''";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCommentSummary = rs.getString("method_comment_summary");
                if (ca.isContainOtherLanguage(methodCommentSummary)) {
                    // 中文、日文
                    continue;
                }
                if (".".equals(methodCommentSummary)) {
                    continue;
                }
                Set<String> methodIdSet = methodCommentSummaryMethodIdSetMap.get(methodCommentSummary);
                if (methodIdSet == null) {
                    methodIdSet = new HashSet<>();
                }
                methodIdSet.add(methodId);
                methodCommentSummaryMethodIdSetMap.put(methodCommentSummary, methodIdSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        ca = null;
        return methodCommentSummaryMethodIdSetMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/16 09:45
      * @author sunweisong
      */
    public Map<String,String> searchMethodIdAndCodeIsMut() {
        Map<String, String> methodIdAndCodeMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_code from method_info_table where is_mut = 1";
//        String sql = "select method_id, method_code from method_info_table where method_id = 'bcdd035f3fb91cae0c2a87e313f7932a'";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCode = rs.getString("method_code");
                methodIdAndCodeMap.put(methodId, methodCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdAndCodeMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/16 15:10
      * @author sunweisong
      */
    public Map<Integer, String> getMUTIdWithIndex() {
        Map<Integer, String> indexAndMethodIdMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id from method_info_table where is_mut = 1";
        int count = 0;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                count++;
                String methodId = rs.getString("method_id");
                indexAndMethodIdMap.put(count, methodId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return indexAndMethodIdMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/16 15:01
      * @author sunweisong
      */
    public int getNumberOfMethodWithTestCase() {
        int numberOfMethodWithTestCase = 0;
        Connection conn = DBUtil.getConnection();
        String sql = "select count(*) from method_info_table where is_mut = 1";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                numberOfMethodWithTestCase = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return numberOfMethodWithTestCase;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/22 23:16
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchTopKMethodWithSameSignature(String signature, int k) {
        Connection conn = DBUtil.getConnection();
        String sql = "select * from method_info_table where is_mut = 1 and signature = '" + signature + "' limit " + k;
        List<MethodInfoTableModel> methodInfoTableModelList = new ArrayList<>();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            MethodInfoTableModel methodInfoTableModel;
            while (rs.next()) {
                methodInfoTableModel = new MethodInfoTableModel();
                int id = rs.getInt("id");
                methodInfoTableModel.setId(id);
                String methodId = rs.getString("method_id");
                methodInfoTableModel.setMethodId(methodId);
                String methodExtendedSignature = rs.getString("extended_signature");
                methodInfoTableModel.setExtendedSignature(methodExtendedSignature);

                String packageName = rs.getString("package_name");
                methodInfoTableModel.setPackageName(packageName);

                String className = rs.getString("class_name");
                methodInfoTableModel.setClassName(className);
                String classAnnotations = rs.getString("class_annotations");
                methodInfoTableModel.setClassAnnotations(classAnnotations);
                String extendsClasses = rs.getString("extends_classes");
                methodInfoTableModel.setExtendsClasses(extendsClasses);
                String implementsInterfaces = rs.getString("implements_interfaces");
                methodInfoTableModel.setImplementsInterfaces(implementsInterfaces);

                String methodName = rs.getString("method_name");
                methodInfoTableModel.setMethodName(methodName);
                String parameterTypes = rs.getString("parameter_types");
                methodInfoTableModel.setParameterTypes(parameterTypes);
                String returnType = rs.getString("return_type");
                methodInfoTableModel.setReturnType(returnType);
                String modifiers = rs.getString("modifiers");
                methodInfoTableModel.setModifiers(modifiers);
                methodInfoTableModel.setSignature(signature);

                String methodCommentSummary = rs.getString("method_comment_summary");
                methodInfoTableModel.setMethodCommentSummary(methodCommentSummary);
                String methodCommentKeywords = rs.getString("method_comment_keywords");
                methodInfoTableModel.setMethodCommentKeywords(methodCommentKeywords);

                String methodCode = rs.getString("method_code");
                methodInfoTableModel.setMethodCode(methodCode);
                String methodCFG = rs.getString("method_cfg");
                methodInfoTableModel.setMethodCFG(methodCFG);

                String importDependencies = rs.getString("import_dependencies");
                methodInfoTableModel.setImportDependencies(importDependencies);
                String variableDependencies = rs.getString("variable_dependencies");
                methodInfoTableModel.setVariableDependencies(variableDependencies);
                String initializerDependencies = rs.getString("initializer_dependencies");
                methodInfoTableModel.setInitializerDependencies(initializerDependencies);
                String methodDependencies = rs.getString("method_dependencies");
                methodInfoTableModel.setMethodDependencies(methodDependencies);

                int fromWhere = rs.getInt("from_where");
                methodInfoTableModel.setFromWhere(fromWhere);
                int isMut = rs.getInt("is_mut");
                methodInfoTableModel.setIsMut(isMut);

                String testCaseIds = rs.getString("test_case_ids");
                methodInfoTableModel.setTestCaseIds(testCaseIds);
                String projectId = rs.getString("project_id");
                methodInfoTableModel.setProjectId(projectId);

                Timestamp storageTime = rs.getTimestamp("storage_time");
                methodInfoTableModel.setStorageTime(storageTime);

                methodInfoTableModelList.add(methodInfoTableModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        if (methodInfoTableModelList.isEmpty()) {
            methodInfoTableModelList = null;
        }
        return methodInfoTableModelList;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/23 09:23
      * @author sunweisong
      */
    public List<MethodInfoTableModel> searchMethodByRange(int offset, int number) {
        Connection conn = DBUtil.getConnection();
        String sql = "select * from method_info_table limit " + offset + "," + number;
        List<MethodInfoTableModel> methodInfoTableModelList = new ArrayList<>(number);
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            MethodInfoTableModel methodInfoTableModel;
            while (rs.next()) {
                methodInfoTableModel = new MethodInfoTableModel();
                int id = rs.getInt("id");
                methodInfoTableModel.setId(id);
                String methodId = rs.getString("method_id");
                methodInfoTableModel.setMethodId(methodId);
                String methodExtendedSignature = rs.getString("extended_signature");
                methodInfoTableModel.setExtendedSignature(methodExtendedSignature);

                String packageName = rs.getString("package_name");
                methodInfoTableModel.setPackageName(packageName);

                String className = rs.getString("class_name");
                methodInfoTableModel.setClassName(className);
                String classAnnotations = rs.getString("class_annotations");
                methodInfoTableModel.setClassAnnotations(classAnnotations);
                String extendsClasses = rs.getString("extends_classes");
                methodInfoTableModel.setExtendsClasses(extendsClasses);
                String implementsInterfaces = rs.getString("implements_interfaces");
                methodInfoTableModel.setImplementsInterfaces(implementsInterfaces);

                String methodName = rs.getString("method_name");
                methodInfoTableModel.setMethodName(methodName);
                String parameterTypes = rs.getString("parameter_types");
                methodInfoTableModel.setParameterTypes(parameterTypes);
                String returnType = rs.getString("return_type");
                methodInfoTableModel.setReturnType(returnType);
                String modifiers = rs.getString("modifiers");
                methodInfoTableModel.setModifiers(modifiers);
                String signature = rs.getString("signature");
                methodInfoTableModel.setSignature(signature);

                String methodCommentSummary = rs.getString("method_comment_summary");
                methodInfoTableModel.setMethodCommentSummary(methodCommentSummary);
                String methodCommentKeywords = rs.getString("method_comment_keywords");
                methodInfoTableModel.setMethodCommentKeywords(methodCommentKeywords);

                String methodCode = rs.getString("method_code");
                methodInfoTableModel.setMethodCode(methodCode);
                String methodCFG = rs.getString("method_cfg");
                methodInfoTableModel.setMethodCFG(methodCFG);

                String importDependencies = rs.getString("import_dependencies");
                methodInfoTableModel.setImportDependencies(importDependencies);
                String variableDependencies = rs.getString("variable_dependencies");
                methodInfoTableModel.setVariableDependencies(variableDependencies);
                String initializerDependencies = rs.getString("initializer_dependencies");
                methodInfoTableModel.setInitializerDependencies(initializerDependencies);
                String methodDependencies = rs.getString("method_dependencies");
                methodInfoTableModel.setMethodDependencies(methodDependencies);

                int fromWhere = rs.getInt("from_where");
                methodInfoTableModel.setFromWhere(fromWhere);
                int isMut = rs.getInt("is_mut");
                methodInfoTableModel.setIsMut(isMut);

                String testCaseIds = rs.getString("test_case_ids");
                methodInfoTableModel.setTestCaseIds(testCaseIds);
                String projectId = rs.getString("project_id");
                methodInfoTableModel.setProjectId(projectId);

                Timestamp storageTime = rs.getTimestamp("storage_time");
                methodInfoTableModel.setStorageTime(storageTime);

                methodInfoTableModelList.add(methodInfoTableModel);
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodInfoTableModelList;
    }


    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/23 23:28
      * @author sunweisong
      */
    public Map<String,String> searchMethodIdWithCommentKeywords() {
        Map<String, String> methodIdCommentKeywordsMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_comment_keywords from method_info_table where is_mut = 1 and method_comment_keywords is not null";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCommentKeywords = rs.getString("method_comment_keywords");
                methodIdCommentKeywordsMap.put(methodId, methodCommentKeywords);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdCommentKeywordsMap;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/24 11:17
      * @author sunweisong
      */
    public Map<String,String> searchMUTIdAndWithCFG() {
        Map<String, String> methodIdCFGMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, method_cfg from method_info_table where is_mut = 1 and method_cfg is not null";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String methodCFG = rs.getString("method_cfg");
                methodIdCFGMap.put(methodId, methodCFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdCFGMap;
    }


    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/25 09:45
      * @author sunweisong
      */
    public List<TestTendererNeedMethodModel> searchMUTIdForSumBased() {
        List<TestTendererNeedMethodModel> testTendererNeedMethodModelList = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, class_name, method_name, signature, return_type from method_info_table where is_mut = 1";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String className = rs.getString("class_name");
                String methodName = rs.getString("method_name");
                String signature = rs.getString("signature");
                int start = signature.indexOf("(");
                int end = signature.lastIndexOf(")");
                String parameterType = signature.substring(start + 1, end).trim();
                String returnType = rs.getString("return_type");
                TestTendererNeedMethodModel testTendererNeedMethodModel =  new TestTendererNeedMethodModel(methodId
                        , className, methodName, signature, parameterType, returnType);
                testTendererNeedMethodModelList.add(testTendererNeedMethodModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testTendererNeedMethodModelList;
    }

    /**
     *
     * @param
     * @return
     * @throws
     * @date 2020/8/25 09:43
     * @author sunweisong
     */
    public List<TestTendererNeedMethodModel> searchMUTIdForTestTenderer() {
        List<TestTendererNeedMethodModel> testTendererNeedMethodModelList = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, class_name, method_name, signature, return_type from method_info_table where is_mut = 1";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String className = rs.getString("class_name");
                String methodName = rs.getString("method_name");
                String signature = rs.getString("signature");
                String returnType = rs.getString("return_type");
                TestTendererNeedMethodModel testTendererNeedMethodModel =  new TestTendererNeedMethodModel(methodId
                        , className, methodName, signature, returnType);
                System.out.println(testTendererNeedMethodModel.toString());
                testTendererNeedMethodModelList.add(testTendererNeedMethodModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return testTendererNeedMethodModelList;
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/24 14:53
      * @author sunweisong
      */
    public Map<String,String> searchMUTIdAndSignature() {
        Map<String, String> methodIdCFGMap = new HashMap<>();
        Connection conn = DBUtil.getConnection();
        String sql = "select method_id, signature from method_info_table where is_mut = 1";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String methodId = rs.getString("method_id");
                String signature = rs.getString("signature");
                methodIdCFGMap.put(methodId, signature);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return methodIdCFGMap;
    }
}
