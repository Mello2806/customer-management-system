# customer-management-system

Sistema de cadastro de clientes (CRUD) no console, desenvolvido em Java com JDBC e PostgreSQL. Projeto de estudo do curso de Análise e Desenvolvimento de Sistemas.

## Funcionalidades

- Cadastrar, listar, buscar (por nome ou id), editar e excluir clientes
- Validação de nome, e-mail, telefone e **CPF pelos dígitos verificadores**
- Impede e-mail ou CPF duplicado (no código e no banco, com `UNIQUE`)
- Confirmação antes de excluir
- Consultas com `PreparedStatement`, que protege contra SQL Injection

## Tecnologias

- Java 17
- PostgreSQL + JDBC
- Maven
- JUnit 5

## Arquitetura

O projeto é dividido em camadas, cada uma com uma responsabilidade:

```
Main (menu)  →  ClienteService (regras)  →  ClienteDAO (SQL)  →  PostgreSQL
```

```
src/main/java/br/com/mello/clientes/
├── Main.java                      # menu no console
├── model/Cliente.java             # entidade (espelha a tabela clientes)
├── service/ClienteService.java    # validações e regras de negócio
├── service/ValidadorCpf.java      # cálculo dos dígitos do CPF
├── dao/ClienteDAO.java            # interface de acesso a dados
├── dao/ClienteDAOJdbc.java        # implementação com JDBC
└── config/ConnectionFactory.java  # cria conexões com o banco
sql/
├── setup-banco.sql                # cria usuário e banco
└── schema.sql                     # cria a tabela clientes
```

`ClienteDAO` é uma interface: o programa usa a versão JDBC e os testes usam uma versão em memória, então os testes de regra de negócio rodam sem banco.

## Como rodar

### 1. Criar o banco

Edite a senha em `sql/setup-banco.sql` e rode como administrador do PostgreSQL:

```bash
sudo -u postgres psql < sql/setup-banco.sql
```

Depois crie a tabela:

```bash
psql -h localhost -U clientes_app -d clientes_db -f sql/schema.sql
```

### 2. Configurar a conexão

```bash
cp db.properties.example db.properties
```

Edite o `db.properties` com a senha escolhida. Ele está no `.gitignore` e não vai para o GitHub.

### 3. Executar

```bash
mvn compile exec:java
```

## Testes

```bash
mvn test
```

- `ValidadorCpfTest` e `ClienteServiceTest` rodam sem banco.
- `ClienteDAOJdbcTest` testa o SQL no PostgreSQL de verdade e só roda se o `db.properties` existir. Ele apaga, no final, só os registros que ele mesmo criou.

## Exemplo de uso

```
=== Cadastro de Clientes ===

1 - Cadastrar cliente
2 - Listar clientes
3 - Buscar por nome
4 - Buscar por id
5 - Editar cliente
6 - Excluir cliente
0 - Sair
Escolha uma opção: 2
#1 | Ana Souza | ana@email.com | CPF 529.982.247-25 | Tel. (81) 99999-0000
1 cliente(s).
```

## Próximos passos

- [ ] Endereço do cliente (nova tabela com chave estrangeira)
- [ ] Paginação na listagem
- [ ] Transformar em API REST com Spring Boot (projeto [customer-api](https://github.com/Mello2806/customer-api))
