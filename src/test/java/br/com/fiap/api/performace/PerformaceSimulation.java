package br.com.fiap.api.performace;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformaceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol =
            http.baseUrl("http://localhost:8080")
                    .header("Content-Type", "application/json");

    // Request para adicionar mensagem
    ActionBuilder adicionarMensagemRequest =  http("request: adicionar mensagem")
            .post("/mensagens")
            .body(StringBody("{\"usuario\":   \"user\", \"conteudo\": \"teste de mensagem\"}"))
            .check(status().is(201))
            .check(jsonPath("$.id").saveAs("mensagemId"));

    // Request para buscar mensagem
    ActionBuilder buscarMensagemRequest = http("request:  buscar mensagem")
            .get("/mensagens/#{mensagemId}")
            .check(status().is(200));

    // Request para remover mensagem
    ActionBuilder removerMensagemRequest = http("request:  remover mensagem")
            .delete("/mensagens/#{mensagemId}")
            .check(status().is(200));

    // Cenário para adicionar mensagem
    ScenarioBuilder cenarioAdicionarMensagem = scenario("adicionar mensagem")
            .exec(adicionarMensagemRequest);

    // Cenário para buscar mensagem
    ScenarioBuilder cenarioBuscarMensagem = scenario("buscar mensagem")
            .exec(adicionarMensagemRequest)
            .exec(buscarMensagemRequest);

    // Cenário para remover mensagem
    ScenarioBuilder cenarioRemoverMensagem = scenario("remover mensagem")
            .exec(adicionarMensagemRequest)
            .exec(removerMensagemRequest);

    {
        setUp(

                // Cenário para adicionar mensagem
                cenarioAdicionarMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(2)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(2)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(2)
                                        .to(1)
                                        .during(Duration.ofSeconds(10))
                ),

                // Cenário para buscar mensagem
                cenarioBuscarMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))
                ),

                // Cenário para remover mensagem
                cenarioRemoverMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(5)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(5)
                                .to(1)
                                .during(Duration.ofSeconds(10))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(50)
                );
    }
}