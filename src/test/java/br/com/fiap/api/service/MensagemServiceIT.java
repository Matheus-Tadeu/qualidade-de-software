package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagens() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();

            // Act
            var resultadoObtido = mensagemService.registrarMensagem(mensagem);

            // Assert
            assertThat(resultadoObtido)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull()
                    .isEqualTo(mensagem);
            assertThat(resultadoObtido.getId()).isNotNull();
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isZero();
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagens() {
            // Arrange
            var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f4");

            // Act
            var resultadoObtido = mensagemService.buscarMensagem(id);

            // Assert
            assertThat(resultadoObtido)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
            assertThat(resultadoObtido.getId()).isEqualTo(id);
            assertThat(resultadoObtido.getUsuario()).isEqualTo("José");
            assertThat(resultadoObtido.getConteudo()).isEqualTo("Conteúdo da mensagem 01");
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isZero();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistir() {
            // Arrange
            var id = UUID.fromString("72752f54-51b4-4e44-a7b3-a0765d2c6907");

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagens() {
            // Arrange
            var id = UUID.fromString("3ab78d54-fe97-4044-806f-583f88065d3b");
            var mensagemNova = MensagemHelper.gerarMensagem();
            mensagemNova.setId(id);

            // Act
            var mensagemAtualizada = mensagemService.alterarMensagem(id, mensagemNova);

            // Assert
            assertThat(mensagemAtualizada.getId()).isEqualTo(id);
            assertThat(mensagemAtualizada.getUsuario()).isNotEqualTo(mensagemNova.getUsuario());
            assertThat(mensagemAtualizada.getConteudo()).isEqualTo(mensagemNova.getConteudo());
            assertThat(mensagemAtualizada.getDataCriacao()).isNotNull();
            assertThat(mensagemAtualizada)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("705a492c-eaec-4be2-9d4d-19778f506cb1");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            // Arrange
            var id = UUID.fromString("4f38bddc-3358-4e6a-8bd7-38048fb0ea1c");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(UUID.fromString("ac3b1f30-810b-48be-ab07-d366bc74fba2"));

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem atualizada não apresenta o ID correto");
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMesagens() {
            // Arrange
            var id = UUID.fromString("3ab78d54-fe97-4044-806f-583f88065d3b");

            // Act
            var resultadoObtido = mensagemService.removerMensagem(id);

            // Assert
            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("705a492c-eaec-4be2-9d4d-19778f506cb1");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class ListarMensagens {

        @Test
        void devePermitirListarMensagens() {
            // Arrange
            Page<Mensagem> listDeMensagemObtida = mensagemService.listarMensagens(Pageable.unpaged());

            // Act & Assert
            Assertions.assertThat(listDeMensagemObtida)
                    .hasSize(3);
            Assertions.assertThat(listDeMensagemObtida)
                    .allSatisfy(m -> {
                        assertThat(m).isNotNull();
                    });
        }
    }
}
