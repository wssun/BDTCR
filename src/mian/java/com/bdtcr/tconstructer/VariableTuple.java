package com.bdtcr.tconstructer;

import com.github.javaparser.ast.Node;

public class VariableTuple<T extends Node> {
    boolean isGlobal;
    String type;
    String name;
    T original;

    VariableTuple(boolean isGlobal, String type, String name, T original) {
        this.isGlobal = isGlobal;
        this.type = type;
        this.name = name;
        this.original = original;
    }

    void printString() {
        System.out.println(
                "isGlobal: " + isGlobal +
                        "\ntype: " + type +
                        "\nname: " + name +
                        "\n================================================="
        );
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getOriginal() {
        return original;
    }

    public void setOriginal(T original) {
        this.original = original;
    }
}