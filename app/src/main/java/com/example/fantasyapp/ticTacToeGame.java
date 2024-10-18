package com.example.fantasyapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

public class ticTacToeGame extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private static final String TAG = "WebSocketApp";
    private boolean isUserOne = true;
    private boolean isGameOver = false;
    private String[][] board = new String[3][3];
    private String sessionKey = "your_session_key"; // Replace with actual session key
    private String userId = "your_user_id";
    private int movesCount = 0;
    private int userCount = 0;

    private TextView resultTextview;

    private TextView loadingMessage;
    private View gameBoard;
    private View loadingScreen;
    private ProgressBar loadingSpinner;

    private TextView timerTextView;
    private CountDownTimer countDownTimer;

    String sentMove="";

    int noMoveCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tic_tac_toe_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noMoveCounter = 0;

        timerTextView = findViewById(R.id.timerTextView);
        resultTextview = findViewById(R.id.resultTextView);

        loadingMessage = findViewById(R.id.loadingMessage);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        gameBoard = findViewById(R.id.gameBoard);  // This should be your layout containing the Tic-Tac-Toe board
        loadingScreen = findViewById(R.id.loadingScreen);  // Your loading screen layout

        // Initially show the loading screen
        showLoadingScreen();


        createWebSocketClient();
        setupButtons();
    }

    private void startTurnTimer() {
        // Cancel any existing timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Create a new 30-second countdown timer
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update the timer TextView every second
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Timer finished, disable the buttons and handle turn switching
                timerTextView.setText("0");
                Log.i(TAG, "User took too long. Disabling buttons for current player.");
                disableAllButton();
                resultTextview.setText("Time out! Wait for your opponent's move.");
                sendMessage("no move","no move");
                noMoveCounter++;
                sentMove = "no move";
            }
        }.start();
    }

    private void stopTurnTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("wss://8c8a-49-36-64-205.ngrok-free.app/ws/game/game_1/?session_key=" + URLEncoder.encode(sessionKey, "UTF-8") + "&user_id=" + URLEncoder.encode(userId, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new tictactoeWebsocket(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

                Log.i(TAG, "WebSocket Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "Message received: " + message);

                try {



                    JSONObject jsonMessage = new JSONObject(message);

                    // Handle user count update
                    if (jsonMessage.has("user_count")) {
                        userCount = jsonMessage.getInt("user_count");
                        runOnUiThread(() -> {
                            if (userCount == 2) {
                                hideLoadingScreen();  // Only hide the loading screen when two users are present
                            } else {
                                loadingMessage.setText("Waiting for another player...");  // Show waiting message
                            }
                        });
                    }

                    // Handle the game moves
                    if (jsonMessage.has("boxid") && jsonMessage.has("move")) {
                        String boxId = jsonMessage.getString("boxid");
                        String move = jsonMessage.getString("move");

                        runOnUiThread(() -> {
                                    if(noMoveCounter >=2)
                                    {
                                        onBackPressed();
                                    }
                                    if (move.equals("You win!")) {
                                        if(resultTextview.getText().toString().equals("You win!"))
                                        {
                                            Log.d("you win", "onMessage: you win");
                                        }
                                        else {
                                            resultTextview.setText("You lost!");
                                            disableAllButton();
                                        }
                                    } else if (move.equals("no move") && !move.equals(sentMove)) {
                                        showTimer();  // Show timer for the user whose turn it is
                                        startTurnTimer();
                                        enableUncheckedBox();
                                        resultTextview.setText("Your's turn");
                                        sentMove="";

                                    } else if (move.equals("no move") && move.equals(sentMove)) {
                                        disableAllButton();
                                        sentMove="";

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                resultTextview.setText("opponents's turn");
                                            }
                                        },1000);

                                    } else {
                                        if(!boxId.equals(sentMove)) {
                                            enableUncheckedBox();
                                            showTimer();  // Show timer for the user whose turn it is
                                            startTurnTimer();
                                        }
                                        updateButton(boxId, move);
                                        checkGameResult();  // Check game result after each move


                                        // Switch back to User 1's turn
                                        isUserOne = true;
                                          // Enable User 1's buttons again

                                    }
                        });
                    }



//                    JSONObject jsonMessage = new JSONObject(message);
//                    String boxId = jsonMessage.getString("boxid");
//                    String move = jsonMessage.getString("move");
//
//
//                    runOnUiThread(() -> {
//                        if (move.equals("You win!")) {
//                            if(resultTextview.getText().toString().equals("You win!"))
//                            {
//                                Log.d("you win", "onMessage: you win");
//                            }
//                            else {
//                                resultTextview.setText("You lost!");
//                                disableAllButton();
//                            }
//                        } else {
//                            if(!boxId.equals(sentMove)) {
//                                enableUncheckedBox();
//                                showTimer();  // Show timer for the user whose turn it is
//                                startTurnTimer();
//                            }
//                            updateButton(boxId, move);
//                            checkGameResult();  // Check game result after each move
//
//
//                            // Switch back to User 1's turn
//                            isUserOne = true;
//                            enableUncheckedBox();  // Enable User 1's buttons again
//
//                        }
//                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "WebSocket Closed. Reason: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket Error: " + ex.getMessage());
            }


        };
        webSocketClient.connect();
    }
    private void setupButtons() {
        int[] buttonIds = {R.id.box1, R.id.box2, R.id.box3, R.id.box4, R.id.box5, R.id.box6, R.id.box7, R.id.box8, R.id.box9};
        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(v -> onBoxClick(v.getId()));
        }
    }
    private void onBoxClick(int boxId) {
        if (webSocketClient != null && webSocketClient.isOpen() && !isGameOver) {
            String boxTag = getBoxTag(boxId);

            Button button = findViewById(boxId);
            String move = "O";
            button.setText(move);
            sendMessage(boxTag, move);
            disableButton(boxId);
            updateBoard(boxTag, move);  // Update the board
            movesCount++;
            pauseTurnTimer();
            // Switch to the other user's turn
            isUserOne = false;
            hideTimer();

        }
    }

    private String getBoxTag(int boxId) {
        if (boxId == R.id.box1) return "box1";
        if (boxId == R.id.box2) return "box2";
        if (boxId == R.id.box3) return "box3";
        if (boxId == R.id.box4) return "box4";
        if (boxId == R.id.box5) return "box5";
        if (boxId == R.id.box6) return "box6";
        if (boxId == R.id.box7) return "box7";
        if (boxId == R.id.box8) return "box8";
        if (boxId == R.id.box9) return "box9";
        return "";
    }
    private void updateBoard(String boxTag, String move) {
        // Update the board state with the current move
        switch (boxTag) {
            case "box1": board[0][0] = move; break;
            case "box2": board[0][1] = move; break;
            case "box3": board[0][2] = move; break;
            case "box4": board[1][0] = move; break;
            case "box5": board[1][1] = move; break;
            case "box6": board[1][2] = move; break;
            case "box7": board[2][0] = move; break;
            case "box8": board[2][1] = move; break;
            case "box9": board[2][2] = move; break;
        }
    }

    private void sendMessage(String boxid, String move) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            String message = "{\"boxid\": \"" + boxid + "\", \"move\": \"" + move + "\"}";
            webSocketClient.send(message);

            sentMove = boxid;
            disableUncheckedBox();
        }
    }

    private void updateButton(String boxId, String move) {

        int buttonId = getResources().getIdentifier(boxId, "id", getPackageName());
        Button button = findViewById(buttonId);

        String s = button.getText().toString();
        if(!s.equals("O"))
        {
            button.setText("X");
            button.setEnabled(false);
        }
    }

    private void disableButton(int boxId) {
        Button button = findViewById(boxId);
        button.setEnabled(false);
    }

    private void checkGameResult() {
        // Check if the game is over after each move
        if (isGameOver) return;  // Don't proceed if the game is already over

        if (isWinner()) {
            String result = isUserOne ? "win" : "lose";
            showGameOverDialog(result);
            isGameOver = true;
        } else if (movesCount == 9) {
            // If all 9 moves are made, it's a draw
            showGameOverDialog("draw");
            isGameOver = true;
        } else {
            // Toggle between User 1 and User 2
            isUserOne = !isUserOne;
            String currentPlayer = isUserOne ? "opponent's Turn" : "your's Turn";
            resultTextview.setText(currentPlayer);  // Update TextView with the current player's turn
        }
    }

    private boolean isWinner() {
        // Check rows, columns, and diagonals for a winner
        for (int i = 0; i < 3; i++) {
            // Check rows
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                return true;
            }
            // Check columns
            if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) {
                return true;
            }
        }
        // Check diagonals
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            return true;
        }
        if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            return true;
        }
        return false;  // No winner
    }

    private void showGameOverDialog(String result) {
        String message;
        if (result.equals("win")) {
            // Check if the current player is User 1
            if (isUserOne) {
                message = "You win!";  // User 1 wins
            } else {
                message = "You lose!";  // User 2 loses
            }
        } else if (result.equals("lose")) {
            // If User 2 is the winner
            if (!isUserOne) {
                message = "You win!";  // User 2 wins
            } else {
                message = "You lose!";  // User 1 loses
            }
        } else {
            message = "It's a draw!";
        }
        resultTextview.setText(message);
        resultTextview.setEnabled(false);
        sendMessage("win",message);
    }

    private void disableAllButton()
    {
        for(int i=1;i<=9;i++)
        {
            int buttonId = getResources().getIdentifier("box"+i, "id", getPackageName());
            Button b = findViewById(buttonId);
            b.setClickable(false);
        }
    }

    private void disableUncheckedBox()
    {
        for(int i=1;i<=9;i++)
        {
            int buttonId = getResources().getIdentifier("box"+i, "id", getPackageName());
            Button b = findViewById(buttonId);
            if(b.getText().toString().equals("O") || b.getText().toString().equals("X"))
            {

            }
            else{
                b.setClickable(false);
            }
        }
    }
    private void enableUncheckedBox()
    {
        for(int i=1;i<=9;i++)
        {
            int buttonId = getResources().getIdentifier("box"+i, "id", getPackageName());
            Button b = findViewById(buttonId);
            if(b.getText().toString().equals("O") || b.getText().toString().equals("X"))
            {

            }
            else{
                b.setClickable(true);
            }
        }
    }

    private void pauseTurnTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();  // Pause the timer for the current user
        }
    }

    private void hideTimer() {
        timerTextView.setVisibility(View.INVISIBLE);  // Hide the timer when it's not the current user's turn
    }

    private void showTimer() {
        timerTextView.setVisibility(View.VISIBLE);  // Show the timer for the current user
    }

    private void showLoadingScreen() {
        loadingScreen.setVisibility(View.VISIBLE);  // Show loading screen
        gameBoard.setVisibility(View.GONE);         // Hide the game board
    }

    private void hideLoadingScreen() {
        startTurnTimer();
        loadingScreen.setVisibility(View.GONE);     // Hide loading screen
        gameBoard.setVisibility(View.VISIBLE);      // Show the game board
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}