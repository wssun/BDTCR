package com.bdtcr.tconstructer;

import com.bdtcr.models.MethodInfoTable;
import com.bdtcr.models.MethodModel;
import com.bdtcr.tsearcher.CommentAnalysis;
import com.bdtcr.models.TokenModel;
import com.bdtcr.utils.MD5Util;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.*;

public class MethodCollector {

    final String SRC_PATH = "xxx:/xxx/src";
    String FILE_PATH = "";
    static List<ClassOrInterfaceDeclaration> innerClassList = new ArrayList<>();
    static List<FieldDeclaration> globelVariableList = new ArrayList<>();
    static List<PackageDeclaration> packageList = new ArrayList<>();
    static List<ImportDeclaration> importPackageList = new ArrayList<>();
    static List<MethodDeclaration> normalMethodList = new ArrayList<>();

    public MethodCollector(String filePath) {
        this.FILE_PATH = filePath;
    }
//    public static void main(String[] args) throws Exception {
//        main.java.Analyse.MUTAnalysis mutAnalysis = new main.java.Analyse.MUTAnalysis();
//        mutAnalysis.getOriginalMethod();
//        for (int i = 0; i < originalMethodClassList.size(); i++) {
//            if (i == 1) {
//                mutAnalysis.methodExtraction(i, originalMethodClassList.get(i));
//            }
//        }
//    }

    public CompilationUnit constructCompilationUnit(String code, String filePath) throws FileNotFoundException {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(SRC_PATH));
        combinedTypeSolver.add(javaParserTypeSolver);
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        combinedTypeSolver.add(reflectionTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        return code == null ? StaticJavaParser.parse(new File(filePath)) : StaticJavaParser.parse(code);
    }

    public List<String> getOriginalMethod() throws FileNotFoundException {
        List<String> originalMethodClassList = new ArrayList<>();
        CompilationUnit cu = constructCompilationUnit(null, FILE_PATH);
        //??????package??????
        packageList = cu.findAll(PackageDeclaration.class);
        //????????????import??????
        importPackageList = cu.findAll(ImportDeclaration.class);
        //?????????????????????
        cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(coid -> !coid.getName().toString().equals(FILE_PATH)).forEach(innerClassList::add);
        //????????????????????????????????????????????????????????????
        List<FieldDeclaration> fieldDeclarationList = cu.findAll(FieldDeclaration.class);
//        fieldDeclarationList.forEach(System.out::println);
        fieldDeclarationList.forEach(fd -> {
//            System.out.println(fd.toString());
//            int indexOfPublic = fd.getParentNode().get().toString().indexOf("public");
//            int indexOfBraces = fd.getParentNode().get().toString().indexOf("{");
//            if (indexOfPublic < indexOfBraces) {
            globelVariableList.add(fd);
//            }
        });
        //?????????????????????
        cu.findAll(MethodDeclaration.class).stream().filter(md -> !md.getAnnotationByName("Test").isPresent()).forEach(normalMethodList::add);
        //????????????????????????
        cu.findAll(MethodDeclaration.class).forEach(md -> originalMethodClassList.add("class TestFragment{\n" + md.toString() + "}"));
        //????????????????????????????????????
        cu.findAll(ConstructorDeclaration.class).forEach(cd -> originalMethodClassList.add("class " + cd.getNameAsString() + "{\n" + cd.toString() + "}"));
        return originalMethodClassList;
    }


