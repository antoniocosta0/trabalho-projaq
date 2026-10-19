# ADR 0009 — Cadastros fora da lista de casos de uso

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

O §8.1 do enunciado lista, entre o que a solução precisa permitir, "consultar a
rede aérea e cadastrar novos aeroportos e aerovias" e "consultar pilotos e
aeronaves e cadastrar os dados necessários". O §2 reforça: um aeroporto novo,
informado durante a avaliação, tem que entrar no sistema **sem alteração de
código**.

Ao mesmo tempo, o §8.2 manda a malha aérea, os pilotos e as aeronaves virem
**prontos na carga inicial**, e a orientação do professor em aula foi explícita:
esses cadastros **não são casos de uso** do trabalho — o domínio do problema é o
plano de voo, não a manutenção de tabelas de apoio.

A primeira modelagem da equipe tinha ido para o outro lado: cadastrar aeroporto,
aerovia, piloto e aeronave eram quatro casos de uso numerados, dentro de uma
lista de dezenove, decidida em 15/10. Com a orientação do
professor, essa decisão precisou ser revista — e a revisão não é cosmética,
porque muda o que é entregue como caso de uso, o que entra nos diagramas e o que
a apresentação precisa demonstrar.

A tensão a resolver: tirar os cadastros da lista de casos de uso sem tirá-los da
API, já que o enunciado os exige.

## Alternativas consideradas

1. **Manter os quatro cadastros como casos de uso numerados.** Vantagem: a
   numeração já estava feita, nos diagramas e nos guias. Desvantagem: contraria
   a orientação do professor e infla a lista com operações CRUD que não têm
   regra de negócio nenhuma — cadastrar piloto é gravar um nome. Numerá-los ao
   lado de "validar plano" e "aprovar plano" dá a eles um peso que não têm. Não
   foi escolhida.

2. **Tirar os cadastros também da API, deixando só a carga inicial.** Vantagem:
   menos código, e a carga inicial já resolve a demonstração do dia.
   Desvantagem: contraria o §8.1 letra por letra e torna impossível o teste de
   extensibilidade do §2 — um aeroporto novo só entraria editando o `data.sql` e
   reiniciando, isto é, alterando o repositório. Não foi escolhida.

3. **Tratar os cadastros como um caso de uso genérico único** ("manter dados de
   apoio"). Desvantagem: agrupa quatro recursos com validações distintas sob um
   nome que não existe no enunciado, e piora a rastreabilidade entre o §8.1 e a
   API. Não foi escolhida.

## Decisão

Os cadastros de aeroporto, aerovia, piloto e aeronave **não entram na lista de
casos de uso** — que fica em UC01 a UC14, só com o que tem regra de domínio — e
**permanecem na API REST**, como `POST` nos recursos correspondentes, para
atender o §8.1 e o teste de extensibilidade do §2.

Ficam documentados em seção própria de `docs/dominio/casos-de-uso.md`,
explicitamente rotulada como fora da lista, com as regras de cada um: código ou
prefixo inédito, origem e destino já cadastrados no caso da aerovia, e os dados
próprios da categoria no caso da aeronave. Aerovia não tem alteração nem
exclusão depois de criada.

## Consequências

**Facilita.** A lista de casos de uso passa a conter só o que tem
comportamento — criar, editar percurso, validar, aprovar, cancelar, abandonar e
as consultas — o que é o que a apresentação precisa demonstrar e o que os
diagramas precisam sustentar. A renumeração para UC01–UC14 veio desta decisão.

**Custa.** A API fica com endpoints que não correspondem a nenhum caso de uso
numerado, o que exige a seção explicativa em `casos-de-uso.md`: sem ela, parece
omissão de documentação. O §8.1 continua cobrando esses endpoints, então eles
são trabalho obrigatório mesmo não sendo caso de uso — e, por não terem regra de
negócio, são também o ponto onde é mais tentador improvisar e furar o padrão da
arquitetura.

**Passa a ser obrigatório.**

- Os quatro `POST` seguem a mesma arquitetura do resto: porta em
  `domain/ports/in`, DTO de entrada e de saída, controller sem regra. Não são
  exceção por serem CRUD.
- O cadastro de aeronave instancia a subclasse pela categoria em um único ponto
  (`FabricaDeAeronaves`, no domínio), sem `instanceof` nem `switch` espalhado —
  é a mesma restrição
  da ADR 0006, e é o que faz o §2 valer para categorias novas.
- Um aeroporto cadastrado pela API precisa poder ser usado num plano na mesma
  execução, sem reiniciar a aplicação: é esse o teste do §2.
- A numeração UC01–UC14 é a oficial. Qualquer artefato que ainda cite UC01–UC19
  (guias antigos, anotações de 15/10) está defasado e perde para esta decisão.
