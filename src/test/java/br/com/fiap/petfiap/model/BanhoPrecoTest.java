package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Teste novo (regra sem cobertura): preco do banho por porte.
public class BanhoPrecoTest {

    private Banho banhoDoRex() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));
    }

    @Test
    public void deveCalcularPrecoPorPorteQuandoForBanho() {
        // Arrange
        Banho banho = banhoDoRex();

        // Act + Assert: PEQUENO R$ 60, MEDIO R$ 80, GRANDE R$ 100
        banho.setPetPorte("PEQUENO");
        assertEquals(60.0, banho.calcularPreco(), 0.001);

        banho.setPetPorte("MEDIO");
        assertEquals(80.0, banho.calcularPreco(), 0.001);

        banho.setPetPorte("GRANDE");
        assertEquals(100.0, banho.calcularPreco(), 0.001);
    }
}