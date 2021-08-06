package com.bdtcr.models;

/**
  * @Author sunweisong
  * @Date 2020/4/21 4:51 PM
  */
public class DistanceInfoTableModel {
    private int id;
    private String tt1Id;
    private String tt2Id;
    private double DC;
    private double DL;
    private double DG;
    private double DB;
    private double BD;

    public DistanceInfoTableModel(int id, String tt1Id, String tt2Id
            , double DC, double DL, double DG, double DB, double BD) {
        this.id = id;
        this.tt1Id = tt1Id;
        this.tt2Id = tt2Id;
        this.DC = DC;
        this.DL = DL;
        this.DG = DG;
        this.DB = DB;
        this.BD = BD;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTt1Id() {
        return tt1Id;
    }

    public void setTt1Id(String tt1Id) {
        this.tt1Id = tt1Id;
    }

    public String getTt2Id() {
        return tt2Id;
    }

    public void setTt2Id(String tt2Id) {
        this.tt2Id = tt2Id;
    }

    public double getDC() {
        return DC;
    }

    public void setDC(double DC) {
        this.DC = DC;
    }

    public double getDL() {
        return DL;
    }

    public void setDL(double DL) {
        this.DL = DL;
    }

    public double getDG() {
        return DG;
    }

    public void setDG(double DG) {
        this.DG = DG;
    }

    public double getDB() {
        return DB;
    }

    public void setDB(double DB) {
        this.DB = DB;
    }

    public double getBD() {
        return BD;
    }

    public void setBD(double BD) {
        this.BD = BD;
    }

    @Override
    public String toString() {
        return "DistanceInfoTableModel{" +
                "id=" + id +
                ", tt1Id='" + tt1Id + '\'' +
                ", tt2Id='" + tt2Id + '\'' +
                ", DC=" + DC +
                ", DL=" + DL +
                ", DG=" + DG +
                ", DB=" + DB +
                ", BD=" + BD +
                '}';
    }
}
