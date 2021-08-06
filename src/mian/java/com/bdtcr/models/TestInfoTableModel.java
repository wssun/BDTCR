package com.bdtcr.models;

import java.sql.Timestamp;

/**
  * @Author sunweisong
  * @Date 2020/4/21 5:14 PM
  */
public class TestInfoTableModel {
    private int id;
    private String testCaseId;
    private String extendedSignature;
    private String packageName;
    private String className;
    private String classAnnotations;
    private String extendsClasses;
    private String implementsInterfaces;
    private String testMethodName;
    private String testMethodCode;
    private String testTargets;
    private String beforeClassMethod;
    private String beforeMethod;
    private String afterMethod;
    private String afterClassMethod;
    private String importDependencies;
    private String variableDependencies;
    private String initializerDependencies;
    private String enumDependencies;
    private String methodDependencies;
    private int testFramework;
    private int junitVersion;
    private int assertFramework;
    private String projectId;
    private Timestamp storageTime;

    public TestInfoTableModel() {
    }

    public TestInfoTableModel(int id, String testCaseId, String extendedSignature
            , String packageName, String className, String classAnnotations, String extendsClasses, String implementsInterfaces
            , String testMethodName, String testMethodCode, String testTargets
            , String beforeClassMethod, String beforeMethod, String afterMethod, String afterClassMethod
            , String importDependencies, String variableDependencies, String initializerDependencies, String enumDependencies, String methodDependencies
            , int testFramework, int junitVersion, int assertFramework
            , String projectId, Timestamp storageTime) {
        this.id = id;
        this.testCaseId = testCaseId;
        this.extendedSignature = extendedSignature;
        this.packageName = packageName;
        this.className = className;
        this.classAnnotations = classAnnotations;
        this.extendsClasses = extendsClasses;
        this.implementsInterfaces = implementsInterfaces;
        this.testMethodName = testMethodName;
        this.testMethodCode = testMethodCode;
        this.testTargets = testTargets;
        this.beforeClassMethod = beforeClassMethod;
        this.beforeMethod = beforeMethod;
        this.afterMethod = afterMethod;
        this.afterClassMethod = afterClassMethod;
        this.importDependencies = importDependencies;
        this.variableDependencies = variableDependencies;
        this.initializerDependencies = initializerDependencies;
        this.enumDependencies = enumDependencies;
        this.methodDependencies = methodDependencies;
        this.testFramework = testFramework;
        this.junitVersion = junitVersion;
        this.assertFramework = assertFramework;
        this.projectId = projectId;
        this.storageTime = storageTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getImportDependencies() {
        return importDependencies;
    }

    public void setImportDependencies(String importDependencies) {
        this.importDependencies = importDependencies;
    }

    public String getMethodDependencies() {
        return methodDependencies;
    }

    public void setMethodDependencies(String methodDependencies) {
        this.methodDependencies = methodDependencies;
    }

    public int getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(int testFramework) {
        this.testFramework = testFramework;
    }

    public int getJunitVersion() {
        return junitVersion;
    }

    public void setJunitVersion(int junitVersion) {
        this.junitVersion = junitVersion;
    }

    public int getAssertFramework() {
        return assertFramework;
    }

    public void setAssertFramework(int assertFramework) {
        this.assertFramework = assertFramework;
    }

    public Timestamp getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Timestamp storageTime) {
        this.storageTime = storageTime;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getExtendedSignature() {
        return extendedSignature;
    }

    public void setExtendedSignature(String extendedSignature) {
        this.extendedSignature = extendedSignature;
    }

    public String getClassAnnotations() {
        return classAnnotations;
    }

    public void setClassAnnotations(String classAnnotations) {
        this.classAnnotations = classAnnotations;
    }

    public String getExtendsClasses() {
        return extendsClasses;
    }

    public void setExtendsClasses(String extendsClasses) {
        this.extendsClasses = extendsClasses;
    }

    public String getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public void setImplementsInterfaces(String implementsInterfaces) {
        this.implementsInterfaces = implementsInterfaces;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    public String getTestMethodCode() {
        return testMethodCode;
    }

    public void setTestMethodCode(String testMethodCode) {
        this.testMethodCode = testMethodCode;
    }

    public String getTestTargets() {
        return testTargets;
    }

    public void setTestTargets(String testTargets) {
        this.testTargets = testTargets;
    }

    public String getBeforeMethod() {
        return beforeMethod;
    }

    public void setBeforeMethod(String beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public String getAfterMethod() {
        return afterMethod;
    }

    public void setAfterMethod(String afterMethod) {
        this.afterMethod = afterMethod;
    }

    public String getVariableDependencies() {
        return variableDependencies;
    }

    public void setVariableDependencies(String variableDependencies) {
        this.variableDependencies = variableDependencies;
    }

    public String getInitializerDependencies() {
        return initializerDependencies;
    }

    public void setInitializerDependencies(String initializerDependencies) {
        this.initializerDependencies = initializerDependencies;
    }

    public String getEnumDependencies() {
        return enumDependencies;
    }

    public void setEnumDependencies(String enumDependencies) {
        this.enumDependencies = enumDependencies;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getBeforeClassMethod() {
        return beforeClassMethod;
    }

    public void setBeforeClassMethod(String beforeClassMethod) {
        this.beforeClassMethod = beforeClassMethod;
    }

    public String getAfterClassMethod() {
        return afterClassMethod;
    }

    public void setAfterClassMethod(String afterClassMethod) {
        this.afterClassMethod = afterClassMethod;
    }
}
