# TicTacToe
Implementation of TicTacToe game for Android. User plays against game AI, which is programmed to pick the best possible move each turn.

<H2>Setup</H2>
<b>Supports:</b> Android API 19 (4.4 - KitKat) through API 22 (5.1 Lollipop)
<p>
<b>Tested on:</b>
<ul>
<li>Moto G</li>
<li>Galaxy S4 (emulator)</li>
<li>HTC One (emulator)</li>
<li>Nexus 5 (emulator)</li>
<li>Nexus 4 (emulator)</li>
</ul>
<b>Note:</b> Currently mobile-only. The app will run on a tablet, but the UI isn't optimized for tablet displays.


<H2>Gameplay</H2>
Plays like a standard tic-tac-toe game where each player takes turns putting their marker ('X' or 'O') on the playing board until one player has gotten three of their marker in a row or the board is filled (causing a draw). In this implementation, the user plays against the AI which always selects the best possible move. The user will find that they are unable to win and can only get a draw or a loss.<p>  
<img src="https://cloud.githubusercontent.com/assets/4759914/8022454/aeb04518-0c87-11e5-9483-c55e5f98ae8b.png" height="300"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://cloud.githubusercontent.com/assets/4759914/8022452/ae6c8f9e-0c87-11e5-9e3d-108427638735.png" height="300"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://cloud.githubusercontent.com/assets/4759914/8022453/ae6ced5e-0c87-11e5-9b2b-a65728e2dea3.png" height="300"/>

<H2>Additional Features</H2>
User can choose to play as 'X's or 'O's when beginning a new game of Tic-Tac-Toe. If user selects 'X' as their marker, they will go first. Otherwise, selecting 'O' will allow the computer to take a turn before the user. <p>
The statistics for the completed games (games that are finished with a win, loss or draw outcome) are displayed with the percentage of Wins, Losses and Draws based on the total number of games completed. These outcome percentages can be cleared by hitting the app menu and selecting the "clear" option. 
