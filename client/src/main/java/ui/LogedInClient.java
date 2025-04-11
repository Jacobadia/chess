package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
		try{
		if (params.length == 1) {
			var gameName = params[0];
			var gameId = server.createGame(gameName, ReplMenu.myAuth);
			return "Game " + gameName + " created!";
		}
		} catch (Exception e) {
			throw new ResponseException(400, "Expected: <Game-Name>");
		}
		throw new ResponseException(400, "Expected: <Game-Name>");
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
		if (params.length != 2) {
			throw new ResponseException(400, "Expected: <Game-Number> <White|Black>");
		}

		int gameNum;
		try {
			gameNum = Integer.parseInt(params[0]);
		} catch (NumberFormatException e) {
			throw new ResponseException(400, "Invalid game number. Please enter a valid integer.");
		}

		if (!this.gameIndexMap.containsKey(gameNum)) {
			throw new ResponseException(400, "Invalid game number. Use 'list' to see available games.");
		}

		int gameId = this.gameIndexMap.get(gameNum);
		ChessGame.TeamColor playerColor;

		if (params[1].equalsIgnoreCase("WHITE")) {
			playerColor = ChessGame.TeamColor.WHITE;
		} else if (params[1].equalsIgnoreCase("BLACK")) {
			playerColor = ChessGame.TeamColor.BLACK;
		} else {
			throw new ResponseException(400, "Invalid color choice. Expected 'White' or 'Black'.");
		}

		try {
			server.joinGame(playerColor, gameId, ReplMenu.myAuth);
			ReplMenu.state = State.INGAME;
			((GameClient) ReplMenu.gameClient).setGameId(gameId);
			return String.format("You are on team %s.\nPress enter to continue", playerColor);
		} catch (Exception e) {
			if (e.getMessage().contains("already taken")) { // Adjust this condition based on your actual error handling
				throw new ResponseException(400, "That spot is already taken. Please choose another color.");
			}
			throw new ResponseException(400, "An error occurred while joining the game.");
		}
	}

    public String observeGame(String... params) throws ResponseException {
		int gameNum = Integer.parseInt(params[0]);
		if (!this.gameIndexMap.containsKey(gameNum)) {
			throw new ResponseException(400, "Invalid game number. Use 'list' to see available games.");
		}
		ReplMenu.state = State.INGAME;
		return String.format(" You are watching game %s. \n Press enter to continue", gameNum);
    }


	public String logout() throws ResponseException {
		server.logout(ReplMenu.myAuth);
		ReplMenu.state = State.SIGNEDOUT;
		return "Logged out successfully. \n Press enter to continue";
	}

	@Override
	public String help() {
		return """
				- create <game name> - Create a game
				- list - All games
				- join <game number> [White or Black] - Join as player
				- observe <game number> - Watch a game
				- logout - exit your account
				- quit - exit program
				- help - all possible commands
				""";
	}

}
