package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard myBoard;

    public ChessGame() {
        myBoard = new ChessBoard();
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
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
        if (currentPiece == null){
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

            //look for if move causes check
            if(!isInCheck(currentPiece.getTeamColor())) {
                safeMoves.add(move);
            }

            //back to original position
            myBoard.addPiece(move.getEndPosition(), savedPiece);
            myBoard.addPiece(startPosition, currentPiece);
        }
        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }


    /**
     * finds this team's king
     * added helper function
     */
    private ChessPosition findMyKing(TeamColor teamColor) {
        for (int col = 1; col < 9; col++) {
            for (int row = 1; row < 9; row++) {
                ChessPosition mySquare = new ChessPosition(row,col);
                ChessPiece king = myBoard.getPiece(mySquare);

                if (king.getPieceType() == ChessPiece.PieceType.KING && king.getTeamColor() == teamColor){
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
                ChessPosition mySquare = new ChessPosition(row,col);
                ChessPiece currentPiece = myBoard.getPiece(mySquare);

                if (currentPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(myBoard, mySquare);
                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition() == myKingPosition) {
                            return true;
                        }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
