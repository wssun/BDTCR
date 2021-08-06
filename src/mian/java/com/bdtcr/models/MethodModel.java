package com.bdtcr.models;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    private String packageName;
    private String className;
    private String methodName;
    private String parameterTypeList;
    private MethodDeclaration methodBody;
    private ConstructorDeclaration constructorBody;
    private boolean fromTest;//如果是true表示是test，如果是false表示是product

    public MethodModel(String packageName, String className, String methodName, List<String> typeList, MethodDeclaration methodBody, boolean fromTest) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.methodBody = methodBody;
        if (methodBody.getParameters().size() == 0) {
            this.parameterTypeList = "()";
        } else {
            List<Parameter> parameterList = methodBody.getParameters();
            List<String> parameters = new ArrayList<>();
            for (Parameter p : parameterList) {
                parameters.add(p.getTypeAsString());
            }
            String typeString = "(";
            if (parameters.size() > 0) {
                String s1 = parameters.get(0).replaceAll("\\?", "");
                s1 = s1.replaceAll("<", "");
                s1 = s1.replaceAll(">", "");
                typeString += s1;
                for (int i = 1; i < parameters.size(); i++) {
                    String s2 = parameters.get(i).replaceAll("\\?", "");
                    s2 = s2.replaceAll("<", "");
                    s2 = s2.replaceAll(">", "");
                    typeString = typeString + "," + s2;
                }

            }
            typeString += ")";

//            StringBuilder s = new StringBuilder("(" + parameters.get(0));
//            for (int i = 1; i < parameters.size(); i++) {
//                s.append(",").append(parameters.get(i));
//            }
//            s.append(")");
//            System.out.println(s.toString());
            this.parameterTypeList = typeString;
        }
        this.fromTest = fromTest;
    }

    public MethodModel(String packageName, String className, String methodName, List<String> typeList, ConstructorDeclaration methodBody, boolean fromTest) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.constructorBody = methodBody;
        if (typeList.size() == 0) {
            this.parameterTypeList = "()";
        } else {
            List<Parameter> parameterList = methodBody.getParameters();
            List<String> parameters = new ArrayList<>();
            for (Parameter p : parameterList) {
                parameters.add(p.getTypeAsString());
            }
            StringBuilder s = new StringBuilder("(" + parameters.get(0));
            for (int i = 1; i < parameters.size(); i++) {
                s.append(",").append(parameters.get(i));
            }
            s.append(")");
            this.parameterTypeList = s.toString();
        }
        this.fromTest = fromTest;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public MethodDeclaration getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(MethodDeclaration methodBody) {
        this.methodBody = methodBody;
    }

    public String getParameterTypeList() {
        return parameterTypeList;
    }

    public void setParameterTypeList(String parameterTypeList) {
        this.parameterTypeList = parameterTypeList;
    }

    public ConstructorDeclaration getConstructorBody() {
        return constructorBody;
    }

    public void setConstructorBody(ConstructorDeclaration constructorBody) {
        this.constructorBody = constructorBody;
    }

    public boolean isFromTest() {
        return fromTest;
    }

    public void setFromTest(boolean fromTest) {
        this.fromTest = fromTest;
    }

    @Override
    public String toString() {
        return "main.java.Analyse.MethodModel{" +
                "packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypeList='" + parameterTypeList + '\'' +
                ", methodBody=" + methodBody +
                ", constructorBody=" + constructorBody +
                ", fromTest=" + fromTest +
                '}';
    }
}
