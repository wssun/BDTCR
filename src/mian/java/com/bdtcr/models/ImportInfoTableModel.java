package com.bdtcr.models;

import java.sql.Timestamp;

/**
  * @Author sunweisong
  * @Date 2020/4/21 8:35 PM
  */
public class ImportInfoTableModel {
    private int id;
    private String importId;
    private String importModifiers;
    private String importName;
    private String importString;
    private Timestamp storageTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getImportModifiers() {
        return importModifiers;
    }

    public String getImportName() {
        return importName;
    }

    public void setImportName(String importName) {
        this.importName = importName;
    }

    public void setImportModifiers(String importModifiers) {
        this.importModifiers = importModifiers;
    }

    public String getImportString() {
        return importString;
    }

    public void setImportString(String importString) {
        this.importString = importString;
    }

    public Timestamp getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Timestamp storageTime) {
        this.storageTime = storageTime;
    }

    @Override
    public String toString() {
        return "ImportInfoTableModel{" +
                "id=" + id +
                ", importId='" + importId + '\'' +
                ", importModifiers='" + importModifiers + '\'' +
                ", importName='" + importName + '\'' +
                ", importString='" + importString + '\'' +
                ", storageTime=" + storageTime +
                '}';
    }
}
