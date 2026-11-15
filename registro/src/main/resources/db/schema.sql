CREATE TABLE IF NOT EXISTS registro_aprovacao (
    evento_id    VARCHAR(36) PRIMARY KEY,
    plano_id     BIGINT      NOT NULL UNIQUE,
    aprovado_em  TIMESTAMP,
    recebido_em  TIMESTAMP   NOT NULL,
    conteudo     CLOB        NOT NULL
);
