package service;

import dataaccess.DataAccessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requestresult.*;


import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService();
	}

	@Test
	void testRegister_Service_Success() {
		RegisterRequest request =
				new RegisterRequest("testUser", "password123", "test@example.com");

		assertDoesNotThrow(() -> {
			RegisterResult result = userService.register(request);
			assertNotNull(result);
			assertEquals("testUser", result.username());
			assertNotNull(result.authToken());
		});
	}

	@Test
	void testRegister_Service_Failure_UsernameTaken() {
		RegisterRequest request1 = new RegisterRequest("duplicateUser", "password123", "test@example.com");
		RegisterRequest request2 = new RegisterRequest("duplicateUser", "password456", "test2@example.com");

		assertDoesNotThrow(() -> userService.register(request1));

		Exception exception;
		exception = assertThrows(DataAccessException.class, () -> userService.register(request2));

		assertEquals("Username is already taken", exception.getMessage());
	}
}
