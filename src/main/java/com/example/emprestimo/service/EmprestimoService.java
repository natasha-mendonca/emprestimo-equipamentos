package com.example.emprestimo.service;

import com.example.emprestimo.model.Emprestimo;
import com.example.emprestimo.model.Equipamento;
import com.example.emprestimo.repository.ColaboradorRepository;
import com.example.emprestimo.repository.EmprestimoRepository;
import com.example.emprestimo.repository.EquipamentoRepository;
import com.example.exception.NaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final ColaboradorRepository colaboradorRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             EquipamentoRepository equipamentoRepository,
                             ColaboradorRepository colaboradorRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    public List<Emprestimo> listar(Boolean atrasados) {
        if (Boolean.TRUE.equals(atrasados)) {
            return emprestimoRepository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return emprestimoRepository.findAll();
    }

    public Emprestimo buscar(Long id) {
        return emprestimoRepository.findById(id).orElseThrow(() ->
                new NaoEncontradoException("Empréstimo não encontrado"));
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId) {
        return emprestimoRepository.findByColaboradorId(colaboradorId);
    }

    @Transactional
    public Emprestimo emprestar(Long equipamentoId, Long colaboradorId) {
        Equipamento equipamento = equipamentoRepository.findById(equipamentoId).orElseThrow(() ->
                new NaoEncontradoException("Equipamento não encontrado"));

        if (!colaboradorRepository.existsById(colaboradorId)) {
            throw new NaoEncontradoException("Colaborador não encontrado");
        }

        if (emprestimoRepository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                colaboradorId, LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Colaborador possui empréstimo atrasado");
        }

        equipamento.emprestar();
        Emprestimo emprestimo = new Emprestimo(equipamentoId, colaboradorId, LocalDate.now());

        equipamentoRepository.save(equipamento);
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = buscar(id);
        emprestimo.devolver(LocalDate.now());

        Equipamento equipamento = equipamentoRepository.findById(emprestimo.getEquipamentoId()).orElseThrow(() ->
                new NaoEncontradoException("Equipamento não encontrado"));
        equipamento.devolver();

        equipamentoRepository.save(equipamento);
        return emprestimoRepository.save(emprestimo);
    }
}
