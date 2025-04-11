package ui;

import server.ServerFacade;
import static ui.EscapeSequences.*;
import chess.*;
import model.GameData;
import com.google.gson.Gson;
import exception.ResponseException;
import java.util.ArrayList;


public class GameClient implements BasicClient {
	private final ServerFacade server;
	private int currentGameId;

	private static final String[] COL_LABELS = {"a", EMQUAD, "b", EMQUAD, "c", EMQUAD,
			"d", EMQUAD, "e", EMQUAD, "f", EMQUAD, "g", EMQUAD, "h"};
	private static final String[] ROW_LABELS = {"8", "7", "6", "5", "4", "3", "2", "1"};

//	private static final String[][] INITIAL_BOARD = {
//			{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
//			{BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
//			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//			{WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
//			{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
//	};

	public GameClient(String url) {
		server = new ServerFacade(url);
	}

	public void setGameId(int gameId) {
		this.currentGameId = gameId;
	}

	@Override
	public String eval(String input) {
			var tokens = input.toLowerCase().split(" ");
			var cmd = (tokens.length > 0) ? tokens[0] : "help";
			return switch (cmd) {
				case "white" -> white();
				case "black" -> black();
				case "leave" -> leave();
				case "quit" -> "quit";
				case "help" -> help();
				default -> help();
			};
	}

	public String leave() {
		ReplMenu.state = State.SIGNEDIN;
		return "\n please type help to continue";
	}

	public ChessPiece[][] getBoardFromCurrentGame() throws ResponseException {
		// Retrieve the list of games from the server
		ArrayList<GameData> gameList = server.listGames(ReplMenu.myAuth);
		GameData currentGame = null;

		// Find the game whose id matches the stored currentGameId
		for (GameData game : gameList) {
			if (game.gameID() == currentGameId) {  // Adjust this if your GameData method is different
				currentGame = game;
				break;
			}
		}

		if (currentGame == null) {
			throw new ResponseException(404, "Game not found. Please try again.");
		}

		return currentGame.game().getMyBoard().getSquares();
	}

	public String white() {
		System.out.print(ERASE_SCREEN);

		ChessPiece[][] board = null;
		try {
			board = getBoardFromCurrentGame();

		for (int row = 0; row < 8; row++) {
			System.out.print(ROW_LABELS[row] + " ");
			for (int col = 0; col < 8; col++) {
				boolean isDarkSquare = (row + col) % 2 != 0;
				assert board != null;
				System.out.print((isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY)
						+ board[row][col].toString() + RESET_BG_COLOR);
			}
			System.out.println();
		}

		System.out.print("  ");
		for (String file : COL_LABELS) {
			System.out.print(file);
		}
		System.out.println();
		} catch (ResponseException e) {
			System.out.println("Error retrieving game board: " + e.getMessage());
		}
		return "";
	}

	public String black() {
		System.out.print(ERASE_SCREEN);

		ChessPiece[][] board = null;
		try {
			board = getBoardFromCurrentGame();

			for (int row = 7; row >= 0; row--) {  // Reverse row order
			System.out.print(ROW_LABELS[row] + " "); // Reverse row labels
			for (int col = 7; col >= 0; col--) { // Reverse column order
				boolean isDarkSquare = (row + col) % 2 != 0;
				System.out.print((isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY)
						+ board[row][col].toString() + RESET_BG_COLOR);
			}
			System.out.println();
		}

		System.out.print("  ");
		for (int i = COL_LABELS.length - 1; i >= 0; i--) { // Reverse column labels
			System.out.print(COL_LABELS[i]);
		}
		System.out.println();
		} catch (ResponseException e) {
			System.out.println("Error retrieving game board: " + e.getMessage());
		}
		return "";
	}



	@Override
	public String help() {
		return """
				- White - board from white view
				- Black - board from black view
				- leave - go back to the menu
				- quit - exit program
				- help - all possible commands
				""";
	}

}
