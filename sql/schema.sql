-- Estrutura do banco do sistema de cadastro de clientes.
-- Rodar conectado ao banco "clientes_db":
--   psql -h localhost -U clientes_app -d clientes_db -f sql/schema.sql

CREATE TABLE IF NOT EXISTS clientes (
    id            SERIAL       PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    cpf           CHAR(11)     NOT NULL UNIQUE,
    telefone      VARCHAR(20),
    data_cadastro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_clientes_nome ON clientes (nome);
