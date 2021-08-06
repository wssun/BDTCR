package com.bdtcr.models;

/**
  * @Author sunweisong
  * @Date 2020/3/3 3:10 PM
  */
public class TokenModel {
    private String token;
    private String pos;
    private String lemma;

    public TokenModel(String token, String pos, String lemma) {
        this.token = token;
        this.pos = pos;
        this.lemma = lemma;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "TokenModel{" +
                "token='" + token + '\'' +
                ", pos='" + pos + '\'' +
                ", lemma='" + lemma + '\'' +
                '}';
    }
}
