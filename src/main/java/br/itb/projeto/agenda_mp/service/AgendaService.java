package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {
    
    @Autowired
    private AgendaRepository agendaRepository;
    
    public List<Agenda> findAll() {
        return agendaRepository.findAll();
    }
    
    public Optional<Agenda> findById(Long id) {
        return agendaRepository.findById(id);
    }
    
    public Agenda save(Agenda agenda) {
        return agendaRepository.save(agenda);
    }
    
    public void deleteById(Long id) {
        agendaRepository.deleteById(id);
    }
}