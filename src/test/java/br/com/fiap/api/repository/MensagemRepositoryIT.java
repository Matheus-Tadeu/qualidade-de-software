package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isPositive();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        // Act
        var mensagemRegistrada = mensagemRepository.save(mensagem);

        // Assert
        assertThat(mensagemRegistrada)
                .isInstanceOf(Mensagem.class)
                .isNotNull()
                .isEqualTo(mensagem);
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("a520c636-6205-48bb-ac5f-3e4eaddb56f4");

        // Act
        var mensagemEncontradaOpcional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemEncontradaOpcional).isPresent();
        mensagemEncontradaOpcional.ifPresent(mensagemEncontrada -> {
            assertThat(mensagemEncontrada.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("4f38bddc-3358-4e6a-8bd7-38048fb0ea1c");

        // Act
        mensagemRepository.deleteById(id);

        // Assert
        var mensagemRecebidaOpcional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOpcional).isEmpty();
    }

    @Test
    void devePermitirListarMensagem() {
        // Act
        var mensagens = mensagemRepository.findAll();

        // Assert
        assertThat(mensagens).hasSizeGreaterThan(0);
    }
}
