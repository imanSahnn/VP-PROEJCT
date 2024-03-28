import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import java.sql.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class vsBot6x6 extends JFrame {
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

    public vsBot6x6() {

        conn = Conn.ConnectDB();
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 650);
        setResizable(false);
        setLayout(null);

        // Set the Nimbus look and feel for the JFrame
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(new JLabel(new ImageIcon("gambar/6x6baru.png"))); // Set background image
        ImageIcon logoIcon = new ImageIcon("gambar/image1.jpg"); // Replace "path/to/your/logo.png" with the actual path
                                                                 // to your logo image
        setIconImage(logoIcon.getImage());

        buttons = new JButton[6][6];
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
        timerLabel.setBounds(45, 45, 150, 30);
        ImageIcon icontimer = new ImageIcon("gambar/timer.png"); // Replace with the actual path to your icon image
        timerLabel.setIcon(icontimer);
        add(timerLabel);
        timeElapsed = 0;
        timer = new Timer(1000, new TimerListener());
        timer.start();

        playerLabel = new JLabel("Player X Turn");
        playerLabel.setFont(new Font("", Font.BOLD, 12));
        playerLabel.setBounds(200, 3, 150, 30);
        add(playerLabel);

        player1WinsLabel = new JLabel(": 0");
        player1WinsLabel.setBounds(385, 45, 150, 30);
        ImageIcon icon = new ImageIcon("gambar/p1.png"); // Replace with the actual path to your icon image
        player1WinsLabel.setIcon(icon);
        add(player1WinsLabel);

        player2WinsLabel = new JLabel(": 0");
        player2WinsLabel.setBounds(385, 78, 150, 30);
        ImageIcon icon1 = new ImageIcon("gambar/p2.png"); // Replace with the actual path to your icon image
        player2WinsLabel.setIcon(icon1);
        add(player2WinsLabel);

        moveCount = 0;
        moveCounterLabel = new JLabel(": 0");
        moveCounterLabel.setBounds(45, 78, 150, 30);
        ImageIcon iconmove = new ImageIcon("gambar/paw.png"); // Replace with the actual path to your icon image
        moveCounterLabel.setIcon(iconmove);
        add(moveCounterLabel);

        JButton backButton = new JButton(""); // backbutton functionality
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
        resetButton.setBounds(85, 550, 100, 30);
        resetButton.addActionListener(new ResetButtonListener());
        resetButton.setOpaque(false); // Make the button transparent
        resetButton.setContentAreaFilled(false); // Make the button transparent
        resetButton.setBorderPainted(false);
        ImageIcon imageIconReset = new ImageIcon("gambar//back.png");
        resetButton.setIcon(imageIconReset);
        add(resetButton);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setBounds(300, 550, 120, 30);
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
        muteButton.setBounds(410, 0, 100, 40);
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

    void playBackgroundMusic() {
        if (!isMuted && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopBackgroundMusic() {
        backgroundMusic.stop();
    }

    private void initializeButtons() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setText("");
                buttons[row][col].setFont(new Font("", Font.BOLD, 40));
                buttons[row][col].setBounds(30 + col * 70, 115 + row * 70, 70, 70);
                buttons[row][col].addActionListener(new ButtonListener());
                add(buttons[row][col]);
            }
        }
    }

    private void resetGame() { // guna tuk reset fucntion
        currentPlayer = "X";
        gameEnded = false;
        moveCount = 0;
        moveCounterLabel.setText(": 0");
        if (gameEnded) {
            if (currentPlayer.equals("X"))
                playBotMove();
            playerLabel.setText("Player O Turn");
        }

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                buttons[row][col].setText("");
            }
        }
        enableButtons();
        playerLabel.setText("Player X Turn");
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
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                buttons[row][col].setText("");
            }

        }

        enableButtons();
    }

    private class NewGameButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Reset the game board and clear all data
            int confirm = JOptionPane.showConfirmDialog(vsBot6x6.this, "Are you sure you want to start a new game?",
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

    private class BackButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
             Welcome welcomeFrame = new Welcome(); // Create an instance of the Welcome class
        Welcome.setVisible(true);
        backgroundMusic.stop(); // Make the Welcome frame visible
       dispose();
        }
    }

    private void disableButtons() { // xleh tekan
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }

    private void enableButtons() { // bole tekan
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                buttons[row][col].setEnabled(true);
            }
        }
    }

    private boolean checkWin() {
        String[][] board = new String[6][6];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                board[row][col] = buttons[row][col].getText();
            }
        }

        for (int i = 0; i < 6; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) && board[i][0].equals(board[i][3])
                    && board[i][0].equals(board[i][4]) && board[i][0].equals(board[i][5]) && !board[i][0].isEmpty())
                return true;
            if (board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]) && board[0][i].equals(board[3][i])
                    && board[0][i].equals(board[4][i]) && board[0][i].equals(board[5][i]) && !board[0][i].isEmpty())
                return true;
        }

        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && board[0][0].equals(board[3][3])
                && board[0][0].equals(board[4][4]) && board[0][0].equals(board[5][5]) && !board[0][0].isEmpty())
            return true;
        if (board[0][5].equals(board[1][4]) && board[0][5].equals(board[2][3]) && board[0][5].equals(board[3][2])
                && board[0][5].equals(board[4][1]) && board[0][5].equals(board[5][0]) && !board[0][5].isEmpty())
            return true;

        return false;
    }

    private boolean checkDraw() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    return false; // If any cell is empty, the game is not a draw
                }
            }
        }
        return true; // All cells are filled, indicating a draw
    }

    private void playBotMove() {
        if (!gameEnded) {
            Random random = new Random();
            int row, col;
            do {
                row = random.nextInt(6);
                col = random.nextInt(6);
            } while (!buttons[row][col].getText().isEmpty());

            buttons[row][col].setText("O");
            buttons[row][col].setEnabled(false);
            try {
                String sql = "select boardinfo from matchinfo";
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
                player2Wins++;
                player2WinsLabel.setText(": " + player2Wins);
                JOptionPane.showMessageDialog(this, "Bot wins!");
                disableButtons();
            } else if (checkDraw()) {
                gameEnded = true;
                JOptionPane.showMessageDialog(this, "It's a draw!");
                disableButtons();
            } else {
                currentPlayer = "X";
                playerLabel.setText("Player X Turn");
            }
        }
    }

    public void winLabel() {
        try {
            String sql = "select wins from matchinfo";
            Statement ps = conn.createStatement();
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt(1) == 1) {
                    player1WinsLabel.setText(": " + player1Wins);
                    player2WinsLabel.setText(": " + player2Wins);
                } else {

                }
            }
        } catch (Exception ex) {
            // TODO: handle exception
        }
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            if (!clickedButton.getText().isEmpty() || gameEnded)
                return;

            clickedButton.setText(currentPlayer);
            try {
                String sql = "select boardinfo from matchinfo";
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
                if (currentPlayer.equals("X")) {
                    player1Wins++;
                    winLabel();
                    JOptionPane.showMessageDialog(null, "Player X wins!");
                } else {
                    player2Wins++;
                    winLabel();
                    JOptionPane.showMessageDialog(null, "Player O wins!");
                }
                disableButtons();
            } else if (checkDraw()) {
                gameEnded = true;
                JOptionPane.showMessageDialog(null, "It's a draw!");
                disableButtons();
            } else {
                currentPlayer = (currentPlayer.equals("X")) ? "O" : "X";
                playerLabel.setText("Player " + currentPlayer + " Turn");
                if (currentPlayer.equals("O")) {
                    playBotMove();
                }
            }
        }
    }

    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resetGame();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                vsBot6x6 game = new vsBot6x6();
                game.setVisible(true);
                
            }
        });
    }
}
