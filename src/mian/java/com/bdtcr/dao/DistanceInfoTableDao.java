package com.bdtcr.dao;

import com.bdtcr.tsearcher.ControlFlowGraph;
import com.bdtcr.models.DistanceInfoTableModel;
import com.bdtcr.models.MethodInfoTableModel;
import com.bdtcr.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
  * @Author sunweisong
  * @Date 2020/4/21 4:03 PM
  */
public class DistanceInfoTableDao {
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private Connection conn = null;



    /**
      * Search top k records by the test target id.
      * @param testTargetId
      * @param k
      * @return List<DistanceInfoTableModel>
      * @date 2020/4/21 4:56 PM
      * @author sunweisong
      */
    public List<DistanceInfoTableModel> searchTopKSortedByBD(String testTargetId, int k) {
        conn = DBUtil.getConnection();
//        String sql = "select * from distance_info_table force index(tt1_id_tt2_id_index) where tt1_id = "
//                + testTargetId + " or  tt2_id = " + testTargetId + " order by BD DESC limit 0," + k;
        /*
        2020.04.29s
         */
        String sql = "select * from distance_info_table force index(tt1_id_tt2_id_index) where tt1_id = "
                + testTargetId + " or  tt2_id = " + testTargetId + " order by BD DESC limit 0," + k;

        List<DistanceInfoTableModel> distanceInfoTableModelList = new ArrayList<>();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String tt1_d = rs.getString("tt1_id");
                String tt2_d = rs.getString("tt2_id");
                double dc = rs.getDouble("DC");
                double dl = rs.getDouble("DL");
                double dg = rs.getDouble("DG");
                double db = rs.getDouble("DB");
                double bd = rs.getDouble("BD");
                distanceInfoTableModelList.add(new DistanceInfoTableModel(id, tt1_d, tt2_d, dc, dl, dg, db, bd));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        if (distanceInfoTableModelList.size() == 0) {
            distanceInfoTableModelList = null;
        }
        return distanceInfoTableModelList;
    }

    /**
     * Close the database resources.
     * @throws SQLException
     * @date 2020/4/21 3:07 PM
     * @author sunweisong
     */
    private void closeDBResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
