package com.bdtcr.tsearcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdtcr.utils.FileUtil;
import ghaffarian.progex.CLI;
import ghaffarian.progex.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
  * @Author sunweisong
  * @Date 2020/3/16 2:16 PM
  */
public class ControlFlowGraph {

    private List<Node> nodeList;
    private List<Edge> edgeList;

    public ControlFlowGraph(List<Node> nodes, List<Edge> edges) {
        this.nodeList = nodes;
        this.edgeList = edges;
        initNodeRelationships();
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    /**
      * Init the relationships among all nodes.
      * @return void
      * @date 2020/3/16 2:43 PM
      * @author sunweisong
      */
    private void initNodeRelationships() {
        for (Node node : nodeList) {
            int nodeId = node.getId();
            List<Node> inNeighborList = new ArrayList<>();
            List<Node> outNeighborList = new ArrayList<>();
            List<Edge> inEdgeList = new ArrayList<>();
            List<Edge> outEdgeList = new ArrayList<>();

            List<Integer> inNeighborIdList = new ArrayList<>();
            List<Integer> outNeighborIdList = new ArrayList<>();

            for (Edge edge : edgeList) {
                int sourceId = edge.getSource();
                int targetId = edge.getTarget();
                if (sourceId != nodeId && targetId != nodeId) {
                    continue;
                }
                if (sourceId == nodeId) {
                    outNeighborIdList.add(targetId);
                    outEdgeList.add(edge);
                }
                if (targetId == nodeId) {
                    inNeighborIdList.add(sourceId);
                    inEdgeList.add(edge);
                }
            }
            for (Node tempNode : nodeList) {
                int tempNodeId = tempNode.getId();
                if (inNeighborIdList.contains(tempNodeId)) {
                    inNeighborList.add(tempNode);
                }
                if (outNeighborIdList.contains(tempNodeId)) {
                    outNeighborList.add(tempNode);
                }
            }
            if (inEdgeList.size() > 0) {
                node.setInEdgeList(inEdgeList);
            }
            if (outEdgeList.size() > 0) {
                node.setOutEdgeList(outEdgeList);
            }
            if (inNeighborList.size() > 0) {
                node.setInNeighborList(inNeighborList);
            }
            if (outNeighborList.size() > 0) {
                node.setOutNeighborList(outNeighborList);
            }
        }
    }


    /**
      * Generate the cost matrix for two control flow graphs.
      * @param cfg1
      * @param cfg2
      * @return double[][]
      * @date 2020/3/17 12:24 PM
      * @author sunweisong
      */
    public static double[][] generateCostMatrix(ControlFlowGraph cfg1, ControlFlowGraph cfg2) {
        List<Node> nodeList_cfg1 = cfg1.getNodeList();
        List<Node> nodeList_cfg2 = cfg2.getNodeList();
        int cfg1Size = nodeList_cfg1.size();
        int cfg2Size = nodeList_cfg2.size();
        int row = cfg1Size + cfg2Size;
        int column = row;

        // (cfg1Size + cfg2Size) X (cfg1Size + cfg2Size)
        double[][] costMatrix = new double[row][column];
        // init costMatrix
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                costMatrix[i][j] = 0;
            }
        }

        // cfg1Size * cfg2Size: the matrix at the top left corner.
        for (int i = 0; i < cfg1Size; i++) {
            for (int j = 0; j < cfg2Size; j++) {
                Node nodeA = nodeList_cfg1.get(i);
                Node nodeB = nodeList_cfg2.get(j);
                int inNeighborDiff = 0;
                int outNeighborDiff = 0;
                List<Node> inNeighborList_nodeA = nodeA.getInNeighborList();
                List<Node> inNeighborList_nodeB = nodeB.getInNeighborList();
                if (inNeighborList_nodeA != null) {
                    inNeighborDiff += inNeighborList_nodeA.size();
                    if (inNeighborList_nodeB != null) {
                        inNeighborDiff += inNeighborList_nodeB.size();
                        if (inNeighborList_nodeA.size() == inNeighborList_nodeB.size()) {
                            inNeighborDiff -= 2 * inNeighborList_nodeA.size();
                        } else {
                            inNeighborDiff -= 2 * Math.min(inNeighborList_nodeB.size(), inNeighborList_nodeA.size());
                        }
                    }
                } else {
                    if (inNeighborList_nodeB != null) {
                        inNeighborDiff += inNeighborList_nodeB.size();
                    }
                }
                List<Node> outNeighborList_nodeA = nodeA.getOutNeighborList();
                List<Node> outNeighborList_nodeB = nodeB.getOutNeighborList();
                if (outNeighborList_nodeA != null) {
                    outNeighborDiff += outNeighborList_nodeA.size();
                    if (outNeighborList_nodeB != null) {
                        outNeighborDiff += outNeighborList_nodeB.size();
                        if (outNeighborList_nodeB.size() == outNeighborList_nodeA.size()) {
                            outNeighborDiff -= 2 * outNeighborList_nodeA.size();
                        } else {
                            outNeighborDiff -= 2 * Math.min(outNeighborList_nodeA.size(), outNeighborList_nodeB.size());
                        }
                    }
                } else {
                    if (outNeighborList_nodeB != null) {
                        outNeighborDiff += outNeighborList_nodeB.size();
                    }
                }
                costMatrix[i][j] = inNeighborDiff + outNeighborDiff;
            }
        }

