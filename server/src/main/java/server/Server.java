package server;

import dataaccess.*;
import server.handlers.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

	public int run(int desiredPort) {
		Spark.port(desiredPort);

		Spark.staticFiles.location("web");

		// Initialize DAOs and Services
		UserDAO userDAO = new MySqlUserDAO();
		MemoryAuthDAO authDAO = new MemoryAuthDAO();
		MemoryGameDAO gameDAO = new MemoryGameDAO();

		UserService userService = new UserService(userDAO, authDAO);
		GameService gameService = new GameService(authDAO, gameDAO);

		// Register your endpoints and handle exceptions here.
		Spark.post("/user", new RegisterHandler(userService));
		Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));
		Spark.post("/session", new LoginHandler(userService));
		Spark.delete("/session", new LogoutHandler(userService));
		Spark.get("/game", new GameListHandler(gameService));
		Spark.post("/game", new CreateGameHandler(gameService));
		Spark.put("/game", new JoinGameHandler(gameService));

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
