CREATE TABLE IF NOT EXISTS aeroporto (
    codigo  VARCHAR(10)  PRIMARY KEY,
    nome    VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS vaga (
    aeroporto_codigo  VARCHAR(10) NOT NULL REFERENCES aeroporto (codigo),
    numero            INT         NOT NULL,
    PRIMARY KEY (aeroporto_codigo, numero)
);

CREATE TABLE IF NOT EXISTS aerovia (
    id              VARCHAR(20)      PRIMARY KEY,
    origem_codigo   VARCHAR(10)      NOT NULL REFERENCES aeroporto (codigo),
    destino_codigo  VARCHAR(10)      NOT NULL REFERENCES aeroporto (codigo),
    distancia_km    DOUBLE PRECISION NOT NULL
);
