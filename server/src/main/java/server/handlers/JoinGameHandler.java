package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import service.GameService;
import service.requestresult.JoinGameRequest;
import service.requestresult.MessageResult;
import spark.Request;
import spark.Response;
import spark.Route;


public class JoinGameHandler implements Route {
	private final GameService gameService;
	private final Gson gson = new Gson();

	public JoinGameHandler(GameService gameService) {
		this.gameService = gameService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		String authToken = req.headers("Authorization");
		JoinGameRequest bodyRequest = gson.fromJson(req.body(), JoinGameRequest.class);
		int gameID = bodyRequest.gameID();
		ChessGame.TeamColor teamColor = bodyRequest.playerColor();

		JoinGameRequest request = new JoinGameRequest(teamColor, gameID, authToken);
		MessageResult result = gameService.joinGame(request);


		if (result.message() != null) {
			if (result.message().equals("Error: unauthorized")) {
				res.status(401);
				throw new ResponseException(401, result.message());
			} else if (result.message().equals("Error: bad request")) {
				res.status(400);
				throw new ResponseException(400, result.message());
			} else if (result.message().equals("Error: already taken")) {
				res.status(403);
				throw new ResponseException(403, result.message());
			} else {
				res.status(500);
				throw new ResponseException(500, result.message());
			}
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}