package org.example;
import databaseConnection.DatabaseConnection;
import login.LoginApp;

import java.sql.*;


public class Main {
    public static void main(String[] args) throws SQLException {
        LoginApp.main(args);
        /*PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection;
        connection = DatabaseConnection.getConnection();
        System.out.println(connection.getSchema());

        String query = "SELECT * from login_data";
        try {
            pr = connection.prepareStatement(query);


            rs = pr.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName + "\t");
            }
            System.out.println();

// Print the data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    System.out.print(value + "\t");
                }
                System.out.println();
            }
            while (rs.next()){
                String column1Value = rs.getString("passwd");
                System.out.println(column1Value);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }*/
    }
}
