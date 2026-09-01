package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import br.itb.projeto.agenda_mp.model.repository.HistoricoRepository;
import br.itb.projeto.agenda_mp.model.repository.LembreteRepository;
import br.itb.projeto.agenda_mp.model.repository.MedicamentoRepository;
import br.itb.projeto.agenda_mp.model.repository.NotificacaoAtrasoRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final LocalDate ONBOARDING_PENDING_DATE = LocalDate.of(1900, 1, 1);
    private static final String TIPO_NOTIFICACAO_SISTEMA = "sistema";
    private static final String TIPO_NOTIFICACAO_BROWSER = "browser";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private NotificacaoAtrasoRepository notificacaoAtrasoRepository;

    public List<Usuario> findAll() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("Encontrados " + usuarios.size() + " usuarios no banco");
            return usuarios;
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os usuarios: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        usuario.setTipoNotificacao(normalizarTipoNotificacao(usuario.getTipoNotificacao()));

        if (usuario.getSenha() != null && !usuario.getSenha().isBlank() && !isSenhaComHash(usuario.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> update(Long id, Usuario dadosAtualizados) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(dadosAtualizados.getNome());
            usuario.setEmail(dadosAtualizados.getEmail());
            usuario.setDataNascimento(dadosAtualizados.getDataNascimento());
            usuario.setComorbidade(dadosAtualizados.getComorbidade());

            if (dadosAtualizados.getTipoNotificacao() != null) {
                usuario.setTipoNotificacao(normalizarTipoNotificacao(dadosAtualizados.getTipoNotificacao()));
            }

            if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
            }

            return usuarioRepository.save(usuario);
        });
    }

    // Exclui usuario respeitando a ordem das FKs: Historico -> Medicamento -> Agenda -> Usuario
    @Transactional
    public boolean deleteComValidacao(Long id, String senhaAtual) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();
        if (!senhaConfere(usuario, senhaAtual)) return false;

        excluirUsuarioEDadosRelacionados(id);
        return true;
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public boolean deleteByAdmin(Long id) {
        if (!usuarioRepository.existsById(id)) return false;

        excluirUsuarioEDadosRelacionados(id);
        return true;
    }

    private void excluirUsuarioEDadosRelacionados(Long usuarioId) {
        // As exclusoes em lote seguem a ordem das chaves estrangeiras. A transacao
        // garante que nao seja possivel remover o usuario e deixar agendas orfas.
        notificacaoAtrasoRepository.deleteByUsuarioId(usuarioId);
        historicoRepository.deleteByUsuarioId(usuarioId);
        medicamentoRepository.deleteByUsuarioId(usuarioId);
        agendaRepository.deleteByUsuarioId(usuarioId);
        lembreteRepository.deleteByUsuarioId(usuarioId);
        usuarioRepository.deleteById(usuarioId);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> login(String email, String senha) {
        try {
            System.out.println("Buscando usuario com email: " + email);
            Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail(email);
            if (usuarioExiste.isEmpty()) {
                System.out.println("Usuario nao encontrado com email: " + email);
                return Optional.empty();
            }

            Usuario usuario = usuarioExiste.get();
            if (!senhaConfere(usuario, senha)) {
                System.out.println("Senha incorreta para o email: " + email);
                return Optional.empty();
            }

            System.out.println("Login bem-sucedido para: " + email);
            return Optional.of(usuario);
        } catch (Exception e) {
            System.err.println("Erro durante autenticacao: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean updateProfile(Long userId, String nome, String senhaAtual, String novaSenha) {
        Optional<Usuario> usuarioOpt = findById(userId);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();
        if (!senhaConfere(usuario, senhaAtual)) return false;

        usuario.setNome(nome);
        if (novaSenha != null && !novaSenha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }
        usuarioRepository.save(usuario);
        return true;
    }

    private boolean senhaConfere(Usuario usuario, String senhaInformada) {
        if (senhaInformada == null || usuario.getSenha() == null) return false;

        if (isSenhaComHash(usuario.getSenha())) {
            return passwordEncoder.matches(senhaInformada, usuario.getSenha());
        }

        boolean senhaLegadaConfere = usuario.getSenha().equals(senhaInformada);
        if (senhaLegadaConfere) {
            usuario.setSenha(passwordEncoder.encode(senhaInformada));
            usuarioRepository.save(usuario);
        }
        return senhaLegadaConfere;
    }

    private boolean isSenhaComHash(String senha) {
        return senha != null && senha.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }

    public String normalizarTipoNotificacao(String tipoNotificacao) {
        if (TIPO_NOTIFICACAO_BROWSER.equalsIgnoreCase(String.valueOf(tipoNotificacao))) {
            return TIPO_NOTIFICACAO_BROWSER;
        }

        return TIPO_NOTIFICACAO_SISTEMA;
    }

    public boolean perfilCompleto(Usuario usuario) {
        return usuario != null
                && usuario.getNome() != null
                && !usuario.getNome().isBlank()
                && usuario.getDataNascimento() != null
                && !ONBOARDING_PENDING_DATE.equals(usuario.getDataNascimento())
                && usuario.getComorbidade() != null
                && !usuario.getComorbidade().isBlank();
    }

    public Optional<Usuario> finalizarOnboarding(Long id, String nome, String dataNascimento, String comorbidade) {
        if (nome == null || nome.isBlank() || dataNascimento == null || dataNascimento.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(nome.trim());
            usuario.setDataNascimento(LocalDate.parse(dataNascimento));
            usuario.setComorbidade(
                    comorbidade == null || comorbidade.isBlank()
                            ? "Nao possuo comorbidades"
                            : comorbidade.trim()
            );
            return usuarioRepository.save(usuario);
        });
    }

    public Optional<Usuario> atualizarTipoNotificacao(Long id, String tipoNotificacao) {
        return usuarioRepository.findById(id).map(usuario -> {
            String tipoNormalizado = normalizarTipoNotificacao(tipoNotificacao);
            usuario.setTipoNotificacao(tipoNormalizado);

            if (TIPO_NOTIFICACAO_BROWSER.equals(tipoNormalizado)) {
                usuario.setFcmToken(null);
            }

            return usuarioRepository.save(usuario);
        });
    }

    public void atualizarFcmToken(Long id, String token) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        usuario.setFcmToken(token);
        usuarioRepository.save(usuario);
    }
}
}
