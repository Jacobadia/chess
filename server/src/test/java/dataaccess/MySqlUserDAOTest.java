package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MySqlUserDAOTest {
	private static MySqlUserDAO userDAO;

	@BeforeAll
	static void setup() {
		userDAO = new MySqlUserDAO();
	}

	@BeforeEach
	void beforeEach() throws DataAccessException {
		userDAO.clearAllUsers();
		userDAO.createUser(new UserData("testUser", "password123", "test@example.com"));
	}

	@Test
	void createUserPositive() {
		UserData newUser = new UserData("newUser", "securePass", "new@example.com");
		assertDoesNotThrow(() -> userDAO.createUser(newUser));
		assertTrue(userDAO.userExists("newUser"));
	}

	@Test
	void createUserNegative() {
		assertThrows(DataAccessException.class,
				() -> userDAO.createUser(new UserData("testUser",
						"password123",
						"duplicate@example.com")));
	}

	@Test
	void getUserPositive() throws DataAccessException {
		UserData retrievedUser = userDAO.getUser("testUser");
		assertNotNull(retrievedUser);
		assertEquals("testUser", retrievedUser.username());
		assertEquals("test@example.com", retrievedUser.email());
	}

	@Test
	void getUserNegative() {
		assertThrows(DataAccessException.class, () -> userDAO.getUser("nonExistentUser"));
	}

	@Test
	void verifyPasswordPositive() {
		assertTrue(userDAO.verifyPassword("testUser", "password123"));
	}

	@Test
	void verifyPasswordNegative() {
		assertFalse(userDAO.verifyPassword("testUser", "wrongPassword"));
		assertFalse(userDAO.verifyPassword("nonExistentUser", "password123"));
	}

	@Test
	void userExistsPositive() {
		assertTrue(userDAO.userExists("testUser"));
	}

	@Test
	void userExistsNegative() {
		assertFalse(userDAO.userExists("nonExistentUser"));
	}

	@Test
	void clearAllUsersPositive() {
		assertDoesNotThrow(() -> userDAO.clearAllUsers());
		assertFalse(userDAO.userExists("testUser"));
	}
}





