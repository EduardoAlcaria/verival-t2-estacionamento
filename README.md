# Trabalho 2: Cobertura de Codigo e Teste de Mutacao da Calculadora de Tarifa

Verificacao e Validacao de Software, PUCRS.

**Integrantes:** Eduardo Alcaria, Arthur Pimentel, Ethan Soares

Este repositorio parte do codigo do Trabalho 1 (calculadora de tarifa de
estacionamento) e aplica JaCoCo (cobertura de codigo) e PIT (teste de mutacao)
para avaliar e complementar a suite de testes existente.

## O que mudou em relacao ao Trabalho 1

| Arquivo | Mudanca |
|---|---|
| `pom.xml` | adicionados `jacoco-maven-plugin` 0.8.12 e `pitest-maven` 1.17.4 (com `pitest-junit5-plugin` 1.2.1) |
| `src/test/.../CalculadoraTarifaComplementarTest.java` | novo: 27 execucoes de teste (19 validas e 8 invalidas) |
| `relatorio_t2.pdf` | novo: relatorio do Trabalho 2 |
| `docs/relatorio_t2.tex` | novo: fonte latex do relatorio |
| `README.md` | reescrito para o Trabalho 2 |

Nao foram alterados: `CalculadoraTarifa.java` (codigo de producao) e
`CalculadoraTarifaTest.java` (testes do Trabalho 1). Os documentos
`docs/casos_de_teste.*` e `docs/defeitos_encontrados.*` continuam sendo do
Trabalho 1.

## Resultados

### JaCoCo

| Metrica | Perdidos | Cobertos | Cobertura |
|---|---|---|---|
| Instrucoes | 0 | 160 | 100% |
| Branches | 0 | 20 | 100% |
| Linhas | 0 | 38 | 100% |
| Metodos | 0 | 9 | 100% |

A suite do Trabalho 1 ja cobria 100%, antes e depois dos testes novos. Nao havia
lacuna de cobertura a fechar.

### PIT

| Grupo de mutadores | Mutantes | Mortos | Timeout | Sobreviventes |
|---|---|---|---|---|
| `DEFAULTS` | 32 | 30 | 0 | 2 |
| `STRONGER` | 42 | 39 | 1 | 2 |
| `ALL` | 94 | 88 | 4 | 2 |

Mutation score com `DEFAULTS`: 93,75%. Os resultados sao os mesmos antes e depois
dos testes novos.

Os 2 sobreviventes estao na linha 53 de `calcularPorPermanencia`
(`minutos <= MINUTOS_TARIFA_FIXA`) e sao **equivalentes**: com `<` ou com a
condicao removida, o resultado continua `15 + 0 * 5 = 15`. Nenhum teste mata
esses mutantes sem alterar o codigo de producao. Ajustado, o score e 100%. A
prova esta em `relatorio_t2.pdf`.

## Testes novos

Os casos de `CalculadoraTarifaComplementarTest` reforcam regioes onde a suite do
Trabalho 1 era rasa, usando classes de equivalencia e valor limite:

- fronteiras do cliente VIP (60, 61 e 121 minutos, cortesia, multiplas diarias);
- tres pernoites, virada de mes, virada de ano e ano bissexto;
- saida as 01:59 dois dias depois (uma pernoite);
- janela de saida proibida nos pontos 02:01 e 07:58;
- permanencia de 180 e 181 minutos e teto de 23:59 no mesmo dia;
- entradas e saidas invalidas combinadas com VIP.

Como a cobertura ja era total e os unicos mutantes sobreviventes sao
equivalentes, esses testes nao alteram as metricas. Eles ampliam a verificacao do
comportamento.

## Regras de negocio

- Entrada permitida das 08:00 as 23:59.
- Saida proibida das 02:00 as 07:59.
- 20 minutos de cortesia, sem cobranca.
- Ate 1 hora de permanencia: tarifa fixa de R$ 15,00.
- Acima de 1 hora, sem pernoite: incremento de R$ 5,00 a cada hora iniciada.
- Saida a partir das 08:00 do dia seguinte a entrada: pernoite, R$ 50,00 por
  diaria.
- Cliente VIP: 50% de desconto sobre o valor final.

## Estrutura

```
pom.xml
relatorio_t2.pdf
src/main/java/br/pucrs/verival/estacionamento/CalculadoraTarifa.java
src/test/java/br/pucrs/verival/estacionamento/CalculadoraTarifaTest.java
src/test/java/br/pucrs/verival/estacionamento/CalculadoraTarifaComplementarTest.java
docs/relatorio_t2.tex          fonte latex do relatorio do Trabalho 2
docs/casos_de_teste.*          Trabalho 1
docs/defeitos_encontrados.*    Trabalho 1
```

## Requisitos

- Java 17
- Maven

## Como rodar

Testes e relatorio JaCoCo (`target/site/jacoco/index.html`):

```bash
mvn test
```

Teste de mutacao com PIT (`target/pit-reports/index.html`):

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
mvn test-compile org.pitest:pitest-maven:mutationCoverage -Dmutators=ALL
```
