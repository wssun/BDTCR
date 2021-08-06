package com.bdtcr.models;
import java.sql.Timestamp;

/**
  * @Author chenqianzhu
  * @Date
  */
public class MethodInfoTableModel {
    private long id;
    private String methodId;
    private String extendedSignature;
    private String packageName;
    private String className;
    private String classAnnotations;
    private String extendsClasses;
    private String implementsInterfaces;
    private String methodName;
    private String parameterTypes;
    private String returnType;
    private String modifiers;
    private String signature;
    private String methodCode;
    private String methodCommentSummary;
    private String methodCommentKeywords;
    private String methodCFG; // 表格中还未添加该字段
    private int isMut; // long 修改为 int
    private String importDependencies;
    private String variableDependencies;
    private String initializerDependencies;
    private String enumDependencies;
    private String methodDependencies;
    private int fromWhere;
    private String testCaseIds; // the id strings of the test cases
    private String projectId;
    private Timestamp storageTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getExtendedSignature() {
        return extendedSignature;
    }

    public void setExtendedSignature(String extendedSignature) {
        this.extendedSignature = extendedSignature;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodCFG() {
        return methodCFG;
    }

    public void setMethodCFG(String methodCFG) {
        this.methodCFG = methodCFG;
    }

    public String getMethodCommentSummary() {
        return methodCommentSummary;
    }

    public void setMethodCommentSummary(String methodCommentSummary) {
        this.methodCommentSummary = methodCommentSummary;
    }

    public String getMethodCommentKeywords() {
        return methodCommentKeywords;
    }

    public void setMethodCommentKeywords(String methodCommentKeywords) {
        this.methodCommentKeywords = methodCommentKeywords;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public int getIsMut() {
        return isMut;
    }

    public void setIsMut(int isMut) {
        this.isMut = isMut;
    }

    public String getImportDependencies() {
        return importDependencies;
    }

    public void setImportDependencies(String importDependencies) {
        this.importDependencies = importDependencies;
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

    public String getMethodDependencies() {
        return methodDependencies;
    }

    public void setMethodDependencies(String methodDependencies) {
        this.methodDependencies = methodDependencies;
    }

    public int getFromWhere() {
        return fromWhere;
    }

    public void setFromWhere(int fromWhere) {
        this.fromWhere = fromWhere;
    }

    public String getTestCaseIds() {
        return testCaseIds;
    }

    public void setTestCaseIds(String testCaseIds) {
        this.testCaseIds = testCaseIds;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Timestamp getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Timestamp storageTime) {
        this.storageTime = storageTime;
    }

    public MethodInfoTableModel() {
    }

    public MethodInfoTableModel(String methodId) {
        this.methodId = methodId;
    }

    @Override
    public String toString() {
        return "MethodInfoTableModel{" +
                "id=" + id +
                ", methodId='" + methodId + '\'' +
                ", extendedSignature='" + extendedSignature + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes='" + parameterTypes + '\'' +
                ", returnType='" + returnType + '\'' +
                ", modifiers='" + modifiers + '\'' +
                ", signature='" + signature + '\'' +
                ", methodCode='" + methodCode + '\'' +
                ", methodCommentSummary='" + methodCommentSummary + '\'' +
                ", methodCommentKeywords='" + methodCommentKeywords + '\'' +
                ", methodCFG='" + methodCFG + '\'' +
                ", isMut=" + isMut +
                ", importDependencies='" + importDependencies + '\'' +
                ", variableDependencies='" + variableDependencies + '\'' +
                ", initializerDependencies='" + initializerDependencies + '\'' +
                ", enumDependencies='" + enumDependencies + '\'' +
                ", methodDependencies='" + methodDependencies + '\'' +
                ", fromWhere=" + fromWhere +
                ", projectId='" + projectId + '\'' +
                ", storageTime=" + storageTime +
                '}';
    }
}
