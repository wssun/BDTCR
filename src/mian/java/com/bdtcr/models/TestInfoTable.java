package com.bdtcr.models;


public class TestInfoTable {

  private long id;
  private String testCaseName;
  private String testCaseCode;
  private String testTargetId;
  private String testTargetSignature;
  private String className;
  private String packageName;
  private String importDependencies;
  private String methodDependencies;
  private long testFramework;
  private long junitVersion;
  private long assertFramework;
  private java.sql.Timestamp storageTime;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getTestCaseName() {
    return testCaseName;
  }

  public void setTestCaseName(String testCaseName) {
    this.testCaseName = testCaseName;
  }


  public String getTestCaseCode() {
    return testCaseCode;
  }

  public void setTestCaseCode(String testCaseCode) {
    this.testCaseCode = testCaseCode;
  }


  public String getTestTargetId() {
    return testTargetId;
  }

  public void setTestTargetId(String testTargetId) {
    this.testTargetId = testTargetId;
  }


  public String getTestTargetSignature() {
    return testTargetSignature;
  }

  public void setTestTargetSignature(String testTargetSignature) {
    this.testTargetSignature = testTargetSignature;
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


  public long getTestFramework() {
    return testFramework;
  }

  public void setTestFramework(long testFramework) {
    this.testFramework = testFramework;
  }


  public long getJunitVersion() {
    return junitVersion;
  }

  public void setJunitVersion(long junitVersion) {
    this.junitVersion = junitVersion;
  }


  public long getAssertFramework() {
    return assertFramework;
  }

  public void setAssertFramework(long assertFramework) {
    this.assertFramework = assertFramework;
  }


  public java.sql.Timestamp getStorageTime() {
    return storageTime;
  }

  public void setStorageTime(java.sql.Timestamp storageTime) {
    this.storageTime = storageTime;
  }

  @Override
  public String toString() {
    return "TestInfoTable{" +
            "id=" + id +
            ", testCaseName='" + testCaseName + '\'' +
            ", testCaseCode='" + testCaseCode + '\'' +
            ", testTargetId='" + testTargetId + '\'' +
            ", testTargetSignature='" + testTargetSignature + '\'' +
            ", className='" + className + '\'' +
            ", packageName='" + packageName + '\'' +
            ", importDependencies='" + importDependencies + '\'' +
            ", methodDependencies='" + methodDependencies + '\'' +
            ", testFramework=" + testFramework +
            ", junitVersion=" + junitVersion +
            ", assertFramework=" + assertFramework +
            ", storageTime=" + storageTime +
            '}';
  }
}
