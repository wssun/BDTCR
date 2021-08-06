package com.bdtcr.dao;

import com.bdtcr.models.ProjectInfoTableModel;
import com.bdtcr.utils.DBUtil;

import java.sql.*;

/**
  * @Author sunweisong
  * @Date 2020/8/2 10:49 AM
  */
public class ProjectInfoTableDao {

    private PreparedStatement pst = null;
    private ResultSet rs = null;
    
    /**
      * 
      * @param 
      * @return
      * @throws
      * @date 2020/8/2 10:51 AM
      * @author sunweisong
      */
    public int saveProjectInfoToDatabase(ProjectInfoTableModel projectInfoTableModel) {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT IGNORE INTO project_info_table (project_id, project_name, repository_id, repository_name, storage_time) VALUES (?, ?, ?, ?, ?)";
        Timestamp storageTime = new Timestamp(System.currentTimeMillis());
        int returnVale = 0;
        try {
            pst = conn.prepareStatement(sql); //预编译SQL，减少sql执行
            pst.setString(1, projectInfoTableModel.getProjectId());
            pst.setString(2, projectInfoTableModel.getProjectName());
            pst.setString(3, projectInfoTableModel.getRepositoryId());
            pst.setString(4, projectInfoTableModel.getRepositoryName());
            pst.setTimestamp(5, storageTime);
            returnVale = pst.executeUpdate();
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
        return returnVale;
    }
}
