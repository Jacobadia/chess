package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.handlers.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

	public int run(int desiredPort) {
		Spark.port(desiredPort);

		Spark.staticFiles.location("web");

		// Initialize DAOs and Services
		MemoryUserDAO userDAO = new MemoryUserDAO();
		MemoryAuthDAO authDAO = new MemoryAuthDAO();
		MemoryGameDAO gameDAO = new MemoryGameDAO();
		UserService userService = new UserService(userDAO, authDAO);
		GameService gameService = new GameService(userDAO, authDAO, gameDAO);

		// Register your endpoints and handle exceptions here.
		Spark.post("/user", new RegisterHandler(userService));
        Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));
		Spark.post("/session", new LoginHandler(userService));
		Spark.delete("/session", new LogoutHandler(userService));
		Spark.get("/game", new GameListHandler(gameService));

		//This line initializes the server and can be removed once you have a functioning endpoint
		Spark.init();

		Spark.awaitInitialization();
		return Spark.port();
	}

	public void stop() {
		Spark.stop();
		Spark.awaitStop();
	}
}
