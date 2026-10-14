-- Carga inicial.
--
-- Exigencias do enunciado (secao 8.2):
--   - malha aerea inicial: 4 aeroportos (POA 41, FLN 16, CWB 26, GRU 115) e
--     8 aerovias (R1..R8);
--   - pilotos;
--   - aeronaves das TRES categorias (particular, passageiros, carga);
--   - planos APROVADOS com suas reservas;                       [PENDENTE]
--   - na DATA DE REFERENCIA, pelo menos 5 das 8 aerovias com alguma ocupacao;
--   - a ocupacao e consequencia dos planos aprovados, nunca reserva solta;
--   - os dados permitem tanto sucesso quanto conflito: nao deixar todos os
--     recursos indisponiveis durante todo o dia.
--
-- DATA DE REFERENCIA: [PREENCHER] - e preciso constar tambem no README.md.
--
-- O script roda a cada inicializacao. MERGE (em vez de INSERT) evita duplicar
-- os dados quando a aplicacao e reiniciada com o banco ja preenchido.

MERGE INTO aeroporto (codigo, nome, quantidade_vagas) KEY (codigo) VALUES
    ('POA', 'Aeroporto Internacional Salgado Filho - Porto Alegre', 41),
    ('FLN', 'Aeroporto Internacional de Florianópolis/Hercílio Luz', 16),
    ('CWB', 'Aeroporto Internacional Afonso Pena - Curitiba', 26),
    ('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 115);

MERGE INTO aerovia (id, origem_codigo, destino_codigo, distancia_km) KEY (id) VALUES
    ('R1', 'POA', 'FLN', 363),
    ('R2', 'POA', 'GRU', 865),
    ('R3', 'GRU', 'POA', 865),
    ('R4', 'FLN', 'CWB', 245),
    ('R5', 'CWB', 'FLN', 245),
    ('R6', 'CWB', 'GRU', 360),
    ('R7', 'GRU', 'CWB', 360),
    ('R8', 'FLN', 'POA', 363);

MERGE INTO piloto (id, nome) KEY (id) VALUES
    (1, 'Ana Souza'),
    (2, 'Bruno Lima'),
    (3, 'Carla Mendes'),
    (4, 'Diego Rocha');

-- PP-CUR tem autonomia curta de proposito: 85% de 1000 km = 850 km, menos que
-- os 865 km da R2. Serve para demonstrar a falha de autonomia.
MERGE INTO aeronave (prefixo, categoria, velocidade_cruzeiro_kmh, autonomia_km,
                     empresa_manutencao, companhia_aerea, capacidade_passageiros, capacidade_toneladas)
    KEY (prefixo) VALUES
    ('PP-PAR', 'PARTICULAR',  650, 2800, 'Manutenção Aérea Sul', NULL, NULL, NULL),
    ('PP-CUR', 'PARTICULAR',  500, 1000, 'Hangar POA',           NULL, NULL, NULL),
    ('PR-PAX', 'PASSAGEIROS', 830, 5000, NULL, 'Linhas Aéreas do Sul', 180, NULL),
    ('PR-CRG', 'CARGA',       780, 4500, NULL, 'Sul Cargas',          NULL, 20);
