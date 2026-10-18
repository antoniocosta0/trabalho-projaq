-- Carga inicial.
--
-- Exigencias do enunciado (secao 8.2):
--   - malha aerea inicial: 4 aeroportos (POA 41, FLN 16, CWB 26, GRU 115) e
--     8 aerovias (R1..R8);
--   - pilotos;
--   - aeronaves das TRES categorias (particular, passageiros, carga);
--   - planos APROVADOS com suas reservas;
--   - na DATA DE REFERENCIA, pelo menos 5 das 8 aerovias com alguma ocupacao;
--   - a ocupacao e consequencia dos planos aprovados, nunca reserva solta;
--   - os dados permitem tanto sucesso quanto conflito: nao deixar todos os
--     recursos indisponiveis durante todo o dia.
--
-- DATA DE REFERENCIA: 19/10/2026 (declarada tambem no README.md).
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

-- Planos aprovados na data de referencia, com as reservas que a aprovacao
-- teria gravado. Cada reserva e UMA linha por slot de 1 hora, calculada como
-- o codigo calcula (Aerovia.duracao arredonda ao minuto; Slot.entre e
-- semiaberto). SeedConsistenciaTest confere isso a cada build.
--
-- Ids a partir de 1001: o roteiro docs/api/requests.http espera que os planos
-- criados pela API recebam 1, 2, 3... e a identidade do H2 nao avanca quando o
-- id e informado.
--
-- Escolhas para nao atrapalhar o roteiro: a R1 fica livre o dia inteiro e
-- nenhum pouso e em FLN. Ocupadas: R2, R4, R5, R6, R7, R8 (6 de 8).
--
--   1001 PR-CRG POA -R2-> GRU (pouso) -R7-> CWB   01:00  carga na janela 00-06
--   1002 PR-PAX CWB -R5-> FLN (sobrevoo) -R8-> POA 06:30
--   1003 PR-PAX POA -R2-> GRU                      21:00  permanencia vira o dia
--   1004 PP-PAR FLN -R4-> CWB (pouso) -R6-> GRU    20:00
--
-- 1001 e 1002 partem de madrugada/manha: durante a apresentacao ja partiram e
-- nao podem ser cancelados. 1003 e 1004 sao a noite e podem.
--
-- Aqui NAO se usa MERGE ... KEY como acima: ele reescreveria a linha a cada
-- inicializacao e desfaria um cancelamento feito pela API. MERGE ... USING com
-- WHEN NOT MATCHED so insere o que falta.

MERGE INTO plano_de_voo t USING (VALUES
    (1001, 3, 'PR-CRG', 'POA', 'CWB', TIMESTAMP '2026-10-19 01:00:00', 'APROVADO'),
    (1002, 1, 'PR-PAX', 'CWB', 'POA', TIMESTAMP '2026-10-19 06:30:00', 'APROVADO'),
    (1003, 2, 'PR-PAX', 'POA', 'GRU', TIMESTAMP '2026-10-19 21:00:00', 'APROVADO'),
    (1004, 4, 'PP-PAR', 'FLN', 'GRU', TIMESTAMP '2026-10-19 20:00:00', 'APROVADO')
) AS s (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida, situacao)
ON t.id = s.id
WHEN NOT MATCHED THEN INSERT (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida, situacao)
    VALUES (s.id, s.piloto_id, s.aeronave_prefixo, s.origem_codigo, s.destino_codigo, s.partida, s.situacao);

-- Duracoes: PR-CRG (780 km/h) R2 67 min, R7 28 min; PR-PAX (830 km/h) R5 18,
-- R8 26, R2 63; PP-PAR (650 km/h) R4 23, R6 33.
MERGE INTO trecho t USING (VALUES
    -- 1001: R2 01:00-02:07, GRU ate 03:07; R7 03:07-03:35, CWB ate 05:05
    (1001, 1, 'R2', 30000, TIMESTAMP '2026-10-19 01:00:00', 10, 60),
    (1001, 2, 'R7', 30000, TIMESTAMP '2026-10-19 03:07:00', 5, 90),
    -- 1002: R5 06:30-06:48, sobrevoo de FLN; R8 06:48-07:14, POA ate 07:59
    (1002, 1, 'R5', 29000, TIMESTAMP '2026-10-19 06:30:00', NULL, NULL),
    (1002, 2, 'R8', 29000, TIMESTAMP '2026-10-19 06:48:00', 12, 45),
    -- 1003: R2 21:00-22:03, GRU ate 00:03 do dia 20
    (1003, 1, 'R2', 31000, TIMESTAMP '2026-10-19 21:00:00', 20, 120),
    -- 1004: R4 20:00-20:23, CWB ate 20:53; R6 20:53-21:26, GRU ate 23:26
    (1004, 1, 'R4', 26000, TIMESTAMP '2026-10-19 20:00:00', 8, 30),
    (1004, 2, 'R6', 26000, TIMESTAMP '2026-10-19 20:53:00', 21, 120)
) AS s (plano_id, ordem, aerovia_id, altitude, horario_entrada, numero_vaga, permanencia_minutos)
ON t.plano_id = s.plano_id AND t.ordem = s.ordem
WHEN NOT MATCHED THEN INSERT (plano_id, ordem, aerovia_id, altitude, horario_entrada, numero_vaga, permanencia_minutos)
    VALUES (s.plano_id, s.ordem, s.aerovia_id, s.altitude, s.horario_entrada, s.numero_vaga, s.permanencia_minutos);

