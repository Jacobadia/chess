package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MySqlAuthDAOTest {
	private static MySqlAuthDAO authDAO;
	private static MySqlUserDAO userDAO;

	@BeforeAll
	static void setup() {
		authDAO = new MySqlAuthDAO();
		userDAO = new MySqlUserDAO();
	}

	@BeforeEach
	void beforeEach() throws DataAccessException {
		authDAO.clearAllAuths();
		userDAO.clearAllUsers();

		userDAO.createUser(new UserData("testUser", "password123", "test@example.com"));
	}

	@Test
	void createAuthPositive() throws DataAccessException {
		AuthData auth = new AuthData("token123", "testUser");
		authDAO.createAuth(auth);

		AuthData retrieved = authDAO.getAuth("token123");
		assertNotNull(retrieved);
		assertEquals("testUser", retrieved.username());
	}

	@Test
	void createAuthNegative() throws DataAccessException {
		authDAO.createAuth(new AuthData("duplicateToken", "testUser"));
		assertThrows(DataAccessException.class,
				() -> authDAO.createAuth(new AuthData("duplicateToken", "testUser")));
	}

	@Test
	void getAuthPositive() throws DataAccessException {
		authDAO.createAuth(new AuthData("validToken", "testUser"));
		assertNotNull(authDAO.getAuth("validToken"));
	}

	@Test
	void getAuthNegative() {
		assertThrows(DataAccessException.class, () -> authDAO.getAuth("invalidToken"));
	}

	@Test
	void authExistsPositive() throws DataAccessException {
		authDAO.createAuth(new AuthData("existsToken", "testUser"));
		assertTrue(authDAO.authExists("existsToken"));
	}

	@Test
	void authExistsNegative() {
		assertFalse(authDAO.authExists("nonExistentToken"));
	}

	@Test
	void deleteAuthPositive() throws DataAccessException {
		authDAO.createAuth(new AuthData("deleteToken", "testUser"));
		authDAO.deleteAuth("deleteToken");
		assertFalse(authDAO.authExists("deleteToken"));
	}

	@Test
	void deleteAuthNegative() {
		assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("nonExistentToken"));
	}

	@Test
	void clearAllAuthsPositive() {
		assertDoesNotThrow(authDAO::clearAllAuths);
	}

}




