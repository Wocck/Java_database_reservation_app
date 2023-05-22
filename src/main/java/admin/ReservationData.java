package admin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReservationData {
    private final IntegerProperty classNumber;
    private final IntegerProperty reservationId;
    private final StringProperty reservationDate;
    private final StringProperty startHour;
    private final StringProperty endHour;
    private final IntegerProperty cateringId;

    public int getClassNumber() {
        return classNumber.get();
    }

    public IntegerProperty classNumberProperty() {
        return classNumber;
    }

    public int getReservationId() {
        return reservationId.get();
    }

    public IntegerProperty reservationIdProperty() {
        return reservationId;
    }

    public String getReservationDate() {
        return reservationDate.get();
    }

    public StringProperty reservationDateProperty() {
        return reservationDate;
    }

    public String getStartHour() {
        return startHour.get();
    }

    public StringProperty startHourProperty() { return startHour;}

    public String getEndHour() {
        return endHour.get();
    }

    public StringProperty endHourProperty() {
        return endHour;
    }

    public int getCateringId() {
        return cateringId.get();
    }

    public IntegerProperty cateringIdProperty() {
        return cateringId;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber.set(classNumber);
    }

    public void setReservationId(int reservationId) {
        this.reservationId.set(reservationId);
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate.set(reservationDate);
    }

    public void setStartHour(String startHour) {
        this.startHour.set(startHour);
    }

    public void setEndHour(String endHour) {
        this.endHour.set(endHour);
    }

    public void setCateringId(int cateringId) {
        this.cateringId.set(cateringId);
    }

    public ReservationData(int reservationId, String reservationDate, String startHour, String endHour, int classNumber, int cateringId){
        this.classNumber = new SimpleIntegerProperty(classNumber);
        this.reservationId = new SimpleIntegerProperty(reservationId);
        this.startHour = new SimpleStringProperty(startHour);
        this.endHour = new SimpleStringProperty(endHour);
        this.cateringId = new SimpleIntegerProperty(cateringId);
        this.reservationDate = new SimpleStringProperty(reservationDate);
    }
}
