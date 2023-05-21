package login;

public class UserSession {
    private static UserSession instance;
    private int loggedInUserId;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }
}
