package com.aklopp.tictactoe;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * The AI that makes moves against the player.
 * Created by Allison on 6/5/2015.
 */
public class GameAI
{
    /**
     * One dimension (l or w) of the board.
     */
    private static final int BOARD_DIMENSION = 3;

    /**
     * Determines how many moves ahead the AI looks when deciding where to play next.
     */
    private static final int MOVES_AHEAD = 4;

    /**
     * Determines which marker type the game AI will use.
     */
    private GameSpace.State mAIMarker;

    /**
     * Determines which marker the user will use.
     */
    private GameSpace.State mUserMarker;

    /**
     * The grid containing the game squares.
     */
    private GameSpace[][] mGameBoard;

    /**
     * The possible winning patterns for a 3 x 3 grid.
     * In binary format. '1's signify spots where the designated marker is on the board.
     */
    private final int[] mWinningPatterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // columns
            0b100010001, 0b001010100               // diagonals
    };


    /**
     * Constructor
     */
    public GameAI()
    {
        // Intentionally blank
    }

    /**
     * Sets the marker for the AI and human players.
     *
     * @param marker
     */
    public void setMarker(GameSpace.State marker)
    {
        this.mAIMarker = marker;
        if (marker == GameSpace.State.O_MARK)
            this.mUserMarker = GameSpace.State.X_MARK;
        else
            this.mUserMarker = GameSpace.State.O_MARK;
    }

    /**
     * Select and make a move on the gameboard
     *
     * @param gameBoard the pre-AI-move game board condition
     * @return the gameboard containing the AI's move
     */
    public GameSpace[][] makeMove(GameSpace[][] gameBoard)
    {
        mGameBoard = gameBoard;

        int[] bestPossibleMove = getGameWinningMove(mGameBoard);

        if (null == bestPossibleMove)
            bestPossibleMove = findBestMove(MOVES_AHEAD, mAIMarker);

        doMove(bestPossibleMove[0], bestPossibleMove[1]);

        return mGameBoard;
    }

    /**
     * Check the possible moves to see if one is a game winning move.
     *
     * @param gameBoard
     * @return first game winning move found, null if none found.
     */
    private int[] getGameWinningMove(GameSpace[][] gameBoard)
    {

        List<int[]> possibleMoves = getPossibleMoves(gameBoard);

        for (int[] move : possibleMoves)
        {
            // Try move
            gameBoard[move[0]][move[1]].setState(mAIMarker);

            // If success, return winning move
            if (hasWon(gameBoard, mAIMarker))
            {
                mGameBoard[move[0]][move[1]].setState(GameSpace.State.BLANK);
                return move;
            }

            // Undo move
            mGameBoard[move[0]][move[1]].setState(GameSpace.State.BLANK);
        }


        return null;
    }

    /**
     * Check if the game is over.
     *
     * @param gameBoard
     * @return true if game is over, false otherwise
     */
    public boolean isGameOver(GameSpace[][] gameBoard)
    {
        boolean isGridFull = false;
        boolean hasUserWon = false;
        boolean hasAIWon = false;

        // Check if out of blank spaces
        if (getPossibleMoves(gameBoard).isEmpty())
            isGridFull = true;

        // Check if the user has won the game
        if (hasWon(gameBoard, mUserMarker))
            hasUserWon = true;

        // Check if the AI has won the game
        if (hasWon(gameBoard, mAIMarker))
            hasAIWon = true;

        return isGridFull || hasUserWon || hasAIWon;
    }

    /**
     * Get the outcome of the completed game
     *
     * @param gameBoard
     * @return outcome of the game
     */
    public Outcome getGameOverOutcome(GameSpace[][] gameBoard)
    {
        Outcome gameOutcome = Outcome.TIE;

        // Check if the user has won
        if (hasWon(gameBoard, mUserMarker))
            gameOutcome = Outcome.WIN;

        // Check if the AI has won
        if (hasWon(gameBoard, mAIMarker))
            gameOutcome = Outcome.LOSE;

        return gameOutcome;
    }

    /**
     * Check if the marker indicated has won on the gameboard specified.
     *
     * @param gameBoard the gameboard to analyse for the win
     * @param marker    the marker for which to check win
     * @return true if the user of the indicated marker has won the game
     */
    private boolean hasWon(GameSpace[][] gameBoard, GameSpace.State marker)
    {
        // A blank 9-bit pattern for the 9 cells
        int pattern = 0b000000000;

        for (int row = 0; row < BOARD_DIMENSION; ++row)
        {
            for (int col = 0; col < BOARD_DIMENSION; ++col)
            {
                if (gameBoard[row][col].getState().equals(marker))
                {
                    // Stick 1 in corresponding spot to indicate marker location on the board
                    pattern |= (1 << (row * BOARD_DIMENSION + col));
                }
            }
        }

        // See if the pattern matches a winning pattern.
        for (int winningPattern : mWinningPatterns)
        {
            if ((pattern & winningPattern) == winningPattern)
                return true;
        }
        return false;
    }

    /**
     * Find the best possible next move and return it.
     *
     * @param numOfNextMoves
     * @param marker
     * @return best available move
     */
    private int[] findBestMove(int numOfNextMoves, GameSpace.State marker)
    {
        List<int[]> possibleMoves = getPossibleMoves(mGameBoard);

        int bestScore, currentScore;
        // If it's the AI, we want to start from low value and build a higher win chance
        if (marker.equals(mAIMarker))
            bestScore = Integer.MIN_VALUE;

            // If it's the user, we want to start from high value and lower chance of win
        else
            bestScore = Integer.MAX_VALUE;

        int bestRow = -1;
        int bestCol = -1;

        // Base case, will return this score
        if (numOfNextMoves == 0 || isGameOver(mGameBoard))
        {
            bestScore = getScore();
        } else
        {
            for (int[] move : possibleMoves)
            {
                // Try the move
                mGameBoard[move[0]][move[1]].setState(marker);

                if (marker.equals(mAIMarker))
                {
                    // Get score recursively
                    currentScore = findBestMove(numOfNextMoves - 1, mUserMarker)[2];

                    // If better score than previous choice, save the move
                    if (currentScore > bestScore)
                    {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }

                } else
                {  // user marker
                    // Get score recursively
                    currentScore = findBestMove(numOfNextMoves - 1, mAIMarker)[2];

                    // If lesser score than previous choice, save the move
                    if (currentScore < bestScore)
                    {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }

                // Undo the move
                mGameBoard[move[0]][move[1]].setState(GameSpace.State.BLANK);
            }
        }

        return new int[]{bestRow, bestCol, bestScore};
    }

    /**
     * Get a list of all the possible moves on the specified gameboard.
     *
     * @param gameBoard
     * @return list of possible moves
     */
    private List<int[]> getPossibleMoves(GameSpace[][] gameBoard)
    {
        List<int[]> possibleMoves = new ArrayList<>();

        // If game over, return empty list
        if (hasWon(gameBoard, mAIMarker) || hasWon(gameBoard, mUserMarker))
        {
            return possibleMoves;
        }

        for (int row = 0; row < BOARD_DIMENSION; row++)
        {
            for (int col = 0; col < BOARD_DIMENSION; col++)
            {
                // If it's a blank space, add it to the list
                if (gameBoard[row][col].getState().equals(GameSpace.State.BLANK))
                {
                    possibleMoves.add(new int[]{row, col});
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Get the heuristic score of the current board.
     *
     * @return score
     */
    public int getScore()
    {
        int score = 0;

        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2);  // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2);  // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2);  // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0);  // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1);  // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2);  // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2);  // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0);  // alternate diagonal
        return score;
    }

    /**
     * Evaluate the heuristic score of the given line defined by three sets of (row,col) parameters.
     *
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @param row3
     * @param col3
     * @return score
     */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3)
    {
        int score = 0;

        // First square
        if (mGameBoard[row1][col1].getState().equals(mAIMarker))
        {
            score = 1;
        } else if (mGameBoard[row1][col1].getState().equals(mUserMarker))
        {
            score = -1;
        }

        // Second square
        if (mGameBoard[row2][col2].getState().equals(mAIMarker))
        {
            // First square was marked the same.
            if (score == 1)
            {
                score = 10;
            }
            // First square had opposite marker.
            else if (score == -1)
            {
                return 0;
            }
            // Blank space
            else
            {
                score = 1;
            }
        } else if (mGameBoard[row2][col2].getState().equals(mUserMarker))
        {
            // First square had same marker.
            if (score == -1)
            {
                score = -10;
            }
            // First cell had opposite marker.
            else if (score == 1)
            {
                return 0;
            }
            // Blank space
            else
            {
                score = -1;
            }
        }

        // Third square
        if (mGameBoard[row3][col3].getState().equals(mAIMarker))
        {
            // First square and/or second square have same marker
            if (score > 0)
            {
                score *= 10;
            }
            // First square and/or second square have opposite marker
            else if (score < 0)
            {
                return 0;
            }
            // Both first and second squares are empty
            else
            {
                score = 1;
            }
        } else if (mGameBoard[row3][col3].getState().equals(mUserMarker))
        {
            // First square and/or second square hass same marker.
            if (score < 0)
            {
                score *= 10;
            }
            // First square and/or second square has opposite marker.
            else if (score > 1)
            {
                return 0;
            }
            // First and second squares are empty;
            else
            {
                score = -1;
            }
        }

        return score;
    }

    /**
     * Performs the specified move on the board.
     *
     * @param row
     * @param col
     */
    private void doMove(int row, int col)
    {
        mGameBoard[row][col].setState(mAIMarker);
    }

    /**
     * Testing purposes - dumb AI. Takes first empty spot on the board.
     *
     * @param spaces
     * @return board with completed move
     */
    private void takeFirstEmptySpot(GameSpace[][] spaces)
    {
        // Go in first untaken space
        for (int row = 0; row < BOARD_DIMENSION; row++)
        {
            for (int col = 0; col < BOARD_DIMENSION; col++)
            {
                if (spaces[row][col].getState().equals(GameSpace.State.BLANK))
                {
                    doMove(row, col);
                    return;
                }
            }
        }
    }

    /**
     * Print out the board for debugging purposes
     */
    public void printBoard(GameSpace[][] gameBoard)
    {
        String board = "";
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 3; col++)
            {
                GameSpace.State state = gameBoard[row][col].getState();
                if (state.equals(GameSpace.State.BLANK))
                    board += "[ ]";
                else if (state.equals(GameSpace.State.X_MARK))
                    board += "[X]";
                else if (state.equals(GameSpace.State.O_MARK))
                    board += "[O]";
            }
            board += "\n";
        }
        Log.i("GameAI", "Current board \n" + board);
    }
}
