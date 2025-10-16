package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Cadastro;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
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

        public boolean updateProfile(Long userId, String nome, String senhaAtual, String novaSenha) {
        Optional<Cadastro> usuarioOpt = findById(userId);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Cadastro usuario = usuarioOpt.get();
        
        // Validar senha atual
        if (!usuario.getSenha().equals(senhaAtual)) {
            return false;
        }
        
        // Atualizar dados
        usuario.setNome(nome);
        usuario.setSenha(novaSenha);
        
        save(usuario);
        return true;
    }
}