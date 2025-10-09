package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Login;
import br.itb.projeto.agenda_mp.model.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoginService {
    
    @Autowired
    private LoginRepository loginRepository;
    
    public List<Login> findAll() {
        return loginRepository.findAll();
    }
    
    public Optional<Login> findById(Long id) {
        return loginRepository.findById(id);
    }
    
    public Login save(Login login) {
        return loginRepository.save(login);
    }
    
    public void deleteById(Long id) {
        loginRepository.deleteById(id);
    }
}