    public MethodInfoTable methodExtraction(int index, String methodClass) throws FileNotFoundException {
        List<FieldDeclaration> externalFieldDeclarationList = new ArrayList<>();
        List<String> methodDependency = new ArrayList<>();
        Set<MethodDeclaration> methodDeclarationSet = new HashSet<>();
        Set<ClassOrInterfaceDeclaration> constructorClassDeclarationSet = new HashSet<>();
        Set<ConstructorDeclaration> constructorDeclarationSet = new HashSet<>();
        String testOrProduct = "";
        if (index == 0) {
            //????????????
            testOrProduct = "prod";
        } else if (index == 1) {
            //????????????
            testOrProduct = "test";
        }
        CompilationUnit cu2 = constructCompilationUnit(methodClass, null);
        List<VariableTuple> variableList = new ArrayList<>();
        for (FieldDeclaration fd : globelVariableList) {
//            System.out.println(fd);
            variableList.add(new VariableTuple(true, fd.getVariable(0).getTypeAsString(), fd.getVariable(0).getNameAsString(), fd));
        }
        //????????????????????????????????????.???????????????true????????????????????????????????????
        Map<Expression, Boolean> normalArgumentMap = new HashMap<>();
        Map<Expression, Boolean> abnormalArgumentMap = new HashMap<>();
        cu2.findAll(MethodCallExpr.class).forEach(mce -> {
            NodeList<Expression> argumentList = mce.getArguments();
            for (Expression expression : argumentList) {
                try {
                    expression.calculateResolvedType().describe();
                    normalArgumentMap.put(expression, true);
                } catch (Exception e) {
                    if (e.getMessage().contains(" in ")) {
                        abnormalArgumentMap.put(expression, true);
                    } else {
                        abnormalArgumentMap.put(expression, false);
                    }
                }
            }
        });
        //????????????????????????????????????
        cu2.findAll(ObjectCreationExpr.class).forEach(oce -> {
            NodeList<Expression> argumentList = oce.getArguments();
            for (Expression expression : argumentList) {
                try {
                    expression.calculateResolvedType().describe();
                    normalArgumentMap.put(expression, true);
                } catch (Exception e) {
                    if (e.getMessage().contains(" in ")) {
                        abnormalArgumentMap.put(expression, true);
                    } else {
                        abnormalArgumentMap.put(expression, false);
                    }
                }
            }
        });
        //????????????????????????????????????import
        List<String> externalMethodList = new ArrayList<>();
        List<MethodCallExpr> staticMethod = cu2.findAll(MethodCallExpr.class);
        for (MethodCallExpr mce : staticMethod) {
            if (!mce.getScope().isPresent()) {
                ImportDeclaration id = getImportDeclartion(importPackageList, mce.getNameAsString());
                if (id != null) {
                    String externalMethod = id.toString();
                    if (!externalMethod.contains(packageList.get(0).getNameAsString())) {
                        externalMethodList.add(externalMethod);
                    }
                }

            }
        }

        //?????????ClassOrInterfaceDeclaration,?????????????????????????????????
        ClassOrInterfaceDeclaration myClass = new ClassOrInterfaceDeclaration();

        myClass.setName("MUT");
        Set<String> importTypeSet = new HashSet<>();
        cu2.findAll(VariableDeclarator.class).forEach(v -> importTypeSet.add(v.getTypeAsString()));

        Map<Expression, Boolean> tempAbnormalArgumentMap = new HashMap<>(abnormalArgumentMap);
        for (Map.Entry<Expression, Boolean> entry : abnormalArgumentMap.entrySet()) {
            Expression var = entry.getKey();
            boolean isGlobal = entry.getValue();
            for (VariableTuple vt : variableList) {
                if (var.toString().equals(vt.getName()) && (isGlobal == vt.isGlobal())) {
                    if (vt.original.getClass().equals(FieldDeclaration.class)) {
                        FieldDeclaration fd = (FieldDeclaration) vt.original;
                        myClass.addMember(fd);
                        importTypeSet.add(fd.getElementType().toString());
                        tempAbnormalArgumentMap.remove(var);
                    }
                }
            }
        }

        //????????????????????????
//        List<MethodCallExpr> methodCallList = cu2.findAll(MethodCallExpr.class);
//        List<VariableDeclarator> variableDeclarators = cu2.findAll(VariableDeclarator.class);
//        //????????????????????????
//        List<ObjectCreationExpr> objectCreationExprList = cu2.findAll(ObjectCreationExpr.class);

        for (Map.Entry<Expression, Boolean> entry : tempAbnormalArgumentMap.entrySet()) {
            if (entry.getValue() == true) {
                importTypeSet.add(entry.getKey().toString());
            }
        }

        //??????import
        List<ImportDeclaration> importList = new ArrayList<>();
        for (String s : importTypeSet) {
            for (ImportDeclaration id : importPackageList) {
                String[] importNameArray = id.getNameAsString().split("\\.");
                String importName = importNameArray[importNameArray.length - 1];
                if (importName.equals(s)) {
                    importList.add(id);
                }
            }
        }
        List<String> writeFileImportList = new ArrayList<>();
        Map<String, String> importMap = new HashMap<>();
        for (ImportDeclaration i : importList) {
            String path = "D:/picasso/src/main/java/" + i.getNameAsString().replaceAll("\\.", "/");
            String newPath = findFile(path);
            if (newPath.length() == 0) {
                writeFileImportList.add(i.toString());
            }
            if (newPath.length() > 0) {
                String tempPath = newPath.replaceAll("\\.java", "");
                String variable = path.replaceAll(tempPath + "/", "");
                importMap.put(variable, newPath);
            }
        }
        for (Map.Entry<String, String> entry : importMap.entrySet()) {
            //????????????????????????????????????????????????????????????????????????
            if (entry.getKey().contains("/")) {
                EnumDeclaration enumDeclaration = findEnum(entry.getKey(), entry.getValue());
                if (enumDeclaration != null) {
                    myClass.addMember(enumDeclaration);
                }
            } else {
                Map<FieldDeclaration, ImportDeclaration> declarationMap = findVariable(entry.getKey(), entry.getValue());
                FieldDeclaration f = new FieldDeclaration();
                ImportDeclaration i = null;
                for (Map.Entry<FieldDeclaration, ImportDeclaration> entry1 : declarationMap.entrySet()) {
                    f = entry1.getKey();
                    i = entry1.getValue();
                }
                if (i != null) {
                    writeFileImportList.add(i.toString());
                }
                if (f != null) {
                    myClass.addMember(f);
                    externalFieldDeclarationList.add(f);
                }
            }
        }

//        //????????????????????????
//        for (MethodCallExpr mce : methodCallList) {
//            if (!mce.toString().contains("assert")) {
//                MethodModel methodModel = getTargetMethod(mce, variableDeclarators, externalFieldDeclarationList);
//                if (methodModel != null) {
//                    MethodDeclaration method = methodModel.getMethodBody();
//                    JavadocComment jc = new JavadocComment();
//                    String javaDocCommentString = method.hasJavaDocComment() ? method.getJavadocComment().get().getContent() + "\n" : "";
//                    javaDocCommentString +=
//                            "packagename:" + methodModel.getPackageName() + "\n" +
//                                    "classname:" + methodModel.getClassName() + "\n" +
//                                    "methodname:" + methodModel.getMethodName() + "\n" +
//                                    "parametertype:" + methodModel.getParameterTypeList() + "\n" +
//                                    "fromTest:" + methodModel.isFromTest();
//                    jc.setContent(javaDocCommentString);
//                    method.setJavadocComment(jc);
//                    methodDependency.add(method.toString());
//                    methodDeclarationSet.add(method);
//                }
//            }
//        }
//
//        //??????????????????????????????
//        for (ObjectCreationExpr oce : objectCreationExprList) {
//            String objectClass = oce.getTypeAsString();
//            ImportDeclaration id = getImportDeclartion(importPackageList, objectClass);
////            System.out.println(id);
//            if (id == null) {
//                //????????????????????????
//                for (ClassOrInterfaceDeclaration coid : innerClassList) {
//                    if (coid.getNameAsString().equals(objectClass)) {
//                        List<ConstructorDeclaration> constructorDeclarationList = coid.getConstructors();
//                        List<Expression> arguments = oce.getArguments();
//                        List<String> argumentsType = getVariableTypeList(arguments, variableDeclarators, externalFieldDeclarationList);
//                        for (ConstructorDeclaration cd : constructorDeclarationList) {
//                            List<Parameter> parameters = cd.getParameters();
//                            List<String> parametersType = new ArrayList<>();
//                            for (Parameter p : parameters) {
//                                parametersType.add(p.getTypeAsString());
//                            }
//                            String parameterTypeList;
//                            if (parametersType.size() == 0) {
//                                parameterTypeList = "()";
//                            } else {
//                                StringBuilder s = new StringBuilder("(" + parametersType.get(0));
//                                for (int i = 1; i < parametersType.size(); i++) {
//                                    s.append(",").append(parametersType.get(i));
//                                }
//                                s.append(")");
//                                parameterTypeList = s.toString();
//                            }
//                            JavadocComment jc = new JavadocComment();
//                            String javaDocCommentString = cd.hasJavaDocComment() ? cd.getJavadocComment().get().getContent() + "\n" : "";
//                            if (!javaDocCommentString.contains("packagename")) {
//                                javaDocCommentString +=
//                                        "packagename:" + packageList.get(0).getNameAsString() + "\n" +
//                                                "classname:" + FILE_PATH.split("/")[FILE_PATH.split("/").length - 1].split("\\.")[0] + "\n" +
//                                                "methodname:" + oce.getTypeAsString() + "\n" +
//                                                "parametertype:" + parameterTypeList + "\n" +
//                                                "fromTest:" + "true";
//                                jc.setContent(javaDocCommentString);
//                                cd.setJavadocComment(jc);
//                            }
//                        }
//                        constructorClassDeclarationSet.add(coid);
//                        break;
//                    }
//                }
//            } else {
//                //????????????????????????????????????
//                String packageName = packageList.get(0).getNameAsString();
//                List<Expression> arguments = oce.getArguments();
//                List<String> argumentsType = getVariableTypeList(arguments, variableDeclarators, externalFieldDeclarationList);
//                MethodModel methodModel = getExternalMethod(objectClass, packageName, objectClass, argumentsType);
//
//                if (methodModel != null) {
//                    System.out.println(methodModel.toString());
//                    ConstructorDeclaration method = methodModel.getConstructorBody();
//                    JavadocComment jc = new JavadocComment();
//                    System.out.println(method);
//                    String javaDocCommentString = method.hasJavaDocComment() ? method.getJavadocComment().get().getContent() + "\n" : "";
//                    javaDocCommentString +=
//                            "packagename:" + methodModel.getPackageName() + "\n" +
//                                    "classname:" + methodModel.getClassName() + "\n" +
//                                    "methodname:" + methodModel.getMethodName() + "\n" +
//                                    "parametertype:" + methodModel.getParameterTypeList() + "\n" +
//                                    "fromTest:" + methodModel.isFromTest();
//                    jc.setContent(javaDocCommentString);
//
//                    method.setJavadocComment(jc);
//                    methodDependency.add(method.toString());
//                    constructorDeclarationSet.add(method);
//                }
//            }
//        }

        String packageName = packageList.get(0).getNameAsString();

        MethodInfoTable methodInfoTable = null;
        //??????MethodDeclanation???????????????????????????
        if (cu2.findAll(MethodDeclaration.class).size() != 0) {
            MethodDeclaration myMethod = cu2.findAll(MethodDeclaration.class).get(0);
            String typeString = fileNameTypeList(myMethod);
            myClass.addMember(myMethod);
            String methodName = myMethod.getNameAsString();
            String className = FILE_PATH.split("/")[FILE_PATH.split("/").length - 1].split("\\.")[0];
            writeMUTClass(testOrProduct, packageName, methodName, typeString, writeFileImportList, externalMethodList, myClass);
//            methodInfoTable = constructMethodEntity(packageName, className, methodName, typeString, writeFileImportList, externalMethodList, methodDeclarationSet, constructorDeclarationSet, constructorClassDeclarationSet, myMethod);
        } else {
            ConstructorDeclaration myMethod = cu2.findAll(ConstructorDeclaration.class).get(0);
            String typeString = fileNameTypeList(myMethod);
            myClass.addMember(myMethod);
            String methodName = myMethod.getNameAsString();
            String className = FILE_PATH.split("/")[FILE_PATH.split("/").length - 1].split("\\.")[0];
            writeMUTClass(testOrProduct, packageName, methodName, typeString, writeFileImportList, externalMethodList, myClass);
//            methodInfoTable = constructMethodEntity(packageName, className, methodName, typeString, writeFileImportList, externalMethodList, methodDeclarationSet, constructorDeclarationSet, constructorClassDeclarationSet, myMethod);

        }


        return methodInfoTable;
    }

