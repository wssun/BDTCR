package com.bdtcr.tsearcher;


import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
  * @Author sunweisong
  * @Date 2020/4/16 6:44 PM
  */
public class LiteralTextAnalysis {

    /**
      * Measure the distance between literal texts of two code blocks.
      * @param codeBlock1
      * @param codeBlock2
      * @return double
      * @date 2020/4/18 5:43 PM
      * @author sunweisong
      */
    public static double measureDLBetweenTwoCodeBlocks(String codeBlock1, String codeBlock2) {
        codeBlock1 = codeBlock1.replaceAll("\n", " ");
        codeBlock2 = codeBlock2.replaceAll("\n", " ");
        codeBlock1 = codeBlock1.replaceAll("\t", " ");
        codeBlock2 = codeBlock2.replaceAll("\t", " ");
        String str1 = "'" + codeBlock1 + "'";
        String str2 = "'" + codeBlock2 + "'";
        long startTime = System.currentTimeMillis();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("from functools import reduce");
        interpreter.exec("import difflib as diff");
        interpreter.exec("s1 = diff.SequenceMatcher(lambda x: x==\" \", " + str1 + ", " + str2 + ", autojunk=True)");
        interpreter.exec("matching_blocks1 = s1.get_matching_blocks()");
        interpreter.exec("s2 = diff.SequenceMatcher(lambda x: x==\" \", " + str2 + ", " + str1 + ", autojunk=True)");
        interpreter.exec("matching_blocks2 = s2.get_matching_blocks()");
        interpreter.exec("matches1 = reduce(lambda sum, triple: sum + triple[-1], matching_blocks1, 0)");
        interpreter.exec("matches2 = reduce(lambda sum, triple: sum + triple[-1], matching_blocks2, 0)");
        interpreter.exec("matches = max(matches1, matches2)");
        interpreter.exec("total = len(s1.a) + len(s1.b)");
        interpreter.exec("sim = diff._calculate_ratio(matches, total)");
        long endTime = System.currentTimeMillis();
        System.out.println("Cost Time: " + (endTime - startTime) + "ms");
        double sim = interpreter.getLocals().__finditem__("sim").asDouble();
        return 1 - sim;
    }

    public static void main(String[] args) {
        String method1 = "void bubbleSort(int arr[]) {\n" +
                "    int n = arr.length;\n" +
                "    for (int i = 0; i < n-1; i++)\n" +
                "        for (int j = 0; j < n-i-1; j++)\n" +
                "            if (arr[j] > arr[j+1]) {\n" +
                "                int temp = arr[j];\n" +
                "                arr[j] = arr[j+1];\n" +
                "                arr[j+1] = temp;\n" +
                "            }\n" +
                "}";
        String method2 = "void bubbleSort(int[] array) {\n" +
                "    int n = array.length;\n" +
                "    int temp = 0;\n" +
                "    for(int i=0; i < n; i++)\n" +
                "        for(int j=1; j < (n-i); j++)\n" +
                "            if(array[j-1] > array[j]) {\n" +
                "                temp = array[j-1];\n" +
                "                array[j-1] = array[j];\n" +
                "                array[j] = temp;\n" +
                "            }\n" +
                "}";
        int start = method1.indexOf("{");
        int end = method1.lastIndexOf("}");
        method1 = method1.substring(start, end + 1).trim();
        start= method2.indexOf("{");
        end = method2.lastIndexOf("}");
        method2 = method2.substring(start, end + 1).trim();
        double dl = measureDLBetweenTwoCodeBlocks(method1, method2);
        System.out.println(dl);
    }
}
