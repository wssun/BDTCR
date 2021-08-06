package com.bdtcr.models;

import java.util.List;

/**
  * @Author sunweisong
  * @Date 2020/4/19 1:02 PM
  */
public class RecommendedTestCaseModel extends TestTargetModel{

    private double similarity; // similarity measured by NiCad
    private double DC; // distance_between_comments
    private double DL; // distance_between_literal_texts
    private double DG; // distance_between_cfgs
    private double DB; // distance_between_code_blocks
    private double BD; // balanced_distance

    private int testFramework;
    private int junitVersion;
    private int assertFramework;

    private List<String> third_party_dependencies;
    private List<MethodInfoTableModel> external_method_dependencies;

    public RecommendedTestCaseModel() {
    }

    public RecommendedTestCaseModel(String mut_signature, String mut_comment, String mut_code, String tc_code
            , double DC, double DL, double DG, double DB, double BD) {
        super(mut_signature, mut_comment, mut_code, tc_code);
        this.DC = DC;
        this.DL = DL;
        this.DG = DG;
        this.DB = DB;
        this.BD = BD;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
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

    public List<String> getThird_party_dependencies() {
        return third_party_dependencies;
    }

    public void setThird_party_dependencies(List<String> third_party_dependencies) {
        this.third_party_dependencies = third_party_dependencies;
    }

    public List<MethodInfoTableModel> getExternal_method_dependencies() {
        return external_method_dependencies;
    }

    public void setExternal_method_dependencies(List<MethodInfoTableModel> external_method_dependencies) {
        this.external_method_dependencies = external_method_dependencies;
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

    @Override
    public String toString() {
        return "RecommendedTestCaseModel{" +
                "DC=" + DC +
                ", DL=" + DL +
                ", DG=" + DG +
                ", DB=" + DB +
                ", BD=" + BD +
                '}';
    }
}
