package databaseConnection;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try
        {
            FileReader fileReader = new FileReader("src/main/resources/databaseConfig.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(fileReader);
            String cname = json.get("cname").toString();
            String username = json.get("username").toString();
            String password = json.get("password").toString();
            String url = json.get("url").toString();

            System.out.println(password);
            Class.forName(cname);
            return DriverManager.getConnection(url, username, password);
        }catch(ClassNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
