import java.awt.*;
import javax.swing.*;

public class SistemaGUI extends JFrame {
    private final SalaoDAO dao = new SalaoDAO();

    public SistemaGUI() {
        setTitle("Salãozinho do T.I");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10)); // 7 botões, com espaçamento

        JButton btnCadastrarCliente     = new JButton("Cadastrar Cliente");
        JButton btnListarClientes       = new JButton("Listar Clientes");
        JButton btnCancelarCliente      = new JButton("Cancelar Cad.Cliente");
        JButton btnAgendarServico       = new JButton("Agendar Serviço");
        JButton btnListarAgendamentos   = new JButton("Listar Agendamentos");
        JButton btnCancelarAgendamento  = new JButton("Cancelar Agendamento");
        JButton btnSair                 = new JButton("Sair");

        btnCadastrarCliente     .addActionListener(e -> dao.cadastrarClienteGUI());
        btnListarClientes       .addActionListener(e -> dao.listarClientesGUI());
        btnCancelarCliente      .addActionListener(e -> dao.cancelarClienteGUI());
        btnAgendarServico       .addActionListener(e -> dao.agendarGUI());
        btnListarAgendamentos   .addActionListener(e -> dao.listarAgendamentosGUI());
        btnCancelarAgendamento  .addActionListener(e -> dao.cancelarAgendamentoGUI());
        btnSair                 .addActionListener(e -> System.exit(0));

        add(btnCadastrarCliente);
        add(btnListarClientes);
        add(btnCancelarCliente);
        add(btnAgendarServico);
        add(btnListarAgendamentos);
        add(btnCancelarAgendamento);
        add(btnSair);

        setVisible(true);
    }
}
