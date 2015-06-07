package com.aklopp.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Represents a single game space on the playing board.
 * Created by Allison on 6/5/2015.
 */
public class GameSpace extends ImageView{
    /**
     * The starting state of the space
     */
    private State mState = State.BLANK;

    /**
     * The marker for the user.
     */
    private State mUserMarker;

    /**
     * The listener for state change events.
     */
    private OnStateChangeListener mOnStateChangeListener;

    /**
     * Drawable assets to display on game square.
     */
    private Bitmap mBlankImage;
    private Bitmap mXmarkImage;
    private Bitmap mOmarkImage;

    /**
     * Constructor
     * @param context
     */
    public GameSpace(Context context, State userMarker) {
        this(context, null, userMarker);
    }

    /**
     * Constructor
     * @param context
     */
    public GameSpace(Context context, AttributeSet attrs, State userMarker) {
        this(context, attrs, 0);
        this.mUserMarker = userMarker;
    }

    /**
     * Constructor
     * @param context
     */
    public GameSpace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setBackgroundColor(Color.WHITE);

        mBlankImage = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.blank)).getBitmap();
        mXmarkImage = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_x_marker)).getBitmap();
        mOmarkImage = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_o_marker)).getBitmap();

        this.setImageBitmap(mBlankImage);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // If square is blank, toggle to user marker
                if (mState.equals(State.BLANK)) {
                    mState = mUserMarker;

                    if (null != mOnStateChangeListener)
                        mOnStateChangeListener.onStateChange(mState);
                }
                setImageBasedOnState();
            }
        });

    }

    /**
     * Set the image for the game square based on the current state.
     */
    private void setImageBasedOnState() {
        switch(mState)
        {
            case BLANK:
                this.setImageBitmap(mBlankImage);
                break;
            case X_MARK:
                this.setImageBitmap(mXmarkImage);
                break;
            case O_MARK:
                this.setImageBitmap(mOmarkImage);
                break;
        }
    }

    /**
     * Getter for the state of the space.
     * @return state
     */
    public State getState() {
        return mState;
    }

    /**
     * Setter for the state of the space.
     * @param state
     */
    public void setState(State state) {
        this.mState = state;
        setImageBasedOnState();
    }

    /**
     * Setter for the user marker.
     * @param marker
     */
    public void setMarker(State marker) {
        this.mUserMarker = marker;
    }

    /**
     * Enum describing game space's current state
     */
    public enum State
    {
        BLANK,
        X_MARK,
        O_MARK
    }

    /**
     * Sets the listener for the state change.
     * @param listener
     */
    public void setOnStateChangedListener(OnStateChangeListener listener)
    {
        this.mOnStateChangeListener = listener;
    }

    /**
     * Interface to listen for changes of the current state.
     */
    public interface OnStateChangeListener {

        /**
         * Called upon a change of the current value.
         */
        void onStateChange(State state);
    }

}
