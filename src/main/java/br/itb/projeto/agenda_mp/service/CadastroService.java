package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Cadastro;
import br.itb.projeto.agenda_mp.model.repository.CadastroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CadastroService {
    
    @Autowired
    private CadastroRepository cadastroRepository;
    
    public List<Cadastro> findAll() {
        return cadastroRepository.findAll();
    }
    
    public Optional<Cadastro> findById(Long id) {
        return cadastroRepository.findById(id);
    }
    
    public Cadastro save(Cadastro cadastro) {
        return cadastroRepository.save(cadastro);
    }
    
    public void deleteById(Long id) {
        cadastroRepository.deleteById(id);
    }
}