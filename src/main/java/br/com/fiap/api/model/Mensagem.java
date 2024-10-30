package br.com.fiap.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {
    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "O campo usuario é obrigatório")
    private String usuario;

    @Column(nullable = false)
    @NotEmpty(message = "O campo conteudo é obrigatório")
    private String conteudo;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Builder.Default
    private int gostei = 0;
}
