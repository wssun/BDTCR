package com.bdtcr.tsearcher;

import java.util.List;

/**
  * @Author sunweisong
  * @Date 2020/3/16 2:16 PM
  */
public class Node {
    private int id;
    private int line;
    private String label;

    /**
     * in: incoming
     * out: outgoing
     */
    private List<Node> inNeighborList;
    private List<Node> outNeighborList;
    private List<Edge> inEdgeList;
    private List<Edge> outEdgeList;

    public Node(int id, int line, String label) {
        this.id = id;
        this.line = line;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Node> getInNeighborList() {
        return inNeighborList;
    }

    public void setInNeighborList(List<Node> inNeighborList) {
        this.inNeighborList = inNeighborList;
    }

    public List<Node> getOutNeighborList() {
        return outNeighborList;
    }

    public void setOutNeighborList(List<Node> outNeighborList) {
        this.outNeighborList = outNeighborList;
    }

    public List<Edge> getInEdgeList() {
        return inEdgeList;
    }

    public void setInEdgeList(List<Edge> inEdgeList) {
        this.inEdgeList = inEdgeList;
    }

    public List<Edge> getOutEdgeList() {
        return outEdgeList;
    }

    public void setOutEdgeList(List<Edge> outEdgeList) {
        this.outEdgeList = outEdgeList;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", line=" + line +
                ", label='" + label + '\'' +
                '}';
    }
}
