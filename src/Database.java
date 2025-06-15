import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Logger.log("Driver MySQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            Logger.log("Driver MySQL não encontrado: " + e.getMessage());
            return null;
        }

        String url = "jdbc:mysql://localhost:3306/salaodb?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "admin";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Logger.log("Conexão estabelecida com o banco: " + url);
            return conn;
        } catch (SQLException e) {
            Logger.log("Falha ao conectar no banco de dados: " + e.getMessage());
            throw e;
        }
    }
}
