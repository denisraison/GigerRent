package br.com.gr.controller;

import br.com.gr.dao.UsuarioDao;
import br.com.gr.model.Cliente;
import br.com.gr.model.Funcionario;
import br.com.gr.model.Usuario;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@ManagedBean
@RequestScoped
public class UsuarioController implements Serializable {
    private Usuario usuario;
    private UsuarioDao usuarioDao = new UsuarioDao();
    Map<String,String> requestParams;
    
    private Usuario novoUsuario = new Cliente();

    @PostConstruct
    public void initMyBean(){
        requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        usuario = new Usuario();
        SecurityContext context = SecurityContextHolder.getContext();
        if (context instanceof SecurityContext){
            Authentication authentication = context.getAuthentication();
            if (authentication instanceof Authentication){
                if("anonymousUser".equals(authentication.getPrincipal())){
                    usuario = null;
                }else{
                    usuario = usuarioDao.getUserByUsername(((User)authentication.getPrincipal()).getUsername());
                }
            }
        }
    }
    
    public void cadastrarUsuario(ActionEvent actionEvent){
        if(!novoUsuario.getCPF_CNPJ().isEmpty() && !novoUsuario.getUsername().isEmpty()){
            usuarioDao.inserir(novoUsuario);
        }
    }
    
    public Usuario getNovoUsuario() {
        return novoUsuario;
    }

    public void setNovoUsuario(Usuario novoUsuario) {
        this.novoUsuario = novoUsuario;
    }
 
    public Usuario getUsuario() {
        if (usuario != null){
            return getFuncOrCli(usuario);
        }else {
            return usuario;
        }
    }
 
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Usuario getFuncOrCli(Usuario user) {
        if("ROLE_ADMIN".equals(user.getPermissoes())) {
            return (Funcionario) user;
        } else {
            return (Cliente) user;
        }
    }
}
