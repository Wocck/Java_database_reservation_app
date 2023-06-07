package admin;

import databaseConnection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Auxiliary class for adding new reservations to the database
 */
public class ReservationModel {
    Connection connection;
    private Integer rClassNumber;
    private String date;
    private Integer hourS;
    private Integer hourE;
    private Integer userId;
    private Integer rCateringId;

    private Integer changeReservationId;
    private Integer changeClassNumber;
    private String changeDate;
    private Integer changeStartHour;
    private Integer changeEndHour;
    private Integer changeCateringId;

    public void setChangeReservationId(Integer changeReservationId) {
        this.changeReservationId = changeReservationId;
    }

    public void setChangeClassNumber(Integer changeClassNumber) {
        this.changeClassNumber = changeClassNumber;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public void setChangeStartHour(Integer changeStartHour) {
        this.changeStartHour = changeStartHour;
    }

    public void setChangeEndHour(Integer changeEndHour) {
        this.changeEndHour = changeEndHour;
    }

    public void setChangeCateringId(Integer changeCateringId) {
        this.changeCateringId = changeCateringId;
    }

    public ReservationModel() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        if(this.connection == null) {
            System.out.println("Failed to connect to DB: Exit 1");
            System.exit(1);
        }
    }

    public Integer getRClassNumber() {
        return rClassNumber;
    }

    public void setRClassNumber(Integer classNumber) {
        this.rClassNumber = classNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getHourS() {
        return hourS;
    }

    public void setHourS(Integer hourS) {
        this.hourS = hourS;
    }

    public Integer getHourE() {
        return hourE;
    }

    public void setHourE(Integer hourE) {
        this.hourE = hourE;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setrCateringId(Integer rCateringId) { this.rCateringId = rCateringId;}

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public void addReservation() throws SQLException {
        PreparedStatement pr = null;
        String dateS;
        String dateE;

        String query = "INSERT INTO reservations VALUES(NULL, ?, ?, ?, ?, ?, ?)";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            dateS = this.date + " " + Integer.toString(this.hourS) + ":00:00";
            dateE = this.date + " " + Integer.toString(this.hourE) + ":00:00";
            pr.setTimestamp(2, Timestamp.valueOf(dateS));
            pr.setTimestamp(3, Timestamp.valueOf(dateE));
            pr.setInt(4, this.rClassNumber);
            pr.setInt(5, this.userId);
            pr.setInt(6, this.rCateringId);
            int k = pr.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
        }
    }

    public boolean validateReservation() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;
        String query = "SELECT id_room, DATE_FORMAT(start_time, '%H') as start_hour, DATE_FORMAT(end_time, '%H') as end_hour FROM reservations WHERE id_room = ? and DATE_FORMAT(start_time, '%Y-%m-%d')=?";
        try{
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.rClassNumber);
            pr.setString(2, this.date);
            rs = pr.executeQuery();
            while(rs.next()){
                if ((this.hourS < rs.getInt("end_hour") && this.hourE > rs.getInt("start_hour")) || (this.hourE > rs.getInt("start_hour") && this.hourS < rs.getInt("end_hour"))) {
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
            rs.close();
        }
        return true;
    }

    public boolean validateChangeReservation() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;
        String query = "SELECT id_reservation, id_room, DATE_FORMAT(start_time, '%H') as start_hour, DATE_FORMAT(end_time, '%H') as end_hour FROM reservations WHERE id_room = ? and DATE_FORMAT(start_time, '%Y-%m-%d')=?";
        try{
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.changeClassNumber);
            pr.setString(2, this.changeDate);
            rs = pr.executeQuery();
            while(rs.next()){
                if ((this.changeStartHour < rs.getInt("end_hour") && this.changeEndHour > rs.getInt("start_hour")) || (this.changeEndHour > rs.getInt("start_hour") && this.changeStartHour < rs.getInt("end_hour"))) {
                    if(this.changeReservationId == rs.getInt("id_reservation"))
                        continue;
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
            rs.close();
        }
        return true;
    }

    public boolean validateClassNumber() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT * from rooms where room_number = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.rClassNumber);
            rs = pr.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
            rs.close();
        }
        return false;
    }

    public boolean validateChangeClassNumber() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT * from rooms where id_room = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.changeClassNumber);
            rs = pr.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
            rs.close();
        }
        return false;
    }

    public void changeReservation() throws SQLException{
        PreparedStatement pr = null;
        String dateS;
        String dateE;

        String query = "UPDATE reservations SET reservation_date = ?, start_time = ?, end_time = ?, id_room = ?, id_users = ?, id_catering = ? WHERE id_reservation = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            dateS = this.changeDate + " " + Integer.toString(this.changeStartHour) + ":00:00";
            dateE = this.changeDate + " " + Integer.toString(this.changeEndHour) + ":00:00";
            pr.setTimestamp(2, Timestamp.valueOf(dateS));
            pr.setTimestamp(3, Timestamp.valueOf(dateE));
            pr.setInt(4, this.changeClassNumber);
            pr.setInt(5, this.userId);
            pr.setInt(6, this.changeCateringId);
            pr.setInt(7, this.changeReservationId);
            int k = pr.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
        }
    }

    public boolean validateChangeUserId() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;
        String query = "SELECT id_reservation, id_users FROM reservations WHERE id_users = ? AND id_reservation = ?";
        try{
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.userId);
            pr.setInt(2, this.changeReservationId);
            rs = pr.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
            rs.close();
        }
        return true;
    }
}