    public MethodInfoTable constructMethodEntity(String packageName, String className, String methodName, String typeString, List<String> writeFileImport, List<String> ExternalMethodList, Set<MethodDeclaration> MethodDependencySet, Set<ConstructorDeclaration> ConstructorDependencySet, Set<ClassOrInterfaceDeclaration> ConstructorClassDependencySet, MethodDeclaration myMethod) {
        int isMut = 0;
        if (myMethod.getAnnotationByName("Test").isPresent()) {
            isMut = 1;
        }
        MethodInfoTable methodInfoTable = new MethodInfoTable();
        String signature = packageName + "+" + className + "+" + methodName + "+" + typeString;
        String methodId = MD5Util.getMD5("000000" + signature);
        methodInfoTable.setMethodId(methodId);

        methodInfoTable.setMethodSignature(signature);

        methodInfoTable.setMethodName(methodName);

        methodInfoTable.setParameterTypes(typeString);

        methodInfoTable.setClassName(className);

        methodInfoTable.setPackageName(packageName);

        methodInfoTable.setMethodComment(myMethod.getAllContainedComments().toString() + "\n" + myMethod.getJavadoc().toString());

        String comment = myMethod.getAllContainedComments().toString() + "\n" + myMethod.getJavadoc().toString();
        comment = CommentAnalysis.extractCommentDescription(comment);
        List<TokenModel> tokenModelList = CommentAnalysis.commentNLPProcessing(comment);
        String keywords = "";
        if (tokenModelList.size() > 0) {
            keywords += tokenModelList.get(0).toString();
            for (int i = 1; i < tokenModelList.size(); i++) {
                keywords += "," + tokenModelList.get(i).toString();
            }
        }
        methodInfoTable.setMethodCommentKeywords(keywords);

        methodInfoTable.setMethodCode(myMethod.toString());

        methodInfoTable.setIsMut(isMut);

        writeFileImport.addAll(ExternalMethodList);
        methodInfoTable.setImportDependencies(writeFileImport.toString());

        List<String> methodDependency = new ArrayList<>();
        for (MethodDeclaration md : MethodDependencySet) {
            methodDependency.add(md.toString());
        }
        for (ConstructorDeclaration cd : ConstructorDependencySet) {
            methodDependency.add(cd.toString());
        }
        for (ClassOrInterfaceDeclaration coid : ConstructorClassDependencySet) {
            methodDependency.add(coid.toString());
        }
        methodInfoTable.setMethodDependencies(methodDependency.toString());

        methodInfoTable.setProjectId("000000");

        Timestamp time = new Timestamp(System.currentTimeMillis());
        methodInfoTable.setStorageTime(time);

        methodInfoTable.setReturnType(myMethod.getTypeAsString());

        return methodInfoTable;
    }

