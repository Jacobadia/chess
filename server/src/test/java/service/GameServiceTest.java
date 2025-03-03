package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.ListGamesResult;
import service.requestresult.AuthTokenRequest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
	private GameService gameService;
	private UserService userService;
	private MemoryUserDAO userDAO;
	private MemoryAuthDAO authDAO;
	private MemoryGameDAO gameDAO;

	@BeforeEach
	void setUp() {
		userDAO = new MemoryUserDAO();
		authDAO = new MemoryAuthDAO();
		gameDAO = new MemoryGameDAO();
		userService = new UserService(userDAO, authDAO);
		gameService = new GameService(userDAO, authDAO, gameDAO);
	}

	@Test
	void testGameListSuccess() throws DataAccessException {
		GameData game =
				new GameData(12, "Dan", "jake", "name", new ChessGame());
		userDAO.createUser(new model.UserData("user1", "password1", "email1@example.com"));
		authDAO.createAuth(new model.AuthData("token1", "user1"));
		gameDAO.createGame(game);

		ListGamesResult result = gameService.listgames(new AuthTokenRequest("token1"));

		assertNotNull(result.games());
		ArrayList<GameData> games = new ArrayList<>();
		games.add(new GameData(12, "Dan", "jake", "name", null));
		assertEquals(games, result.games());
		assertNull(result.message());
	}

	@Test
	void testGameListFailure() throws DataAccessException {
		GameData game =
				new GameData(12, "Dan", "jake", "name", new ChessGame());
		userDAO.createUser(new model.UserData("user1", "password1", "email1@example.com"));
		authDAO.createAuth(new model.AuthData("token1", "user1"));
		gameDAO.createGame(game);

		ListGamesResult result = gameService.listgames(new AuthTokenRequest("token2"));
		assertEquals("Error: unauthorized", result.message());
	}

}

