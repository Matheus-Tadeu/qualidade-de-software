package br.com.fiap.api.controller;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.utils.MensagemHelper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();
            given()
                    .filter(new AllureRestAssured())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(mensagem)
                    .when()
                        .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() {
            String xmlPayload = "<mensagem><id>1</id><conteudo>Olá, mundo!</conteudo></mensagem>";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
            .when()
                    .post("/mensagens")
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {
            var id = "a520c636-6205-48bb-ac5f-3e4eaddb56f4";
            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistir() {
            var id = "3ab78d54-fe97-4044-806f-583f88065d3";
            given()
                    .filter(new AllureRestAssured())
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f4");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("José")
                    .conteudo("Conteúdo da mensagem")
                    .build();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("José")
                    .conteudo("Conteúdo da mensagem")
                    .build();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f4");
            var mensagem = Mensagem.builder()
                    .id(UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f0"))
                    .usuario("José")
                    .conteudo("Conteúdo da mensagem")
                    .build();

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem atualizada não apresenta o ID correto"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() {
            var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f4");
            String xmlPayload = "<mensagem><id>a520c636-6205-48bb-ac5f-3e4eaddb56f4</id><conteudo>Olá, mundo!</conteudo></mensagem>";

            given()
                    .filter(new AllureRestAssured())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens/a520c636-6205-48bb-ac5f-3e4eaddb56f4"))
                    .body("path", containsString("/mensagens"));
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("3ab78d54-fe97-4044-806f-583f88065d3b");
            given()
                    .filter(new AllureRestAssured())
                    .when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("3ab78d54-fe97-4044-806f-583f88065d3");
            given()
                    .filter(new AllureRestAssured())
                    .when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }
    }

    @Nested
    class ListarMensagem {

        @Test
        void devePermitirListarMensagem() {
            given()
                    .filter(new AllureRestAssured())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/mensagens")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));        }

        @Test
        void devePermitirListarMensagem_QuandoNaoInformadoPaginacao() {
            given()
                    .filter(new AllureRestAssured())
                    .when()
                    .get("/mensagens")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }
    }
}
