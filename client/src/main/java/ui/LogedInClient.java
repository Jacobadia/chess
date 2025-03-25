package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class LogedInClient implements BasicClient {
	private final ServerFacade server;
    private final Map<Integer, Integer> gameIndexMap;

	public LogedInClient(String url) {
		server = new ServerFacade(url);
        gameIndexMap = new HashMap<>();
	}

	@Override
	public String eval(String input) {
		try {
			var tokens = input.toLowerCase().split(" ");
			var cmd = (tokens.length > 0) ? tokens[0] : "help";
			var params = Arrays.copyOfRange(tokens, 1, tokens.length);
			return switch (cmd) {
				case "create" -> createGame(params);
				case "list" -> listGames();
				case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
				case "logout" -> logout();
				case "quit" -> "quit";
				case "help" -> help();
				default -> help();
			};
		} catch (ResponseException ex) {
			return ex.getMessage();
		}
	}

	public String createGame(String... params) throws ResponseException {
		if (params.length == 1) {
			var gameName = params[0];
			var gameId = server.createGame(gameName, ReplMenu.myAuth);
			return "Game created with ID: " + gameId;
		}
		throw new ResponseException(400, "Expected: <GAME NAME>");
	}

	public String listGames() throws ResponseException {
		List<GameData> games = server.listGames(ReplMenu.myAuth);
        gameIndexMap.clear();

        if (games.isEmpty()) {
			return "No games available.";
		}

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            int displayIndex = i + 1; // 1-based index for user
            gameIndexMap.put(displayIndex, game.gameID()); // Store mapping

            output.append(String.format("| %d. %s | White: %s | Black: %s |\n",
                    displayIndex,
                    game.gameName(),
                    game.whiteUsername() == null ? "None" : game.whiteUsername(),
                    game.blackUsername() == null ? "None" : game.blackUsername()));
        }

        return output.toString();
    }

	public String joinGame(String... params) throws ResponseException {
		if (params.length == 2) {
				int gameNum = Integer.parseInt(params[0]);
                if (!this.gameIndexMap.containsKey(gameNum)) {
                    throw new ResponseException(400, "Invalid game number. Use 'list' to see available games.");
                }
            try {
                int gameId = this.gameIndexMap.get(gameNum);

				ChessGame.TeamColor playerColor = null;
				if (params[1].equalsIgnoreCase("WHITE")) {
					playerColor = WHITE;
				}
				if (params[1].equalsIgnoreCase("BLACK")) {
					playerColor = BLACK;
				}

				server.joinGame(playerColor, gameId, ReplMenu.myAuth);
				ReplMenu.state = State.INGAME;
				return String.format(" You are on team %s. \n Type help to continue", playerColor);
			} catch (Exception e) {
				throw new ResponseException(400, "Expected: <Game-Number> <WHITE|BLACK>");
			}
		} else {
			throw new ResponseException(400, "Expected: <Game-Number> <WHITE|BLACK>");
		}

	}

    public String observeGame(String... params) throws ResponseException {
        return joinGame(params);
    }


	public String logout() throws ResponseException {
		server.logout(ReplMenu.myAuth);
		ReplMenu.state = State.SIGNEDOUT;
		return "Logged out successfully. \n please type help to continue";
	}

	@Override
	public String help() {
		return """
				- create <GAME NAME> - Create a game
				- list - All games
				- join <ID> [WHITE or BLACK] - Join as player
				- observe <ID> - Watch a game
				- logout - exit your account
				- quit - exit program
				- help - all possible commands
				""";
	}

}
