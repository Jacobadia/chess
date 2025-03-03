package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.ClearResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
	private ClearService clearService;
	private MemoryUserDAO userDAO;
	private MemoryAuthDAO authDAO;
	private MemoryGameDAO gameDAO;

	@BeforeEach
	void setUp() {
		userDAO = new MemoryUserDAO();
		authDAO = new MemoryAuthDAO();
		gameDAO = new MemoryGameDAO();
		clearService = new ClearService(userDAO, authDAO, gameDAO);
	}

	@Test
	void testClear_Success() throws DataAccessException {
		userDAO.createUser(new model.UserData("user1", "password1", "email1@example.com"));
		authDAO.createAuth(new model.AuthData("token1", "user1"));
		gameDAO.createGame(
				new GameData(12, "Dan", "jake", "name", new ChessGame()));

		assertNotNull(userDAO.getUser("user1"));
		assertNotNull(authDAO.getAuth("token1"));
		assertNotNull(gameDAO.getGame(12));

		ClearResult result = clearService.clear();

		assertNull(result.message());

		assertThrows(DataAccessException.class, () -> userDAO.getUser("user1"));
		assertThrows(DataAccessException.class, () -> authDAO.getAuth("token1"));
		assertThrows(DataAccessException.class, () -> gameDAO.getGame(12));
	}
}
