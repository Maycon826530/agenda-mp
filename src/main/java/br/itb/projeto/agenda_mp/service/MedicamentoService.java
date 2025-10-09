package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import br.itb.projeto.agenda_mp.model.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoService {
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    public List<Medicamento> findAll() {
        return medicamentoRepository.findAll();
    }
    
    public Optional<Medicamento> findById(Long id) {
        return medicamentoRepository.findById(id);
    }
    
    public Medicamento save(Medicamento medicamento) {
        if (medicamento.getDataCadastro() == null) {
            medicamento.setDataCadastro(LocalDateTime.now());
        }
        return medicamentoRepository.save(medicamento);
    }
    
    public void deleteById(Long id) {
        medicamentoRepository.deleteById(id);
    }
}