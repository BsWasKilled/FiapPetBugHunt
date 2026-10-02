# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** ___

| Integrante | RM | Turma |
|Bernardo Silva Berwanger|565776|2CCPH|
|João Vitor Angeloti Sena|563473|2CCPH|



| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas |

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | `GeradorProtocoloTest`: `assertSame` falhou e a sequência veio 1, 1, 1 em vez de 1, 2, 3 | `GeradorProtocolo.getInstancia()` (~l.18): fazia `return new GeradorProtocolo()` e nunca guardava o resultado no campo `instancia` | `instancia = new GeradorProtocolo();` antes do `return instancia` | Padrão Singleton (Aula 14) |
| bug02 | `devePreencherOsDadosDoPetNaConsulta`: `expected: <Mimi> but was: <null>` | `ConsultaVeterinaria` (~l.17): o construtor chamava `super()` vazio e descartava todos os parâmetros (inclusive o status AGENDADO) | `super(protocolo, petNome, petPorte, tutorNome, dataHora);` | Herança e construtores (POO) |
| bug03 | `deveCriarTosaQuandoTipoForTosa`: `expected: Tosa but was: Banho` | `AtendimentoFactory` (~l.17): o `case "TOSA"` instanciava `new Banho(...)` (copiar/colar) | `case "TOSA" -> new Tosa(...)` | Padrão Factory (Aula 14), polimorfismo |
| bug04 | `deveMontarAtendimentoCompleto`: `expected: <Rex> but was: <null>` | `AtendimentoBuilder.comPet` (~l.24): `petNome = petNome;` atribuía o parâmetro a ele mesmo (`this` ausente) | `this.petNome = petNome;` | `this`, escopo de variáveis (POO) |
| bug05 | `deveRecusarMontagemSemNomeDoPet` e `...SemPorte`: nenhuma exceção foi lançada | `AtendimentoBuilder.construir` (~l.41): nada validava nome/porte; a validação "ficaria com o controller" | `construir()` lança `IllegalArgumentException` se nome ou porte forem nulos/vazios | Padrão Builder (Aula 14), validação, exceções (Aula 11) |
| bug06 | `deveRecusarAgendamentoComHorarioJaOcupado`: a `HorarioOcupadoException` não veio e o `save` foi chamado | `AgendaService.agendar` (~l.23): `==` comparando `String` e `LocalDateTime` (compara referência, não conteúdo) | `a.getPetNome().equals(...)` e `a.getDataHora().equals(...)` | `==` vs `equals()` (Aula 7) |
| bug07 | `deveLancarExcecaoQuandoAtendimentoNaoExiste`: nenhuma exceção; `concluir/cancelar` de id inexistente dariam NPE | `AgendaService.buscarPorId` (~l.37): `catch (Exception e) { return null; }` engolia a `AtendimentoNaoEncontradoException` | Removido o try/catch; o `orElseThrow` propaga a exceção | Exceções customizadas unchecked (Aula 11) |
| bug08 | (revelado pelo teste01) Banho PEQUENO custava R$ 100 e GRANDE R$ 60 | `Banho.calcularPreco` (~l.27): valores de PEQUENO e GRANDE invertidos | PEQUENO 60, MEDIO 80, GRANDE 100 | Regras de negócio no model, polimorfismo |
| bug09 | (revelado pelo teste02) `Tosa.getDuracaoMinutos()` retornava 30, não 60 | `Tosa` (~l.40): `getDuracaoMinutos(String porte)` é **sobrecarga**, não sobrescrita; a chamada sem argumento caía no padrão da superclasse | `@Override public int getDuracaoMinutos() { return 60; }` | Sobrescrita vs sobrecarga, `@Override` (Aula 7) |
| bug10 | (revelado pelo teste03) `cancelar()` aceitava atendimento CONCLUIDO e CANCELADO | `Atendimento.cancelar` (~l.63): atribuía o status sem validar o estado atual | Só cancela se AGENDADO; senão lança `StatusInvalidoException` | Encapsulamento de regras de estado (model), exceções |
| bug11 | (revelado pelo teste04) `agendar()` aceitava data/hora no passado | `AgendaService.agendar`: nenhuma validação de data | Recusa com `IllegalArgumentException` **antes** de consultar o repositório (o controller já converte para 400) | Validação de entrada, fail-fast |
| bug12 | Code review: o comentário dizia "thread-safe", mas não era | `GeradorProtocolo`: `getInstancia()` e `proximo()` sem `synchronized` — duas requisições simultâneas podiam criar duas instâncias ou gerar o mesmo protocolo | `synchronized` nos dois métodos | Singleton thread-safe (Aula 14), concorrência |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.criar(int p, String t, String n, String po, String tu, LocalDateTime d)` | Nomes significativos (parâmetros de uma letra) | `protocolo, tipo, petNome, petPorte, tutorNome, dataHora` |
| clean02 | `GeradorProtocolo` (construtor) | Sem `System.out.println` de depuração em código de produção | Linha removida |
| clean03 | `AgendaService.agendar` | `System.out.println` no lugar de logger | `Logger` do SLF4J (já vem no Spring Boot) |
| clean04 | `AtendimentoController.calcularDescontoFidelidade` | Código morto (método privado nunca usado + comentário de "futuro") | Método e comentário removidos |
| clean05 | `Atendimento`, `AgendaService` | Strings mágicas `"AGENDADO"`, `"CONCLUIDO"`, `"CANCELADO"` repetidas | Constantes `STATUS_AGENDADO/CONCLUIDO/CANCELADO` |
| clean06 | `Banho` e `Tosa` | Strings mágicas `"PEQUENO"`, `"MEDIO"` repetidas | Constantes `PORTE_PEQUENO/MEDIO/GRANDE` em `Atendimento` |

## Parte 3 — Testes novos (regras que estavam sem cobertura)

| # | Teste escrito (classe.método) | Regra coberta | Resultado ao escrever (vermelho/verde) |
|---|---|---|---|
| teste01 | `BanhoPrecoTest.deveCalcularPrecoPorPorteQuandoForBanho` | Banho: R$ 60 / 80 / 100 por porte | Vermelho → bug08 |
| teste02 | `TosaDuracaoTest.deveDurar60MinutosQuandoForTosa` | Tosa dura 60 min | Vermelho → bug09 |
| teste03 | `AgendaServiceCancelamentoTest.deveRecusarCancelamentoQuandoAtendimentoJaConcluido` | `cancelar()` de CONCLUIDO recusa e não salva | Vermelho → bug10 |
| teste04 | `AgendaServiceDataPassadoTest.deveRecusarAgendamentoQuandoDataForNoPassado` | Data no passado recusa e o banco nem é consultado | Vermelho → bug11 |
| teste05 | `ConsultaPrecoTest.deveCustar150ReaisQuandoQualquerPorte` | Consulta: R$ 150 fixo para qualquer porte | Verde de cara (regra já correta) |
| teste06 | `AgendaServiceConclusaoCanceladoTest.deveRecusarConclusaoQuandoAtendimentoCancelado` | `concluir()` de CANCELADO recusa e não salva | Verde de cara (regra já correta) |

---

## Parte 4 — Perguntas de reflexão

### 1. A suíte como contrato (Aula 15)
Das 9 falhas vermelhas, cada mensagem apontou para uma classe. `expected: <Rex> but was: <null>` no `AtendimentoBuilderTest` levou direto ao `comPet`, onde `petNome = petNome;` não tinha `this`. `expected: Tosa but was: Banho` levou ao `case "TOSA"` da factory. Duas falhas no `GeradorProtocoloTest` (instância diferente e sequência 1, 1, 1) apontaram para o mesmo bug do Singleton, e as duas do builder sem nome/porte para a falta de validação. Com curl eu teria de subir a API e o Oracle, montar a requisição e conferir o JSON de cabeça; a suíte roda em milissegundos, sem banco, e depois de cada correção mostra se alguma outra regra quebrou.

### 2. Mock e injeção de dependência (Aulas 13 a 15)
Em produção o container do Spring cria o `AtendimentoRepository` (um proxy do Spring Data ligado ao Oracle) e o injeta no campo `@Autowired` do `AgendaService`. No teste, `@Mock` cria um repositório falso e `@InjectMocks` faz o mesmo papel do Spring: coloca o falso dentro do service. O service só depende da interface `AtendimentoRepository`, então não sabe se do outro lado há Oracle ou um mock. Por isso o teste roda sem Spring e sem rede, e eu controlo o que `findByPetNome` e `findById` devolvem.

### 3. `==` vs `.equals()` (Aula 7)
Em `agendar`, `a.getPetNome() == novo.getPetNome()` e `a.getDataHora() == novo.getDataHora()` comparavam referências. Na requisição real, cada chamada cria seus próprios objetos (o `LocalDateTime` vem do parse do parâmetro), então dois valores iguais são objetos diferentes e o `==` dava `false`: o conflito nunca era detectado e o agendamento duplicado era salvo. Com literais como `"Rex"` o `==` "funciona por sorte" porque o Java reaproveita a mesma String no pool, o que esconde o problema. Troquei por `.equals()`, que compara o conteúdo.

### 4. Sobrescrita vs sobrecarga (Aula 7)
`Atendimento` tem `getDuracaoMinutos()` sem parâmetros. A `Tosa` declarava `getDuracaoMinutos(String porte)`: assinatura diferente, ou seja, sobrecarga (um método novo, não relacionado). Como o controller e o teste chamam `getDuracaoMinutos()` sem argumento, o polimorfismo usava o da superclasse e devolvia 30 em vez de 60. Compilava normalmente porque era um método válido. Com `@Override`, o compilador teria reclamado que o método não sobrescreve nenhum da superclasse. Corrigi com a assinatura sem parâmetros e `@Override`.

### 5. Singleton manual vs bean do Spring (Aula 14)
O Singleton garante uma única instância e, portanto, um único contador global de protocolos. O bug era que `getInstancia()` fazia `return new GeradorProtocolo()` sem guardar o resultado: cada chamada criava um gerador novo com contador zerado, e todo protocolo saía 1. Depois de guardar a instância, ainda faltava `synchronized`: em requisições concorrentes duas threads poderiam criar duas instâncias ou gerar o mesmo número. O `AgendaService` é um `@Service`, ou seja, um bean singleton gerenciado pelo container, que cria o objeto uma vez e o injeta onde for pedido; ninguém chama `new AgendaService()`, então não há como cair nesse erro.

### 6. Cobertura de testes: onde parar? (Aula 15)
Vale manter os testes que ficaram verdes: o `teste05` (consulta com preço fixo) e o `teste06` (concluir atendimento cancelado) documentam regras do contrato e protegem contra regressão, por exemplo se alguém reutilizar o `if/else` de porte do Banho na consulta. Com prazo curto, eu priorizaria o caminho feliz de cada regra de negócio e, em seguida, os caminhos de erro, porque foi neles que estavam os bugs: cancelar atendimento concluído, data no passado, id inexistente. Perseguir 100% de cobertura rende pouco; getters e setters triviais não valem o tempo.

---


```
