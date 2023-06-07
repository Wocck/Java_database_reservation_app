package login;

import databaseConnection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

/**
 * Auxiliary class for login data collected from the user
 */


public class LoginModel {
    private int user_id;
    Connection connection;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public LoginModel() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        if(this.connection == null) {
            System.out.println("Failed to connect to DB: Exit 1");
            System.exit(1);
        }
    }

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public boolean isLogin(String login, String password, String isAdmin) throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT id_users from login_data where LOGIN = ? and PASSWD = ? and ADM = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setString(1, login);
            //pr.setString(2, BCrypt.hashpw(password, getSalt(login)));
            pr.setString(2, password);
            pr.setString(3, isAdmin);

            rs = pr.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id_users");
                UserSession userSession = UserSession.getInstance();
                userSession.setLoggedInUserId(userId);
                return true;
            }
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }
        return false;
    }

    public String getSalt(String login) throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs = null;

        String query = "SELECT saltt from login_data where LOGIN = ?";
        try {
            pr = this.connection.prepareStatement(query);
            pr.setString(1, login);

            rs = pr.executeQuery();
            rs.next();
            String salt = rs.getString("saltt");
            return salt;
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(pr != null && rs != null) {
                pr.close();
                rs.close();
            }
        }
        return "";
    }


}