-- Reserva so entra se o plano dono continua APROVADO: plano do seed cancelado
-- pela API teve as reservas liberadas, e reinserir aqui as ressuscitaria.
MERGE INTO reserva_aerovia t USING (SELECT v.* FROM (VALUES
    (1001, 'R2', 30000, TIMESTAMP '2026-10-19 01:00:00'),
    (1001, 'R2', 30000, TIMESTAMP '2026-10-19 02:00:00'),
    (1001, 'R7', 30000, TIMESTAMP '2026-10-19 03:00:00'),
    (1002, 'R5', 29000, TIMESTAMP '2026-10-19 06:00:00'),
    (1002, 'R8', 29000, TIMESTAMP '2026-10-19 06:00:00'),
    (1002, 'R8', 29000, TIMESTAMP '2026-10-19 07:00:00'),
    (1003, 'R2', 31000, TIMESTAMP '2026-10-19 21:00:00'),
    (1003, 'R2', 31000, TIMESTAMP '2026-10-19 22:00:00'),
    (1004, 'R4', 26000, TIMESTAMP '2026-10-19 20:00:00'),
    (1004, 'R6', 26000, TIMESTAMP '2026-10-19 20:00:00'),
    (1004, 'R6', 26000, TIMESTAMP '2026-10-19 21:00:00')
) AS v (plano_id, aerovia_id, altitude, slot_inicio)
JOIN plano_de_voo p ON p.id = v.plano_id AND p.situacao = 'APROVADO') s
ON t.aerovia_id = s.aerovia_id AND t.altitude = s.altitude AND t.slot_inicio = s.slot_inicio
WHEN NOT MATCHED THEN INSERT (plano_id, aerovia_id, altitude, slot_inicio)
    VALUES (s.plano_id, s.aerovia_id, s.altitude, s.slot_inicio);

MERGE INTO reserva_vaga t USING (SELECT v.* FROM (VALUES
    (1001, 'GRU', 10, TIMESTAMP '2026-10-19 02:00:00'),
    (1001, 'GRU', 10, TIMESTAMP '2026-10-19 03:00:00'),
    (1001, 'CWB',  5, TIMESTAMP '2026-10-19 03:00:00'),
    (1001, 'CWB',  5, TIMESTAMP '2026-10-19 04:00:00'),
    (1001, 'CWB',  5, TIMESTAMP '2026-10-19 05:00:00'),
    (1002, 'POA', 12, TIMESTAMP '2026-10-19 07:00:00'),
    (1003, 'GRU', 20, TIMESTAMP '2026-10-19 22:00:00'),
    (1003, 'GRU', 20, TIMESTAMP '2026-10-19 23:00:00'),
    (1003, 'GRU', 20, TIMESTAMP '2026-10-20 00:00:00'),
    (1004, 'CWB',  8, TIMESTAMP '2026-10-19 20:00:00'),
    (1004, 'GRU', 21, TIMESTAMP '2026-10-19 21:00:00'),
    (1004, 'GRU', 21, TIMESTAMP '2026-10-19 22:00:00'),
    (1004, 'GRU', 21, TIMESTAMP '2026-10-19 23:00:00')
) AS v (plano_id, aeroporto_codigo, numero_vaga, slot_inicio)
JOIN plano_de_voo p ON p.id = v.plano_id AND p.situacao = 'APROVADO') s
ON t.aeroporto_codigo = s.aeroporto_codigo AND t.numero_vaga = s.numero_vaga AND t.slot_inicio = s.slot_inicio
WHEN NOT MATCHED THEN INSERT (plano_id, aeroporto_codigo, numero_vaga, slot_inicio)
    VALUES (s.plano_id, s.aeroporto_codigo, s.numero_vaga, s.slot_inicio);
