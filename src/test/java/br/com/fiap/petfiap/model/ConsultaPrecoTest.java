package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Teste novo (regra sem cobertura): consulta tem preco fixo, independente do porte.
public class ConsultaPrecoTest {

    private ConsultaVeterinaria consultaDaMimi() {
        return new ConsultaVeterinaria(1, "Mimi", "MEDIO", "Bruno", LocalDateTime.of(2026, 10, 1, 14, 0));
    }

    @Test
    public void deveCustar150ReaisQuandoQualquerPorte() {
        // Arrange
        ConsultaVeterinaria consulta = consultaDaMimi();

        // Act + Assert: preco fixo, o porte nao muda o valor
        consulta.setPetPorte("PEQUENO");
        assertEquals(150.0, consulta.calcularPreco(), 0.001);

        consulta.setPetPorte("MEDIO");
        assertEquals(150.0, consulta.calcularPreco(), 0.001);

        consulta.setPetPorte("GRANDE");
        assertEquals(150.0, consulta.calcularPreco(), 0.001);
    }
}