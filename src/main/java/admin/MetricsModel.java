package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import databaseConnection.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MetricsModel {
    private int totalReservations;
    private double reservationDuration;
    private String busiestReservationDay;
    private int busiestReservationTimeSlot;
    private ArrayList<AverageDurationPerUser> averageDurationPerUser;
    private ArrayList<AverageDurationPerRoom> averageDurationPerRoom;
    private int mostBookedRoom;
    Connection connection;

    public MetricsModel() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e){
            e.printStackTrace();
        }
        if(this.connection == null) {
            System.out.println("Failed to connect to DB: Exit 1");
            System.exit(1);
        }
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public void setReservationDuration(double reservationDuration) {
        this.reservationDuration = reservationDuration;
    }

    public void setBusiestReservationDay(String busiestReservationDay) {
        this.busiestReservationDay = busiestReservationDay;
    }

    public void setBusiestReservationTimeSlot(int busiestReservationTimeSlot) {
        this.busiestReservationTimeSlot = busiestReservationTimeSlot;
    }

    public void setMostBookedRoom(int mostBookedRoom) {
        this.mostBookedRoom = mostBookedRoom;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public double getReservationDuration() {
        return reservationDuration;
    }

    public String getBusiestReservationDay() {
        return busiestReservationDay;
    }

    public int getBusiestReservationTimeSlot() {
        return busiestReservationTimeSlot;
    }

    public int getMostBookedRoom() {
        return mostBookedRoom;
    }

    public ArrayList<AverageDurationPerUser> getAverageDurationPerUser() {
        return averageDurationPerUser;
    }

    public ArrayList<AverageDurationPerRoom> getAverageDurationPerRoom() {
        return averageDurationPerRoom;
    }

    public void setAverageDurationPerRoom(ArrayList<AverageDurationPerRoom> averageDurationPerRoom) {
        this.averageDurationPerRoom = averageDurationPerRoom;
    }

    public void setAverageDurationPerUser(ArrayList<AverageDurationPerUser> averageDurationPerUser) {
        this.averageDurationPerUser = averageDurationPerUser;
    }

    public void addAverageDurationPerUser(AverageDurationPerUser averageDurationPerUser) {
        if(this.averageDurationPerUser == null)
            this.averageDurationPerUser = new ArrayList<>();
        this.averageDurationPerUser.add(averageDurationPerUser);
    }

    public void addAverageDurationPerRoom(AverageDurationPerRoom averageDurationPerRoom) {
        if(this.averageDurationPerRoom == null)
            this.averageDurationPerRoom = new ArrayList<>();
        this.averageDurationPerRoom.add(averageDurationPerRoom);
    }

    public void saveToJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(new File(filePath), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateRaport(){
        try
        {
            String gueryTotalNum = "SELECT COUNT(*) AS total_reservations FROM reservations";
            String queryDuration = "SELECT AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations";
            String queryBuisiestDay = "SELECT DATE(start_time) AS reservation_day, COUNT(*) AS reservation_count FROM reservations GROUP BY reservation_day ORDER BY reservation_count DESC LIMIT 1";
            String querybuisiestTimeSlot = "SELECT HOUR(start_time) AS reservation_hour, COUNT(*) AS reservation_count FROM reservations GROUP BY reservation_hour ORDER BY reservation_count DESC LIMIT 1";
            String queryAvgReservationTime = "SELECT user_id, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations GROUP BY user_id";
            String queryAvgdurationRoom = "SELECT room_id, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations GROUP BY room_id";
            String queryMostBookedRoom = "SELECT room_id, COUNT(*) AS reservation_count FROM reservations GROUP BY room_id ORDER BY reservation_count DESC LIMIT 1";

            Statement statement = this.connection.createStatement();

            ResultSet totalNumResult = statement.executeQuery("SELECT COUNT(*) AS total_reservations FROM reservations");
            if (totalNumResult.next()) {
                this.totalReservations = totalNumResult.getInt("total_reservations");
            }

            ResultSet durationResult = statement.executeQuery("SELECT AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations");
            if (durationResult.next()) {
                this.reservationDuration = durationResult.getDouble("average_duration");
            }

            ResultSet busiestDayResult = statement.executeQuery("SELECT DATE(start_time) AS reservation_day, COUNT(*) AS reservation_count FROM reservations GROUP BY reservation_day ORDER BY reservation_count DESC LIMIT 1");
            if (busiestDayResult.next()) {
                this.busiestReservationDay = busiestDayResult.getString("reservation_day");
            }

            ResultSet busiestTimeSlotResult = statement.executeQuery("SELECT HOUR(start_time) AS reservation_hour, COUNT(*) AS reservation_count FROM reservations GROUP BY reservation_hour ORDER BY reservation_count DESC LIMIT 1");
            if (busiestTimeSlotResult.next()) {
                this.busiestReservationTimeSlot = busiestTimeSlotResult.getInt("reservation_hour");
            }

            ResultSet avgReservationTimeResult = statement.executeQuery("SELECT id_users, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations GROUP BY id_users");
            while (avgReservationTimeResult.next()) {
                AverageDurationPerUser avgDurationPerUser = new AverageDurationPerUser();
                avgDurationPerUser.setUserId(avgReservationTimeResult.getString("id_users"));
                avgDurationPerUser.setAverageDuration(avgReservationTimeResult.getDouble("average_duration"));
                this.addAverageDurationPerUser(avgDurationPerUser);
            }

            ResultSet avgDurationRoomResult = statement.executeQuery("SELECT id_room, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations GROUP BY id_room");
            while (avgDurationRoomResult.next()) {
                AverageDurationPerRoom avgDurationPerRoom = new AverageDurationPerRoom();
                avgDurationPerRoom.setRoomId(avgDurationRoomResult.getInt("id_room"));
                avgDurationPerRoom.setAverageDuration(avgDurationRoomResult.getDouble("average_duration"));
                this.addAverageDurationPerRoom(avgDurationPerRoom);
            }

            ResultSet mostBookedRoomResult = statement.executeQuery("SELECT id_room, COUNT(*) AS reservation_count FROM reservations GROUP BY id_room ORDER BY reservation_count DESC LIMIT 1");
            if (mostBookedRoomResult.next()) {
                this.mostBookedRoom = mostBookedRoomResult.getInt("id_room");
            }
            totalNumResult.close();
            durationResult.close();
            busiestDayResult.close();
            busiestTimeSlotResult.close();
            avgReservationTimeResult.close();
            avgDurationRoomResult.close();
            mostBookedRoomResult.close();
            statement.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}

class AverageDurationPerUser {
    private String userId;
    private double averageDuration;

    public String getUserId() {
        return userId;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }
}

class AverageDurationPerRoom {
    private int roomId;
    private double averageDuration;

    public int getRoomId() {
        return roomId;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }
}