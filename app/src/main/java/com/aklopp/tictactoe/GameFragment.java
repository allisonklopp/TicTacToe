package com.aklopp.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A fragment containg the gameboard, game settings and win/tie/lose rate views.
 * Created by Allison on 6/5/2015.
 */
public class GameFragment extends Fragment {
    /**
     * String of blank text used to blank out fields.
     */
    private static final String BLANK_TEXT = "";

    /**
     * The format of the decimal places to display for the outcome rates.
     */
    private static final String RATE_DECIMAL_FORMAT = "%.2f";

    /**
     * The percent sign character.
     */
    private static final char PERCENT_SIGN = '%';

    /**
     * The textview displaying the outcome of a game.
     */
    TextView mGameResultTextView;

    /**
     * The radio group containing the settings for user's marker.
     */
    private RadioGroup mMarkerRadioGroup;

    /**
     * The grid of tiles to display the game.
     */
    private GridView mGameBoard;

    /**
     * The textview displaying the current win rate.
     */
    private TextView mWinRateTextView;

    /**
     * The textview displaying the current lose rate.
     */
    private TextView mLoseRateTextView;

    /**
     * The textview displaying the current tie rate.
     */
    private TextView mTieRateTextView;

    /**
     * The number of wins by the user.
     */
    private int mNumberOfWins = 0;
    /**
     * The number of losses by the user.
     */
    private int mNumberOfLosses = 0;
    /**
     * The number of ties by the user.
     */
    private int mNumberOfTies = 0;
    /**
     * The number of completed games - games that resulted in an outcome.
     */
    private int mNumberOfCompletedGames = 0;

    /**
     * Constructor
     */
    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        mGameBoard = (GridView) rootView.findViewById(R.id.game_board);
        mGameResultTextView = (TextView) rootView.findViewById(R.id.game_result);

        mMarkerRadioGroup = (RadioGroup) rootView.findViewById(R.id.marker_selection);
        mMarkerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // If we just started a fresh game, user can still switch markers
                if (!((GameBoardAdapter)mGameBoard.getAdapter()).isGameInProgress())
                {
                    ((GameBoardAdapter)mGameBoard.getAdapter()).setUserMarker(getCurrentMarkerSetting());
                }
            }
        });

        initAdapter(mGameBoard);

        Button newGameButton = (Button) rootView.findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                mGameResultTextView.setText(BLANK_TEXT);
                initAdapter(mGameBoard);
            }
        });

        mWinRateTextView = (TextView) rootView.findViewById(R.id.win_rate);
        mLoseRateTextView = (TextView) rootView.findViewById(R.id.lose_rate);
        mTieRateTextView = (TextView) rootView.findViewById(R.id.tie_rate);

        // Initialize the outcome rates if there are stored rates.
        loadOutcomeRates();

        return rootView;
    }

    /**
     * Get the current marker selection from the radiogroup.
     * @return selected marker
     */
    private GameSpace.State getCurrentMarkerSetting()
    {
        GameSpace.State marker = GameSpace.State.BLANK;

        int checkedId = mMarkerRadioGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.o_marker:
                marker = GameSpace.State.O_MARK;
                break;
            case R.id.x_marker:
                marker = GameSpace.State.X_MARK;
                break;
        }
        return marker;
    }

    /**
     * Initialize the adapter for the gameboard.
     * @param gameBoard
     */
    public void initAdapter(GridView gameBoard)
    {
        final GameBoardAdapter mGameAdapter = new GameBoardAdapter(getActivity(), getCurrentMarkerSetting());
        mGameAdapter.setOnGameOverListener(new GameBoardAdapter.OnGameOverListener() {
            @Override
            public void onGameOver(Outcome outcome) {
                mNumberOfCompletedGames++;

                // Increment related counter depending on outcome.
                switch (outcome) {
                    case LOSE:
                        mNumberOfLosses++;
                        break;
                    case WIN:
                        mNumberOfWins++;
                        break;
                    case TIE:
                        mNumberOfTies++;
                        break;
                }

                mGameResultTextView.setText("YOU " + outcome.getPastTense());

                updateOutcomeRates();
            }
        });

        gameBoard.setAdapter(mGameAdapter);
    }

    /**
     * Update the visual percentages of win/lose/tie.
     */
    private void updateOutcomeRates()
    {
        setOutcomeRatesToCurrent();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_win_rate), mNumberOfWins);
        editor.putInt(getString(R.string.saved_lose_rate), mNumberOfLosses);
        editor.putInt(getString(R.string.saved_tie_rate), mNumberOfTies);
        editor.putInt(getString(R.string.saved_num_games), mNumberOfCompletedGames);
        editor.commit();
    }

    /**
     * Set the outcome rates based on the current numbers for wins, losses, ties.
     */
    private void setOutcomeRatesToCurrent() {
        String defaultRate = getResources().getString(R.string.default_rate);
        if(mNumberOfCompletedGames == 0)
        {
            mWinRateTextView.setText(defaultRate);
            mLoseRateTextView.setText(defaultRate);
            mTieRateTextView.setText(defaultRate);
        }
        else {
            mWinRateTextView.setText((String.format(RATE_DECIMAL_FORMAT, (((float) mNumberOfWins / mNumberOfCompletedGames) * 100f))) + PERCENT_SIGN);
            mLoseRateTextView.setText((String.format(RATE_DECIMAL_FORMAT, (((float) mNumberOfLosses / mNumberOfCompletedGames) * 100f))) + PERCENT_SIGN);
            mTieRateTextView.setText((String.format(RATE_DECIMAL_FORMAT, (((float) mNumberOfTies / mNumberOfCompletedGames) * 100f))) + PERCENT_SIGN);
        }
    }

    /**
     * Loads the previous outcome rates from SharedPreferences
     */
    private void loadOutcomeRates()
    {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mNumberOfWins = sharedPref.getInt(getString(R.string.saved_win_rate), 0);
        mNumberOfLosses = sharedPref.getInt(getString(R.string.saved_lose_rate), 0);
        mNumberOfTies = sharedPref.getInt(getString(R.string.saved_tie_rate), 0);
        mNumberOfCompletedGames = sharedPref.getInt(getString(R.string.saved_num_games),0);

        setOutcomeRatesToCurrent();
    }

    /**
     * Clear the win-lose-tie rates.
     */
    public void clearScores() {
        mNumberOfWins = 0;
        mNumberOfLosses = 0;
        mNumberOfTies = 0;
        mNumberOfCompletedGames = 0;

        updateOutcomeRates();
    }
}
