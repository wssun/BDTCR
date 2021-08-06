package com.bdtcr.models;


public class ProjectInfoTable {

  private long id;
  private String projectId;
  private String projectName;
  private long projectType;
  private String repositoryId;
  private String repositoryUrl;
  private String repositoryName;
  private java.sql.Timestamp storageTime;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }


  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }


  public long getProjectType() {
    return projectType;
  }

  public void setProjectType(long projectType) {
    this.projectType = projectType;
  }


  public String getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(String repositoryId) {
    this.repositoryId = repositoryId;
  }


  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }


  public String getRepositoryName() {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }


  public java.sql.Timestamp getStorageTime() {
    return storageTime;
  }

  public void setStorageTime(java.sql.Timestamp storageTime) {
    this.storageTime = storageTime;
  }

}
