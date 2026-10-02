package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

// Teste novo (regra sem cobertura): agendar com data/hora no passado.
@ExtendWith(MockitoExtension.class)
public class AgendaServiceDataPassadoTest {

    @Mock
    private AtendimentoRepository repository;

    @InjectMocks
    private AgendaService service;

    @Test
    public void deveRecusarAgendamentoQuandoDataForNoPassado() {
        // Arrange: data/hora de ontem
        Banho noPassado = new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> service.agendar(noPassado));

        verifyNoInteractions(repository);
    }
}