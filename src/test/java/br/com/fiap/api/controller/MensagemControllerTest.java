package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import br.com.fiap.api.utils.MensagemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            // Act & Assert
            mockMvc.perform(
                        post("/mensagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));

        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            // Arrange
            String xmlPayload = "<mensagem><id>1</id><conteudo>Olá, mundo!</conteudo></mensagem>";

            // Act & Assert
            mockMvc.perform(
                        post("/mensagens")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlPayload)
                    )
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("612bffb7-d39f-421b-aa53-140fbcb6d682");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenReturn(mensagem);

            // Act & Assert
            mockMvc.perform(
                        get("/mensagens/{id}", id)
                        .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        @Description("Valida o cenário de exeção ao efetuar uma busca de mensagem quando o ID não existir")
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistir() throws Exception {
            // Arrange
            var id = UUID.fromString("dcc8b985-556d-45be-8136-1c860b567b78");
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(MensagemNotFoundException.class);

            // Act & Assert
            mockMvc.perform(
                        get("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("d8f8d2f6-96fb-420e-8eb8-3914bea91624");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(1));

            // Act & Assert
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(1));
            mockMvc.perform(
                        put("/mensagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isAccepted());
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("dcc8b985-556d-45be-8136-1c860b567b78");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExececao = "Mensagem não encontrada";
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExececao));

            // Act & Assert
            mockMvc.perform(
                        put("/mensagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExececao));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            // Arrange
            var id = UUID.fromString("dcc8b985-556d-45be-8136-1c860b567b78");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.fromString("07aa77d2-dfc9-4a66-87da-3afb4dccaa21"));
            var conteudoDaExececao = "Mensagem atualizada não apresenta o ID correto";
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExececao));

            // Act & Assert
            mockMvc.perform(
                            put("/mensagens/{id}", id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExececao));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() throws Exception {
            // Arrange
            var id = UUID.fromString("d8f8d2f6-96fb-420e-8eb8-3914bea91624");
            String xmlPayload = "<mensagem><id>"+ id.toString() +"</id><conteudo>Olá, mundo!</conteudo></mensagem>";

            // Act & Assert
            mockMvc.perform(
                            put("/mensagens/{id}", id)
                                    .contentType(MediaType.APPLICATION_XML)
                                    .content(xmlPayload)
                    )
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("d8f8d2f6-96fb-420e-8eb8-3914bea91624");
            when(mensagemService.removerMensagem(any(UUID.class)))
                    .thenReturn(true);

            // Act & Assert
            mockMvc.perform(
                        delete("/mensagens/{id}", id)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string("mensagem removida"));
            verify(mensagemService, times(1)).removerMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("dcc8b985-556d-45be-8136-1c860b567b78");
            var mensagem = "Mensagem não encontrada";

            when(mensagemService.removerMensagem(id))
                    .thenThrow(new MensagemNotFoundException(mensagem));

            // Act & Assert
            mockMvc.perform(
                        delete("/mensagens/{id}", id)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mensagem));
            verify(mensagemService, times(1)).removerMensagem(id);
        }
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper.writeValueAsString(object);
    }
}
