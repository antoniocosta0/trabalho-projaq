# Modelo de domínio

Entrega de **05/10**: diagrama de domínio e casos de uso identificados.

| Arquivo | O que é |
|---|---|
| `modelo-dominio.puml` | fonte do diagrama, versionado |
| `modelo-dominio.png` | imagem exportada, com a data |
| `casos-de-uso.md` | UC01 a UC14, com ator, pré-condição e resultado; cadastros fora da lista |

As decisões de modelagem que o diagrama embute estão nas ADRs: `Slot` como
value object (0005), regra de operação polimórfica (0006), herança de aeronave
em tabela única (0007) e as interpretações de borda (0010).

**Regra:** se o código mudar o domínio, o diagrama muda no mesmo PR. Diagrama
divergente da implementação é critério de avaliação explícito do enunciado.
