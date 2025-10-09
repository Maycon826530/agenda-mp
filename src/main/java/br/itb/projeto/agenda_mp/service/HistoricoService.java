package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Historico;
import br.itb.projeto.agenda_mp.model.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {
    
    @Autowired
    private HistoricoRepository historicoRepository;
    
    public List<Historico> findAll() {
        return historicoRepository.findAll();
    }
    
    public Optional<Historico> findById(Long id) {
        return historicoRepository.findById(id);
    }
    
    public Historico save(Historico historico) {
        return historicoRepository.save(historico);
    }
    
    public void deleteById(Long id) {
        historicoRepository.deleteById(id);
    }
}