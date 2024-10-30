package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Mensagem> registrarMensagem(@RequestBody Mensagem mensagem) {
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        return new ResponseEntity<>(mensagemRegistrada, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);

        try {
            var mensagem = mensagemService.buscarMensagem(uuid);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return new ResponseEntity<>("ID Inválido", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Mensagem>> listarMensagens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Mensagem> mensagens = mensagemService.listarMensagens(pageable);
        return new ResponseEntity<>(mensagens, HttpStatus.OK);
    }


    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> alterarMensagem(@PathVariable String id, @RequestBody Mensagem mensagem) {
        var uuid = UUID.fromString(id);

        try {
            var mensagemAlterada = mensagemService.alterarMensagem(uuid, mensagem);
            return new ResponseEntity<>(mensagemAlterada, HttpStatus.ACCEPTED);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mensagemNotFoundException.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removerMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);

        try {
            mensagemService.removerMensagem(uuid);
            return new ResponseEntity<>("mensagem removida",HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mensagemNotFoundException.getMessage());
        }
    }
}
