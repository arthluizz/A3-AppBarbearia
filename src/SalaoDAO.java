import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.text.MaskFormatter;

public class SalaoDAO {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dbFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- Cadastrar cliente via GUI com máscara para telefone e CPF ---
    public void cadastrarClienteGUI() {
        try {
            MaskFormatter maskTelefone = new MaskFormatter("(##) #####-####");
            maskTelefone.setPlaceholderCharacter('_');
            JFormattedTextField ftfTelefone = new JFormattedTextField(maskTelefone);

            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            JFormattedTextField ftfCPF = new JFormattedTextField(maskCPF);

            JLabel lblNome = new JLabel("Nome:");
            JTextArea tfNome = new JTextArea(1, 20);

            JPanel panel = new JPanel();
            panel.add(lblNome);
            panel.add(tfNome);
            panel.add(new JLabel("Telefone:"));
            panel.add(ftfTelefone);
            panel.add(new JLabel("CPF:"));
            panel.add(ftfCPF);

            int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Cadastrar Cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                Logger.log("Cadastro de cliente cancelado pelo usuário.");
                return;
            }

            String nome = tfNome.getText().trim();
            String telefone = ftfTelefone.getText().trim();
            String cpf = ftfCPF.getText().trim();

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(null, "O nome não pode ficar vazio.");
                Logger.log("Cadastro de cliente falhou: nome vazio.");
                return;
            }
            if (telefone.contains("_")) {
                JOptionPane.showMessageDialog(null, "Telefone incompleto ou inválido.");
                Logger.log("Cadastro de cliente falhou: telefone inválido.");
                return;
            }
            if (cpf.contains("_")) {
                JOptionPane.showMessageDialog(null, "CPF incompleto ou inválido.");
                Logger.log("Cadastro de cliente falhou: CPF inválido.");
                return;
            }

