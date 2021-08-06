package com.bdtcr.tsearcher;

/**
  * @Author sunweisong
  * @Date 2020/3/5 2:54 PM
  */
public class SynonymPair {
    private String word1;
    private String word2;
    private String pos;

    public SynonymPair(String word1, String word2) {
        this.word1 = word1;
        this.word2 = word2;
    }

    public SynonymPair(String word1, String word2, String pos) {
        this.word1 = word1;
        this.word2 = word2;
        this.pos = pos;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "SynonymPair{" +
                "word1='" + word1 + '\'' +
                ", word2='" + word2 + '\'' +
                ", pos='" + pos + '\'' +
                '}';
    }
}
