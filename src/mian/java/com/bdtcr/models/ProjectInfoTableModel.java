package com.bdtcr.models;

import java.sql.Timestamp;

/**
  * @Author sunweisong
  * @Date 2020/8/2 10:13 AM
  */
public class ProjectInfoTableModel {

    private int id;
    private String projectId;
    private String projectName;
    private String projectType;
    private String repositoryId;
    private String repositoryUrl;
    private String repositoryName;

    private Timestamp storageTime;

    public ProjectInfoTableModel() {
    }

    public ProjectInfoTableModel(String projectName, String repositoryId, String repositoryName) {
        this.projectName = projectName;
        this.repositoryId = repositoryId;
        this.repositoryName = repositoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
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

    public Timestamp getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Timestamp storageTime) {
        this.storageTime = storageTime;
    }

    @Override
    public String toString() {
        return "ProjectInfoTableModel{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectType='" + projectType + '\'' +
                ", repositoryId='" + repositoryId + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", repositoryName='" + repositoryName + '\'' +
                ", storageTime=" + storageTime +
                '}';
    }
}
