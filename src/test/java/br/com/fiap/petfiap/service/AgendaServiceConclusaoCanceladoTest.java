package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.StatusInvalidoException;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Teste novo (regra sem cobertura): concluir atendimento cancelado.
@ExtendWith(MockitoExtension.class)
public class AgendaServiceConclusaoCanceladoTest {

    @Mock
    private AtendimentoRepository repository;

    @InjectMocks
    private AgendaService service;

    private Banho banhoDoRexAmanha10h() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now().plusDays(1).withNano(0));
    }

    @Test
    public void deveRecusarConclusaoQuandoAtendimentoCancelado() {
        // Arrange
        Banho cancelado = banhoDoRexAmanha10h();
        cancelado.setStatus("CANCELADO");
        when(repository.findById(1L)).thenReturn(Optional.of(cancelado));

        // Act + Assert
        assertThrows(StatusInvalidoException.class, () -> service.concluir(1L));

        // Nada e salvo e o status continua CANCELADO
        verify(repository, never()).save(any());
        assertEquals("CANCELADO", cancelado.getStatus());
    }
}