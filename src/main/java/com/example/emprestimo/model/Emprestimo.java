package com.example.emprestimo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Emprestimo {
    private static final int DIAS_DE_EMPRESTIMO = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long equipamentoId;

    private Long colaboradorId;

    private LocalDate dataEmprestimo;

    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao;

    public Emprestimo() {
    }

    public Emprestimo(Long equipamentoId, Long colaboradorId, LocalDate hoje) { //Para criar um empréstimo, me entregue 3 coisas": um Long chamado equipamentoId, um Long chamado colaboradorId e um LocalDate chamado hoje. Só tipo + nome, sem contas.
        this.equipamentoId = equipamentoId; //Guarda o parâmetro no atributo. O this. indica o atributo da classe; o nome sem this. é o parâmetro que chegou.
        this.colaboradorId = colaboradorId;
        this.dataEmprestimo = hoje;
        this.dataPrevistaDevolucao = hoje.plusDays(DIAS_DE_EMPRESTIMO);
    }

    public Long getId() {
        return id;
    }

    public Long getEquipamentoId() {
        return equipamentoId;
    }

    public Long getColaboradorId() {
        return colaboradorId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void devolver(LocalDate data) {
        if (dataDevolucao != null) {
            throw new IllegalStateException("Empréstimo já devolvido");
        }
        this.dataDevolucao = data;
    }

    public boolean estaAtrasadoEm(LocalDate data) {
        return dataDevolucao == null && data.isAfter(dataPrevistaDevolucao);
    }

    public boolean isAtrasado() {
        return estaAtrasadoEm(LocalDate.now());
    }
}
