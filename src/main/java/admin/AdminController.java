package admin;

import databaseConnection.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import login.UserSession;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Main class responsible for all admin functionalities */
public class AdminController implements Initializable {

    ClassroomModel classroomModel = new ClassroomModel();

    EquipmentModel equipmentModel = new EquipmentModel();

    ClassroomFilterData classroomFilterData = new ClassroomFilterData();

    ReservationModel reservationModel = new ReservationModel();

    int loggedInUserId;

    @FXML
    private TextField addClassCatering;

    @FXML
    private TextField addClassRoomType;

    @FXML
    private TextField classNumber;

    @FXML
    private TextField floor;

    @FXML
    private TextField numberOfSeats;

    @FXML
    private Label classErrorLabel;

    @FXML
    private TextField eqClassNumber;

    @FXML
    private TextField eqModel;

    @FXML
    private ComboBox eqType;

    @FXML
    private Label eqErrorLabel;

    @FXML
    private TableView<ClassroomData> classroomtable;

    @FXML
    private TableColumn<ClassroomData, String> classnumbercolumn;

    @FXML
    private TableColumn<ClassroomData, String> floorcolumn;

    @FXML
    private TableColumn<ClassroomData, String> seatsnumbercolumn;

    @FXML
    private TableColumn<ClassroomData, Integer> computersnumbercolumn;

    @FXML
    private TableColumn<ClassroomData, Integer> projectorcolumn;

    @FXML
    private TextField fSeats;

    @FXML
    private TextField fComputers;

    @FXML
    private TextField fPrinters;

    @FXML
    private TextField fProjectors;

    @FXML
    private DatePicker fDate;

    @FXML
    private TextField fStart;

    @FXML
    private TextField fEnd;

    @FXML
    private Label fLabelError;

    @FXML
    private DatePicker rDate;

    @FXML
    private TextField rClassNumber;

    @FXML
    private TextField rStart;

    @FXML
    private TextField rEnd;

    @FXML
    private Label rLabelError;
    @FXML
    private TextField rCateringIdField;

    @FXML
    private Button rBookButton;
    @FXML
    private TextField changeReservationId;
    @FXML
    private TextField changeCateringId;
    @FXML
    private DatePicker changeDate;
    @FXML
    private TextField changeStartHour;
    @FXML
    private TextField changeEndHour;
    @FXML
    private TextField changeClassNum;
    @FXML
    private Label changeErrorLabel;

    protected DatabaseConnection dc;
    private ObservableList<ClassroomData> data;

    @FXML
    private TableView<ReservationData> reservationTable;
    @FXML
    private TableColumn<ReservationData, Integer> reservationClassNumberColumn;
    @FXML
    private TableColumn<ReservationData, Integer> reservationIdColumn;
    @FXML
    private TableColumn<ReservationData, String> dateColumn;
    @FXML
    private TableColumn<ReservationData, String> startHourColumn;
    @FXML
    private TableColumn<ReservationData, String> endHourColumn;
    @FXML
    private TableColumn<ReservationData, Integer> cateringIdColumn;

    @FXML
    private Label generateReportLabel;
    @FXML
    private DatePicker reportStart;
    @FXML
    private DatePicker reportEnd;
    @FXML
    private TextField reportRooms;
    @FXML
    private TextField reportUsers;

    @FXML
    private Tab paneTab;

    private ObservableList<ReservationData> reservationData;

    /**sql query used in the loadClassroomData method */
    private String sql1 = "SELECT r.id_room, r.floor, r.seats_number, coalesce((SELECT count(equipments.id_equipment) FROM equipments WHERE r.id_room = id_room and equipment_type = 'computer' GROUP BY id_room), 0) as NUMBER_OF_COMPUTERS, coalesce((SELECT count(id_equipment) FROM equipments WHERE r.id_room = id_room and equipment_type = 'printer' GROUP BY id_room), 0) as NUMBER_OF_PRINTERS from rooms r LEFT JOIN equipments e on (e.id_room = r.id_room) GROUP BY r.id_room, r.floor, r.seats_number";

    public AdminController() throws SQLException {
    }

    public void initialize(URL url, ResourceBundle rb){
        UserSession userSession = UserSession.getInstance();
        loggedInUserId = userSession.getLoggedInUserId();
        this.paneTab.setText("User_" + loggedInUserId);
        this.eqType.setItems(FXCollections.observableArrayList(typeOption.values()));
    }

