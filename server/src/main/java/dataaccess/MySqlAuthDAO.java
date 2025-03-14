package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.sql.Types;


public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() {
        try {
            configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure database", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authExists(auth.authToken())) {
            throw new DataAccessException("AuthToken Taken");
        }

        var statement = "INSERT INTO AuthTokens (authtoken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String aToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authtoken, username FROM AuthTokens WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, aToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authtoken"),
                                rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Auth Token Does Not Exist");
        }
        throw new DataAccessException("Auth Token Does Not Exist");
    }

    @Override
    public boolean authExists(String auth) {
        try {
            getAuth(auth);
            return true;
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Auth Token Does Not Exist")) {
                return false;
            }
        }
		return false;
	}


    @Override
    public void clearAllAuths() throws DataAccessException {
        var statement = "DELETE FROM AuthTokens Constant…";
        executeUpdate(statement);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authExists(authToken)) {
            throw new DataAccessException("Auth Token Does Not Exist");
        }
        var statement = "DELETE FROM AuthTokens WHERE authtoken=?";
        executeUpdate(statement, authToken);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, Types.NULL);
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
                throw new DataAccessException("Unable to update database: " + e.getMessage());
        }
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(100) PRIMARY KEY,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
