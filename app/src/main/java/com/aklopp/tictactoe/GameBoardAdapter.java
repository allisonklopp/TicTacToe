package com.aklopp.tictactoe;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Allison on 6/5/2015.
 */
public class GameBoardAdapter extends ArrayAdapter<GameSpace> {
    private static final int NUMBER_OF_BOARD_SQUARES = 9;
    private static final int BOARD_DIMENSION = 3;
    private GameSpace.State mUserMarker;
    private GameSpace[][] mGameBoard;
    private GameAI mGameAI;

    private GameBoardAdapter mAdapter;
    private OnGameOverListener mOnGameOverListener;
    private boolean mIsGameInProgress = false;

    /**
     * Constructor
     * @param context
     */
    public GameBoardAdapter(Context context, GameSpace.State userMarker) {
        super(context, -1);
        mAdapter = this;

        setUserMarker(userMarker);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameSpace space = getSpace(position);

        space.setOnStateChangedListener(new GameSpace.OnStateChangeListener() {
            @Override
            public void onStateChange(GameSpace.State state) {
                makeAIMoveOnBoard();
            }
        });

        convertView = space;

        return convertView;
    }

    /**
     * Attempt to place an AI move on the board.
     */
    private void makeAIMoveOnBoard() {
        mIsGameInProgress = true;

        if(!checkIfGameIsOver())
            mGameBoard = mGameAI.makeMove(mGameBoard);

        checkIfGameIsOver();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Check if the game is over.
     * @return true if game is over, false otherwise.
     */
    private boolean checkIfGameIsOver() {
        boolean isGameOver = mGameAI.isGameOver(mGameBoard);

        if(isGameOver) {
            freezeBoard();
            mOnGameOverListener.onGameOver(mGameAI.getGameOverOutcome(mGameBoard));
        }
        return isGameOver;
    }

    /**
     * Prevent the user from clicking on the board squares.
     */
    private void freezeBoard() {
        for(int row = 0; row < BOARD_DIMENSION; row ++)
        {
            for (int col = 0; col < BOARD_DIMENSION; col ++)
            {
                mGameBoard[row][col].setClickable(false);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * Get the game space associated with the given position.
     * @param position
     * @return the game space
     */
    private GameSpace getSpace(int position) {
        int col = position % 3;
        int row = 2;
        if(position <= 2)
            row = 0;
        else if(position <= 5)
            row = 1;

        return mGameBoard[row][col];
    }

    @Override
    public int getCount()
    {
        return NUMBER_OF_BOARD_SQUARES;
    }

    /**
     * Initialize a new game with all blank spaces.
     */
    public void initNewGame() {
        mGameBoard = new GameSpace[BOARD_DIMENSION][BOARD_DIMENSION]; // TODO: magic
        for (int row = 0; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                GameSpace space = new GameSpace(getContext(), mUserMarker);
                mGameBoard[row][col] = space;
            }
        }
        this.notifyDataSetChanged();

        // If the user is playing as O, the AI gets to move first.
        if(mUserMarker.equals(GameSpace.State.O_MARK))
        {
            makeAIMoveOnBoard();
        }
    }

    /**
     * Sets the game over listener.
     * @param listener
     */
    public void setOnGameOverListener(OnGameOverListener listener)
    {
        this.mOnGameOverListener = listener;
    }

    /**
     * Gets the status of the game.
     * @return true if game is in progress, false otherwise.
     */
    public boolean isGameInProgress() {
        return mIsGameInProgress;
    }

    /**
     * Set the marker for the user to use.
     * @param userMarker
     */
    public void setUserMarker(GameSpace.State userMarker) {
        this.mUserMarker = userMarker;

        // Set the opposite marker for the Game AI
        if(userMarker.equals(GameSpace.State.O_MARK))
            mGameAI = new GameAI(GameSpace.State.X_MARK);
        else
            mGameAI = new GameAI(GameSpace.State.O_MARK);

        initNewGame();
    }

    /**
     * Interface to listen for changes of the current state.
     */
    public interface OnGameOverListener {

        /**
         * Called upon a change of the current value.
         */
        void onGameOver(Outcome outcome);
    }
}