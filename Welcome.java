import javax.swing.*;

import com.mysql.cj.xdevapi.Statement;

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Welcome {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel framePanel;
    private static Clip backgroundMusicClip1;
    Connection conn = null;
    ResultSet rs = null;
    Statement ps = null;

    

    public Welcome() {
        frame = new JFrame("THE ONE AND ONLY");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);
        ImageIcon icon = new ImageIcon("gambar/image1.jpg");
        frame.setIconImage(icon.getImage());

        mainPanel = new JPanel(new BorderLayout());
        Connection conn = Conn.ConnectDB();
        if (conn == null) {
            // Handle the case when the connection couldn't be established
            System.exit(1);
        }

        // Create a panel with a background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = new ImageIcon("gambar/bgwelcome.png").getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null); // Use absolute positioning for buttons

        // Create the "Play" button
        JButton playButton = new JButton("Play");
        playButton.setBounds(120, 350, 150, 30);
        playButton.setOpaque(false); // Make the button transparent
        playButton.setContentAreaFilled(false); // Make the button transparent
        playButton.setBorderPainted(false);
        playButton.setForeground(Color.decode("#A44E19"));
        playButton.setIcon(new ImageIcon("gambar/playcat.png"));
        playButton.setFont(new Font("Roman", Font.BOLD, 20)); // Change font and size

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add code for the play button action
                System.out.println("Play button clicked");
                playSound("gambar\\clickmeow.wav");
                stopBackgroundMusic1();
                try {
                    String sql = "select game_mode from generalsettings";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getInt(1) == 0) {
                            String sql2 = "select boardsize from generalsettings";
                            PreparedStatement pst = conn.prepareStatement(sql2);
                            ResultSet rst = pst.executeQuery();
                            while (rst.next()) {
                                if (rst.getInt(1) == 0) {
                                    TicTacToeGame ticTacToeGame = new TicTacToeGame();
                                    ticTacToeGame.show();

                                } else if (rst.getInt(1) == 1) {
                                    TictacToe4x4 tictacToe4x4 = new TictacToe4x4();
                                    tictacToe4x4.show();
                                } else if (rst.getInt(1) == 2) {
                                    TicTacToe5x5 ticTacToe5x5 = new TicTacToe5x5();
                                    ticTacToe5x5.show();
                                } else if (rst.getInt(1) == 3) {
                                    TicTacToe6x6 ticTacToe6x6 = new TicTacToe6x6();
                                    ticTacToe6x6.show();
                                }
                            }

                        } else {
                            String sql2 = "select boardsize from generalsettings";
                            PreparedStatement pst = conn.prepareStatement(sql2);
                            ResultSet rst = pst.executeQuery();
                            while (rst.next()) {
                                if (rst.getInt(1) == 0) {
                                    vsCom vsCom = new vsCom();
                                    vsCom.show();

                                } else if (rst.getInt(1) == 1) {
                                    vsBot4x4 vsBot4x4 = new vsBot4x4();
                                    vsBot4x4.show();
                                } else if (rst.getInt(1) == 2) {
                                    vsBot5x5 vsBot5x5 = new vsBot5x5();
                                    vsBot5x5.show();
                                } else if (rst.getInt(1) == 3) {
                                    vsBot6x6 vsBot6x6 = new vsBot6x6();
                                    vsBot6x6.show();
                                }
                            }
                        }
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, e);
                }

                frame.setVisible(false);
                

            }
        });
        panel.add(playButton);

        // Create the "Settings" button
        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(120, 420, 150, 30);
        // Make the button transparent
        settingsButton.setOpaque(false); // Make the button transparent
        settingsButton.setContentAreaFilled(false); // Make the button transparent
        settingsButton.setBorderPainted(false);
        settingsButton.setForeground(Color.decode("#A44E19"));
        settingsButton.setIcon(new ImageIcon("gambar/setting2.png"));
        settingsButton.setFont(new Font("Arial", Font.BOLD, 20)); // Change font and size
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add code for the settings button action
                System.out.println("Settings button clicked");
                playSound("gambar\\clickmeow.wav");
                settings set = new settings();
                set.setVisible(true);

                // Close the current frame
              
            }
        });
        panel.add(settingsButton);

        // Add the panel to the frame panel
        framePanel = new JPanel(new GridLayout(1, 1));
        framePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        framePanel.add(panel);

        // Add the frame panel to the main panel
        mainPanel.add(framePanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
        playSoundLoop("gambar\\aesbg.wav");

        // Add window listener to handle program close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ImageIcon questionIcon = new ImageIcon("gambar/crycat.png");

                int option = JOptionPane.showOptionDialog(
                        frame,
                        "Are you sure you want to close the program?",
                        "Confirm Close",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        questionIcon,
                        new Object[]{"Yes", "No"},
                        "No");

                if (option == JOptionPane.YES_OPTION) {
                    stopBackgroundMusic1();
                    playSound("gambar\\hasta1.wav"); // Play the closing song
            try {
                Thread.sleep(5500); // Delay the program for 2 seconds to play the song
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            frame.dispose();
            System.exit(0);
            }
        }
        });
    }


    // Method to play a sound from a file
    public static void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Welcome.class.getResourceAsStream(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to play a sound in a loop
    public static void playSoundLoop(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
            Welcome.class.getResourceAsStream(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip1 = clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopBackgroundMusic1() {
    if (backgroundMusicClip1 != null && backgroundMusicClip1.isRunning()) {
        backgroundMusicClip1.stop();
        backgroundMusicClip1.close();
    }
}
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Welcome welcome = new Welcome();
        });
    }

    public static void setVisible(boolean b) {
    }
}