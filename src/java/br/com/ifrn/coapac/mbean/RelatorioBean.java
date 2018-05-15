/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.mbean;

import br.com.ifrn.coapac.dao.UsuarioDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import br.com.ifrn.coapac.model.Copia;
import br.com.ifrn.coapac.model.Emprestimo;
import br.com.ifrn.coapac.model.TipoUsuario;
import br.com.ifrn.coapac.model.Usuario;

/**
 *
 * @author Luan
 */
@ManagedBean
@RequestScoped
public class RelatorioBean implements Serializable{
    private Usuario usuario;
    private Emprestimo emprestimo;
    private List<Emprestimo> emprestimos;
    private List<Copia> copias;
    private UsuarioDAO dao = new UsuarioDAO();
    private final String BOLSISTA = "bolsista";
    private final String SERVIDOR = "servidor";
    
    public String selecionar_usuarioNC(Usuario user) {
        usuario = user;
        if (dao.usuario_session.getAcesso() == TipoUsuario.SERVIDOR) {
            return "/"+SERVIDOR+"/nada_consta";
        }else if(dao.usuario_session.getAcesso() == TipoUsuario.BOLSISTA){
            return "/"+BOLSISTA+"/nada_consta";
        }
        return null;
    }
    
    public String selecionar_emprestimo(Emprestimo emp) {
        emprestimo = emp;
        if (dao.usuario_session.getAcesso() == TipoUsuario.SERVIDOR) {
            return "/"+SERVIDOR+"/detalhe_emprestimo";
        }else if(dao.usuario_session.getAcesso() == TipoUsuario.BOLSISTA){
            return "/"+BOLSISTA+"/detalhe_emprestimo";
        }
        return null;
    }
       
    public String selecionar_emprestimos(ArrayList<Emprestimo> emps) {
        emprestimos = emps;
        if (dao.usuario_session.getAcesso() == TipoUsuario.SERVIDOR) {
            return "/"+SERVIDOR+"/imprimir_relatorio_emp";
        }else if(dao.usuario_session.getAcesso() == TipoUsuario.BOLSISTA){
            return "/"+BOLSISTA+"/imprimir_relatorio_emp";
        }
        return null;
    }
    
    public String selecionar_copias(ArrayList<Copia> cps) {
        copias = cps;
        if (dao.usuario_session.getAcesso() == TipoUsuario.SERVIDOR) {
            return "/"+SERVIDOR+"/imprimir_relatorio_cop";
        }else if(dao.usuario_session.getAcesso() == TipoUsuario.BOLSISTA){
            return "/"+BOLSISTA+"/imprimir_relatorio_cop";
        }
        return null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public List<Copia> getCopias() {
        return copias;
    }

    public void setCopias(List<Copia> copias) {
        this.copias = copias;
    }
    
}
