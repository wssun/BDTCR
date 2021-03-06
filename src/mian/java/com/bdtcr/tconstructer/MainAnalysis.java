package com.bdtcr.tconstructer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainAnalysis {
    static List<String> filePathList = new ArrayList<>();
    static List<String> testFilePathList = new ArrayList<>();
    static String SRC_PATH = "xxx:/xxx/src";

    public static void main(String[] args) throws IOException {
        List<String> fileNames = new ArrayList<String>();
        MainAnalysis mainAnalysis = new MainAnalysis();
        mainAnalysis.findFileList(new File(SRC_PATH), fileNames);
        for (String value : fileNames) {
            if (value.contains(".java") && value.contains("src\\main")) {
                value = value.replaceAll("\\\\", "/");
                filePathList.add(value);
            }
            if (value.contains(".java") && value.contains("src\\test")) {
                value = value.replaceAll("\\\\", "/");
                testFilePathList.add(value);
            }
        }
        for (int j = 0; j < testFilePathList.size(); j++) {
            TestFileAnalysis testFileAnalysis = new TestFileAnalysis(testFilePathList.get(j));
            List<String> originalTestFragmentClassList = testFileAnalysis.getOriginalTestFragment();
            for (int i = 0; i < originalTestFragmentClassList.size(); i++) {
                    testFileAnalysis.dependencyAnalysis(originalTestFragmentClassList.get(i));
            }

        }
        for (int j = 0; j < filePathList.size(); j++) {
            MethodCollector methodCollector = new MethodCollector(filePathList.get(j));
            List<String> originalMethodClassList = methodCollector.getOriginalMethod();
            for (int i = 0; i < originalMethodClassList.size(); i++) {
                methodCollector.methodExtraction(0, originalMethodClassList.get(i));
            }
        }
        for (int j = 0; j < testFilePathList.size(); j++) {
            MethodCollector methodCollector = new MethodCollector(testFilePathList.get(j));
            List<String> originalMethodClassList = methodCollector.getOriginalMethod();
            for (int i = 0; i < originalMethodClassList.size(); i++) {
                methodCollector.methodExtraction(1, originalMethodClassList.get(i));
            }
        }

        File file = new File("Test Fragment/");
        if(!file.exists()){
            file.mkdir();
        }
        File file1 = new File("Test Class/");
        if(!file1.exists()){
            file1.mkdir();
        }
        File file2 = new File("MUT/");
        if(!file2.exists()){
            file2.mkdir();
        }

    }

    //???????????????????????????
    public void findFileList(File dir, List<String> fileNames) {
        if (!dir.exists() || !dir.isDirectory()) {// ????????????????????????
            return;
        }
        String[] files = dir.list();// ??????????????????????????????????????????
        if (files != null) {
            for (String s : files) {// ???????????????????????????????????????
                File file = new File(dir, s);
                if (file.isFile()) {// ????????????
                    fileNames.add(dir + File.separator + file.getName());// ????????????????????????
                } else {// ???????????????
                    findFileList(file, fileNames);// ????????????????????????
                }
            }
        }
    }
}
