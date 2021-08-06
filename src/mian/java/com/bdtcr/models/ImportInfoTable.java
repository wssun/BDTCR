package com.bdtcr.models;


import java.sql.Timestamp;

public class ImportInfoTable {

  private long id;
  private String importId;
  private String importString;
  private Timestamp storageTime;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getImportId() {
    return importId;
  }

  public void setImportId(String importId) {
    this.importId = importId;
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

}
