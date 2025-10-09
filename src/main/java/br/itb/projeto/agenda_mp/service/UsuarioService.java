package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        if (usuario.getDataCadastro() == null) {
            usuario.setDataCadastro(LocalDateTime.now());
        }
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository.findByEmailAndSenha(email, senha);
    }
}