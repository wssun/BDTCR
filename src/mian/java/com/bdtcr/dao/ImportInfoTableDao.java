package com.bdtcr.dao;

import com.bdtcr.models.ImportInfoTableModel;
import com.bdtcr.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
  * @Author sunweisong
  * @Date 2020/4/21 8:34 PM
  */
public class ImportInfoTableDao {
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private Connection conn = null;


    /**
      * Search the import string by the import id.
      * @param importId
      * @return String
      * @date 2020/4/23 2:57 PM
      * @author sunweisong
      */
    public String searchImportStringByImportId(String importId) {
        conn = DBUtil.getConnection();
        StringBuffer stringBuffer = new StringBuffer();
        String sql = "select import_string from import_info_table";
        String whereSql = " where import_id = '" + importId + "'";
        sql = sql + whereSql;
        String importString = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                importString = rs.getString("import_string");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        return importString;
    }

    /**
      * Search the list of the import strings by the list of import ids.
      * @param importIdList
      * @return List<String>
      * @date 2020/4/21 8:52 PM
      * @author sunweisong
      */
    public List<String> searchImportStringListByImportIdList(List<String> importIdList) {
        conn = DBUtil.getConnection();
        StringBuffer stringBuffer = new StringBuffer();
        String sql = "select import_string from import_info_table";
        int size = importIdList.size();
        for (int i = 0; i < size; i++) {
            stringBuffer.append("'" + importIdList.get(i) + "'");
            if (i < size - 1) {
                stringBuffer.append(",");
            }
        }
        String whereSql = " where import_id in (" + stringBuffer.toString() + ")";
        sql = sql + whereSql;
        List<String> importStringList = new ArrayList<>();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                importStringList.add(rs.getString("import_string"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResources();
        }
        if (importStringList.size() == 0) {
            importStringList = null;
        }
        return importStringList;
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
//            if (conn != null) {
//                conn.close();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
      *
      * @param
      * @return
      * @throws
      * @date 2020/8/2 3:53 PM
      * @author sunweisong
      */
    public void saveImportInfoToDatabase(ImportInfoTableModel importInfoTableModel) {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT IGNORE INTO import_info_table (import_id, import_modifiers, import_name, storage_time) VALUES (?, ?, ?, ?)";
        Timestamp storageTime = new Timestamp(System.currentTimeMillis());
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, importInfoTableModel.getImportId());
            pst.setString(2, importInfoTableModel.getImportModifiers());
            pst.setString(3, importInfoTableModel.getImportName());
            pst.setTimestamp(4, storageTime);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
