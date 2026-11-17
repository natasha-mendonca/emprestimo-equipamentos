package com.example.emprestimo.service;

import com.example.emprestimo.model.Equipamento;
import com.example.emprestimo.model.TipoEquipamento;
import com.example.emprestimo.repository.EquipamentoRepository;
import com.example.exception.NaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EquipamentoService {
    private final EquipamentoRepository repository;

    public EquipamentoService(EquipamentoRepository repository) {
        this.repository = repository;
    }

    public List<Equipamento> listar(TipoEquipamento tipo) {
        if (tipo == null) {
            return repository.findAll();
        }
        return repository.findByTipo(tipo);
    }
    public Equipamento buscar(Long id){
        return repository.findById(id).orElseThrow(() ->
        new NaoEncontradoException("Equipamento não encontrado"));
    }

    public Equipamento cadastrar (Equipamento equipamento){
        if (repository.existsByPatrimonio(equipamento.getPatrimonio())){
            throw  new ResponseStatusException(
                    HttpStatus.CONFLICT, "Patrimonio ja cadastrado");
        }
        equipamento.setDisponivel(true);
        return repository.save(equipamento);
    }
}