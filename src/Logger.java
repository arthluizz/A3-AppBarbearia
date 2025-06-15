import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = "sistema_log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Método para registrar uma mensagem no log
    public static void log(String mensagem) {
        String dataHora = LocalDateTime.now().format(formatter);
        String texto = dataHora + " - " + mensagem + System.lineSeparator();

        try (FileWriter fw = new FileWriter(LOG_FILE, true)) { // 'true' = append no arquivo
            fw.write(texto);
        } catch (IOException e) {
            System.out.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}
