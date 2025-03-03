package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.AuthTokenRequest;
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
	void testRegisterSuccess() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testRegisterFailureMissingFields() {
		RegisterRequest request = new RegisterRequest("", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNull(result.authToken());
		assertNull(result.username());
		assertEquals("Error: bad request", result.message());
	}

	@Test
	void testLoginSuccess() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		userService.register(request);
		RegisterResult result = userService.login(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testLoginFailureWrongPassword() {
		RegisterRequest request1 =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterRequest request2 =
				new RegisterRequest("testUser", "password1234", "test@example.com");
		userService.register(request1);
		RegisterResult result = userService.login(request2);

		assertNull(result.authToken());
		assertNull(result.username());
		assertEquals("Error: unauthorized", result.message());
	}

	@Test
	void testLogoutSuccess() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result1 = userService.register(request);

		RegisterResult result2 = userService.logout(new AuthTokenRequest(result1.authToken()));

		assertNull(result2.message());
		assertNull(result2.authToken());
	}

	@Test
	void testLogoutFailureNotLoggedIn() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result1 = userService.register(request);

		userService.logout(new AuthTokenRequest(result1.authToken()));
		RegisterResult result3 = userService.logout(new AuthTokenRequest(result1.authToken()));

		assertEquals("Error: unauthorized", result3.message());
	}

}

