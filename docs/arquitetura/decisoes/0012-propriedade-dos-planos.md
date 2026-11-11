# ADR 0012 — Propriedade dos planos pelo cabeçalho X-Usuario

**Decisão.** Toda rota `/planos` exige o cabeçalho `X-Usuario`. O plano guarda quem o criou;
só o criador consulta, altera, valida, aprova, abandona ou cancela (403 para os demais) e a
listagem traz só os planos do usuário. Sem o cabeçalho a resposta é 401.

**Alternativas.**
- Spring Security com usuários e senhas: mais seguro, mas exige um cadastro de usuários que o
  enunciado não pede (§ etapa 2: não é exigido provedor de identidade).
- Sem controle: o enunciado da parte 2 cobra propriedade e acesso aos planos e o criador na
  mensagem de aprovação.

**Justificativa.** É o mínimo que atende ao §2.1 e à mensagem de aprovação. O gateway repassa o
cabeçalho sem alteração, então a regra continua igual quando o acesso passar por ele.
