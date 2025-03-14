package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;


public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() {
        try {
            configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure database", e);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (gameIDExists(game.gameID())) {
            throw new DataAccessException("Game ID already exists");
        }

        var statement = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM Games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                rs.getString("gameData") // fix: gameData needs to be stored as a JSON string
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Game not found");
        }
        throw new DataAccessException("Game not found");
    }

    @Override
    public boolean gameIDExists(int gameID) throws DataAccessException {
        try {
            getGame(gameID);
            return true;
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Game not found")) {
                return false;
            }
        }
		return false;
	}


    @Override
    public void clearAllGames() throws DataAccessException {
        var statement = "DELETE FROM Games";
        executeUpdate(statement);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!gameIDExists(game.gameID())) {
            throw new DataAccessException("Game not found");
        }

        var statement = "UPDATE Games SET whiteUsername=?, blackUsername=?, gameName=?, gameData=? WHERE gameID=?";
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), game.gameID());
        //fix game is not json
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param instanceof String p) {
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
            CREATE TABLE IF NOT EXISTS Games (
              `gameID` INT PRIMARY KEY,
              `whiteUsername` VARCHAR(100),
              `blackUsername` VARCHAR(100),
              `gameName` VARCHAR(255) NOT NULL,
              `gameData` TEXT NOT NULL,
              FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`) ON DELETE SET NULL,
              FOREIGN KEY (`blackUsername`) REFERENCES users(`username`) ON DELETE SET NULL
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
