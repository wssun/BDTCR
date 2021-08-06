package com.bdtcr.models;


public class MethodInfoTable {

    private long id;
    private String methodId;
    private String methodSignature;
    private String methodName;
    private String parameterTypes;
    private String className;
    private String packageName;
    private String methodComment;
    private String methodCommentKeywords;
    private String methodCode;
    private long isMut;
    private String importDependencies;
    private String methodDependencies;
    private String projectId;
    private java.sql.Timestamp storageTime;
    private String returnType;


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


    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
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


    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
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


    public long getIsMut() {
        return isMut;
    }

    public void setIsMut(long isMut) {
        this.isMut = isMut;
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


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


    public java.sql.Timestamp getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(java.sql.Timestamp storageTime) {
        this.storageTime = storageTime;
    }


    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

  @Override
  public String toString() {
    return "MethodInfoTable{" +
            "id=" + id +
            ", methodId='" + methodId + '\'' +
            ", methodSignature='" + methodSignature + '\'' +
            ", methodName='" + methodName + '\'' +
            ", parameterTypes='" + parameterTypes + '\'' +
            ", className='" + className + '\'' +
            ", packageName='" + packageName + '\'' +
            ", methodComment='" + methodComment + '\'' +
            ", methodCommentKeywords='" + methodCommentKeywords + '\'' +
            ", methodCode='" + methodCode + '\'' +
            ", isMut=" + isMut +
            ", importDependencies='" + importDependencies + '\'' +
            ", methodDependencies='" + methodDependencies + '\'' +
            ", projectId='" + projectId + '\'' +
            ", storageTime=" + storageTime +
            ", returnType='" + returnType + '\'' +
            '}';
  }
}