    @FXML
    private void restrictToNumbers(KeyEvent event) {
        String input = rCateringIdField.getText();
        if (!input.matches("\\d*")) {
            rCateringIdField.setText(input.replaceAll("[^\\d]", ""));
        }
    }
    /**
     *  Method triggered by the Refresh button
     *  Fill a window with information about all the classes
     */
    @FXML
    private void loadClassroomData(ActionEvent event) throws SQLException{
        UserSession userSession = UserSession.getInstance();
        int loggedUserId = userSession.getLoggedInUserId();
        String query = "SELECT id_reservation, reservation_date, start_time, end_time, id_room, id_catering from reservations where id_users = ?";

        PreparedStatement reservationStatement = null;
        ResultSet reservationResult = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            this.reservationData = FXCollections.observableArrayList();
            ResultSet rs = conn.createStatement().executeQuery(sql1);
            while (rs.next()){
                this.data.add(new ClassroomData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5)));
            }

            reservationStatement = conn.prepareStatement(query);
            reservationStatement.setInt(1, loggedUserId);
            reservationResult = reservationStatement.executeQuery();
            while (reservationResult.next()){
                this.reservationData.add(new ReservationData(reservationResult.getInt(1), reservationResult.getString(2), reservationResult.getString(3), reservationResult.getString(4), reservationResult.getInt(5), reservationResult.getInt(6)));
            }
        }catch (SQLException e){
            System.err.println("Error " + e);
        }
        this.classnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("classNumber"));
        this.floorcolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("floor"));
        this.seatsnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("numberOfSeats"));
        this.computersnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, Integer>("numberOfComputers"));
        this.projectorcolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, Integer>("numberOfPrinters"));

        this.classroomtable.setItems((null));
        this.classroomtable.setItems(this.data);

        this.reservationClassNumberColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, Integer>("classNumber"));
        this.reservationIdColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, Integer>("reservationId"));
        this.dateColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, String>("reservationDate"));
        this.startHourColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, String>("startHour"));
        this.endHourColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, String>("endHour"));
        this.cateringIdColumn.setCellValueFactory(new PropertyValueFactory<ReservationData, Integer>("cateringId"));

        this.reservationTable.setItems((null));
        this.reservationTable.setItems(this.reservationData);
    }

    /**
     *  Method triggered by the Filtered button
     *  Fill a window with information about all the classes with parameters provided by the user
     */
    @FXML
    private void loadClassroomFiltered(ActionEvent event) throws SQLException{
        filteredAction();
        Integer computers = classroomFilterData.getfComputers();
        Integer seats = classroomFilterData.getfSeats();
        Integer printers = classroomFilterData.getfPrinters();
        Integer projectors = classroomFilterData.getfProjectors();
        PreparedStatement pr = null;
        PreparedStatement pr2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        // computers, printers, projectors, seats)
        String query = "SELECT id_room, floor, SEATS_NUMBER, (SELECT COUNT(id_equipment) FROM equipments WHERE EQUIPMENT_TYPE = 'computer' AND rooms.id_room = equipments.id_room) as computers, (SELECT COUNT(id_equipment) FROM equipments WHERE equipment_type = 'projector' AND rooms.id_room = equipments.id_room) as projectors FROM rooms WHERE ? <= (SELECT COUNT(id_equipment) FROM equipments WHERE equipment_type = 'computer' AND rooms.id_room = equipments.id_room) AND ? <= (SELECT COUNT(id_equipment) FROM equipments WHERE equipment_type = 'printer' AND rooms.id_room = equipments.id_room) AND ? <= (SELECT COUNT(id_equipment) FROM equipments WHERE equipment_type = 'projector' AND rooms.id_room = equipments.id_room) AND ? <= rooms.seats_number";

        try {
            Connection conn = DatabaseConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            pr = conn.prepareStatement(query);
            pr.setInt(1, computers);
            pr.setInt(2, printers);
            pr.setInt(3, projectors);
            pr.setInt(4, seats);
            rs = pr.executeQuery();
            while (rs.next()){
                Integer hourS = classroomFilterData.getfStart();
                Integer hourE = classroomFilterData.getfEnd();
                String roomId = rs.getString(1);
                String date = classroomFilterData.getfDate();
                String query2 = "SELECT DATE_FORMAT(start_time, '%H') as start_hour, DATE_FORMAT(end_time, '%H') as end_hour FROM reservations WHERE id_room = ? and DATE_FORMAT(start_time, '%Y-%m-%d') = ?";
                pr2 = conn.prepareStatement(query2);
                pr2.setInt(1, Integer.parseInt(roomId));
                pr2.setString(2, date);
                rs2 = pr2.executeQuery();
                int condition = 0;
                while (rs2.next()){
                    if (((hourS < rs2.getInt("end_hour") && hourE > rs2.getInt("start_hour")) || (hourE > rs2.getInt("start_hour") && hourS < rs2.getInt("end_hour")))) {
                        condition = 1;
                    }
                }
                rs2.close();
                if (condition == 0){
                    this.data.add(new ClassroomData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5)));
                }
            }
            rs.close();
        }catch (SQLException e){
            System.err.println("Error " + e);
        }

        this.classnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("classNumber"));
        this.floorcolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("floor"));
        this.seatsnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, String>("numberOfSeats"));
        this.computersnumbercolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, Integer>("numberOfComputers"));
        this.projectorcolumn.setCellValueFactory(new PropertyValueFactory<ClassroomData, Integer>("numberOfPrinters"));

        this.classroomtable.setItems((null));
        this.classroomtable.setItems(this.data);

    }

    /**
     *  Auxiliary method used in loadClassroomFiltered
     *  Checks whether the data provided by the user is correct
     */
    public void filteredAction(){
        try{
            try{
                Integer.parseInt(this.fSeats.getText());
                Integer.parseInt(this.fComputers.getText());
                Integer.parseInt(this.fPrinters.getText());
                Integer.parseInt(this.fProjectors.getText());
                Integer.parseInt(this.fStart.getText());
                Integer.parseInt(this.fEnd.getText());
            }catch (Exception e) {
                fLabelError.setText("Invalid type of data!");
                return;
            }
            int fSeats = Integer.parseInt(this.fSeats.getText());
            int fComputers = Integer.parseInt(this.fComputers.getText());
            int fPrinters = Integer.parseInt(this.fPrinters.getText());
            int fProjectors = Integer.parseInt(this.fProjectors.getText());
            int fStart = Integer.parseInt(this.fStart.getText());
            int fEnd = Integer.parseInt(this.fEnd.getText());

            if(fStart < 6 || fStart > 22){
                fLabelError.setText("Start hour must be between 6 - 22!");
            }
            if(fEnd < 6 || fEnd > 22){
                fLabelError.setText("End hour must be between 6 - 22!");
            }
            if(fStart >= fEnd){
                fLabelError.setText("End hour must be bigger then Start!");
            }
            fLabelError.setText("");
            classroomFilterData.setfSeats(fSeats);
            classroomFilterData.setfComputers(fComputers);
            classroomFilterData.setfPrinters(fPrinters);
            classroomFilterData.setfProjectors(fProjectors);
            classroomFilterData.setfStart(fStart);
            classroomFilterData.setfEnd(fEnd);

        }catch (Exception e){
            e.printStackTrace();
        }
        String fDate;
        try {
            fDate = this.fDate.getValue().toString();
        }catch (Exception e) {
            fDate = "2000-01-01";
            // if date not given sets to date with no reservations
        }
        classroomFilterData.setfDate(fDate);
    }

    /**
     *  Method triggered by the Add equipment button
     *  Adds new equipment to the database with the parameters provided by the user
     */
    public void addEquipmentAction(ActionEvent event){
        try{
            if (this.eqType.getValue() == null){
                this.eqErrorLabel.setText("Choose one!");
            }
            else {
                try {
                    Integer eqClassNumber = Integer.parseInt(this.eqClassNumber.getText());
                } catch (Exception e) {
                    eqErrorLabel.setText("Class number must be a number!");
                    return;
                }
                Integer eqClassNumber = Integer.parseInt(this.eqClassNumber.getText());
                String eqModel = this.eqModel.getText();
                String eqType = this.eqType.getValue().toString();
                equipmentModel.setClassNumber(eqClassNumber);
                equipmentModel.setModel(eqModel);
                equipmentModel.setType(eqType);

                if (eqModel.isEmpty() || eqType.isEmpty()) {
                    eqErrorLabel.setText("Please fill all fields!");
                    return;
                }else if(!equipmentModel.validateClassNumber()){
                    eqErrorLabel.setText("Classroom with this number does not exists!");
                    return;
                }else{
                    eqErrorLabel.setText("");
                }

                equipmentModel.addEquipment();
                eqErrorLabel.setTextFill(Color.GREEN);
                eqErrorLabel.setText("Equipment added");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Method triggered by the Add class button
     *  Adds new class to the database with the parameters provided by the user
     */
    public void addClassroomAction(ActionEvent event){
        try{
            try {
                Integer classNumber = Integer.parseInt(this.classNumber.getText());
                Integer floor = Integer.parseInt(this.floor.getText());
                Integer numberOfSeats = Integer.parseInt(this.numberOfSeats.getText());
                Byte AddClassCatering = Byte.parseByte(this.addClassCatering.getText());
                String AddClassRoomType = this.addClassRoomType.getText();
            }
            catch (Exception e){
                classErrorLabel.setText("");
                classErrorLabel.setTextFill(Color.RED);
                classErrorLabel.setText("Values must be a number!");
                return;
            }

            Integer classNumber = Integer.parseInt(this.classNumber.getText());
            Integer floor = Integer.parseInt(this.floor.getText());
            Integer numberOfSeats = Integer.parseInt(this.numberOfSeats.getText());
            Byte AddClassCatering = Byte.parseByte(this.addClassCatering.getText());
            String AddClassRoomType = this.addClassRoomType.getText();

            classroomModel.setClassNumber(classNumber);
            classroomModel.setFloor(floor);
            classroomModel.setNumberOfSeats(numberOfSeats);
            classroomModel.setAddClassRoomType(AddClassRoomType);
            classroomModel.setAddClassCatering(AddClassCatering);

            if(!classroomModel.validateClassNumber()){
                classErrorLabel.setTextFill(Color.RED);
                classErrorLabel.setText("Classroom with this number already exists!");
                return;
            }

            classroomModel.addClassroom();
            classErrorLabel.setText("");
            classErrorLabel.setTextFill(Color.GREEN);
            classErrorLabel.setText("Classroom added");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Method triggered by the Book button
     *  Adds new reservation to the database with the parameters provided by the user
     */
    public void reserveAction(ActionEvent event){
        UserSession userSession = UserSession.getInstance();
        int loggedInUserId = userSession.getLoggedInUserId();
        try{
            if(this.rClassNumber.getText().isEmpty() ||
                    this.rStart.getText().isEmpty() ||
                    this.rEnd.getText().isEmpty() ||
                    this.rDate.getValue() == null ||
                    this.rCateringIdField.getText().isEmpty())
            {
                this.rLabelError.setText("Pleas fill all fields");
            } else {
                try {
                    int classNum = Integer.parseInt(this.rClassNumber.getText());
                } catch (NumberFormatException e) {
                    this.rLabelError.setText("Class id must be Numeric");
                    return;
                }
                LocalDate date = this.rDate.getValue();
                try {
                    int start = Integer.parseInt(this.rStart.getText());
                    int end = Integer.parseInt(this.rEnd.getText());
                } catch (NumberFormatException e) {
                    this.rLabelError.setText("Hours must be numeric");
                    return;
                }
                int classNum = Integer.parseInt(this.rClassNumber.getText());
                int start = Integer.parseInt(this.rStart.getText());
                int end = Integer.parseInt(this.rEnd.getText());
                if(start < 6 || end < 6 || end > 20 || start >= end){
                    this.rLabelError.setText("Hours must be from 6 to 20!");
                    return;
                }
                reservationModel.setRClassNumber(classNum);
                reservationModel.setDate(date.toString());
                reservationModel.setHourS(start);
                reservationModel.setHourE(end);
                reservationModel.setUserId(loggedInUserId);
                reservationModel.setrCateringId(Integer.parseInt(this.rCateringIdField.getText()));

                if(!reservationModel.validateClassNumber()){
                    rLabelError.setText("Classroom with this number does not exist!");
                    return;
                }

                if(!reservationModel.validateReservation()){
                    rLabelError.setText("This classroom is already reserved for this date!");
                    return;
                }

                reservationModel.addReservation();
                this.rLabelError.setTextFill(Color.GREEN);
                this.rLabelError.setText("Reservation added");

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeReservationAction(ActionEvent event) {
        try {
            if (this.changeReservationId.getText().isEmpty() ||
                    this.changeStartHour.getText().isEmpty() ||
                    this.changeEndHour.getText().isEmpty() ||
                    this.changeDate.getValue() == null ||
                    this.changeClassNum.getText().isEmpty() ||
                    this.changeCateringId.getText().isEmpty()) {
                this.changeErrorLabel.setText("Pleas fill all fields");
            } else {
                try {
                    int classNum = Integer.parseInt(this.changeClassNum.getText());
                } catch (NumberFormatException e) {
                    this.changeErrorLabel.setText("Class id must be Numeric");
                    return;
                }
                LocalDate date = this.changeDate.getValue();
                try {
                    int start = Integer.parseInt(this.changeStartHour.getText());
                    int end = Integer.parseInt(this.changeEndHour.getText());
                } catch (NumberFormatException e) {
                    this.changeErrorLabel.setText("Hours must be numeric");
                    return;
                }
                int classNum = Integer.parseInt(this.changeClassNum.getText());
                int start = Integer.parseInt(this.changeStartHour.getText());
                int end = Integer.parseInt(this.changeEndHour.getText());
                if (start < 6 || end < 6 || end > 20 || start >= end) {
                    this.changeErrorLabel.setText("Hours must be from 6 to 20!");
                    return;
                }
                reservationModel.setChangeReservationId(Integer.parseInt(this.changeReservationId.getText()));
                reservationModel.setChangeClassNumber(classNum);
                reservationModel.setChangeDate(date.toString());
                reservationModel.setChangeStartHour(start);
                reservationModel.setChangeEndHour(end);
                reservationModel.setChangeCateringId(Integer.parseInt(this.changeCateringId.getText()));
                reservationModel.setUserId(loggedInUserId);
                if (!reservationModel.validateChangeClassNumber()) {
                    changeErrorLabel.setTextFill(Color.RED);
                    changeErrorLabel.setText("Classroom with this number does not exist!");
                    return;
                }
                if(!reservationModel.validateChangeReservation()){
                    changeErrorLabel.setTextFill(Color.RED);
                    changeErrorLabel.setText("This classroom is already reserved for this date!");
                    return;
                }
                if(!reservationModel.validateChangeUserId()){
                    changeErrorLabel.setTextFill(Color.RED);
                    changeErrorLabel.setText("You cannot alter this reservation");
                    return;
                }
                reservationModel.changeReservation();
                this.changeErrorLabel.setTextFill(Color.GREEN);
                this.changeErrorLabel.setText("Reservation Changed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGenerateReport(ActionEvent event) throws SQLException {
        MetricsModel metrics = new MetricsModel();

        LocalDate dateStart = this.reportStart.getValue();
        LocalDate dateEnd = this.reportEnd.getValue();
        if(dateStart == null){
            metrics.setDateStart("2000-01-01");
        } else
            metrics.setDateStart(dateStart.toString());
        if(dateEnd == null){
            /*LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            metrics.setDateEnd(today.format(formatter));*/
            metrics.setDateEnd("2100-01-01");
        } else
            metrics.setDateEnd(dateEnd.toString());

        String users = this.reportUsers.getText();
        String rooms = this.reportRooms.getText();
        if(users.isEmpty()){
            metrics.setUsers("");
        } else{
            metrics.setUsers(users);
        }
        if(rooms.isEmpty()){
            metrics.setRooms("");
        } else {
            metrics.setRooms(rooms);
        }
        if(!(isValidUsersRoomsInput(users) && isValidUsersRoomsInput(rooms))){
            this.generateReportLabel.setTextFill(Color.RED);
            this.generateReportLabel.setText("check users or rooms format!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Metrics");

        // Set the initial directory for the dialog (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set a file extension filter (optional)
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Show the file save dialog
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // Save the metrics to the selected file
        if (file != null) {
            metrics.generateRaport();
            metrics.saveToJsonFile(file.getAbsolutePath());
            this.generateReportLabel.setTextFill(Color.GREEN);
            this.generateReportLabel.setText("Report saved to file!");;
        }
        else {
            this.generateReportLabel.setTextFill(Color.RED);
            this.generateReportLabel.setText("Report not saved!");
        }
    }

    public boolean isValidUsersRoomsInput(String input) {
        if (input.isEmpty()) {
            return true;
        }
        String pattern = "^'(\\d+)'(,'(\\d+)')*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        return m.matches();
    }
}
