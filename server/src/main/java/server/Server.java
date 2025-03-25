package server;

import dataaccess.*;
import server.handlers.*;
import service.GameService;
import service.UserService;
import spark.*;
import exception.ResponseException;

public class Server {

	public int run(int desiredPort) {
		Spark.port(desiredPort);

		Spark.staticFiles.location("web");

		// Initialize DAOs this is where you can switch from memory to SQL
		UserDAO userDAO = new MySqlUserDAO();
		AuthDAO authDAO = new MySqlAuthDAO();
		GameDAO gameDAO = new MySqlGameDAO();
		//Initialize Services
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
		Spark.exception(ResponseException.class, this::exceptionHandler);


		Spark.awaitInitialization();
		return Spark.port();
	}

	public void stop() {
		Spark.stop();
		Spark.awaitStop();
	}

	private void exceptionHandler(ResponseException ex, Request req, Response res) {
		res.status(ex.StatusCode());
		res.body(ex.toJson());
	}
}
