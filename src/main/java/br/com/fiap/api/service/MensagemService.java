package br.com.fiap.api.service;

import br.com.fiap.api.model.Mensagem;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface que define os métodos que devem ser implementados pela classe de serviço de Mensagem.
 */
public interface  MensagemService {
    Mensagem registrarMensagem(Mensagem mensagem);
    Mensagem buscarMensagem(UUID id);
    Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada);
    boolean removerMensagem(UUID id);
    Page<Mensagem> listarMensagens(Pageable pageable);
}
