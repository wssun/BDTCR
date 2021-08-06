package com.bdtcr.tsearcher;

/**
  * @Author sunweisong
  * @Date 2020/3/16 2:21 PM
  */
public class Edge {

    private int id;
    private int source;
    private int target;
    private String label;

    public Edge(int id, int source, int target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public Edge(int id, int source, int target, String label) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", source=" + source +
                ", target=" + target +
                ", label='" + label + '\'' +
                '}';
    }
}
