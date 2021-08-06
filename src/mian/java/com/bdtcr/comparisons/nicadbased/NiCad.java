package com.bdtcr.comparisons.nicadbased;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;

/**
  * @Author sunweisong
  * @Date 2020/4/20 3:31 PM
  */
public class NiCad {

    final static String NiCAD_PATH = "/Users/sunweisong/NiCad-5.2";   // NiCad 安装目录

    public static void main(String[] args) {
        String method1 = "void attach(Action action) {\n" +
                "        boolean loggingEnabled = picasso.loggingEnabled;\n" +
                "        Request request = action.request;\n" +
                "        if (this.action == null) {\n" +
                "            this.action = action;\n" +
                "            if (loggingEnabled) {\n" +
                "                if (actions == null || actions.isEmpty()) {\n" +
                "                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), \"to empty hunter\");\n" +
                "                } else {\n" +
                "                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, \"to \"));\n" +
                "                }\n" +
                "            }\n" +
                "            return;\n" +
                "        }\n" +
                "        if (actions == null) {\n" +
                "            actions = new ArrayList<>(3);\n" +
                "        }\n" +
                "        actions.add(action);\n" +
                "        if (loggingEnabled) {\n" +
                "            log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, \"to \"));\n" +
                "        }\n" +
                "        Priority actionPriority = action.request.priority;\n" +
                "        if (actionPriority.ordinal() > priority.ordinal()) {\n" +
                "            priority = actionPriority;\n" +
                "        }\n" +
                "        dtest();\n" +
                "    }";
        String method2 = "void attach(Action action) {\n" +
                "        boolean loggingEnabled = picasso.loggingEnabled;\n" +
                "        Request request = action.request;\n" +
                "        if (this.action == null) {\n" +
                "            this.action = action;\n" +
                "            if (loggingEnabled) {\n" +
                "                if (actions == null || actions.isEmpty()) {\n" +
                "                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), \"to empty hunter\");\n" +
                "                } else {\n" +
                "                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, \"to \"));\n" +
                "                }\n" +
                "            }\n" +
                "            return;\n" +
                "        }\n" +
                "        if (actions == null) {\n" +
                "            actions = new ArrayList<>(3);\n" +
                "        }\n" +
                "        actions.add(action);\n" +
                "        if (loggingEnabled) {\n" +
                "            log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, \"to \"));\n" +
                "        }\n" +
                "        Priority actionPriority = action.request.priority;\n" +
                "        if (actionPriority.ordinal() > priority.ordinal()) {\n" +
                "            priority = actionPriority;\n" +
                "        }\n" +
                "        dtest();\n" +
                "    }";
        String method3 = "void attach(Action action) {\n" +
                "         System.out.println(\"hello world\");\n" +
                "        boolean logging = picasso.logging;\n" +
                "        Request request = action.request;\n" +
                "        if (this.action == null) {\n" +
                "            this.action = action;\n" +
                "            if (loggingEnabled) {\n" +
                "                if (actions == null || actions.isEmpty()) {\n" +
                "                } else {\n" +
                "                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, \"to \"));\n" +
                "                }\n" +
                "            }\n" +
                "            return;\n" +
                "        }\n" +
                "    }";

        double similarity = judgeTwoMethodIsClone(method1, method2);
        if (similarity == -1) {
            System.out.println("The method1 and method3 are not clone pair.");
        } else {
            System.out.println("The method1 and method2 are not clone pair, and their similarity is: " + similarity);
        }
    }

    /**
      * Judge whether two methods are clone pair.
      * @param method1Code
      * @param method2Code
      * @return double
      * @date 2020/4/20 9:24 PM
      * @author sunweisong
      */
    public static double judgeTwoMethodIsClone(String method1Code, String method2Code) {
        String tempFolderPath = NiCAD_PATH + File.separator + "temp_folder";
        File tempFolder = new File(tempFolderPath);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        String srcFolderPath = tempFolderPath + File.separator + "src";
        File srcFolder = new File(srcFolderPath);
        if (!srcFolder.exists()) {
            srcFolder.mkdir();
        } else {
            deleteOldDetectionFile(tempFolderPath);
        }
        String method1CodeFilePath = srcFolderPath + File.separator + "TempMethod1Code.java";
        String method2CodeFilePath = srcFolderPath + File.separator + "TempMethod2Code.java";
        FileUtil.writeMethodCodeToFile(method1Code, method1CodeFilePath);
        FileUtil.writeMethodCodeToFile(method2Code, method2CodeFilePath);
        int exitValue = execute("cd " + NiCAD_PATH + " && ./nicad5 functions java " + srcFolderPath + " default-report");
        if (exitValue != 0) {
            System.err.println("NiCad Error");
            return -1;
        }
        String xmlFilePath = tempFolderPath + "/src_functions-blind-clones/src_functions-blind-clones-0.10.xml";
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            System.out.println("The xml file does not exist!");
            return -1;
        }
        try {
            Document document = (new SAXReader()).read(xmlFile);
            Element root = document.getRootElement();
            Element cloneElement = root.element("clone");
            if (cloneElement == null) {
                return -1;
            }
            Attribute similarityAttribute = cloneElement.attribute("similarity");
            String similarityValue = similarityAttribute.getValue();
            return Integer.parseInt(similarityValue) / 100.0;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
      * Delete the previous detection file.
      * @param tempFolderPath
      * @return void
      * @date 2020/4/20 9:41 PM
      * @author sunweisong
      */
    private static void deleteOldDetectionFile(String tempFolderPath) {
        File tempFolder = new File(tempFolderPath);
        File[] fileList = tempFolder.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                file.delete();
                continue;
            }
            String fileName = file.getName();
            if (!fileName.contains("src_functions")) {
                continue;
            }
            File[] subFileList = file.listFiles();
            for (File subFile : subFileList) {
                subFile.delete();
            }
            file.delete();
        }
    }

    /**
      * 使用 mac(unix) 脚本命令执行 NiCad 5 克隆检测。
      * @param command
      * @return int
      * @date 2020/4/20 9:42 PM
      * @author sunweisong
      */
    public static int execute(String command) {
        String[] cmd = {"/bin/bash"};
        Runtime rt = Runtime.getRuntime();
        int exitValue = 0;
        Process process = null;
        BufferedReader br = null;
        InputStream fis = null;
        BufferedWriter bw = null;
        OutputStream os = null;
        StringBuffer cmdOut = new StringBuffer();
        String line;
        try {
            process = rt.exec(cmd);
            os = process.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(command);
            bw.flush();
            bw.close(); // 必须先关掉 bw

            fis = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(fis));
            while ((line = br.readLine()) != null) {
                cmdOut.append(line).append(System.getProperty("line.separator"));
            }
            /** waitFor() 的作用在于 java 程序是否等待 Terminal 执行脚本完毕~ */
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                exitValue = process.exitValue();
                process.destroy();
            }
        }
        if (exitValue != 0) {
            System.err.println(cmdOut.toString());
            cmdOut = null;
        }
        return exitValue;
    }


}
