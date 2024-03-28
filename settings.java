import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class settings {

    Connection conn = null;
    ResultSet rs, rst;
    Statement ps, pst;
    private JComboBox<String> gameModeComboBox, boardComboBox;
    private JCheckBox timerCheckBox, boardinfoCheckBox, winCheckBox;
    private String selectedgameMode;

    public void retrieveSettings() {
        try {
            String generalSettingsSql = "SELECT game_mode, boardsize FROM generalsettings";
            PreparedStatement generalSettingsPs = conn.prepareStatement(generalSettingsSql);
            ResultSet generalSettingsRs = generalSettingsPs.executeQuery();

            if (generalSettingsRs.next()) {
                int gameMode = generalSettingsRs.getInt("game_mode");
                int boardSize = generalSettingsRs.getInt("boardsize");

                gameModeComboBox.setSelectedIndex(gameMode);
                boardComboBox.setSelectedIndex(boardSize);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        try {
            String matchInfoSql = "SELECT timer, boardinfo, wins FROM matchinfo";
            PreparedStatement matchInfoPs = conn.prepareStatement(matchInfoSql);
            ResultSet matchInfoRs = matchInfoPs.executeQuery();

            if (matchInfoRs.next()) {
                int timerValue = matchInfoRs.getInt("timer");
                int boardInfo = matchInfoRs.getInt("boardinfo");
                int winValue = matchInfoRs.getInt("wins");

                timerCheckBox.setSelected(timerValue == 1);
                boardinfoCheckBox.setSelected(boardInfo == 1);
                winCheckBox.setSelected(winValue == 1);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void applysettings() {// game mode apply settings
        if (gameModeComboBox.getSelectedIndex() == 0) {
            try {
                String sql = "update generalsettings set game_mode = 0 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else if (gameModeComboBox.getSelectedIndex() == 1) {
            try {
                String sql = "update generalsettings set game_mode = 1 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public void applyBoard() {// board apply method
        if (boardComboBox.getSelectedIndex() == 0) {
            try {
                String sql = "update generalsettings set boardsize = 0 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else if (boardComboBox.getSelectedIndex() == 1) {
            try {
                String sql = "update generalsettings set boardsize = 1 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else if (boardComboBox.getSelectedIndex() == 2) {
            try {
                String sql = "update generalsettings set boardsize = 2 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else if (boardComboBox.getSelectedIndex() == 3) {
            try {
                String sql = "update generalsettings set boardsize = 3 ";
                ps = conn.createStatement();
                ps.executeUpdate(sql);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }
     public void playSound(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public settings() {
        conn = Conn.ConnectDB();

        JFrame settingsFrame = new JFrame("Settings");
        settingsFrame.setSize(400, 600);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = new ImageIcon(
                        "gambar\\bggame.png")
                        .getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        settingsPanel.setLayout(null); // Set layout manager to null

        // Set the size and position of the settingsPanel
        settingsPanel.setBounds(0, 0, 400, 600);

        JLabel label = new JLabel("Settings");
        label.setBounds(150, 10, 100, 30); // Set the position and size of the label
        label.setForeground(Color.decode("#A44E19"));
        label.setFont(new Font("Roman", Font.BOLD, 20));
        settingsPanel.add(label);

        // GAME MODE CHANGE
        JLabel modeLabel = new JLabel("Game Mode: ");
        modeLabel.setBounds(50, 50, 100, 30); // Set the position and size of the modeLabel
        modeLabel.setForeground(Color.decode("#A44E19"));
        modeLabel.setFont(new Font("Roman", Font.BOLD, 13));
        settingsPanel.add(modeLabel);

        String[] gameMode = { "vs Player", "Easy Bot" };
        gameModeComboBox = new JComboBox<>(gameMode);
        gameModeComboBox.setBounds(150, 50, 150, 30); // Set the position and size of the gameModeComboBox
        selectedgameMode = (String) gameModeComboBox.getSelectedItem();

        settingsPanel.add(gameModeComboBox);
        // END OF GAME MODE CHANGE
        // BOARD TYPE CHANGE

        
        JLabel boardLabel = new JLabel("Board:");
        boardLabel.setBounds(80, 100, 100, 30);
        boardLabel.setForeground(Color.decode("#A44E19"));
        boardLabel.setFont(new Font("Roman", Font.BOLD, 13));
        settingsPanel.add(boardLabel);

        String[] boards = { "3x3", "4x4", "5x5", "6x6" };
        boardComboBox = new JComboBox<>(boards);
        boardComboBox.setBounds(150, 100, 150, 30); // Set the position and size of the boardComboBox
        settingsPanel.add(boardComboBox);

        // END OF BOARD TYPE CHANGE
        // TIMER CHECKBOX
        timerCheckBox = new JCheckBox("Enable Match Timer");
        timerCheckBox.setBounds(120, 200, 150, 30); // Set the position and size of the checkbox
        timerCheckBox.setForeground(Color.decode("#A44E19"));
        timerCheckBox.setOpaque(false);
        timerCheckBox.setSelected(true);

        timerCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int timerValue = timerCheckBox.isSelected() ? 1 : 0;
                    String sql = "update matchinfo set timer = " + timerValue;
                    ps = conn.createStatement();
                    ps.executeUpdate(sql);
                    playSound("gambar/clickmeow.wav");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        settingsPanel.add(timerCheckBox);

        // BOARD INFO
        boardinfoCheckBox = new JCheckBox("Enable Board Info ");
        boardinfoCheckBox.setBounds(120, 250, 150, 30); // Set the position and size of the checkbox
        boardinfoCheckBox.setForeground(Color.decode("#A44E19"));
        boardinfoCheckBox.setOpaque(false);
        boardinfoCheckBox.setSelected(true);

        boardinfoCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int boardInfo = boardinfoCheckBox.isSelected() ? 1 : 0;
                    String sql = "update matchinfo set boardinfo = " + boardInfo;
                    ps = conn.createStatement();
                    ps.executeUpdate(sql);
                    playSound("gambar/clickmeow.wav");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        settingsPanel.add(boardinfoCheckBox);

        // WIN CHECK
        winCheckBox = new JCheckBox("Enable Wins Info ");
        winCheckBox.setBounds(120, 300, 150, 30); // Set the position and size of the checkbox
        winCheckBox.setForeground(Color.decode("#A44E19"));
        winCheckBox.setOpaque(false);
        winCheckBox.setSelected(true);

        winCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int winValue = winCheckBox.isSelected() ? 1 : 0;
                    String sql = "update matchinfo set wins = " + winValue;
                    ps = conn.createStatement();
                    ps.executeUpdate(sql);
                    playSound("gambar/clickmeow.wav");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });
        settingsPanel.add(winCheckBox);

        // RESET BUTTON
        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(90, 350, 100, 30);
        resetButton.setForeground(Color.decode("#A44E19"));
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setOpaque(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("reset button clicked");
                playSound("gambar/clickmeow.wav");
                try {
                    String sql = "update generalsettings set boardsize = 0, game_mode=0";
                    String sql2 = "update matchinfo set timer = 1, wins=1, boardinfo=1";
                    ps = conn.prepareStatement(sql);
                    pst =conn.prepareStatement(sql2);
                    gameModeComboBox.setSelectedIndex(0);
                    boardComboBox.setSelectedIndex(0);
                    timerCheckBox.setSelected(true);
                    boardinfoCheckBox.setSelected(true);
                    winCheckBox.setSelected(true);
                    ps.executeUpdate(sql);
                    pst.executeUpdate(sql2);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

        });
        settingsPanel.add(resetButton);
        // END OF RESET BUTTON
        // APPLY BUTTON
          JButton applyButton = new JButton("Apply");
        applyButton.setBounds(180, 350, 100, 30);
        applyButton.setForeground(Color.decode("#A44E19"));
        applyButton.setContentAreaFilled(false);
        applyButton.setBorderPainted(false);
        applyButton.setOpaque(false);
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("apply button clicked");
                // db stuff kat sini
                applysettings();
                applyBoard();
                playSound("gambar/clickmeow.wav");

                settingsFrame.dispose();
            }

        });
        settingsPanel.add(applyButton);
        // END OF APPLY BUTTON
        settingsFrame.add(settingsPanel);
        settingsFrame.setVisible(true);
        retrieveSettings();
    }

    public static void main(String[] args) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new settings();
            }
        });
    }

    public void setVisible(boolean b) {
    }
}

   
