package org.tum.project.utils;

import org.tum.project.controller.LoginController;

import java.sql.*;


public class sqlUtils {


    public static final String REGISTER_NAME="com.mysql.jdbc.Driver";
    public static final String URL="jdbc:mysql://localhost:3306";
    private sqlUtils() {
    }


    public static Connection getMySqlConnection(String database) throws ClassNotFoundException, SQLException {
        Class.forName(REGISTER_NAME);
        if (!database.equals("")) {
            database="/"+database;
        }
      //  System.out.println(MaterialLoginController.getRealName()+MaterialLoginController.getRealPassword());
        return DriverManager.getConnection(URL+database,LoginController.realName,LoginController.realPassword);
    }

    public static void closeConn(Connection rs, Statement stat, ResultSet conn){
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                rs=null;
            }
        }

        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                stat=null;
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                conn=null;
            }
        }

    }


}
