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
    private String users;
    private String rooms;
    private String dateStart;
    private String dateEnd;

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

    public ArrayList<AverageDurationPerUser> getAverageDurationPerUser() {
        return averageDurationPerUser;
    }

    public ArrayList<AverageDurationPerRoom> getAverageDurationPerRoom() {
        return averageDurationPerRoom;
    }

    public int getMostBookedRoom() {
        return mostBookedRoom;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
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
            Statement statement = this.connection.createStatement();

            String totalNumQuery = "SELECT COUNT(*) AS total_reservations FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                totalNumQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                totalNumQuery += " AND id_room IN (" + rooms + ")";
            }
            ResultSet totalNumResult = statement.executeQuery(totalNumQuery);
            this.totalReservations = totalNumResult.next() ? totalNumResult.getInt("total_reservations") : 0;


            String durationQuery = "SELECT AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                durationQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                durationQuery += " AND id_room IN (" + rooms + ")";
            }
            ResultSet durationResult = statement.executeQuery(durationQuery);
            this.reservationDuration = durationResult.next() ? durationResult.getDouble("average_duration") : 0;


            String busiestDayQuery = "SELECT DATE(start_time) AS reservation_day, COUNT(*) AS reservation_count FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                busiestDayQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                busiestDayQuery += " AND id_room IN (" + rooms + ")";
            }
            busiestDayQuery += " GROUP BY reservation_day ORDER BY reservation_count DESC LIMIT 1";
            ResultSet busiestDayResult = statement.executeQuery(busiestDayQuery);
            this.busiestReservationDay = busiestDayResult.next() ? busiestDayResult.getString("reservation_day") : "0";


            String busiestTimeSlotQuery = "SELECT HOUR(start_time) AS reservation_hour, COUNT(*) AS reservation_count FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                busiestTimeSlotQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                busiestTimeSlotQuery += " AND id_room IN (" + rooms + ")";
            }
            busiestTimeSlotQuery += " GROUP BY reservation_hour ORDER BY reservation_count DESC LIMIT 1";
            ResultSet busiestTimeSlotResult = statement.executeQuery(busiestTimeSlotQuery);
            this.busiestReservationTimeSlot = busiestTimeSlotResult.next() ? busiestTimeSlotResult.getInt("reservation_hour") : 0;


            String avgReservationTimeQuery = "SELECT id_users, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                avgReservationTimeQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                avgReservationTimeQuery += " AND id_room IN (" + rooms + ")";
            }
            avgReservationTimeQuery += " GROUP BY id_users";
            ResultSet avgReservationTimeResult = statement.executeQuery(avgReservationTimeQuery);
            while (avgReservationTimeResult.next()) {
                AverageDurationPerUser avgDurationPerUser = new AverageDurationPerUser();
                avgDurationPerUser.setUserId(avgReservationTimeResult.getString("id_users"));
                avgDurationPerUser.setAverageDuration(avgReservationTimeResult.getDouble("average_duration"));
                this.addAverageDurationPerUser(avgDurationPerUser);
            }

            String avgDurationRoomQuery = "SELECT id_room, AVG(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS average_duration FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                avgDurationRoomQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                avgDurationRoomQuery += " AND id_room IN (" + rooms + ")";
            }
            avgDurationRoomQuery += " GROUP BY id_room";
            ResultSet avgDurationRoomResult = statement.executeQuery(avgDurationRoomQuery);
            while (avgDurationRoomResult.next()) {
                AverageDurationPerRoom avgDurationPerRoom = new AverageDurationPerRoom();
                avgDurationPerRoom.setRoomId(avgDurationRoomResult.getInt("id_room"));
                avgDurationPerRoom.setAverageDuration(avgDurationRoomResult.getDouble("average_duration"));
                this.addAverageDurationPerRoom(avgDurationPerRoom);
            }


            String mostBookedRoomQuery = "SELECT id_room, COUNT(*) AS reservation_count FROM reservations WHERE DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + dateStart + "' AND DATE_FORMAT(end_time, '%Y-%m-%d') <= '" + dateEnd + "'";
            if (!users.isEmpty()) {
                mostBookedRoomQuery += " AND id_users IN (" + users + ")";
            }
            if (!rooms.isEmpty()) {
                mostBookedRoomQuery += " AND id_room IN (" + rooms + ")";
            }
            mostBookedRoomQuery += " GROUP BY id_room ORDER BY reservation_count DESC LIMIT 1";
            ResultSet mostBookedRoomResult = statement.executeQuery(mostBookedRoomQuery);
            this.mostBookedRoom = mostBookedRoomResult.next() ? mostBookedRoomResult.getInt("id_room") : 0;


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