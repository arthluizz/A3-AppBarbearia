import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) {
        // Registra início do programa
        Logger.log("Programa iniciado.");

        SwingUtilities.invokeLater(() -> {
            SistemaGUI gui = new SistemaGUI();
            gui.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Logger.log("Programa encerrado pelo usuário.");
                    System.exit(0);
                }
            });
        });
    }
}