            try (Connection conn = Database.connect()) {
                String sql = "INSERT INTO clientes (nome, telefone, cpf) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nome);
                ps.setString(2, telefone);
                ps.setString(3, cpf);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
                Logger.log("Cliente cadastrado: nome=" + nome + ", telefone=" + telefone + ", cpf=" + cpf);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar cliente no banco de dados: " + e.getMessage());
            Logger.log("Erro ao salvar cliente (GUI): " + e.getMessage());
        } catch (Exception e) {
            Logger.log("Erro inesperado no cadastro de cliente (GUI): " + e.getMessage());
        }
    }

    // --- Listar clientes via GUI com filtro por nome e lista completa ---
    public void listarClientesGUI() {
        // 1) Carrega todos os clientes em memória
        List<String> allClients = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome, telefone, cpf FROM clientes ORDER BY id")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String cpf = rs.getString("cpf");
                // Monta a string exibida em cada linha da lista
                allClients.add(id + " - " + nome + " | " + telefone + " | " + cpf);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + e.getMessage());
            Logger.log("Erro ao listar clientes (GUI): " + e.getMessage());
            return;
        }

        if (allClients.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
            Logger.log("Listagem de clientes exibida: nenhum cliente cadastrado.");
            return;
        }

        // 2) Cria modelo e JList para exibir todos os clientes
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String linha : allClients) {
            model.addElement(linha);
        }
        JList<String> jList = new JList<>(model);
        jList.setVisibleRowCount(10);

        // 3) Campo de texto para filtro de nome
        JTextField tfSearch = new JTextField(15);
        JButton btnSearch = new JButton("Pesquisar");

        // 4) Ao clicar em "Pesquisar", filtra o model de acordo com o texto digitado
        btnSearch.addActionListener(e -> {
            String filtro = tfSearch.getText().trim().toLowerCase();
            model.clear();
            for (String linha : allClients) {
                // Só compara o conteúdo inteiro da linha (que inclui "id - nome ...")
                if (linha.toLowerCase().contains(filtro)) {
                    model.addElement(linha);
                }
            }
        });

        // 5) Monta painel superior com etiqueta, campo de pesquisa e botão
        JPanel panelTop = new JPanel();
        panelTop.add(new JLabel("Pesquisar nome:"));
        panelTop.add(tfSearch);
        panelTop.add(btnSearch);

        // 6) Cria janela própria para exibir lista + filtro
        JFrame frame = new JFrame("Lista de Clientes");
        frame.setLayout(new BorderLayout());
        frame.add(panelTop, BorderLayout.NORTH);
        frame.add(new JScrollPane(jList), BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Logger.log("Listagem de clientes exibida (GUI) com caixa de pesquisa.");
    }

    // --- Cancelar (excluir) cliente via GUI com lista clicável ---
    public void cancelarClienteGUI() {
        try {
            List<String> listaClientes = new ArrayList<>();
            try (Connection conn = Database.connect();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, nome FROM clientes ORDER BY id")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    listaClientes.add(id + " - " + nome);
                }
            }

            if (listaClientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado para excluir.");
                Logger.log("Tentativa de cancelar cliente sem clientes cadastrados.");
                return;
            }

            JList<String> jList = new JList<>(listaClientes.toArray(new String[0]));
            jList.setVisibleRowCount(8);
            JScrollPane scrollPane = new JScrollPane(jList);
            int sel = JOptionPane.showConfirmDialog(
                null,
                scrollPane,
                "Selecione o Cliente para Excluir",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (sel != JOptionPane.OK_OPTION) {
                Logger.log("Cancelamento de cliente abortado pelo usuário (não selecionado).");
                return;
            }

            String selecionado = jList.getSelectedValue();
            if (selecionado == null) {
                JOptionPane.showMessageDialog(null, "Nenhum cliente selecionado.");
                Logger.log("Nenhum cliente selecionado para cancelamento.");
                return;
            }
            int clienteId = Integer.parseInt(selecionado.split(" - ")[0]);

            try (Connection conn = Database.connect()) {
                String sqlDelete = "DELETE FROM clientes WHERE id = ?";
                PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
                psDelete.setInt(1, clienteId);
                int result = psDelete.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente excluído com sucesso.");
                    Logger.log("Cliente excluído: id=" + clienteId);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir cliente: " + e.getMessage());
            Logger.log("Erro SQL no cancelamento de cliente (GUI): " + e.getMessage());
        } catch (Exception e) {
            Logger.log("Erro inesperado no cancelamento de cliente (GUI): " + e.getMessage());
        }
    }

    // --- Agendar via GUI ---
    // Exibe lista clicável de clientes, serviços, e dois Spinners (data e hora separados),
    // valida não aceitar data/hora anterior, verifica conflito mínimo 30 minutos,
    // salva usando Timestamp para não haver erro de formatação.
    public void agendarGUI() {
        try {
            // (1) Carregar lista de clientes disponíveis
            List<String> listaClientes = new ArrayList<>();
            try (Connection conn = Database.connect();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, nome FROM clientes ORDER BY id")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    listaClientes.add(id + " - " + nome);
                }
            }

            if (listaClientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado para agendar.");
                Logger.log("Tentativa de agendar sem clientes cadastrados.");
                return;
            }

            // (2) Exibir JList para seleção de cliente
            JList<String> jList = new JList<>(listaClientes.toArray(new String[0]));
            jList.setVisibleRowCount(8);
            JScrollPane scrollPane = new JScrollPane(jList);
            int selCliente = JOptionPane.showConfirmDialog(
                null,
                scrollPane,
                "Selecione o Cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (selCliente != JOptionPane.OK_OPTION) {
                Logger.log("Agendamento cancelado na seleção de cliente.");
                return;
            }
            String clienteSelecionado = jList.getSelectedValue();
            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(null, "Nenhum cliente selecionado.");
                Logger.log("Nenhum cliente selecionado para agendamento.");
                return;
            }
            int clienteId = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

            // (3) Seleção de serviço
            String[] opcoesServico = { "Corte de cabelo (R$30)", "Barba (R$25)", "Combo (Corte + Barba) (R$55)" };
            int opcao = JOptionPane.showOptionDialog(
                null,
                "Escolha o serviço:",
                "Serviços",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesServico,
                opcoesServico[0]
            );
            if (opcao == JOptionPane.CLOSED_OPTION) {
                Logger.log("Agendamento cancelado pelo usuário antes de escolher serviço.");
                return;
            }
            String servico;
            double preco;
            switch (opcao) {
                case 0 -> { servico = "Corte de cabelo"; preco = 30.0; }
                case 1 -> { servico = "Barba"; preco = 25.0; }
                case 2 -> { servico = "Combo (Corte + Barba)"; preco = 55.0; }
                default -> {
                    JOptionPane.showMessageDialog(null, "Opção de serviço inválida.");
                    Logger.log("Opção de serviço inválida no agendamento (GUI): opcao=" + opcao);
                    return;
                }
            }

            // (4) Spinner para data (DD/MM/AAAA)
            SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
            JSpinner spinnerData = new JSpinner(dateModel);
            spinnerData.setEditor(new DateEditor(spinnerData, "dd/MM/yyyy"));

            // (5) Spinner para hora (HH:mm)
            SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
            JSpinner spinnerHora = new JSpinner(timeModel);
            spinnerHora.setEditor(new DateEditor(spinnerHora, "HH:mm"));

            JPanel panel = new JPanel();
            panel.add(new JLabel("Data (dd/MM/yyyy):"));
            panel.add(spinnerData);
            panel.add(new JLabel("Hora (HH:mm):"));
            panel.add(spinnerHora);

            int opcaoDataHora = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Selecione Data e Hora",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (opcaoDataHora != JOptionPane.OK_OPTION) {
                Logger.log("Agendamento cancelado na seleção de data/hora (clienteId=" + clienteId + ").");
                return;
            }

            // Extrair valores dos spinners
            Date dateValue = (Date) spinnerData.getValue();
            LocalDate localDate = Instant.ofEpochMilli(dateValue.getTime())
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDate();

            Date timeValue = (Date) spinnerHora.getValue();
            LocalTime localTime = Instant.ofEpochMilli(timeValue.getTime())
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalTime()
                                         .withSecond(0)
                                         .withNano(0);

            LocalDateTime dataHora = LocalDateTime.of(localDate, localTime);

            // (6) Validar data/hora não ser anterior ao momento atual
            if (dataHora.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(null, "Não é permitido agendar para data/hora passada.");
                Logger.log("Tentativa de agendar para data/hora passada: clienteId=" + clienteId
                           + ", dataHora=" + dataHora.format(dbFormatter));
                return;
            }

            // (7) Verificar conflito de horários: mínimo 30 minutos
            try (Connection conn = Database.connect()) {
                String sqlCheck = """
                    SELECT data_hora FROM agendamentos
                    WHERE ABS(TIMESTAMPDIFF(MINUTE, data_hora, ?)) < 30
                """;
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setTimestamp(1, Timestamp.valueOf(dataHora));
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    JOptionPane.showMessageDialog(null,
                        "Erro: ESCOLHA UM HORÁRIO DIFERENTE! Já existe um agendamento próximo a este horário.");
                    Logger.log("Falha no agendamento (conflito): clienteId=" + clienteId
                               + ", dataHora=" + dataHora.format(dbFormatter));
                    return;
                }

                // (8) Inserir agendamento usando Timestamp
                String sqlInsert = "INSERT INTO agendamentos (cliente_id, data_hora, servico, preco) VALUES (?, ?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setInt(1, clienteId);
                psInsert.setTimestamp(2, Timestamp.valueOf(dataHora));
                psInsert.setString(3, servico);
                psInsert.setDouble(4, preco);

                int result = psInsert.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Agendamento realizado com sucesso!");
                    Logger.log("Agendamento realizado: clienteId=" + clienteId
                               + ", servico=" + servico
                               + ", dataHora=" + dataHora.format(dbFormatter));
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao realizar o agendamento.");
                    Logger.log("Erro ao realizar o agendamento no banco: clienteId=" + clienteId
                               + ", servico=" + servico);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco: " + e.getMessage());
            Logger.log("Erro SQL no agendamento (GUI): " + e.getMessage());
        } catch (Exception e) {
            Logger.log("Erro inesperado no agendamento (GUI): " + e.getMessage());
        }
    }

    // --- Listar agendamentos via GUI ---
    public void listarAgendamentosGUI() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== AGENDAMENTOS ===\n\n");
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("""
                    SELECT a.id, c.nome, c.telefone, c.cpf, a.data_hora, a.servico, a.preco
                    FROM agendamentos a
                    JOIN clientes c ON a.cliente_id = c.id
                    ORDER BY a.data_hora
                """)) {

            boolean temAgendamentos = false;
            while (rs.next()) {
                temAgendamentos = true;
                Timestamp ts = rs.getTimestamp("data_hora");
                LocalDateTime ldt = ts.toLocalDateTime();
                String data = ldt.toLocalDate().format(dateFormatter);
                String hora = ldt.toLocalTime().format(timeFormatter);

                sb.append("ID: ").append(rs.getInt("id"))
                  .append(" | Cliente: ").append(rs.getString("nome"))
                  .append(" | Tel: ").append(rs.getString("telefone"))
                  .append(" | CPF: ").append(rs.getString("cpf"))
                  .append(" | Data: ").append(data)
                  .append(" | Hora: ").append(hora)
                  .append(" | Serviço: ").append(rs.getString("servico"))
                  .append(" | Preço: R$").append(rs.getDouble("preco"))
                  .append("\n");
            }
            if (!temAgendamentos) {
                sb.append("Nenhum agendamento encontrado.");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Agendamentos", JOptionPane.INFORMATION_MESSAGE);
            Logger.log("Listagem de agendamentos exibida (GUI).");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar agendamentos: " + e.getMessage());
            Logger.log("Erro ao listar agendamentos (GUI): " + e.getMessage());
        }
    }

    // --- Cancelar agendamento via GUI com lista clicável ---
    public void cancelarAgendamentoGUI() {
        try {
            List<String> listaAgendamentos = new ArrayList<>();
            try (Connection conn = Database.connect();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("""
                        SELECT a.id, c.nome, a.data_hora, a.servico
                        FROM agendamentos a
                        JOIN clientes c ON a.cliente_id = c.id
                        ORDER BY a.data_hora
                    """)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    Timestamp ts = rs.getTimestamp("data_hora");
                    LocalDateTime ldt = ts.toLocalDateTime();
                    String data = ldt.toLocalDate().format(dateFormatter);
                    String hora = ldt.toLocalTime().format(timeFormatter);
                    String servico = rs.getString("servico");
                    listaAgendamentos.add(id + " - " + nome + " - " + data + " " + hora + " - " + servico);
                }
            }

            if (listaAgendamentos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum agendamento para cancelar.");
                Logger.log("Tentativa de cancelar agendamento sem agendamentos cadastrados.");
                return;
            }

            JList<String> jList = new JList<>(listaAgendamentos.toArray(new String[0]));
            jList.setVisibleRowCount(8);
            JScrollPane scrollPane = new JScrollPane(jList);
            int sel = JOptionPane.showConfirmDialog(
                null,
                scrollPane,
                "Selecione o Agendamento para Cancelar",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (sel != JOptionPane.OK_OPTION) {
                Logger.log("Cancelamento de agendamento abortado pelo usuário (não selecionado).");
                return;
            }

            String selecionado = jList.getSelectedValue();
            if (selecionado == null) {
                JOptionPane.showMessageDialog(null, "Nenhum agendamento selecionado.");
                Logger.log("Nenhum agendamento selecionado para cancelamento.");
                return;
            }
            int agendamentoId = Integer.parseInt(selecionado.split(" - ")[0]);

            try (Connection conn = Database.connect()) {
                String sqlDelete = "DELETE FROM agendamentos WHERE id = ?";
                PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
                psDelete.setInt(1, agendamentoId);
                int result = psDelete.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Agendamento cancelado com sucesso.");
                    Logger.log("Agendamento cancelado com sucesso: id=" + agendamentoId);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cancelar agendamento: " + e.getMessage());
            Logger.log("Erro SQL no cancelamento de agendamento (GUI): " + e.getMessage());
        } catch (Exception e) {
            Logger.log("Erro inesperado no cancelamento de agendamento (GUI): " + e.getMessage());
        }
    }
}
