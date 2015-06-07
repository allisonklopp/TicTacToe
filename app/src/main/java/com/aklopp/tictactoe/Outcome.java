package com.aklopp.tictactoe;

/**
 * Enum describing the result of the completed game.
 * Created by Allison on 6/6/2015.
 */
public enum Outcome
{
    WIN,
    LOSE,
    TIE;

    public String getPastTense()
    {
        switch (this)
        {
            case WIN:
                return "WON";
            case LOSE:
                return "LOST";
            case TIE:
                return "TIED";
        }
        return "";
    }
}
