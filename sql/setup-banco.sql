-- Cria o usuário e o banco da aplicação. Rode UMA vez como administrador do PostgreSQL:
--   sudo -u postgres psql < sql/setup-banco.sql
--
-- Troque 'troque-esta-senha' por uma senha sua e use a mesma no arquivo db.properties.

CREATE USER clientes_app WITH PASSWORD 'troque-esta-senha';
CREATE DATABASE clientes_db OWNER clientes_app;
