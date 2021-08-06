package com.bdtcr.models;

/**
  * @Author sunweisong
  * @Date 2020/8/25 09:20
  */
public class TestTendererNeedMethodModel {

    private String methodId;
    private String className;
    private String methodName;
    private String signature;
    private String parameterType;
    private String returnType;

    public TestTendererNeedMethodModel() {
    }

    public TestTendererNeedMethodModel(String methodId, String className, String methodName, String signature
            , String returnType) {
        this.methodId = methodId;
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.returnType = returnType;
    }

    public TestTendererNeedMethodModel(String methodId, String className, String methodName
            , String signature, String parameterType, String returnType) {
        this.methodId = methodId;
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.parameterType = parameterType;
        this.returnType = returnType;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


    @Override
    public String toString() {
        return "TestTendererNeedMethodModel{" +
                "methodId='" + methodId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", signature='" + signature + '\'' +
                ", parameterType='" + parameterType + '\'' +
                ", returnType='" + returnType + '\'' +
                '}';
    }
}