    public MethodInfoTable constructMethodEntity(String packageName, String className, String methodName, String typeString, List<String> writeFileImport, List<String> ExternalMethodList, Set<MethodDeclaration> MethodDependencySet, Set<ConstructorDeclaration> ConstructorDependencySet, Set<ClassOrInterfaceDeclaration> ConstructorClassDependencySet, ConstructorDeclaration myMethod) {
        int isMut = 0;
        if (myMethod.getAnnotationByName("Test").isPresent()) {
            isMut = 1;
        }
        MethodInfoTable methodInfoTable = new MethodInfoTable();
        String signature = packageName + "+" + className + "+" + methodName + "+" + typeString;
        String methodId = MD5Util.getMD5("000000" + signature);
        methodInfoTable.setMethodId(methodId);

        methodInfoTable.setMethodSignature(signature);

        methodInfoTable.setMethodName(methodName);

        methodInfoTable.setParameterTypes(typeString);

        methodInfoTable.setClassName(className);

        methodInfoTable.setPackageName(packageName);

        methodInfoTable.setMethodComment(myMethod.getAllContainedComments().toString() + "\n" + myMethod.getJavadoc().toString());

        String comment = myMethod.getAllContainedComments().toString() + "\n" + myMethod.getJavadoc().toString();
        comment = CommentAnalysis.extractCommentDescription(comment);
        List<TokenModel> tokenModelList = CommentAnalysis.commentNLPProcessing(comment);
        String keywords = "";
        if (tokenModelList.size() > 0) {
            keywords += tokenModelList.get(0).toString();
            for (int i = 1; i < tokenModelList.size(); i++) {
                keywords += "," + tokenModelList.get(i).toString();
            }
        }
        methodInfoTable.setMethodCommentKeywords(keywords);

        methodInfoTable.setMethodCode(myMethod.toString());

        methodInfoTable.setIsMut(isMut);

        writeFileImport.addAll(ExternalMethodList);
        methodInfoTable.setImportDependencies(writeFileImport.toString());

        List<String> methodDependency = new ArrayList<>();
        for (MethodDeclaration md : MethodDependencySet) {
            methodDependency.add(md.toString());
        }
        for (ConstructorDeclaration cd : ConstructorDependencySet) {
            methodDependency.add(cd.toString());
        }
        for (ClassOrInterfaceDeclaration coid : ConstructorClassDependencySet) {
            methodDependency.add(coid.toString());
        }
        methodInfoTable.setMethodDependencies(methodDependency.toString());

        methodInfoTable.setProjectId("000000");

        Timestamp time = new Timestamp(System.currentTimeMillis());
        methodInfoTable.setStorageTime(time);

        return methodInfoTable;
    }

