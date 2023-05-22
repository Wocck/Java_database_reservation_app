package register;

import databaseConnection.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * Auxiliary class for adding new user to the database
 */
public class RegisterModel {
    Connection connection;
    private String login;
    private String name;
    private String surname;
    private String password;
    private String passwordCheck;
    private int age;
    private String phone;

    public RegisterModel(){
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

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public void setAge(int age){this.age = age;}
    public void setPhone(String phone){this.phone = phone;}

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public boolean validateLogin() throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT * from login_data where LOGIN = ?";
        boolean result = false;
        try {
            pr = this.connection.prepareStatement(query);
            pr.setString(1, this.login);
            rs = pr.executeQuery();
            result = !rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }
        return result;
    }
    public boolean validatePasswords(){
        return this.password.equals(this.passwordCheck);
    }

    public boolean validatePasswordStrength(){
        return this.password.length() >= 6;
    }

    private void addLogin(int userId) throws SQLException{
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "INSERT INTO login_data VALUES(?, ?, ?, ?, ?)";
        String salt = BCrypt.gensalt();
        try {
            pr = this.connection.prepareStatement(query);
            pr.setString(1, this.login);
            //pr.setString(2, BCrypt.hashpw(this.password, salt));
            pr.setString(2, this.password);
            pr.setString(3, "N");
            pr.setString(4, salt);
            pr.setInt(5, userId);
            int k = pr.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }
    }

    private int addUser() throws SQLException{
        PreparedStatement pr = null;
        ResultSet rs = null;
        int userId = -1;
        String query = "INSERT INTO users (name, surname, phone, age) VALUES (?, ?, ?, ?)";
        try {
            pr = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pr.setString(1, this.name);
            pr.setString(2, this.surname);
            pr.setString(3, this.phone);
            pr.setInt(4, this.age);
            int rowsAffected = pr.executeUpdate();
            if (rowsAffected == 1) {
                rs = pr.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }
        return userId;
    }

    public void registerUser(){
        try {
            int userId = this.addUser();
            this.addLogin(userId);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
