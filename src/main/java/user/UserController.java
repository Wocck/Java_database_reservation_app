package user;

import databaseConnection.DatabaseConnection;
import admin.AdminController;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** Main class responsible for all admin functionalities */
public class UserController extends AdminController {
    public UserController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        this.dc = new DatabaseConnection();
    }
}
