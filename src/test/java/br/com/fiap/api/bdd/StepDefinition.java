package br.com.fiap.api.bdd;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.utils.MensagemHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;
    private Mensagem mensagemResposta;
    private final String ENDPOINT_API_MENSAGENS = "http://localhost:8080/mensagens";

    @Quando("registrar uma nova mensagem")
    public Mensagem registrar_uma_nova_mensagem() {
        var mensagemRequest = MensagemHelper.gerarMensagem();
        response = given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mensagemRequest)
            .when()
            .post(ENDPOINT_API_MENSAGENS);
        return response.then().extract().as(Mensagem.class);
    }

    @Então("a mensagem é registrada com sucesso")
    public void a_mensagem_é_registrada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Então("deve ser apresentada")
    public void deve_ser_apresentada() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Dado("que uma mensagem já foi publicada")
    public void que_uma_mensagem_já_foi_publicada() {
        mensagemResposta = registrar_uma_nova_mensagem();
    }
    @Quando("efetuar a buscar da mensagem")
    public void efetuar_a_buscar_da_mensagem() {
        response = when()
                    .get(ENDPOINT_API_MENSAGENS + "/{id}", mensagemResposta.getId());
    }
    @Então("a mensagem é exibida com sucesso")
    public void a_mensagem_é_exibida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Quando("efetuar requisição para alterar mensagem")
    public void efetuar_requisição_para_alterar_mensagem() {
        mensagemResposta.setConteudo("ABC 123");
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagemResposta)
                .when()
                .put(ENDPOINT_API_MENSAGENS + "/{id}", mensagemResposta.getId());
    }
    @Então("a mensagem é atualizada com sucesso")
    public void a_mensagem_é_atualizada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Quando("requisitar a remoção da mensagem")
    public void requisitar_a_remoção_da_mensagem() {
        response = when()
                .delete(ENDPOINT_API_MENSAGENS + "/{id}", mensagemResposta.getId());
    }
    @Então("a mensagem é removida com sucesso")
    public void a_mensagem_é_removida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value());
    }
}
