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
	void createAuth_positive() throws DataAccessException {
		AuthData auth = new AuthData("token123", "testUser");
		authDAO.createAuth(auth);

		AuthData retrieved = authDAO.getAuth("token123");
		assertNotNull(retrieved);
		assertEquals("testUser", retrieved.username());
	}

	@Test
	void createAuth_negative() throws DataAccessException {
		authDAO.createAuth(new AuthData("duplicateToken", "testUser"));
		assertThrows(DataAccessException.class,
				() -> authDAO.createAuth(new AuthData("duplicateToken", "testUser")));
	}

	@Test
	void getAuth_positive() throws DataAccessException {
		authDAO.createAuth(new AuthData("validToken", "testUser"));
		assertNotNull(authDAO.getAuth("validToken"));
	}

	@Test
	void getAuth_negative() {
		assertThrows(DataAccessException.class, () -> authDAO.getAuth("invalidToken"));
	}

	@Test
	void authExists_positive() throws DataAccessException {
		authDAO.createAuth(new AuthData("existsToken", "testUser"));
		assertTrue(authDAO.authExists("existsToken"));
	}

	@Test
	void authExists_negative() {
		assertFalse(authDAO.authExists("nonExistentToken"));
	}

	@Test
	void deleteAuth_positive() throws DataAccessException {
		authDAO.createAuth(new AuthData("deleteToken", "testUser"));
		authDAO.deleteAuth("deleteToken");
		assertFalse(authDAO.authExists("deleteToken"));
	}

	@Test
	void deleteAuth_negative() {
		assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("nonExistentToken"));
	}

	@Test
	void clearAllAuths_positive() {
		assertDoesNotThrow(authDAO::clearAllAuths);
	}

}




