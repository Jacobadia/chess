package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.LogoutRequest;
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
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testRegister_Failure_MissingFields() {
		RegisterRequest request = new RegisterRequest("", "password123", "test@example.com");
		RegisterResult result = userService.register(request);

		assertNull(result.authToken());
		assertNull(result.username());
		assertEquals("Error: bad request", result.message());
	}

	@Test
	void testLogin_Success() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		userService.register(request);
		RegisterResult result = userService.login(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testLogin_Failure_WrongPassword() {
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
	void testLogout_Success() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result1 = userService.register(request);

		RegisterResult result2 = userService.logout(new LogoutRequest(result1.authToken()));

		assertNull(result2.message());
		assertNull(result2.authToken());
	}

	@Test
	void testLogout_Failure_NotLoggedIn() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");
		RegisterResult result1 = userService.register(request);

		userService.logout(new LogoutRequest(result1.authToken()));
		RegisterResult result3 = userService.logout(new LogoutRequest(result1.authToken()));

		assertEquals("Error: unauthorized", result3.message());
	}

}

