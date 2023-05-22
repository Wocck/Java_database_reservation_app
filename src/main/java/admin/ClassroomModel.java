package admin;

import databaseConnection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Auxiliary class for adding new classrooms to the database
 */
public class ClassroomModel {
    Connection connection;
    private Integer classNumber;
    private Integer floor;
    private Integer numberOfSeats;
    private byte addClassCatering;
    private String addClassRoomType;

    public byte getAddClassCatering() {
        return addClassCatering;
    }

    public String getAddClassRoomType() {
        return addClassRoomType;
    }

    public void setAddClassCatering(byte addClassCatering) {
        this.addClassCatering = addClassCatering;
    }

    public void setAddClassRoomType(String addClassRoomType) {
        this.addClassRoomType = addClassRoomType;
    }

    public ClassroomModel() {
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

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public boolean validateClassNumber() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT * from rooms where id_room = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.classNumber);
            rs = pr.executeQuery();
            return !rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
        }
        return false;
    }

    public void addClassroom() throws SQLException {
        PreparedStatement pr = null;

        String query = "INSERT INTO rooms VALUES(?, ?, ?, ?, ?)";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setInt(1, this.classNumber);
            pr.setInt(2, this.floor);
            pr.setInt(3, this.numberOfSeats);
            pr.setByte(4, this.addClassCatering);
            pr.setString(5, this.addClassRoomType);
            pr.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            pr.close();
        }
    }
}
