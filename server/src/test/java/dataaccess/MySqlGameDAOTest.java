package dataaccess;

import model.GameData;
import chess.ChessGame;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {
	private static MySqlGameDAO gameDAO;
	private static  MySqlUserDAO userDAO;

	@BeforeAll
	static void setup() {
		gameDAO = new MySqlGameDAO();
		userDAO = new MySqlUserDAO();
	}

	@BeforeEach
	void setUp() throws Exception {
		gameDAO.clearAllGames();
		userDAO.clearAllUsers();
		userDAO.createUser(new UserData("player1", "password1", "Player One"));
		userDAO.createUser(new UserData("player2", "password2", "Player Two"));
	}

	@Test
	void testCreateGamePositive() throws Exception {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(1,
				"player1",
				"player2",
				"Test Game",
				game);
		gameDAO.createGame(gameData);

		GameData retrievedGame = gameDAO.getGame(1);
		assertNotNull(retrievedGame);
		assertEquals("Test Game", retrievedGame.gameName());
	}

	@Test
	void testCreateGameNegativeDuplicateID() throws Exception {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(1,
				"player1",
				"player2",
				"Test Game",
				game);
		gameDAO.createGame(gameData);

		assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData));
	}

	@Test
	void testGetGamePositive() throws Exception {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(2,
				"player1",
				"player2",
				"Retrieve Game",
				game);
		gameDAO.createGame(gameData);

		GameData retrievedGame = gameDAO.getGame(2);
		assertNotNull(retrievedGame);
		assertEquals("Retrieve Game", retrievedGame.gameName());
	}

	@Test
	void testGetGameNegativeNotFound() {
		assertThrows(DataAccessException.class, () -> gameDAO.getGame(99));
	}

	@Test
	void testGameIDExistsPositive() throws Exception {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(3,
				"player1",
				"player2",
				"Exists Test",
				game);
		gameDAO.createGame(gameData);

		assertTrue(gameDAO.gameIDExists(3));
	}

	@Test
	void testGameIDExistsNegativeNotExists() {
		assertFalse(gameDAO.gameIDExists(100));
	}

	@Test
	void testUpdateGamePositive() throws Exception {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(4,
				"player1",
				"player2",
				"Initial Game",
				game);
		gameDAO.createGame(gameData);

		GameData updatedGameData = new GameData(4,
				"player1",
				"player2",
				"Updated Game",
				game);
		gameDAO.updateGame(updatedGameData);

		GameData retrievedGame = gameDAO.getGame(4);
		assertEquals("Updated Game", retrievedGame.gameName());
	}

	@Test
	void testUpdateGameNegativeNotExists() {
		ChessGame game = new ChessGame();
		GameData gameData = new GameData(5,
				"player1",
				"player2",
				"Non-Existent Game",
				game);

		assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameData));
	}

	@Test
	void testListGamesPositive() throws Exception {
		ChessGame game1 = new ChessGame();
		ChessGame game2 = new ChessGame();
		gameDAO.createGame(new GameData(6,
				"player1",
				"player2",
				"Game 1",
				game1));
		gameDAO.createGame(new GameData(7,
				"player1",
				"player2",
				"Game 2",
				game2));

		List<GameData> games = gameDAO.listGames();
		assertEquals(2, games.size());
	}

	@Test
	void testListGamesNegativeEmptyList() throws Exception {
		List<GameData> games = gameDAO.listGames();
		assertEquals(0, games.size());
	}

	@Test
	void testClearAllGamesPositive() throws Exception {
		ChessGame game = new ChessGame();
		gameDAO.createGame(new GameData(8,
				"player1",
				"player2",
				"Game to Clear",
				game));

		gameDAO.clearAllGames();
		assertFalse(gameDAO.gameIDExists(8));
	}
}

