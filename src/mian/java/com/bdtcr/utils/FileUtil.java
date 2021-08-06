package com.bdtcr.utils;

import com.bdtcr.models.RecommendedTestCaseModel;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
  * @Author sunweisong
  * @Date 2019/9/20 10:27 AM
  */
public class FileUtil {

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i],newPath + "/" + file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/6/28 3:26 PM
      * @author sunweisong
      */
    public static void copyFileUsingFileChannels(File source, File destination) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(destination).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputChannel != null) {
                    inputChannel.close();
                    inputChannel = null;
                }
                if (outputChannel != null) {
                    outputChannel.close();
                    outputChannel = null;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/7/27 2:09 PM
      * @author sunweisong
      */
    public static File[] findFilesWithSpecifiedSuffixInTargetDirectory(File targetDirectory
            , String specifiedFileSuffix) {
        List<File> fileList = new ArrayList<>();
        findFileListInTargetDirectory(targetDirectory, specifiedFileSuffix, fileList);
        if (fileList.isEmpty()) {
            return null;
        }
        int fileNumber = fileList.size();
        File[] fileArray = new File[fileNumber];
        for (int i = 0; i < fileNumber; i++) {
            fileArray[i] = fileList.get(i);
        }
        return fileArray;
    }

    /**
      *
      * @param targetDirectory
      * @param specifiedFileSuffix the required file suffix.
      * @param fileList
      * @return fileList
      * @date 2020/7/27 2:22 PM
      * @author sunweisong
      */
    public static void findFileListInTargetDirectory(File targetDirectory
            , String specifiedFileSuffix, List<File> fileList) {
        if (!targetDirectory.isDirectory()) {
            return;
        }
        File[] fileArray = targetDirectory.listFiles();
        for (File file : fileArray) {
            if (file.isFile()) {
                if (specifiedFileSuffix == null) {
                    fileList.add(file);
                    continue;
                }
                String fileName = file.getName();
                if (!fileName.endsWith(specifiedFileSuffix)) {
                    continue;
                }
                fileList.add(file);
                continue;
            }
            findFileListInTargetDirectory(file, specifiedFileSuffix, fileList);
        }
    }

    /**
      *
      * @param directory
      * @param filePathList
      * @return
      * @date 2020/5/26 11:48 PM
      * @author sunweisong
      */
    public static void findFilePathList(File directory, List<String> filePathList) {
        if (!directory.isDirectory()) {
            return;
        }
        File[] fileArray = directory.listFiles();
        for (File file : fileArray) {
            if (file.isFile()) {
                filePathList.add(file.getAbsolutePath());
            } else {
                findFilePathList(file, filePathList);
            }
        }
    }


    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/5/26 11:48 PM
      * @author sunweisong
      */
    public static void writeMethodListToTargetDirectory(List<String> jsonStringList
            , String targetDirectoryPath, int offset) {
        File targetDirectory = new File(targetDirectoryPath);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        File targetFile;
        for (int i = 0; i < jsonStringList.size(); i++) {
            String targetFilePath = targetDirectoryPath + File.separator + "mut_" + (offset + i) + ".json";
            targetFile = new File(targetFilePath);
            try {
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                fileWriter = new FileWriter(targetFile, false);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonStringList.get(i));
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
    }


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
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/15 22:58
      * @author sunweisong
      */
    public static void writeStringSetToTargetFile(Set<String> stringSet, File targetFile) {
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
            fileWriter = new FileWriter(targetFile, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            int count = 0;
            int totalNumber = stringSet.size();
            for (String record : stringSet) {
                count++;
                if (count < totalNumber) {
                    bufferedWriter.write(record + System.lineSeparator());
                } else {
                    bufferedWriter.write(record);
                }
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

    /**
      * Write the recommended test cases to files.
      * @param recommendedTestCaseModelList
      * @param targetDirectory
      * @param strategy
      * @return void
      * @date 2020/4/23 12:15 AM
      * @author sunweisong
      */
    public static void writeRecommendedTestCaseToFile(List<RecommendedTestCaseModel> recommendedTestCaseModelList
            , String targetDirectory, String strategy) {
        int size = recommendedTestCaseModelList.size();
        StringBuffer stringBuffer;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        File targetFile;
        RecommendedTestCaseModel testCase;
        String testCaseCode, testTargetComment, testTargetCode, testTargetSignature;
        String jsonString;
        String targetFilePath;
        double dl, dg, db, dc, bd, similarity;
        for (int i = 0; i < size; i++) {
            stringBuffer = new StringBuffer("{");
            testCase = recommendedTestCaseModelList.get(i);
            testCaseCode = testCase.getTc_code();
            stringBuffer.append("\"tc_code\":\"" + testCaseCode + "\",");
            testTargetComment = testCase.getMut_comment();
            stringBuffer.append("\"tt_comment\":\"" + testTargetComment + "\",");
            testTargetCode = testCase.getMut_code();
            stringBuffer.append("\"tt_code\":\"" + testTargetCode + "\",");
            if ("TBooster".equals(strategy)) {
                dl = testCase.getDL();
                stringBuffer.append("\"dl\":\"" + dl + "\",");
                dg = testCase.getDG();
                stringBuffer.append("\"dg\":\"" + dg + "\",");
                db = testCase.getDB();
                stringBuffer.append("\"db\":\"" + db + "\",");
                dc = testCase.getDC();
                stringBuffer.append("\"dc\":\"" + dc + "\",");
                bd = testCase.getBD();
                stringBuffer.append("\"bd\":\"" + bd + "\"");
            }
            if ("NiCadBased".equals(strategy)) {
                similarity = testCase.getSimilarity();
                stringBuffer.append("\"similarity\":\"" + similarity + "\"");
            }
            if ("TestTenderer".equals(strategy) || "Baseline".equals(strategy)) {
                testTargetSignature = testCase.getMut_signature();
                stringBuffer.append("\"tt_signature\":\"" + testTargetSignature + "\"");
            }
            stringBuffer.append("}");
            jsonString = stringBuffer.toString();
            targetFilePath = targetDirectory + File.separator + "tc_" + (i + 1) + ".json";
            targetFile = new File(targetFilePath);
            try {
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                fileWriter = new FileWriter(targetFile, false);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonString);
                bufferedWriter.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                        bufferedWriter = null;
                    }
                    if (fileWriter != null) {
                        fileWriter.close();
                        fileWriter = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            targetFile = null;
            stringBuffer = null;
        }
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/6/18 4:10 PM
      * @author sunweisong
      */
    public static void writeStringToTargetFile(String string, String targetFilePath) {
        File targetFile = new File(targetFilePath);
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
            fileWriter = new FileWriter(targetFile, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string);
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
                if (fileWriter != null) {
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        targetFile = null;
    }

    
    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/7 10:29
      * @author sunweisong
      */
    public static void deleteDirectory(File directory) {
        File[] fileOrDirectoryArray = directory.listFiles();
        if (fileOrDirectoryArray == null) {
            return;
        }
        for (File fileOrDirectory : fileOrDirectoryArray) {
            if (fileOrDirectory.isFile()) {
                fileOrDirectory.delete();
            } else {
                deleteDirectory(fileOrDirectory);
            }
        }
        directory.delete();
    }
}
