# ADR 0004 — Atomicidade da aprovação de um plano

**Data:** 2026-10-18
**Situação:** aceita

## Contexto

O enunciado é literal sobre a aprovação, em dois lugares. No §5.4: "as reservas
são efetivadas somente na aprovação, que deverá ser integral: ou todos os
recursos são reservados e o plano é aprovado, ou nenhuma reserva da tentativa
permanece e o plano continua em edição". E no §6.5: "a aprovação deverá
verificar novamente as condições necessárias ou garantir que continuem válidas
no momento das reservas".

Uma aprovação escreve em três lugares: a situação do plano, uma linha de
`reserva_aerovia` por slot de cada trecho e uma linha de `reserva_vaga` por slot
de permanência de cada pouso. Um voo de três trechos com dois pousos passa de
dez gravações. Daí vêm dois riscos distintos, que a decisão precisa cobrir ao
mesmo tempo:

1. **Falha no meio.** Se a quarta reserva falhar, as três primeiras e a situação
   `APROVADO` não podem ficar gravadas. É exatamente a "reserva parcial" que o
   §5.4 proíbe — e o enunciado cobra o teste disso por escrito.
2. **Duas aprovações simultâneas.** A consulta de disponibilidade (UC07 e UC08)
   responde sobre o passado: entre consultar e gravar, outro plano pode ter
   reservado a mesma altitude no mesmo slot. Reconferir antes de gravar diminui
   a janela, mas não a fecha — duas requisições podem passar juntas pela
   conferência e só se encontrar na gravação.

A ADR 0001 (hexagonal) e a ADR 0002 (domínio separado da persistência) impõem a
restrição que elimina a solução óbvia: `domain/` não pode importar Spring.

## Alternativas consideradas

1. **`@Transactional` no caso de uso.** Vantagem: uma anotação resolve o risco 1
   e é o que qualquer tutorial de Spring faria. Desvantagem: põe
   `org.springframework.transaction` dentro de `domain/ports/in/usecases`,
   quebrando a regra de dependência verificável por `grep` que sustenta as ADRs
   0001 e 0002, e amarra o caso de uso ao proxy do Spring — sem contexto
   carregado a anotação é inerte, então o teste unitário passaria a testar um
   caso de uso sem transação nenhuma. Não foi escolhida.

2. **Conferir a disponibilidade antes e gravar sem transação.** Vantagem:
   nenhuma infraestrutura nova. Desvantagem: não cobre nenhum dos dois riscos —
   uma falha no meio deixa reserva parcial, e a janela entre conferir e gravar
   continua aberta para a aprovação simultânea. Não foi escolhida: contraria o
   §5.4 diretamente.

3. **Lock otimista, com coluna de versão.** Vantagem: detecta a escrita
   concorrente sem bloquear. Desvantagem: o recurso disputado não é uma entidade
   com linha própria — o que se disputa é o par altitude+slot, que só existe como
   linha *depois* de reservado. Versionar a aerovia ou o aeroporto inteiro seria
   serializar aprovações que não conflitam (duas altitudes diferentes da mesma
   aerovia, dois aeroportos distintos), e ainda exigiria coluna nova e
   tratamento de `OptimisticLockException`. Não foi escolhida: custa mais e
   protege com granularidade pior do que a alternativa adotada.

4. **Lock pessimista (`SELECT ... FOR UPDATE`) sobre os slots disputados.**
   Desvantagem: quando o slot está livre não existe linha para travar, que é
   justamente o caso de interesse. Travar a tabela inteira resolveria, ao custo
   de serializar toda aprovação do sistema. Não foi escolhida.

## Decisão

A atomicidade é garantida por **duas linhas de defesa**, e o acesso à transação
entra no domínio como **porta de saída**:

- `Transacao` é uma interface em `domain/ports/out`, com um único método
  `<T> T executar(Supplier<T> bloco)`. O `AprovarPlanoUC` roda **toda** a
  aprovação dentro desse bloco: buscar o plano, reconferir os conflitos
  (`VerificadorDeConflitos`), mudar a situação e gravar as reservas. O adaptador
  `TransacaoSpring` a implementa com `TransactionTemplate`, por programação —
  exceção dentro do bloco significa rollback de tudo.
- As restrições `UNIQUE (aerovia_id, altitude, slot_inicio)` e
  `UNIQUE (aeroporto_codigo, numero_vaga, slot_inicio)` são a segunda linha.
  Se duas aprovações passarem juntas pela reconferência, o banco derruba a
  segunda no momento de inserir. O `RepositorioReservasJPA` traduz a
  `DataIntegrityViolationException` em `ConflitoDeReservaException` do domínio,
  que a API devolve como **409**, e a transação desfaz a situação e as reservas
  já gravadas na tentativa.

A reconferência vem **antes** por um motivo de qualidade de resposta, não de
integridade: ela devolve **422 com a lista completa** dos problemas, enquanto o
`UNIQUE` só informa o primeiro conflito encontrado.

## Consequências

**Facilita.** O caso de uso continua Java puro e testável sem Spring: os testes
injetam `TransacaoDireta`, que apenas executa o bloco. O rollback de verdade é
testado com H2 em `AprovacaoAtomicaTest`, que é o teste que o enunciado cobra
por escrito — e foi conferido que ele **falha** quando a porta `Transacao` é
removida do caso de uso, isto é, ele testa mesmo o que promete.

**Custa.** A transação é aberta por programação, não por anotação: quem escrever
um caso de uso novo com várias gravações precisa lembrar de passar pela porta,
porque não há anotação declarativa para esquecer de pôr e o esquecimento é
silencioso. O cancelamento (UC12) segue a mesma regra.

**Passa a ser obrigatório.**

- Toda operação com mais de uma gravação roda dentro de `Transacao.executar` —
  o template de PR cobra isso explicitamente.
- Os dois `UNIQUE` são parte da garantia, não otimização: removê-los reabre a
  janela da aprovação simultânea. O schema não pode perdê-los numa alteração
  futura.
- A granularidade da reserva é o slot (ADR 0005). Mudar essa granularidade
  invalida os dois `UNIQUE` e, com eles, esta decisão.
- Um caso de uso novo que grave reservas precisa de teste equivalente ao
  `AprovacaoAtomicaTest`, com banco de verdade: o fake de transação dos testes
  de domínio, por construção, não desfaz nada.
