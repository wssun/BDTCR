package com.bdtcr.comparisons.nicadbased;

import java.io.*;
import java.util.List;

/**
  * @Author sunweisong
  * @Date 2019/9/20 10:27 AM
  */
public class FileUtil {


    /**
      *
      * @param methodCode
      * @return filePath
      * @throws
      * @date 2020/4/20 3:34 PM
      * @author sunweisong
      */
    public static void writeMethodCodeToFile(String methodCode, String filePath) {
        StringBuffer codeStringBuffer = new StringBuffer("class MethodClass {\n");
        codeStringBuffer.append(methodCode);
        codeStringBuffer.append("}");
        BufferedWriter bw = null;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(codeStringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                codeStringBuffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param stringList
     * @param targetFile
     * @return void
     * @date 2018/7/25 下午4:12
     * @author sunweisong
     */
    public static void writeStringListToTargetFile(List<String> stringList, File targetFile) {
        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(targetFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (String record : stringList) {
                bufferedWriter.write(record + System.lineSeparator());
            }
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Read file content to string.
     *
     * @param file
     * @return String
     * @throws
     * @date 2018/4/9 下午8:33
     * @author sunweisong
     */
    public static String readFileContentToString(File file) {
        StringBuffer stringBuffer = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(" " + line + "\n");
            }
        } catch (IOException e) {
            e.getStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                    bufferedReader = null;
                }
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String content = stringBuffer.toString();
        stringBuffer = null;
        return content.trim();
    }
}
