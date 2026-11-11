# ADR 0010 — Malha aérea atrás de uma porta, com cópia da aerovia no trecho

**Decisão.** A gestão dos planos lê aeroportos e aerovias só por `MalhaAereaPort` (buscar
aeroporto, buscar aerovia). Na etapa 1 ela é implementada localmente (`MalhaAereaLocalAdapter`);
na etapa 3 passa a ser HTTP. O plano guarda os códigos dos aeroportos, e cada trecho guarda uma
cópia da aerovia (id, origem, destino, distância). As chaves estrangeiras para `aeroporto` e
`aerovia` saíram das tabelas de plano, trecho e reservas.

**Alternativas.**
- Guardar só o id da aerovia e consultar o cadastro sempre que um plano for lido: cada consulta
  de plano viraria várias chamadas HTTP, e um plano antigo deixaria de abrir com o cadastro fora.
- Manter um segundo cadastro de aeroportos e aerovias na gestão: proibido pelo §3 da parte 2.

**Justificativa.** Aerovias não mudam depois de cadastradas (§2 da parte 1), então a cópia nunca
fica desatualizada, e o §3 da parte 2 permite conservar os dados históricos dos planos. A
existência da vaga continua sendo perguntada ao cadastro na validação e na aprovação.
