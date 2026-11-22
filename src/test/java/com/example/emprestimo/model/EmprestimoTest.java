package com.example.emprestimo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @Test
    void criarEmprestimoDefineDataDeHojeEPrevisaoEm14Dias() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 11, 14));

        assertEquals(LocalDate.of(2026, 11, 14), emprestimo.getDataEmprestimo());
        assertEquals(LocalDate.of(2026, 11, 28), emprestimo.getDataPrevistaDevolucao());
        assertNull(emprestimo.getDataDevolucao());
    }

    @Test
    void devolverRegistraADataDeDevolucao() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 11, 14));

        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertEquals(LocalDate.of(2026, 11, 20), emprestimo.getDataDevolucao());
    }

    @Test
    void naoDevolveDuasVezes() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 11, 14));
        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertThrows(IllegalStateException.class,
                () -> emprestimo.devolver(LocalDate.of(2026, 11, 21)));
    }

    @Test
    void naoDevolvidoComPrazoVencidoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 10, 27)); // prazo 10/11

        assertTrue(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void naoDevolvidoDentroDoPrazoNaoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 11, 6)); // prazo 20/11

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void devolvidoNaoEstaAtrasadoMesmoComPrazoVencido() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 10, 27)); // prazo 10/11
        emprestimo.devolver(LocalDate.of(2026, 11, 12));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void noDiaDoPrazoAindaNaoEstaAtrasado() {
        Emprestimo emprestimo = new Emprestimo(1L, 1L, LocalDate.of(2026, 10, 31)); // prazo 14/11

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }
}