        // cfg1Size * cfg1Size: the matrix at the top right corner.
        for (int i = 0; i < cfg1Size; i ++) {
            for (int j = cfg2Size; j < column; j ++) {
                int k = j - cfg2Size;
                if (k != i) {
                    costMatrix[i][j] = Double.POSITIVE_INFINITY;
                    continue;
                }
                int edgeCount = 0;
                Node nodeA = nodeList_cfg1.get(i);
                List<Edge> inEdgeList = nodeA.getInEdgeList();
                List<Edge> outEdgeList = nodeA.getOutEdgeList();
                if (inEdgeList != null) {
                    edgeCount += inEdgeList.size();
                }
                if (outEdgeList != null) {
                    edgeCount += outEdgeList.size();
                }
                costMatrix[i][j] = 1 + edgeCount;
            }
        }

        // cfg2Size * cfg2Size: the matrix at the bottom left corner.
        for (int i = cfg1Size; i < row; i ++) {
            for (int j = 0; j < cfg2Size; j++) {
                int k = i - cfg1Size;
                if (k != j) {
                    costMatrix[i][j] = Double.POSITIVE_INFINITY;
                    continue;
                }
                int edgeCount = 0;
                Node nodeB = nodeList_cfg2.get(j);
                List<Edge> inEdgeList = nodeB.getInEdgeList();
                List<Edge> outEdgeList = nodeB.getOutEdgeList();
                if (inEdgeList != null) {
                    edgeCount += inEdgeList.size();
                }
                if (outEdgeList != null) {
                    edgeCount += outEdgeList.size();
                }
                costMatrix[i][j] = 1 + edgeCount;
            }
        }
        return costMatrix;
    }

    /**
      * Calculate the minimal matching cost on the cost matrix.
      * @param costMatrix
      * @return double
      * @date 2020/3/17 12:19 PM
      * @author sunweisong
      */
    public static double calculateEditCostOnCostMatrix(double[][] costMatrix) {
        HungarianBipartiteMatching hbm = new HungarianBipartiteMatching(costMatrix);
        int[] result = hbm.execute();
        double total = 0;
        for (int i = 0; i < costMatrix.length; i++) {
            System.out.println("(" + i + "," + result[i] + ")->" + costMatrix[i][result[i]]);
            total += costMatrix[i][result[i]];
        }
        return total;
    }

    /**
      * Create the control flow graph from file.
      * @param filePath
      * @return ControlFlowGraph
      * @date 2020/3/17 12:07 PM
      * @author sunweisong
      */
    public static ControlFlowGraph createCFGFromJSONFile(String filePath) {
        File file = new File(filePath);
        String fileContent = FileUtil.readFileContentToString(file);
        return createCFGFromJSONString(fileContent);
    }

    /**
      * Create the control flow graph from the JSON string.
      * @param jsonString
      * @return ControlFlowGraph
      * @date 2020/3/17 12:13 PM
      * @author sunweisong
      */
    public static ControlFlowGraph createCFGFromJSONString(String jsonString) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        JSONArray nodeArray = jsonObject.getJSONArray("nodes");
        JSONArray edgeArray = jsonObject.getJSONArray("edges");
        List<Node> nodeList = nodeArray.toJavaList(Node.class);
        List<Edge> edgeList = edgeArray.toJavaList(Edge.class);
        return new ControlFlowGraph(nodeList, edgeList);
    }


    /**
      * Generate the control flow graph for the specific method code.
      * @param methodCode
      * @return ControlFlowGraph
      * @date 2020/3/17 4:56 PM
      * @author sunweisong
      */
    public static ControlFlowGraph generateCFGForMethodCode(String methodCode) {
        StringBuffer codeStringBuffer = new StringBuffer("class MethodClass {\n");
        codeStringBuffer.append(methodCode);
        codeStringBuffer.append("}");
        BufferedWriter bw = null;
        File tempFile = null;
        String tempFilePath = null;
        try {
            tempFile = File.createTempFile("temp_method_code_", ".java");
            bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(codeStringBuffer.toString());
            tempFilePath = tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // generate the cfg and save it into a json file by using the tool PROGEX.
        String tempFolderPath = tempFile.getParent() + File.separator + "temp_cfg_folder";
        String[] args = {"-lang", "java", "-cfg"
                , "-outdir", tempFolderPath
                , "-format", "json"
                , tempFilePath};
        (new CLI()).parse(args).execute();

        // generate the control flow graph.
        StringBuffer jsonFilePathBuffer = new StringBuffer(tempFolderPath + File.separator);
        String tempJSONFileName = tempFile.getName().replace(".java", "");
        jsonFilePathBuffer.append(tempJSONFileName);
        jsonFilePathBuffer.append("-CFG.json");
        String jsonFilePath = jsonFilePathBuffer.toString();
        ControlFlowGraph cfg = ControlFlowGraph.createCFGFromJSONFile(jsonFilePath);

        // release resources.
        jsonFilePathBuffer = null;
        codeStringBuffer = null;
        // delete the temp file.
        (new File(jsonFilePath)).deleteOnExit();
        tempFile.deleteOnExit();

        return cfg;
    }

    private static void test() {

    }

    public static void main(String[] args) {

        String jsonFile1 = "/Users/sunweisong/Desktop/Test Case Recommendation/experiment_cfg/bubbleSort-1-CFG.json";
        String jsonFile2 = "/Users/sunweisong/Desktop/Test Case Recommendation/experiment_cfg/bubbleSort-2-CFG.json";
        ControlFlowGraph cfg1 = ControlFlowGraph.createCFGFromJSONFile(jsonFile1);
        ControlFlowGraph cfg2 = ControlFlowGraph.createCFGFromJSONFile(jsonFile2);

        System.out.println("Number of Nodes in CFG1: " + cfg1.getNodeList().size());
        System.out.println("Number of Nodes in CFG2: " + cfg2.getNodeList().size());


        double[][] costMatrix = ControlFlowGraph.generateCostMatrix(cfg2, cfg1);
//        for (int i = 0; i < costMatrix.length; i ++) {
//            for (int j = 0; j < costMatrix[0].length; j++) {
//                System.out.print(costMatrix[i][j] + "\t");
//            }
//            System.out.println();
//        }
//        System.out.println(costMatrix.length);

        double cost = ControlFlowGraph.calculateEditCostOnCostMatrix(costMatrix);

        double editDistance = cost / (cfg1.getNodeList().size() + cfg2.getEdgeList().size()
                + cfg1.getNodeList().size() + cfg1.getEdgeList().size());
        System.out.println("editDistance: " +editDistance);
    }
    
    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/3/16 2:43 PM
      * @author sunweisong
      */
    private void printNodeInformation() {
        for (Node node : nodeList) {
            String label = node.getLabel();
            System.out.println("------------------ Node:" + label + " -----------------");
            System.out.println("++++ incoming neighbors ++++");
            List<Node> inNeighborList = node.getInNeighborList();
            if (inNeighborList != null) {
                for (Node inNeighbor : inNeighborList) {
                    System.out.println(inNeighbor.toString());
                }
            } else {
                System.out.println("空集");
            }
            System.out.println("++++ outgoing neighbors ++++");
            List<Node> outNeighborList = node.getOutNeighborList();
            if (outNeighborList != null) {
                for (Node outNeighbor : outNeighborList) {
                    System.out.println(outNeighbor.toString());
                }
            } else {
                System.out.println("空集");
            }
            System.out.println("++++ incoming edges ++++");
            List<Edge> inEdgeList = node.getInEdgeList();
            if (inEdgeList != null) {
                for(Edge inEdge : inEdgeList) {
                    System.out.println(inEdge.toString());
                }
            } else {
                System.out.println("空集");
            }
            System.out.println("++++ outgoing edges ++++");
            List<Edge> outEdgeList = node.getOutEdgeList();
            if (outEdgeList != null) {
                for (Edge outEdge : outEdgeList) {
                    System.out.println(outEdge.toString());
                }
            }  else {
                System.out.println("空集");
            }
            System.out.println();
        }
    }
}
