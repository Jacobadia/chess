package dataaccess;

import model.UserData;
import java.sql.*;



public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("UserName Taken");
        }

        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
        throw new DataAccessException("User Does Not Exist");
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        try {
            getUser(username);
            return true;
        } catch (DataAccessException e) {
            if (e.getMessage().equals("User Does Not Exist")) {
                return false;
            }
            throw e;
        }
    }


    @Override
    public void clearAllUsers() throws DataAccessException {
        var statement = "DROP TABLE IF EXISTS users";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws dataaccess.DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
//
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new dataaccess.DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
//        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(100) PRIMARY KEY,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws dataaccess.DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new dataaccess.DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
