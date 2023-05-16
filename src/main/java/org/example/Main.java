package org.example;
import databaseConnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args){
        System.out.println("działa");
        try {
            Connection connection = DatabaseConnection.getConnection();
        } catch (SQLException e){
            System.out.println(e);
        }
    }
}
