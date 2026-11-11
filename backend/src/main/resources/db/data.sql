-- Carga inicial (§8.2). Data de referência: 19/10/2026. A malha aérea fica no serviço de cadastro.
-- MERGE evita duplicar os dados quando a aplicação reinicia com o banco preenchido.

MERGE INTO piloto (id, nome) KEY (id) VALUES
    (1, 'Ana Souza'),
    (2, 'Bruno Lima'),
    (3, 'Carla Mendes'),
    (4, 'Diego Rocha');

-- PP-CUR: 85% de 1000 km = 850 km, menos que os 865 km da R2 (autonomia insuficiente).
MERGE INTO aeronave (prefixo, categoria, velocidade_cruzeiro_kmh, autonomia_km,
                     empresa_manutencao, companhia_aerea, capacidade_passageiros, capacidade_toneladas)
    KEY (prefixo) VALUES
    ('PP-PAR', 'PARTICULAR',  650, 2800, 'Manutenção Aérea Sul', NULL, NULL, NULL),
    ('PP-CUR', 'PARTICULAR',  500, 1000, 'Hangar POA',           NULL, NULL, NULL),
    ('PR-PAX', 'PASSAGEIROS', 830, 5000, NULL, 'Linhas Aéreas do Sul', 180, NULL),
    ('PR-CRG', 'CARGA',       780, 4500, NULL, 'Sul Cargas',          NULL, 20);

-- Planos aprovados e as reservas que a aprovação gravaria (uma linha por slot).
-- Ocupam R2, R4, R5, R6, R7 e R8 em 19/10; a R1 fica livre para a demonstração.
-- 1005 partiu em 17/10 e serve para mostrar o cancelamento recusado.
-- Ids a partir de 1001 para os planos criados pela API começarem em 1.
-- WHEN NOT MATCHED só insere o que falta, sem desfazer cancelamentos feitos pela API.

MERGE INTO plano_de_voo t USING (VALUES
    (1001, 'carla', 3, 'PR-CRG', 'POA', 'CWB', TIMESTAMP '2026-10-19 01:00:00', 'APROVADO'),
    (1002, 'ana', 1, 'PR-PAX', 'CWB', 'POA', TIMESTAMP '2026-10-19 06:30:00', 'APROVADO'),
    (1003, 'bruno', 2, 'PR-PAX', 'POA', 'GRU', TIMESTAMP '2026-10-19 21:00:00', 'APROVADO'),
    (1004, 'diego', 4, 'PP-PAR', 'FLN', 'GRU', TIMESTAMP '2026-10-19 20:00:00', 'APROVADO'),
    (1005, 'bruno', 2, 'PR-PAX', 'POA', 'FLN', TIMESTAMP '2026-10-17 08:00:00', 'APROVADO')
) AS s (id, criador, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida, situacao)
ON t.id = s.id
WHEN NOT MATCHED THEN INSERT (id, criador, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida, situacao)
    VALUES (s.id, s.criador, s.piloto_id, s.aeronave_prefixo, s.origem_codigo, s.destino_codigo, s.partida, s.situacao);

MERGE INTO trecho t USING (VALUES
    -- 1001: R2 01:00-02:07, GRU ate 03:07; R7 03:07-03:35, CWB ate 05:05
    (1001, 1, 'R2', 'POA', 'GRU', 865, 30000, TIMESTAMP '2026-10-19 01:00:00', 10, 60),
    (1001, 2, 'R7', 'GRU', 'CWB', 360, 30000, TIMESTAMP '2026-10-19 03:07:00', 5, 90),
    -- 1002: R5 06:30-06:48, sobrevoo de FLN; R8 06:48-07:14, POA ate 07:59
    (1002, 1, 'R5', 'CWB', 'FLN', 245, 29000, TIMESTAMP '2026-10-19 06:30:00', NULL, NULL),
    (1002, 2, 'R8', 'FLN', 'POA', 363, 29000, TIMESTAMP '2026-10-19 06:48:00', 12, 45),
    -- 1003: R2 21:00-22:03, GRU ate 00:03 do dia 20
    (1003, 1, 'R2', 'POA', 'GRU', 865, 31000, TIMESTAMP '2026-10-19 21:00:00', 20, 120),
    -- 1004: R4 20:00-20:23, CWB ate 20:53; R6 20:53-21:26, GRU ate 23:26
    (1004, 1, 'R4', 'FLN', 'CWB', 245, 26000, TIMESTAMP '2026-10-19 20:00:00', 8, 30),
    (1004, 2, 'R6', 'CWB', 'GRU', 360, 26000, TIMESTAMP '2026-10-19 20:53:00', 21, 120),
    -- 1005: R1 08:00-08:26 em 17/10, FLN ate 09:26
    (1005, 1, 'R1', 'POA', 'FLN', 363, 30000, TIMESTAMP '2026-10-17 08:00:00', 3, 60)
) AS s (plano_id, ordem, aerovia_id, origem_codigo, destino_codigo, distancia_km, altitude, horario_entrada, numero_vaga, permanencia_minutos)
ON t.plano_id = s.plano_id AND t.ordem = s.ordem
WHEN NOT MATCHED THEN INSERT (plano_id, ordem, aerovia_id, origem_codigo, destino_codigo, distancia_km, altitude, horario_entrada, numero_vaga, permanencia_minutos)
    VALUES (s.plano_id, s.ordem, s.aerovia_id, s.origem_codigo, s.destino_codigo, s.distancia_km, s.altitude, s.horario_entrada, s.numero_vaga, s.permanencia_minutos);

-- Só insere reserva de plano ainda APROVADO, para não recriar reserva já liberada.
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
    (1004, 'R6', 26000, TIMESTAMP '2026-10-19 21:00:00'),
    (1005, 'R1', 30000, TIMESTAMP '2026-10-17 08:00:00')
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
    (1004, 'GRU', 21, TIMESTAMP '2026-10-19 23:00:00'),
    (1005, 'FLN',  3, TIMESTAMP '2026-10-17 08:00:00'),
    (1005, 'FLN',  3, TIMESTAMP '2026-10-17 09:00:00')
) AS v (plano_id, aeroporto_codigo, numero_vaga, slot_inicio)
JOIN plano_de_voo p ON p.id = v.plano_id AND p.situacao = 'APROVADO') s
ON t.aeroporto_codigo = s.aeroporto_codigo AND t.numero_vaga = s.numero_vaga AND t.slot_inicio = s.slot_inicio
WHEN NOT MATCHED THEN INSERT (plano_id, aeroporto_codigo, numero_vaga, slot_inicio)
    VALUES (s.plano_id, s.aeroporto_codigo, s.numero_vaga, s.slot_inicio);

-- Os planos aprovados da carga também chegam ao registro pela fila; o evento_id fixo evita duplicatas.
MERGE INTO outbox_evento (evento_id, plano_id, aprovado_em) KEY (evento_id) VALUES
    ('00000000-0000-0000-0000-000000001001', 1001, TIMESTAMP '2026-10-16 12:00:00'),
    ('00000000-0000-0000-0000-000000001002', 1002, TIMESTAMP '2026-10-16 12:00:00'),
    ('00000000-0000-0000-0000-000000001003', 1003, TIMESTAMP '2026-10-16 12:00:00'),
    ('00000000-0000-0000-0000-000000001004', 1004, TIMESTAMP '2026-10-16 12:00:00'),
    ('00000000-0000-0000-0000-000000001005', 1005, TIMESTAMP '2026-10-16 12:00:00');
