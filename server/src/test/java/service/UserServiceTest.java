package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.RegisterRequest;
import service.requestresult.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
	private UserService userService;
	private MemoryUserDAO userDAO;
	private MemoryAuthDAO authDAO;

	@BeforeEach
	void setUp() {
		userDAO = new MemoryUserDAO();
		authDAO = new MemoryAuthDAO();
		userService = new UserService(userDAO, authDAO);
	}

	@Test
	void testRegister_Success() {
		RegisterRequest request = new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNotNull(result.authToken(), "Auth token should not be null on success.");
		assertEquals("testUser", result.username(), "Returned username should match the input.");
		assertNull(result.message(), "Error message should be null on success.");
	}

	@Test
	void testRegister_Failure_MissingFields() {
		RegisterRequest request = new RegisterRequest("", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNull(result.authToken(), "Auth token should be null on failure.");
		assertNull(result.username(), "Username should be null on failure.");
		assertEquals("Error: bad request", result.message(), "Should return 'Error: bad request'.");
	}
}

