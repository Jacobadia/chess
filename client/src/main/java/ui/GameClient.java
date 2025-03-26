package ui;

import server.ServerFacade;
import static ui.EscapeSequences.*;



public class GameClient implements BasicClient {
	private final ServerFacade server;
	private static final String[] COL_LABELS = {"a", EMQUAD, "b", EMQUAD, "c", EMQUAD,
			"d", EMQUAD, "e", EMQUAD, "f", EMQUAD, "g", EMQUAD, "h"};
	private static final String[] ROW_LABELS = {"8", "7", "6", "5", "4", "3", "2", "1"};

	private static final String[][] INITIAL_BOARD = {
			{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
			{BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
			{EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
			{WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
			{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
	};

	public GameClient(String url) {
		server = new ServerFacade(url);
	}

	@Override
	public String eval(String input) {
			var tokens = input.toLowerCase().split(" ");
			var cmd = (tokens.length > 0) ? tokens[0] : "help";
			return switch (cmd) {
				case "white" -> White();
				case "black" -> Black();
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

	public static String White() {
		System.out.print(ERASE_SCREEN);

		for (int row = 0; row < 8; row++) {
			System.out.print(ROW_LABELS[row] + " ");
			for (int col = 0; col < 8; col++) {
				boolean isDarkSquare = (row + col) % 2 != 0;
				System.out.print((isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY)
						+ INITIAL_BOARD[row][col] + RESET_BG_COLOR);
			}
			System.out.println();
		}

		System.out.print("  ");
		for (String file : COL_LABELS) {
			System.out.print(file);
		}
		System.out.println();
		return "";
	}

	public static String Black() {
		System.out.print(ERASE_SCREEN);

		for (int row = 7; row >= 0; row--) {  // Reverse row order
			System.out.print(ROW_LABELS[row] + " "); // Reverse row labels
			for (int col = 7; col >= 0; col--) { // Reverse column order
				boolean isDarkSquare = (row + col) % 2 != 0;
				System.out.print((isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY)
						+ INITIAL_BOARD[row][col] + RESET_BG_COLOR);
			}
			System.out.println();
		}

		System.out.print("  ");
		for (int i = COL_LABELS.length - 1; i >= 0; i--) { // Reverse column labels
			System.out.print(COL_LABELS[i]);
		}
		System.out.println();
		return "";
	}



	@Override
	public String help() {
		return """
				- White - board from white view
				- Black - board from black view
				- Back - go back to the menu
				- quit - exit program
				- help - all possible commands
				""";
	}

}
