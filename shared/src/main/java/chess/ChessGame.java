package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
	private ChessBoard myBoard;
	private TeamColor whichColorTurn;

	public ChessGame() {
		myBoard = new ChessBoard();
		myBoard.resetBoard();
		whichColorTurn = TeamColor.WHITE;
	}

	public ChessBoard getMyBoard() {
		return myBoard;
	}

	/**
	 * @return Which team's turn it is
	 */
	public TeamColor getTeamTurn() {
		return whichColorTurn;
	}

	/**
	 * Set's which teams turn it is
	 *
	 * @param team the team whose turn it is
	 */
	public void setTeamTurn(TeamColor team) {
		whichColorTurn = team;
	}


	/**
	 * Enum identifying the 2 possible teams in a chess game
	 */
	public enum TeamColor {
		WHITE,
		BLACK
	}

	/**
	 * Gets a valid moves for a piece at the given location
	 *
	 * @param startPosition the piece to get valid moves for
	 * @return Set of valid moves for requested piece, or null if no piece at
	 * startPosition
	 */
	public Collection<ChessMove> validMoves(ChessPosition startPosition) {
		//If there is no piece at that location, this method returns null
		ChessPiece currentPiece = myBoard.getPiece(startPosition);
		if (currentPiece == null) {
			return null;
		}

		//moves to be returned
		Collection<ChessMove> safeMoves = new ArrayList<>();
		//moves not processed for check
		Collection<ChessMove> givenMoves = currentPiece.pieceMoves(myBoard, startPosition);

		//process each move
		for (ChessMove move : givenMoves) {
			//test move
			ChessPiece savedPiece = myBoard.getPiece(move.getEndPosition());
			myBoard.addPiece(move.getEndPosition(), currentPiece);
			myBoard.addPiece(startPosition, null);

			//add moves that don't cause check
			if (!isInCheck(currentPiece.getTeamColor())
					&& (currentPiece.getPieceType() != ChessPiece.PieceType.KING
					|| Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) <= 1)) {
				safeMoves.add(move);
			}
			//add Castle
			else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING
					&& Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) > 1) {
				addCastleValidMoves(safeMoves, startPosition, move, currentPiece, savedPiece);
			}

			//back to original position
			myBoard.addPiece(move.getEndPosition(), savedPiece);
			myBoard.addPiece(startPosition, currentPiece);
		}
		return safeMoves;
	}


	private void addCastleValidMoves(Collection<ChessMove> safeMoves, ChessPosition startPosition,
									 ChessMove move, ChessPiece currentPiece, ChessPiece savedPiece) {
		//back to original position
		myBoard.addPiece(move.getEndPosition(), savedPiece);
		myBoard.addPiece(startPosition, currentPiece);

		int castleDirection = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();

		if (isCastlePathClear(startPosition, currentPiece, castleDirection)) {
			safeMoves.add(move);
		}
	}

	private boolean isCastlePathClear(ChessPosition startPosition, ChessPiece currentPiece, int direction) {
		int myCol = startPosition.getColumn();
		int myRow = startPosition.getRow();
		boolean pathClear = true;

		int steps = (direction == 2) ? 3 : 4;
		int stepIncrement = (direction == 2) ? 1 : -1;

		for (int i = 0; i < steps; i++) {
			ChessPosition betweenPosition = new ChessPosition(myRow, myCol + (i * stepIncrement));
			ChessPiece heldPiece = myBoard.getPiece(betweenPosition);

			// Test move
			myBoard.addPiece(betweenPosition, currentPiece);
			myBoard.addPiece(startPosition, null);

			if (isInCheck(currentPiece.getTeamColor())) {
				pathClear = false;
			}

			// Back to original position
			myBoard.addPiece(betweenPosition, heldPiece);
			myBoard.addPiece(startPosition, currentPiece);
		}

		return pathClear;
	}

	/**
	 * Makes a move in a chess game
	 *
	 * @param move chess move to preform
	 * @throws InvalidMoveException if move is invalid
	 */
	public void makeMove(ChessMove move) throws InvalidMoveException {
		ChessPosition startSquare = move.getStartPosition();
		ChessPosition endSquare = move.getEndPosition();
		ChessPiece currentPiece = myBoard.getPiece(startSquare);

		//check if square is empty
		if (currentPiece == null) {
			throw new InvalidMoveException("Invalid move: theres no piece there!");
		}
		//check for turn
		else if (currentPiece.getTeamColor() != whichColorTurn) {
			throw new InvalidMoveException("Invalid move: It's not your turn!");
		}

		//check all valid moves
		boolean moveNotMade = true;
		Collection<ChessMove> possibleMoves = validMoves(startSquare);
		for (ChessMove possibleMove : possibleMoves) {
			if (possibleMove.equals(move)) {
				//make the move
				myBoard.addPiece(endSquare, currentPiece);
				myBoard.addPiece(startSquare, null);
				moveNotMade = false;
				currentPiece.setNotMoved(false);

				// Pawn promotion
				if (move.getPromotionPiece() != null) {
					currentPiece.setPieceType(move.getPromotionPiece());
				}

				// Handle special moves
				makeMoveCastling(currentPiece, startSquare, endSquare);
				makeMoveEnPassant(currentPiece, move);

				// Track pawn double move
				if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
						Math.abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
					currentPiece.setDoubleMoved(true);
				} else {
					resetDoubleMoveFlags();
				}

				switchTeamTurn();
			}
		}

		if (moveNotMade) {
			throw new InvalidMoveException("Invalid move: The piece cannot move there.");
		}
	}

	// Helper function to handle castling
	private void makeMoveCastling(ChessPiece currentPiece, ChessPosition startSquare, ChessPosition endSquare) {
		int travelDistance = endSquare.getColumn() - startSquare.getColumn();
		if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(travelDistance) > 1) {
			int row = startSquare.getRow();
			if (travelDistance == 2) {
				ChessPiece myRook = myBoard.getPiece(new ChessPosition(row, 8));
				myBoard.addPiece(new ChessPosition(row, 8), null);
				myBoard.addPiece(new ChessPosition(row, 6), myRook);
			}

			if (travelDistance == -2) {
				ChessPiece myRook = myBoard.getPiece(new ChessPosition(row, 1));
				myBoard.addPiece(new ChessPosition(row, 1), null);
				myBoard.addPiece(new ChessPosition(row, 4), myRook);
			}

		}
	}

	// Helper function to handle en passant
	private void makeMoveEnPassant(ChessPiece currentPiece, ChessMove move) {
		if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
			ChessPosition enemyPosition =
					new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
			if (myBoard.getPiece(enemyPosition) != null && myBoard.getPiece(enemyPosition).isDoubleMoved()) {
				myBoard.addPiece(enemyPosition, null);
			}
		}
	}

	// Helper function to reset double move flags for all pieces
	private void resetDoubleMoveFlags() {
		for (int col = 1; col < 9; col++) {
			for (int row = 1; row < 9; row++) {
				ChessPiece piece = myBoard.getPiece(new ChessPosition(row, col));
				if (piece != null) {
					piece.setDoubleMoved(false);
				}
			}
		}
	}

	// Helper function to switch turns
	private void switchTeamTurn() {
		switch (getTeamTurn()) {
			case WHITE:
				setTeamTurn(TeamColor.BLACK);
				break;
			case BLACK:
				setTeamTurn(TeamColor.WHITE);
				break;
		}
	}


	/**
	 * finds this team's king
	 * added helper function
	 */
	private ChessPosition findMyKing(TeamColor teamColor) {
		for (int col = 1; col < 9; col++) {
			for (int row = 1; row < 9; row++) {
				ChessPosition mySquare = new ChessPosition(row, col);
				ChessPiece king = myBoard.getPiece(mySquare);

				if (king != null && king.getPieceType() == ChessPiece.PieceType.KING && king.getTeamColor() == teamColor) {
					return mySquare;
				}
			}
		}
		return null;
	}

	/**
	 * Determines if the given team is in check
	 *
	 * @param teamColor which team to check for check
	 * @return True if the specified team is in check
	 */
	public boolean isInCheck(TeamColor teamColor) {
		ChessPosition myKingPosition = findMyKing(teamColor);

		for (int col = 1; col < 9; col++) {
			for (int row = 1; row < 9; row++) {
				ChessPosition mySquare = new ChessPosition(row, col);
				ChessPiece currentPiece = myBoard.getPiece(mySquare);

				if (currentPiece == null || currentPiece.getTeamColor() == teamColor) {
					continue;
				}

				Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(myBoard, mySquare);

				for (ChessMove move : enemyMoves) {
					if (move.getEndPosition().equals(myKingPosition)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Helper for checkmate and stalemate
	 *
	 * @param teamColor which team to check for checkmate
	 * @return True if the specified team has no valid moves
	 */
	private boolean hasAnyValidMove(TeamColor teamColor) {
		for (int col = 1; col < 9; col++) {
			for (int row = 1; row < 9; row++) {
				ChessPosition mySquare = new ChessPosition(row, col);
				ChessPiece currentPiece = myBoard.getPiece(mySquare);

				if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
					Collection<ChessMove> myMoves = validMoves(mySquare);
					if (!myMoves.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Determines if the given team is in checkmate
	 *
	 * @param teamColor which team to check for checkmate
	 * @return True if the specified team is in checkmate
	 */
	public boolean isInCheckmate(TeamColor teamColor) {
		return isInCheck(teamColor) && !hasAnyValidMove(teamColor);
	}

	/**
	 * Determines if the given team is in stalemate, which here is defined as having
	 * no valid moves
	 *
	 * @param teamColor which team to check for stalemate
	 * @return True if the specified team is in stalemate, otherwise false
	 */
	public boolean isInStalemate(TeamColor teamColor) {
		return !isInCheck(teamColor) && !hasAnyValidMove(teamColor);
	}

	/**
	 * Sets this game's chessboard with a given board
	 *
	 * @param board the new board to use
	 */
	public void setBoard(ChessBoard board) {
		myBoard = board;
	}

	/**
	 * Gets the current chessboard
	 *
	 * @return the chessboard
	 */
	public ChessBoard getBoard() {
		return myBoard;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChessGame chessGame = (ChessGame) o;
		return Objects.equals(myBoard, chessGame.myBoard) && whichColorTurn == chessGame.whichColorTurn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(myBoard, whichColorTurn);
	}

	@Override
	public String toString() {
		return "ChessGame{" +
				"myBoard=" + myBoard +
				", whichColorTurn=" + whichColorTurn +
				'}';
	}

}


