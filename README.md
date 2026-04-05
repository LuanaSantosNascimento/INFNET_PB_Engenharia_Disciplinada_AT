# CRUD - TP5

Projeto de exemplo CRUD utilizando Spring Boot, com foco em testes unitários e de integração, e geração de relatório de cobertura de testes com Jacoco.
Foi utilizado o CRUD feito no TP4 do projeto de bloco (com banco de dados em memória H2), adicionado uma interface 
web estática utilizando Thymeleaf para facilitar os testes com selenium.

## Pré-requisitos

*   Java 17 ou superior
*   Maven 3.6 ou superior

## Como rodar a aplicação

Para iniciar a aplicação, execute o seguinte comando na raiz do projeto:

```bash
mvn spring-boot:run
```
Acesse o Swagger para visualizar a documentação da API e testar os endpoints em:
[http://localhost:8080/banco-api/swagger-ui/index.html#/](http://localhost:8080/swagger-ui.html)

## Endpoints

URL: `http://localhost:8080/banco-api/contas`

| Método | Endpoint       | Descrição                                                 |
| :--- |:---------------|:----------------------------------------------------------|
| **GET** | `/`            | Lista todas as contas cadastradas.                        |
| **GET** | `/{id}`        | Retorna os detalhes de uma conta específica.              |
| **POST** | `/`            | Cria uma nova conta bancária.                             |
| **PUT** | `/{id}`        | Atualiza dados (nome/saldo) de uma conta existente.       |
| **PUT** | `/{id}/status` | Atualiza status (ATIVA/BLOQUEADA) de uma conta existente. |
| **DELETE** | `/{id}`        | Remove uma conta (apenas se o saldo for zero).            |

## Como rodar os testes

Para executar os testes unitários e de integração, utilize o comando:

```bash
mvn test
```

## Relatório de Cobertura de Testes

Após a execução dos testes, o relatório de cobertura de testes gerado pelo Jacoco estará disponível em:
`target/site/jacoco/index.html`

```bash
mvn jacoco:report
```

## Relatório de Cobertura de Testes
Validação do estilo de código utilizando Checkstyle. O relatório estará disponível em:
`target/site/checkstyle.html`

```bash
mvn checkstyle:checkstyle
```

## Evidências dos Testes com Selenium

As capturas de tela dos testes com Selenium são salvas no decorrer dos testes no diretório:

`src/test/java/org/banco/selenium/screenshots/`
