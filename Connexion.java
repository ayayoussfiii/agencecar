import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                String url = "jdbc:ucanaccess://C:/Users/lenovo/Documents/BDProjetAya.accdb";
                connection = DriverManager.getConnection(url);
                System.out.println("Connexion réussie !");
            } catch (Exception e) {
                System.out.println("Erreur de connexion : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }
}

