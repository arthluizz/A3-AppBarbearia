Sistema de Agendamento para Barbearia

Descrição do Programa

Se trata de um projeto de avaliação de faculade, não será comercializado.

Este projeto é um sistema de agendamento para uma barbearia, desenvolvido em Java com interface gráfica Swing. O sistema permite:
- Cadastrar clientes (com validação e máscara de CPF e telefone).
- Listar clientes (com caixa de pesquisa por nome).
- Excluir clientes via lista clicável.
- Agendar serviços (corte de cabelo, barba ou combo), selecionando data e hora separadamente.
- Não permite horários passados.
- Garante um intervalo mínimo de 30 minutos entre agendamentos.
- Listar agendamentos com exibição de data e hora separadas.
- Cancelar agendamentos via lista clicável.
- Gerar log de todas as operações (cadastro, exclusão, agendamento, cancelamento) no arquivo sistema_log.txt.

Pré-requisitos
1. Java Development Kit (JDK) 11 ou superior instalado e configurado no PATH.
2. MySQL em execução local (porta padrão: 3306).
3. Driver JDBC do MySQL (mysql-connector-java-x.x.x.jar) adicionado ao classpath.
4. IDE ou editor compatível com Java (Eclipse, IntelliJ, VSCode, etc.).
Estrutura do Banco de Dados

Banco: salaodb
Tabelas:
clientes:
CREATE TABLE clientes (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
telefone VARCHAR(15) NOT NULL,
cpf VARCHAR(14) NOT NULL
);
agendamentos:
CREATE TABLE agendamentos (
id INT AUTO_INCREMENT PRIMARY KEY,
cliente_id INT NOT NULL,
data_hora DATETIME NOT NULL,
servico VARCHAR(50) NOT NULL,
preco DECIMAL(7,2) NOT NULL,
FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

Como Compilar e Executar
Estrutura sugerida de pastas:
/AppBarbeariaV3
├── lib/
│   └── mysql-connector-java-8.0.x.jar
├── src/
│   ├── Database.java
│   ├── Logger.java
│   ├── SalaoDAO.java
│   └── SistemaGUI.java
└── README.md

Importando script no MySQLWorkbench(alternativa)
1. Execute o MySQl, abra o servidor na maquina
2. No ambiente de desenvolvimento coloque "create database salaodb"
3. Após criar o banco: server>data import>AppBarbeariaV3>salaodb.ql

Compilação via linha de comando:
Windows:
cd AppBarbeariaV3
javac -cp "lib/mysql-connector-java-8.0.x.jar" src\Database.java src\Logger.java src\SalaoDAO.java src\SistemaGUI.java
Linux/macOS:
cd AppBarbeariaV3
javac -cp "lib/mysql-connector-java-8.0.x.jar:src" src/Database.java src/Logger.java src/SalaoDAO.java src/SistemaGUI.java
Execução:
Windows:
java -cp "lib/mysql-connector-java-8.0.x.jar;src" SistemaGUI
Linux/macOS:
java -cp "lib/mysql-connector-java-8.0.x.jar:src" SistemaGUI

Execução via IDE:
1. Abra a pasta do projeto.
2. Adicione lib/mysql-connector-java-8.0.x.jar como biblioteca externa.
3. Marque a pasta src/ como diretório de código-fonte.
4. Execute a classe Main.

Funcionalidades
Cadastro de Cliente:
- Campos: nome, telefone, CPF
- Validações: nome não pode estar vazio, telefone e CPF devem estar completos
Listagem de Clientes:
- Campo de pesquisa que filtra a lista em tempo real
Exclusão de Cliente:
- Lista de clientes com seleção e confirmação de exclusão
Agendamento de Serviço:
- Selecionar cliente, serviço, data e hora
- Validações de data/hora passada e conflitos de horário
Listagem de Agendamentos:
- Exibição formatada dos dados agendados
Cancelamento de Agendamento:
- Lista de agendamentos para seleção e cancelamento

Classes Principais
Database.java: gerencia conexão com o MySQL
Logger.java: registra logs no arquivo sistema_log.txt
SalaoDAO.java: implementa as funcionalidades de CRUD e agendamento
SistemaGUI.java: interface gráfica principal e ponto de entrada (main)
Logs
Todas as ações relevantes são registradas em sistema_log.txt com data e hora.

Observações Finais
- Garanta que o driver JDBC esteja disponível no classpath antes de executar.
- Ajuste as credenciais de banco (usuário/senha) no método Database.connect() conforme seu ambiente.
- Caso deseje alterar os formatos de data/hora, basta modificar os padrões usados no DateTimeFormatter ou nas máscaras dos JSpinner.
- Este projeto pode ser facilmente estendido para incluir mais serviços, geração de relatórios ou integração com tecnologias como Hibernate, JavaFX, entre outras.
