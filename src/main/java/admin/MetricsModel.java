package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class MetricsModel {
    private int totalReservations;
    private double reservationDuration;
    private String busiestReservationDay;
    private int busiestReservationTimeSlot;
    private AverageDurationPerUser[] averageDurationPerUser;
    private AverageDurationPerRoom[] averageDurationPerRoom;
    private int mostBookedRoom;

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

    public void setAverageDurationPerUser(AverageDurationPerUser[] averageDurationPerUser) {
        this.averageDurationPerUser = averageDurationPerUser;
    }

    public void setAverageDurationPerRoom(AverageDurationPerRoom[] averageDurationPerRoom) {
        this.averageDurationPerRoom = averageDurationPerRoom;
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

    public AverageDurationPerUser[] getAverageDurationPerUser() {
        return averageDurationPerUser;
    }

    public AverageDurationPerRoom[] getAverageDurationPerRoom() {
        return averageDurationPerRoom;
    }

    public int getMostBookedRoom() {
        return mostBookedRoom;
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