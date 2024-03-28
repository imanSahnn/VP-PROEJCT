import java.sql.*;

import javax.swing.JOptionPane;

public class Conn {
    public static Connection ConnectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vpproject", "root", "");
            System.out.println("Database Connected!");
            return Conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
