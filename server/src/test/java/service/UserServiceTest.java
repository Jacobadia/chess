package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.AuthTokenRequest;
import service.requestresult.UserInfoRequest;
import service.requestresult.AuthUserNameResult;

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
		UserInfoRequest request =
				new UserInfoRequest("testUser", "password123", "test@example.com");
		AuthUserNameResult result = userService.register(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testRegisterFailureMissingFields() {
		UserInfoRequest request = new UserInfoRequest("", "password123", "test@example.com");
		AuthUserNameResult result = userService.register(request);

		assertNull(result.authToken());
		assertNull(result.username());
		assertEquals("Error: bad request", result.message());
	}

	@Test
	void testLoginSuccess() {
		UserInfoRequest request =
				new UserInfoRequest("testUser", "password123", "test@example.com");
		userService.register(request);
		AuthUserNameResult result = userService.login(request);

		assertNotNull(result.authToken());
		assertEquals("testUser", result.username());
		assertNull(result.message());
	}

	@Test
	void testLoginFailureWrongPassword() {
		UserInfoRequest request1 =
				new UserInfoRequest("testUser", "password123", "test@example.com");
		UserInfoRequest request2 =
				new UserInfoRequest("testUser", "password1234", "test@example.com");
		userService.register(request1);
		AuthUserNameResult result = userService.login(request2);

		assertNull(result.authToken());
		assertNull(result.username());
		assertEquals("Error: unauthorized", result.message());
	}

	@Test
	void testLogoutSuccess() {
		UserInfoRequest request =
				new UserInfoRequest("testUser", "password123", "test@example.com");
		AuthUserNameResult result1 = userService.register(request);

		AuthUserNameResult result2 = userService.logout(new AuthTokenRequest(result1.authToken()));

		assertNull(result2.message());
		assertNull(result2.authToken());
	}

	@Test
	void testLogoutFailureNotLoggedIn() {
		UserInfoRequest request =
				new UserInfoRequest("testUser", "password123", "test@example.com");
		AuthUserNameResult result1 = userService.register(request);

		userService.logout(new AuthTokenRequest(result1.authToken()));
		AuthUserNameResult result3 = userService.logout(new AuthTokenRequest(result1.authToken()));

		assertEquals("Error: unauthorized", result3.message());
	}

}

