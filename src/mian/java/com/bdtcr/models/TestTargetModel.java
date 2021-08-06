package com.bdtcr.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
  * @Author sunweisong
  * @Date 2020/4/19 12:08 PM
  */
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class TestTargetModel {

    private long id;
    private String mut_id;

    private String mut_signature;
    private String mut_comment;
    private String mut_code;

    private String tc_code;

    public TestTargetModel() {
    }

    public TestTargetModel(long id, String mut_id, String mut_signature, String mut_comment, String mut_code) {
        this.id = id;
        this.mut_id = mut_id;
        this.mut_signature = mut_signature;
        this.mut_comment = mut_comment;
        this.mut_code = mut_code;
    }

    public TestTargetModel(String mut_signature, String mut_comment, String mut_code) {
        this.mut_signature = mut_signature;
        this.mut_comment = mut_comment;
        this.mut_code = mut_code;
    }

    public TestTargetModel(String mut_signature, String mut_comment, String mut_code, String tc_code) {
        this.mut_signature = mut_signature;
        this.mut_comment = mut_comment;
        this.mut_code = mut_code;
        this.tc_code = tc_code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMut_id() {
        return mut_id;
    }

    public void setMut_id(String mut_id) {
        this.mut_id = mut_id;
    }

    public String getMut_signature() {
        return mut_signature;
    }

    public void setMut_signature(String mut_signature) {
        this.mut_signature = mut_signature;
    }

    public String getMut_comment() {
        return mut_comment;
    }

    public void setMut_comment(String mut_comment) {
        this.mut_comment = mut_comment;
    }

    public String getMut_code() {
        return mut_code;
    }

    public void setMut_code(String mut_code) {
        this.mut_code = mut_code;
    }

    public String getTc_code() {
        return tc_code;
    }

    public void setTc_code(String tc_code) {
        this.tc_code = tc_code;
    }

    @Override
    public String toString() {
        return "TestTargetModel{" +
                "mut_signature='" + mut_signature + '\'' +
                ", mut_comment='" + mut_comment + '\'' +
                ", mut_code='" + mut_code + '\'' +
                ", tc_code='" + tc_code + '\'' +
                '}';
    }
}
