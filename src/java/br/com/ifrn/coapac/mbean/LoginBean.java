/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.mbean;

import br.com.ifrn.coapac.dao.UsuarioDAO;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import br.com.ifrn.coapac.model.Usuario;
import br.com.ifrn.coapac.utils.API;
import br.com.ifrn.coapac.utils.AbstractController;
import br.com.ifrn.coapac.utils.ValidatorUtil;

/**
 *
 * @author Luan
 */
@ManagedBean
public class LoginBean extends AbstractController implements Serializable{

    private Usuario usuario = new Usuario();
    private HashMap meus_dados;
    
    public String login() throws IOException {
        //EntityManager manager = this.getEntityManager();
        UsuarioDAO uDAO = new UsuarioDAO();
        boolean userInSUAP;
        boolean login = false;
        
        if (!validarLogin()) {
            addMsgError("Login ou senha não informados");
            return null;
        }
        
        //Buscando na API
        try {
            API api = new API(usuario.getMatricula(),
                              usuario.getSenha());
            meus_dados = api.buscarDados();
            userInSUAP = true;
        } catch (org.apache.http.client.ClientProtocolException e) {
            e.printStackTrace();
            userInSUAP = false;
        }catch (IOException e){
             e.printStackTrace();
             userInSUAP = false;
        }
        
        try {
            //proucura no banco de dados o usuário
            Usuario u_banco = uDAO.buscarPorLoginESenha(usuario.getMatricula(),
                                                        usuario.getSenha());
           
            if (!ValidatorUtil.isEmpty(u_banco) && userInSUAP) {
                //atualizando os dados
                u_banco.setSenha(usuario.getSenha());
                usuario = uDAO.merge(u_banco,meus_dados);
                login = true;
            }else if(userInSUAP){
                //cadastrando usuario
                usuario = uDAO.persist(usuario,meus_dados);
                login = true;
            }else if(!ValidatorUtil.isEmpty(u_banco)){
                //se estiver sem internet
                login = true;
                usuario = u_banco;
            }
            
            if (login) {
                getCurrentSession().setAttribute("usuario", usuario);
                getCurrentSession().setAttribute("acesso", usuario.getAcesso().toString());

                switch (usuario.getAcesso()) {
                    case ALUNO:
                        return "/aluno/inicio";
                    case BOLSISTA:
                        return "/bolsista/inicio";
                    case SERVIDOR:
                        return "/servidor/inicio";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMsgError("Login ou senha incorretos!");
            return "/publico/login";
        }
        addMsgError("Login ou senha incorretos!2");
        return "/publico/login";
    }

    public String logoff() {
        getCurrentSession().removeAttribute("usuario");
        getCurrentSession().removeAttribute("acesso");
        return "/publico/login";
    }
    
    public boolean validarLogin(){
        return !ValidatorUtil.isEmpty(usuario.getMatricula()) &&
                !ValidatorUtil.isEmpty(usuario.getSenha());
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
