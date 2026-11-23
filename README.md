# API de Empréstimo de Equipamentos

API REST para controlar o empréstimo de equipamentos (notebooks, monitores, headsets...) para pessoas colaboradoras de uma empresa: quem está com o quê, até quando, e o que está atrasado.

Projeto 2 do curso ELAS TECH, feito com Spring Boot.

**API no ar (Render):** https://emprestimo-equipamentos.onrender.com
**Front-end:** https://emprestimo-equipamentos.onrender.com/

> O plano gratuito do Render "dorme" após 15 minutos sem uso: a primeira requisição pode levar até 1 minuto. O banco H2 do Render é reiniciado a cada novo deploy.

## Tecnologias

- Java 25
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- Banco H2 (em arquivo)
- JUnit 5
- Docker + Render (deploy)
- HTML, CSS e JavaScript (front-end)

## Como rodar localmente

Pré-requisito: **Java 25** instalado.

```bash
git clone https://github.com/natasha-mendonca/emprestimo-equipamentos.git
cd emprestimo-equipamentos
./mvnw spring-boot:run
```

Ou abra o projeto no IntelliJ e rode a classe `EmprestimoApplication`.

- API: `http://localhost:8080`
- Front-end: `http://localhost:8080/`
- Console do H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./dados/emprestimos`)

### Testes

```bash
./mvnw test
```

Ou, no IntelliJ: botão direito em `src/test/java` → **Run 'All Tests'**.

## Rotas

### Equipamentos

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/equipamentos` | Cadastra um equipamento | 201 |
| GET | `/equipamentos` | Lista todos (filtros opcionais: `?tipo=NOTEBOOK` e/ou `?disponivel=true`) | 200 |
| GET | `/equipamentos/{id}` | Busca um equipamento | 200 |
| PUT | `/equipamentos/{id}` | Atualiza nome e tipo | 200 |
| DELETE | `/equipamentos/{id}` | Remove um equipamento | 204 |

Exemplo de corpo (POST/PUT):

```json
{ "patrimonio": "NB-001", "nome": "Notebook Dell", "tipo": "NOTEBOOK" }
```

Tipos aceitos: `NOTEBOOK`, `MONITOR`, `MOUSE`, `TECLADO`, `HEADSET`, `OUTRO`.

### Colaboradores

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/colaboradores` | Cadastra uma pessoa colaboradora | 201 |
| GET | `/colaboradores` | Lista todas | 200 |
| GET | `/colaboradores/{id}` | Busca uma pessoa | 200 |
| GET | `/colaboradores/{id}/emprestimos` | Histórico de empréstimos da pessoa | 200 |

Exemplo de corpo:

```json
{ "nome": "Ana Souza", "email": "ana@empresa.com", "cargo": "Analista" }
```

### Empréstimos

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/emprestimos?equipamentoId=1&colaboradorId=1` | Empresta (os ids vão na URL, sem corpo) | 201 |
| GET | `/emprestimos` | Lista todos (com `?atrasados=true`, só os atrasados) | 200 |
| GET | `/emprestimos/{id}` | Busca um empréstimo | 200 |
| PUT | `/emprestimos/{id}/devolucao` | Registra a devolução | 200 |

A resposta de um empréstimo inclui o campo calculado `"atrasado": true/false`.

## Regras de negócio

| Regra | Descrição | Onde está |
|---|---|---|
| R1 | Não existem dois equipamentos com o mesmo patrimônio | `EquipamentoService` |
| R2 | Todo equipamento novo nasce disponível | `EquipamentoService` |
| R3 | Não existem duas pessoas com o mesmo e-mail | `ColaboradorService` |
| R4 | Só se empresta equipamento disponível | `Equipamento.emprestar()` |
| R5 | Ao emprestar: equipamento indisponível, empréstimo hoje, devolução prevista em 14 dias | Construtor do `Emprestimo` + `EmprestimoService` |
| R6 | Um empréstimo já devolvido não pode ser devolvido de novo | `Emprestimo.devolver()` |
| R7 | Ao devolver: data de devolução hoje e equipamento disponível de novo | `Emprestimo.devolver()` + `Equipamento.devolver()` + `EmprestimoService` |
| R8 | Atrasado = não devolvido e com a data prevista já vencida (calculado, não é coluna) | `Emprestimo.estaAtrasadoEm()` + `EmprestimoRepository` |
| R9 | Equipamento que já teve empréstimo não pode ser removido | `EquipamentoService` |
| R10 | Quem tem empréstimo atrasado não pode pegar outro equipamento | `EmprestimoService` |

As operações de emprestar e devolver usam `@Transactional`: o empréstimo e o equipamento são salvos juntos, ou nenhum dos dois.

## Respostas de erro

| Situação | Status |
|---|---|
| Equipamento, colaborador ou empréstimo inexistente | 404 |
| R1, R3, R9 e R10 | 409 |
| R4 e R6 | 500 (com a mensagem da regra) |

## Bônus e extras

- **Front-end** em HTML, CSS e JavaScript, servido pelo próprio Spring Boot (`src/main/resources/static/index.html`)
- **Tratamento parcial de erros:** "não encontrado" responde 404 (`NaoEncontradoException` + `@RestControllerAdvice`) e as regras R1, R3, R9 e R10 respondem 409

## Estrutura do projeto

```
src/main/java/com/example/
├── EmprestimoApplication.java
├── emprestimo/
│   ├── controller/   (rotas HTTP)
│   ├── service/      (regras que consultam o banco)
│   ├── repository/   (acesso ao banco)
│   └── model/        (entidades e regras de cada objeto)
└── exception/        (exceção e tratador de erros)

src/test/java/com/example/emprestimo/model/
├── EquipamentoTest.java   (3 testes)
└── EmprestimoTest.java    (7 testes)
```

## Vídeo

[Assistir ao vídeo](https://drive.google.com/file/d/1lC1Nvrb_gxXLyP2RXDmFf95v6RAEitcc/view?usp=sharing)

## Autora

Natasha Mendonça
