package org.example;
import admin.MetricsModel;
import databaseConnection.DatabaseConnection;
import login.LoginApp;

import java.sql.*;


public class Main {
    public static void main(String[] args) throws SQLException {
        MetricsModel metrics = new MetricsModel();
        metrics.setTotalReservations(10);
        metrics.setReservationDuration(120.5);
        metrics.setBusiestReservationDay("2023-05-22");
        metrics.setBusiestReservationTimeSlot(14);
        // Populate other metric data

        // Save the metric data to a JSON file
        metrics.saveToJsonFile("metrics.json");
        LoginApp.main(args);
    }
}
