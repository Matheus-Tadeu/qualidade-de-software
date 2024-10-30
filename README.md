# Projeto de Qualidade de Software

Este é um projeto de exemplo para demonstrar práticas de qualidade de software utilizando diversas ferramentas e frameworks.

## Tecnologias Utilizadas

- **Java**
- **SQL**
- **Maven**
- **Spring Boot**
- **JUnit 5**
- **AssertJ**
- **Rest Assured**
- **Allure**
- **Cucumber**
- **Gatling**
- **Docker**

## Estrutura do Projeto

### Dependências

As principais dependências utilizadas no projeto estão listadas no arquivo `pom.xml`. Algumas das principais são:

- **Spring Boot Starter Test**
- **JUnit Jupiter (API e Engine)**
- **AssertJ**
- **Rest Assured**
- **Allure**
- **Cucumber**
- **Gatling**
- **H2 Database**
- **PostgreSQL**

### Perfis de Teste

O projeto está configurado com diferentes perfis de teste no `pom.xml`:

- **unit-test**: Executa testes unitários.
- **integration-test**: Executa testes de integração.
- **system-test**: Executa testes de sistema.
- **performance-test**: Executa testes de performance utilizando Gatling.

### Makefile

O `Makefile` contém comandos para facilitar a execução de diversas tarefas, como build, testes e operações com Docker:

- **build**: Compila o projeto.
- **unit-test**: Executa testes unitários.
- **integration-test**: Executa testes de integração.
- **system-test**: Executa testes de sistema.
- **performance-test**: Executa testes de performance.
- **test**: Executa todos os testes (unitários e de integração).
- **package**: Empacota o projeto.
- **docker-build**: Constrói a imagem Docker.
- **docker-start**: Inicia os containers Docker.
- **docker-stop**: Para os containers Docker.

## Como Executar

### Pré-requisitos

- **Java 17**
- **Maven**
- **Docker**

### Passos

1. **Compilar o Projeto**:
    ```sh
    make build
    ```

2. **Executar Testes Unitários**:
    ```sh
    make unit-test
    ```

3. **Executar Testes de Integração**:
    ```sh
    make integration-test
    ```

4. **Executar Testes de Sistema**:
    ```sh
    make system-test
    ```

5. **Executar Testes de Performance**:
    ```sh
    make performance-test
    ```

6. **Empacotar o Projeto**:
    ```sh
    make package
    ```

7. **Construir a Imagem Docker**:
    ```sh
    make docker-build
    ```

8. **Iniciar os Containers Docker**:
    ```sh
    make docker-start
    ```

9. **Parar os Containers Docker**:
    ```sh
    make docker-stop
    ```

## Relatórios

### Allure

Para gerar e visualizar relatórios Allure, execute:

```sh
allure serve target/allure-results
```