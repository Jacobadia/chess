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
    private TeamColor whichColorTurn;

    public ChessGame() {
        myBoard = new ChessBoard();
        myBoard.resetBoard();
        whichColorTurn = TeamColor.WHITE;
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

        ChessPosition startSquare = move.getStartPosition();
        ChessPosition endSquare = move.getEndPosition();
        ChessPiece currentPiece = myBoard.getPiece(startSquare);

        //check if square is empty
        if(currentPiece == null) {
            throw new InvalidMoveException("Invalid move: theres no piece there!");
        }
        //check for turn
        else if(currentPiece.getTeamColor() != whichColorTurn) {
            throw new InvalidMoveException("Invalid move: It's not your turn!");
        }

        //check all valid moves
        boolean moveNotMade = true;
        Collection<ChessMove> possibleMoves = validMoves(startSquare);
        for( ChessMove possibleMove : possibleMoves) {
            if (possibleMove.equals(move)) {
                //make the move
                myBoard.addPiece(endSquare, currentPiece);
                myBoard.addPiece(startSquare, null);
                moveNotMade = false;

                //pawn promotion
                if (move.getPromotionPiece() != null) {
                    currentPiece.setPieceType(move.getPromotionPiece());
                }

                //en Passant
                if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    ChessPosition enemyPosition =
                            new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                    if (myBoard.getPiece(enemyPosition) != null && myBoard.getPiece(enemyPosition).isDoubleMoved()) {
                        myBoard.addPiece(enemyPosition, null);
                    }
                }

                //pawn double move tacker
                if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN
                        && Math.abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
                    currentPiece.setDoubleMoved(true);
                }
                //reset all pieces to not doubled moved
                else {
                    for (int col = 1; col < 9; col++) {
                        for (int row = 1; row < 9; row++) {
                            ChessPosition mySquare = new ChessPosition(row,col);
                            ChessPiece myPiece = myBoard.getPiece(mySquare);
                            if (myPiece != null){
                                myPiece.setDoubleMoved(false);
                            }
                        }
                    }
                }

                //change team turn
                switch (getTeamTurn()) {
                    case WHITE:
                        setTeamTurn(TeamColor.BLACK);
                        break;
                    case BLACK:
                        setTeamTurn(TeamColor.WHITE);
                        break;
                }
            }
        }
        if(moveNotMade) {
            throw new InvalidMoveException("Invalid move: The piece cannot move there.");
        }
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

                if (king != null && king.getPieceType() == ChessPiece.PieceType.KING && king.getTeamColor() == teamColor){
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

                if (currentPiece != null && currentPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> enemyMoves = currentPiece.pieceMoves(myBoard, mySquare);
                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(myKingPosition)) {
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
        if (isInCheck(teamColor)) {
            for (int col = 1; col < 9; col++) {
                for (int row = 1; row < 9; row++) {
                    ChessPosition mySquare = new ChessPosition(row,col);
                    ChessPiece currentPiece = myBoard.getPiece(mySquare);

                    if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
                        Collection<ChessMove> myMoves = validMoves(mySquare);
                        if (!myMoves.isEmpty()){return false;}
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            for (int col = 1; col < 9; col++) {
                for (int row = 1; row < 9; row++) {
                    ChessPosition mySquare = new ChessPosition(row,col);
                    ChessPiece currentPiece = myBoard.getPiece(mySquare);

                    if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
                        Collection<ChessMove> myMoves = validMoves(mySquare);
                        if (!myMoves.isEmpty()){return false;}
                    }
                }
            }
            return true;
        }
        return false;
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
}
