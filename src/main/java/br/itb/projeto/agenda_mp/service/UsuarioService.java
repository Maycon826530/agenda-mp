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
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("Encontrados " + usuarios.size() + " usuários no banco");
            return usuarios;
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os usuários: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        try {
            System.out.println("Buscando usuário com email: " + email);
            
            // Primeiro verifica se o usuário existe
            Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail(email);
            if (usuarioExiste.isEmpty()) {
                System.out.println("Usuário não encontrado com email: " + email);
                return Optional.empty();
            }
            
            // Verifica se a senha está correta
            Optional<Usuario> usuario = usuarioRepository.findByEmailAndSenha(email, senha);
            if (usuario.isEmpty()) {
                System.out.println("Senha incorreta para o email: " + email);
            } else {
                System.out.println("Login bem-sucedido para: " + email);
            }
            
            return usuario;
        } catch (Exception e) {
            System.err.println("Erro durante autenticação: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public boolean updateProfile(Long userId, String nome, String senhaAtual, String novaSenha) {
        Optional<Usuario> usuarioOpt = findById(userId);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        
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