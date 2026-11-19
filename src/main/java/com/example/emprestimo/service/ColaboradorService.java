package com.example.emprestimo.service;

import com.example.emprestimo.model.Colaborador;
import com.example.emprestimo.repository.ColaboradorRepository;
import com.example.exception.NaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository) {
        this.repository = repository;
    }

    public List<Colaborador> listar() {
        return repository.findAll();
    }

    public Colaborador buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NaoEncontradoException("Colaborador não encontrado."));
    }

    public Colaborador cadastrar(Colaborador colaborador) {
        if (repository.existsByEmail(colaborador.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        return repository.save(colaborador);
    }
}
