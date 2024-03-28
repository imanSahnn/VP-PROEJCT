import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TicTacToe5x5 extends JFrame {
    Connection conn;
    private JButton[][] buttons;
    private JLabel playerLabel;
    private JLabel player1WinsLabel;
    private JLabel player2WinsLabel;

    private Timer timer;
    private int timeElapsed;
    private JLabel timerLabel;

    private String currentPlayer;
    private boolean gameEnded;

    private int player1Wins;
    private int player2Wins;

    private JLabel moveCounterLabel;
    private int moveCount;

    private boolean isMuted = false;

    private ImageIcon muteIcon;
    private ImageIcon unmuteIcon;

    private Clip backgroundMusic;

    public TicTacToe5x5() {

        conn = Conn.ConnectDB();

        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // nati ganti donothingonclose
        setSize(450, 580);
        setResizable(false);
        setLayout(null);

        UIManager.put("nimbusBorder", Color.CYAN); // Replace Color.RED with your desired color

        // Set the Nimbus look and feel for the JFrame
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(new JLabel(new ImageIcon("gambar/5x5baru.png"))); // Set background image
        ImageIcon logoIcon = new ImageIcon("gambar/image1.jpg"); // Replace "path/to/your/logo.png" with the actual path
                                                                 // to your logo image
        setIconImage(logoIcon.getImage());

        buttons = new JButton[5][5];
        currentPlayer = "X";
        gameEnded = false;

        initializeButtons();
        try {
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("gambar/aesbg.wav")));
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        playBackgroundMusic();

        player1Wins = 0;
        player2Wins = 0;

        timerLabel = new JLabel(": 0 ");
        timerLabel.setBounds(60, 32, 150, 30);
        ImageIcon icontimer = new ImageIcon("gambar/timer.png"); // Replace with the actual path to your icon image
        timerLabel.setIcon(icontimer);
        add(timerLabel);
        timeElapsed = 0;
        timer = new Timer(1000, new TimerListener());
        timer.start();

        playerLabel = new JLabel("Player X Turn");
        playerLabel.setFont(new Font("", Font.BOLD, 12));
        playerLabel.setBounds(180, 3, 150, 30);
        add(playerLabel);

        player1WinsLabel = new JLabel(": 0");
        player1WinsLabel.setBounds(335, 32, 150, 30);
        ImageIcon icon = new ImageIcon("gambar/p1.png"); // Replace with the actual path to your icon image
        player1WinsLabel.setIcon(icon);
        add(player1WinsLabel);

        player2WinsLabel = new JLabel(": 0");
        player2WinsLabel.setBounds(330, 62, 150, 30);
        ImageIcon icon1 = new ImageIcon("gambar/p2.png"); // Replace with the actual path to your icon image
        player2WinsLabel.setIcon(icon1);
        add(player2WinsLabel);

        moveCount = 0;
        moveCounterLabel = new JLabel(": 0");
        moveCounterLabel.setBounds(60, 62, 150, 30);
        ImageIcon iconmove = new ImageIcon("gambar/paw.png"); // Replace with the actual path to your icon image
        moveCounterLabel.setIcon(iconmove);
        add(moveCounterLabel);

        JButton backButton = new JButton("");
        backButton.setBounds(0, 0, 50, 30);
        backButton.addActionListener(new BackButtonListener());
        backButton.setOpaque(false); // Make the button transparent
        backButton.setContentAreaFilled(false); // Make the button transparent
        backButton.setBorderPainted(false);
        ImageIcon imageIcon = new ImageIcon("gambar//return.png");
        backButton.setIcon(imageIcon);
        add(backButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 12));
        resetButton.setForeground(Color.black);
        resetButton.setBounds(65, 477, 100, 30);
        resetButton.addActionListener(new ResetButtonListener());
        resetButton.setOpaque(false); // Make the button transparent
        resetButton.setContentAreaFilled(false); // Make the button transparent
        resetButton.setBorderPainted(false);
        ImageIcon imageIconReset = new ImageIcon("gambar//back.png");
        resetButton.setIcon(imageIconReset);
        add(resetButton);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setBounds(240, 475, 120, 30);
        newGameButton.addActionListener(new NewGameButtonListener());
        newGameButton.setOpaque(false); // Make the button transparent
        newGameButton.setContentAreaFilled(false); // Make the button transparent
        newGameButton.setBorderPainted(false);
        ImageIcon imageIconNew = new ImageIcon("gambar//newgame.png");
        newGameButton.setIcon(imageIconNew);
        add(newGameButton);

        JButton muteButton = new JButton("");
        muteIcon = new ImageIcon("gambar/unmute.png");
        muteButton.setIcon(muteIcon);
        muteButton.setOpaque(false); // Make the button transparent
        muteButton.setContentAreaFilled(false); // Make the button transparent
        muteButton.setBorderPainted(false);
        muteButton.setBounds(360, 0, 100, 40);
        muteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isMuted) {
                    isMuted = false;
                    muteButton.setText("");
                    muteIcon = new ImageIcon("gambar/unmute.png");
                    muteButton.setIcon(muteIcon);
                    playBackgroundMusic();
                } else {
                    isMuted = true;
                    muteButton.setText("");
                    unmuteIcon = new ImageIcon("gambar/mute.png");
                    muteButton.setIcon(unmuteIcon);
                    stopBackgroundMusic();

                }
            }
        });
        add(muteButton);
    }

    private void playBackgroundMusic() {
        if (!isMuted && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopBackgroundMusic() {
        backgroundMusic.stop();
    }

    private void initializeButtons() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Mega", Font.BOLD, 40));
                button.setBounds(45 + col * 70, 100 + row * 70, 70, 70);
                button.addActionListener(new ButtonListener());
                buttons[row][col] = button;
                add(button);
            }
        }
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton.getText().length() > 0 || gameEnded) {
                return; // If the button is already clicked or the game has ended, do nothing
            }

            clickedButton.setText(currentPlayer); // Set the text of the clicked button to the current player (X or O)
            clickedButton.setText(currentPlayer);
            clickedButton.setForeground(currentPlayer.equals("X") ? Color.decode("#C25615") : Color.decode("#C25615"));
            try {
                String sql = "select wins from matchinfo";
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery(sql);
                while (rs.next()) {
                    if (rs.getInt(1) == 1) {
                        moveCount++;
                        moveCounterLabel.setText(": " + moveCount);
                    }
                }
            } catch (Exception ex) {
                // TODO: handle exception
            }
            if (checkWin()) {
                gameEnded = true;
                JOptionPane.showMessageDialog(TicTacToe5x5.this, "Player " + currentPlayer + " wins!");
                try {
                    String sql = "select wins from matchinfo";
                    Statement ps = conn.createStatement();
                    ResultSet rs = ps.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getInt(1) == 1) {
                            if (currentPlayer.equals("X")) {
                                player1Wins++;
                                player1WinsLabel.setText(": " + player1Wins);

                            } else {
                                player2Wins++;
                                player2WinsLabel.setText(": " + player2Wins);
                            }

                        }
                    }
                } catch (Exception ex) {
                    // TODO: handle exception
                }
                disableButtons();

            } else if (checkDraw()) {
                gameEnded = true;
                JOptionPane.showMessageDialog(TicTacToe5x5.this, "Draw!");

            } else {
                if (currentPlayer.equals("X")) {
                    currentPlayer = "O";
                    playerLabel.setText("Player O Turn");
                } else {
                    currentPlayer = "X";
                    playerLabel.setText("Player X Turn");
                }
            }

        }
    }

    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Reset the game board without stopping the timer
            resetGame();

        }

    }

    private class BackButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
    Welcome welcomeFrame = new Welcome(); // Create an instance of the Welcome class
        Welcome.setVisible(true); // Make the Welcome frame visible
       dispose();

        }
    }

    private class NewGameButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Reset the game board and clear all data
            int confirm = JOptionPane.showConfirmDialog(TicTacToe5x5.this, "Are you sure you want to start a new game?",
                    "New Game Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                moveCount = 0;
                moveCounterLabel.setText(": " + moveCount);
                clearGame();
                player1Wins = 0;
                player2Wins = 0;
                player1WinsLabel.setText(": 0");
                player2WinsLabel.setText(": 0");
                timeElapsed = 0;
            }

        }
    }

    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String sql = "select timer from matchinfo";
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery(sql);
                while (rs.next()) {
                    if (rs.getInt(1) == 1) {
                        timeElapsed++;
                        timerLabel.setText(":" + timeElapsed);
                        if (timeElapsed == 30) {
                            // Display the image using a dialog
                            // ImageIcon imageIcon = new ImageIcon("gambar/image1.jpg"); // Replace
                            // "path/to/your/image.png" with the actual path to your image file
                            // JOptionPane.showMessageDialog(TicTacToeGame.this, "", "Image Popup",
                            // JOptionPane.PLAIN_MESSAGE, imageIcon);
                        }
                        if (timeElapsed == 60) {
                            // Display the image using a dialog
                            // ImageIcon imageIcon = new ImageIcon("gambar/after1minute.gif"); // Replace
                            // "path/to/your/image.png" with the actual path to your image file
                            // JOptionPane.showMessageDialog(TicTacToeGame.this, "", "Image Popup",
                            // JOptionPane.PLAIN_MESSAGE, imageIcon);
                        }
                    } else {

                    }
                }
            } catch (Exception ex) {
                // TODO: handle exception
            }

        }
    }

    private boolean checkWin() {
        String[][] board = new String[5][5];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                board[row][col] = buttons[row][col].getText();
            }
        }

        // Check rows
        for (int row = 0; row < 5; row++) {
            if (board[row][0].equals(board[row][1]) && board[row][1].equals(board[row][2])
                    && board[row][2].equals(board[row][3]) && board[row][3].equals(board[row][4])
                    && !board[row][0].equals("")) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 5; col++) {
            if (board[0][col].equals(board[1][col]) && board[1][col].equals(board[2][col])
                    && board[2][col].equals(board[3][col]) && board[3][col].equals(board[4][col])
                    && !board[0][col].equals("")) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]) && board[2][2].equals(board[3][3])
                && board[3][3].equals(board[4][4]) && !board[0][0].equals("")) {
            return true;
        }

        if (board[0][4].equals(board[1][3]) && board[1][3].equals(board[2][2]) && board[2][2].equals(board[3][1])
                && board[3][1].equals(board[4][0]) && !board[0][4].equals("")) {
            return true;
        }

        return false;
    }

    private boolean checkDraw() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (buttons[row][col].getText().equals("")) {
                    return false; // If any button is empty, the game is not a draw
                }
            }
        }
        return true; // All buttons are filled, it's a draw
    }

    private void disableButtons() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }

    private void enableButtons() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                buttons[row][col].setEnabled(true);
            }
        }
    }

    private void resetGame() { // sape kalah jalan dulu sekian
        if (gameEnded) {
            if (currentPlayer.equals("O")) {
                currentPlayer = "X";
                playerLabel.setText("Player X Turn");
            } else {
                currentPlayer = "O";
                playerLabel.setText("Player O Turn");
            }
        } else {
            currentPlayer = "O";
            playerLabel.setText("Player O Turn");
        }

        gameEnded = false;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                buttons[row][col].setText("");
            }

        }

        enableButtons();
    }

    private void clearGame() { // class tuk newgame clear board and record

        currentPlayer = "X";
        playerLabel.setText("Player " + currentPlayer + " turn.");
        enableButtons();
        timeElapsed = 0;
        timerLabel.setText(": " + timeElapsed);
        player1Wins = 0;
        player2Wins = 0;
        moveCount = 0;
        player1WinsLabel.setText(": " + player1Wins);
        player2WinsLabel.setText(": " + player2Wins);
        moveCounterLabel.setText(": " + moveCount);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                buttons[row][col].setText("");
            }

        }

        enableButtons();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TicTacToe5x5 game = new TicTacToe5x5();
                game.setVisible(true);
                
            }
        });
    }
}
