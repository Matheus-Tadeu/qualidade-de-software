package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.utils.MensagemHelper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    void devePermitirRegistrarMensagens() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class))).thenReturn(mensagem);

        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

        // Assert
        assertThat(mensagemRegistrada)
                .isInstanceOf(Mensagem.class)
                .isNotNull()
                .isEqualTo(mensagem);
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    void devePermitirBuscarMensagens() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.of(mensagem));

        // Act
        var mensagemObtida = mensagemService.buscarMensagem(id);

        // Assert
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistir() {
        // Arrange
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(br.com.fiap.api.exception.MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirAlterarMensagens() {
        // Arrange
        var id = UUID.randomUUID();

        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.of(mensagemAntiga));

        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var mensagemAlterada = mensagemService.alterarMensagem(id, mensagemNova);

        // Assert
        assertThat(mensagemAlterada).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemAlterada.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemAlterada.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemAlterada.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(br.com.fiap.api.exception.MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.randomUUID());
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.of(mensagemAntiga));

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem atualizada não apresenta o ID correto");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMesagens() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.of(mensagem));

        doNothing().when(mensagemRepository).delete(any(Mensagem.class));

        // Act
        var mensagemRemovida = mensagemService.removerMensagem(id);

        // Assert
        assertThat(mensagemRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).delete(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                .isInstanceOf(br.com.fiap.api.exception.MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).delete(any(Mensagem.class));
        verify(mensagemRepository, never()).delete(any(Mensagem.class));
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        Page<Mensagem> listaDemensagens = new PageImpl<>(Arrays.asList(
                MensagemHelper.gerarMensagem(),
                MensagemHelper.gerarMensagem()
        ));
        when(mensagemRepository.listarMensagem(any(Pageable.class))).thenReturn(listaDemensagens);

        // Act
        var mensagens = mensagemService.listarMensagens(Pageable.unpaged());

        // Assert
        assertThat(mensagens).hasSize(2);
        assertThat(mensagens.getContent())
                .allSatisfy(m -> {
                    assertThat(m).isInstanceOf(Mensagem.class);
                });
        verify(mensagemRepository, times(1)).listarMensagem(any(Pageable.class));
    }
}
