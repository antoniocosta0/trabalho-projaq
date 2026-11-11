-- Malha inicial (§2 da parte 1). WHEN NOT MATCHED só insere o que falta: reiniciar não duplica
-- nem sobrescreve o que foi cadastrado pela API.

MERGE INTO aeroporto t USING (VALUES
    ('POA', 'Aeroporto Internacional Salgado Filho - Porto Alegre'),
    ('FLN', 'Aeroporto Internacional de Florianópolis/Hercílio Luz'),
    ('CWB', 'Aeroporto Internacional Afonso Pena - Curitiba'),
    ('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos')
) AS s (codigo, nome)
ON t.codigo = s.codigo
WHEN NOT MATCHED THEN INSERT (codigo, nome) VALUES (s.codigo, s.nome);

MERGE INTO vaga t USING (
    SELECT a.codigo, r.X FROM (VALUES ('POA', 41), ('FLN', 16), ('CWB', 26), ('GRU', 115)) AS a (codigo, quantidade)
    JOIN SYSTEM_RANGE(1, 115) r ON r.X <= a.quantidade
) AS s (aeroporto_codigo, numero)
ON t.aeroporto_codigo = s.aeroporto_codigo AND t.numero = s.numero
WHEN NOT MATCHED THEN INSERT (aeroporto_codigo, numero) VALUES (s.aeroporto_codigo, s.numero);

MERGE INTO aerovia t USING (VALUES
    ('R1', 'POA', 'FLN', 363),
    ('R2', 'POA', 'GRU', 865),
    ('R3', 'GRU', 'POA', 865),
    ('R4', 'FLN', 'CWB', 245),
    ('R5', 'CWB', 'FLN', 245),
    ('R6', 'CWB', 'GRU', 360),
    ('R7', 'GRU', 'CWB', 360),
    ('R8', 'FLN', 'POA', 363)
) AS s (id, origem_codigo, destino_codigo, distancia_km)
ON t.id = s.id
WHEN NOT MATCHED THEN INSERT (id, origem_codigo, destino_codigo, distancia_km)
    VALUES (s.id, s.origem_codigo, s.destino_codigo, s.distancia_km);