    public boolean matchMethod(String methodName, List<String> typeList, MethodDeclaration methodDeclaration) {
        boolean flag = true;
        String name = methodDeclaration.getNameAsString();
        List<Parameter> parameterList = methodDeclaration.getParameters();
        List<String> parameterString = new ArrayList<>();
        for (Parameter p : parameterList) {
            parameterString.add(p.getTypeAsString());
        }
        if (!methodName.equals(name)) {
            flag = false;
        } else if (typeList.size() != parameterString.size()) {
            flag = false;
        } else {
            for (int i = 0; i < typeList.size(); i++) {
                if (typeList.get(i) != null) {
                    if (!typeList.get(i).equals(parameterString.get(i))) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    public MethodModel getTargetMethod(Expression expression, List<VariableDeclarator> variableDeclarators, List<FieldDeclaration> externalFieldDeclarationList) throws FileNotFoundException {
        MethodModel targetMethodModel = null;
        if (expression.asMethodCallExpr().getScope().isPresent()) {
            //???????????????+????????????
            Expression object = expression.asMethodCallExpr().getScope().get();
            String methodName = expression.asMethodCallExpr().getNameAsString();
            List<Expression> arguments = expression.asMethodCallExpr().getArguments();
            List<String> newTypeList = getVariableTypeList(arguments, variableDeclarators, externalFieldDeclarationList);
            if (Character.isUpperCase(object.toString().charAt(0))) {
                //???????????????????????????????????????????????????????????????class.xxx()?????????import
                ImportDeclaration id = getImportDeclartion(importPackageList, object.toString());
                if (id != null) {
                    String packageName = getImportDeclartion(importPackageList, object.toString()).getNameAsString().split("." + object.toString())[0];
                    targetMethodModel = getExternalMethod(methodName, packageName, object.toString(), newTypeList);
                } else {
                    //???????????????????????????
                    String packageName = packageList.get(0).getNameAsString();
                    targetMethodModel = getExternalMethod(methodName, packageName, object.toString(), newTypeList);
                }
            } else {
                //??????????????????????????????????????????????????????????????????+???????????????xxx.xxx()???????????????????????????????????????????????????????????????
                if (!object.isMethodCallExpr()) {
                    //????????????xxx().xxx(),??????xxx.xxx().??????????????????????????????
                    VariableDeclarator vd = getVariableInitialize(object, globelVariableList, variableDeclarators, externalFieldDeclarationList);
                    if (vd != null) {
                        String className = getVariableInitialize(object, globelVariableList, variableDeclarators, externalFieldDeclarationList).getType().toString();
                        ImportDeclaration id = getImportDeclartion(importPackageList, className);
                        if (id != null) {//?????????????????????
                            String packageName = id.getNameAsString().split("." + className)[0];
                            targetMethodModel = getExternalMethod(methodName, packageName, className, newTypeList);
                        } else {
                            //?????????????????????
                            String packageName = packageList.get(0).getNameAsString();
                            targetMethodModel = getExternalMethod(methodName, packageName, className, newTypeList);
                        }
                    }
                }
            }
        } else {
            String methodName = expression.asMethodCallExpr().getNameAsString();
            //??????????????????+????????????
            for (MethodDeclaration md : normalMethodList) {//???????????????????????????
                String packageName = packageList.get(0).getNameAsString();
                String className = FILE_PATH.split("/")[FILE_PATH.split("/").length - 1].split("\\.")[0];
                List<Expression> arguments = expression.asMethodCallExpr().getArguments();
                List<String> newTypeList = getVariableTypeList(arguments, variableDeclarators, externalFieldDeclarationList);
                if (matchMethod(methodName, newTypeList, md)) {
                    //?????????????????????
                    targetMethodModel = new MethodModel(packageName, className, methodName, newTypeList, md, true);
                }
            }
            if (targetMethodModel == null) {
                //?????????????????????import???
                ImportDeclaration id = getImportDeclartion(importPackageList, methodName);
                if (id != null) {
                    String importString = id.getNameAsString().split("." + methodName)[0];
                    String className = importString.split("\\.")[importString.split("\\.").length - 1];
                    String packageName = importString.split("." + className)[0];
                    List<Expression> arguments = expression.asMethodCallExpr().getArguments();
                    List<String> newTypeList = getVariableTypeList(arguments, variableDeclarators, externalFieldDeclarationList);
                    targetMethodModel = getExternalMethod(methodName, packageName, className, newTypeList);
                }
            }
        }
        return targetMethodModel;
    }

    public Node findParentClass(Node node){
        Node result = null;
        if(!node.findAll(ClassOrInterfaceDeclaration.class).isEmpty()){
            return node;
        }
        if(node.findAll(ClassOrInterfaceDeclaration.class).isEmpty()){
            result = node.getParentNode().get();
        }
        return findParentClass(result);
    }

    public List<String> getVariableTypeList(List<Expression> arguments, List<VariableDeclarator> variableDeclarators, List<FieldDeclaration> externalFieldDeclarationList) {
        List<String> variableTypeList = new ArrayList<>();
        for (Expression expression1 : arguments) {
            VariableDeclarator vd2 = getVariableInitialize(expression1, globelVariableList, variableDeclarators, externalFieldDeclarationList);
            if (vd2 != null) {
                variableTypeList.add(vd2.getTypeAsString());
            } else {
                String basicType = getBasicDataType(expression1);
                if (basicType != null) {
                    variableTypeList.add(getBasicDataType(expression1));
                } else {

                }
            }
        }
        return variableTypeList;
    }

    public MethodModel getExternalMethod(String targetMethodCallName, String packageName, String className, List<String> typeList) throws FileNotFoundException {
        //targetMethodCallName???????????????
        if (!targetMethodCallName.equals(className)) {
            //??????????????????
            MethodDeclaration externalMethod = null;
            List<String> MUTList = new ArrayList<>();
            MUTList = findFileList(new File("MUTClass/"), MUTList);
            boolean fromTest = false;
            for (String s : MUTList) {
                s = s.split("\\\\")[1];
                String path = "+" + packageName + "+" + className + "+" + targetMethodCallName;
                if (s.contains(path)) {
                    if (judgeArguments(s, typeList)) {
                        String testOrProd = s.split("\\+")[0];
                        if (testOrProd.equals("test")) {
                            fromTest = true;
                        } else if (testOrProd.equals("prod")) {
                            fromTest = false;
                        }
                        System.out.println(s);
                        CompilationUnit cu2 = constructCompilationUnit(null, "MUTClass/" + s);
                        List<MethodDeclaration> methodDeclarationList = cu2.findAll(MethodDeclaration.class);
                        for (MethodDeclaration md : methodDeclarationList) {
                            if (md.getNameAsString().equals(targetMethodCallName)) {
                                externalMethod = md;
                            }
                        }

                    }
                }
            }
            if (externalMethod != null) {
                return new MethodModel(packageName, className, targetMethodCallName, typeList, externalMethod, fromTest);
            }
        } else {
            //???????????????
            ConstructorDeclaration constructorDeclaration = null;
            List<String> MUTList = new ArrayList<>();
            MUTList = findFileList(new File("MUTClass/"), MUTList);
            boolean fromTest = false;
            for (String s : MUTList) {
                s = s.split("\\\\")[1];
                String path = "+" + packageName + "+" + className + "+" + targetMethodCallName;
                if (s.contains(path)) {
                    if (judgeArguments(s, typeList)) {
                        String testOrProd = s.split("\\+")[0];
                        if (testOrProd.equals("test")) {
                            fromTest = true;
                        } else if (testOrProd.equals("prod")) {
                            fromTest = false;
                        }
                        CompilationUnit cu2 = constructCompilationUnit(null, "MUTClass/" + s);
                        List<ConstructorDeclaration> constructorDeclarationList = cu2.findAll(ConstructorDeclaration.class);
                        for (ConstructorDeclaration md : constructorDeclarationList) {
                            if (md.getNameAsString().equals(targetMethodCallName)) {

                                constructorDeclaration = md;
                            }
                        }

                    }
                }
            }
            if (constructorDeclaration != null) {
                return new MethodModel(packageName, className, targetMethodCallName, typeList, constructorDeclaration, fromTest);
            }
        }

        return null;
    }

    public String getBasicDataType(Expression expression) {
        String type = null;
        if (expression.isIntegerLiteralExpr()) {
            type = "int";
        } else if (expression.isLongLiteralExpr()) {
            type = "long";
        } else if (expression.isCharLiteralExpr()) {
            type = "char";
        } else if (expression.isDoubleLiteralExpr()) {
            type = "double";
        } else if (expression.isStringLiteralExpr()) {
            type = "String";
        } else if (expression.isBooleanLiteralExpr()) {
            type = "boolean";
        }
        return type;
    }

    public VariableDeclarator getVariableInitialize(Expression expression, List<FieldDeclaration> fieldDeclarations, List<VariableDeclarator> variableDeclarators, List<FieldDeclaration> externalFieldDeclarationList) {
        VariableDeclarator result = null;
        for (VariableDeclarator vd : variableDeclarators) {
            if (vd.getNameAsExpression().equals(expression)) {
                result = vd;
            }
        }
        for (FieldDeclaration fd : fieldDeclarations) {
            if (fd.getVariable(0).getNameAsExpression().equals(expression)) {
                result = fd.getVariable(0);
            }
        }
        for (FieldDeclaration fd : externalFieldDeclarationList) {
            if (fd.getVariables().size() > 0) {
                if (fd.getVariable(0).getNameAsExpression().equals(expression)) {
                    result = fd.getVariable(0);
                }
            }

        }
        return result;
    }

    //???????????????????????????
    public List<String> findFileList(File dir, List<String> fileNames) {
        String[] files = dir.list();// ??????????????????????????????????????????
        if (files != null) {
            for (String s : files) {// ???????????????????????????????????????
                File file = new File(dir, s);
                if (file.isFile()) {// ????????????
                    fileNames.add(dir + "\\" + file.getName());// ????????????????????????
                } else {// ???????????????
                    findFileList(file, fileNames);// ????????????????????????
                }
            }
        }
        return fileNames;
    }

    public boolean judgeArguments(String filepath, List<String> arguments) {
        boolean flag = true;
        for (String s : arguments) {
            if (s != null) {//???????????????null???
                if (!filepath.contains(s)) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public void writeMUTClass(String testOrProduct, String packageName, String methodName, String typeString, List<String> writeFileImportList, List<String> externalMethodList, ClassOrInterfaceDeclaration myClass) {
        try {
            String[] filenameArray = FILE_PATH.split("/");
            String filename = filenameArray[filenameArray.length - 1].split("\\.")[0];

            String outputFileName0 = "MUTClass/" + testOrProduct + "+" + packageName + "+" + filename + "+" + methodName + "+" + typeString + ".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName0));
            for (String s : writeFileImportList) {
                bw.write(s);
            }
            for (String s : externalMethodList) {
                bw.write(s);
            }
            bw.write(myClass.toString());
            bw.close();
            System.err.println("??????????????????");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fileNameTypeList(MethodDeclaration myMethod) {
        NodeList<Parameter> parameters = myMethod.getParameters();
        List<String> typeList = new ArrayList<>();
        for (Parameter p : parameters) {
            typeList.add(p.getTypeAsString());
        }
        String typeString = "(";
        if (typeList.size() > 0) {
            String s1 = typeList.get(0).replaceAll("\\?", "");
            s1 = s1.replaceAll("<", "");
            s1 = s1.replaceAll(">", "");
            typeString += s1;
            for (int i = 1; i < typeList.size(); i++) {
                String s2 = typeList.get(i).replaceAll("\\?", "");
                s2 = s2.replaceAll("<", "");
                s2 = s2.replaceAll(">", "");
                typeString = typeString + "," + s2;
            }

        }
        typeString += ")";
        return typeString;
    }

    public String fileNameTypeList(ConstructorDeclaration myMethod) {
        NodeList<Parameter> parameters = myMethod.getParameters();
        List<String> typeList = new ArrayList<>();
        for (Parameter p : parameters) {
            typeList.add(p.getTypeAsString());
        }
        String typeString = "(";
        if (typeList.size() > 0) {
            String s1 = typeList.get(0).replaceAll("\\?", "");
            s1 = s1.replaceAll("<", "");
            s1 = s1.replaceAll(">", "");
            typeString += s1;
            for (int i = 1; i < typeList.size(); i++) {
                String s2 = typeList.get(i).replaceAll("\\?", "");
                s2 = s2.replaceAll("<", "");
                s2 = s2.replaceAll(">", "");
                typeString = typeString + "," + s2;
            }

        }
        typeString += ")";
        return typeString;
    }

    public EnumDeclaration findEnum(String variable, String filePath) throws FileNotFoundException {
        CompilationUnit cu = constructCompilationUnit(null, filePath);
        List<EnumDeclaration> enumDeclarationList = cu.findAll(EnumDeclaration.class);
        for (EnumDeclaration ed : enumDeclarationList) {
            if (variable.contains(ed.getName().toString())) {
                return ed;
            }
        }
        return null;
    }

    public ImportDeclaration getImportDeclartion(List<ImportDeclaration> importDeclarations, String objectType) {
        for (ImportDeclaration id : importDeclarations) {
            if (id.getNameAsString().contains("." + objectType)) {
                //??????????????????import????????????
                String[] s = id.getNameAsString().split("." + objectType);
                //???????????????????????????????????????????????????????????????
                if (s.length == 1) {
                    return id;
                } else if (!String.valueOf(s[1].charAt(0)).matches("[a-zA-Z]+")) {
                    return id;
                }
            }
        }
        return null;
    }

    public Map<FieldDeclaration, ImportDeclaration> findVariable(String variable, String filePath) throws FileNotFoundException {
//        System.out.println(filePath);
        CompilationUnit cu = constructCompilationUnit(null, filePath);
        List<FieldDeclaration> newfieldDeclarationList = cu.findAll(FieldDeclaration.class);
        List<ImportDeclaration> newImportDeclarationList = cu.findAll(ImportDeclaration.class);
        Set<String> newImportTypeSet = new HashSet<>();
        ImportDeclaration resultImportDeclaration = null;
        FieldDeclaration resultFieldDeclaration = new FieldDeclaration();
        for (FieldDeclaration fd : newfieldDeclarationList) {
            List<VariableDeclarator> variableDeclaratorList = fd.getVariables();
            for (VariableDeclarator vd : variableDeclaratorList) {
                if (vd.getNameAsString().equals(variable)) {
                    newImportTypeSet.add(fd.getElementType().toString());
                    resultFieldDeclaration = fd;
                }
            }
        }

        for (String s : newImportTypeSet) {
            for (ImportDeclaration id : newImportDeclarationList) {
                String[] importNameArray = id.getNameAsString().split("\\.");
                String importName = importNameArray[importNameArray.length - 1];
                if (importName.equals(s)) {
                    resultImportDeclaration = id;
                }
            }
        }
        Map<FieldDeclaration, ImportDeclaration> resultMap = new HashMap<>();
        resultMap.put(resultFieldDeclaration, resultImportDeclaration);
        return resultMap;
    }

    public String findFile(String sourcePath) {
        String filePath = sourcePath + ".java";
        File file = new File(filePath);
        String[] pathArray = sourcePath.split("/");
        if (file.exists()) return filePath;
        if (pathArray.length == 1) return "";
        String newPathString = "";
        if (!file.exists() && pathArray.length > 1) {
            StringBuilder newPath = new StringBuilder();
            for (int i = 0; i < pathArray.length - 1; i++) {
                newPath.append(pathArray[i]).append("/");
            }
            newPathString = newPath.toString().substring(0, newPath.length() - 1);
        }
        return findFile(newPathString);
    }


}
