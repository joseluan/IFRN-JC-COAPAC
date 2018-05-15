package br.com.ifrn.coapac.utils;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import br.com.ifrn.coapac.model.Usuario;

/**
 * Classe que implementa métodos comuns a todos os controladores do sistema.
 * Portanto, deve ser estendido por eles.
 *
 * @author Renan
 */
@SuppressWarnings("serial")
public class AbstractController implements Serializable {
    
    public Usuario usuario_session = (Usuario) getCurrentSession().getAttribute("usuario");

    protected void addMsgInfo(String msg) {
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

    protected void addMsgWarning(String msg) {
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

    protected void addMsgError(String msg) {
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

    private static String getStackTrace(Throwable t) {
        String msg = t.toString() + "<br/>";

        if (t.getStackTrace() != null) {
            for (StackTraceElement s : t.getStackTrace()) {
                msg += s.toString() + "<br/>";
            }
        }

        if (t.getCause() != null) {
            msg += "Caused by: " + t.getCause().toString() + "<br/>";

            if (t.getCause().getStackTrace() != null) {
                for (StackTraceElement s : t.getCause().getStackTrace()) {
                    msg += s.toString() + "<br/>";
                }
            }
        }

        return msg;
    }

    /**
     * Possibilita o acesso ao HttpServletRequest.
     * @return HttpServletRequest
     */
    public HttpServletRequest getCurrentRequest() {
        return (HttpServletRequest) getExternalContext().getRequest();
    }

    /**
     * Possibilita o acesso ao HttpServletResponse.
     * @return HttpServletResponse
     */
    public HttpServletResponse getCurrentResponse() {
        return (HttpServletResponse) getExternalContext().getResponse();
    }

    /**
     * Possibilita o acesso ao HttpSession.
     * @return HttpSession
     */
    public HttpSession getCurrentSession() {
        return getCurrentRequest().getSession(true);
    }

    /**
     * Retorna um managed-bean existente no container do JavaServer Faces.
     */
//	@SuppressWarnings("unchecked")
//	public static <T> T getMBean(String mbeanName) {
//		FacesContext fc = FacesContext.getCurrentInstance();
//		return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
//	}
    /**
     * Acessa o external context do JavaServer Faces
	 *
     */
    private ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public String getParameter(String param) {
        return getCurrentRequest().getParameter(param);
    }

    public Integer getParameterInt(String param) {
        return Integer.parseInt(getParameter(param));
    }

    public Integer getParameterInt(String param, int padrao) {
        String valor = getParameter(param);
        return valor != null ? Integer.parseInt(valor) : padrao;
    }

    /**
     * M�todo utilizado para fazer download de arquivos.
     */
//	public String baixarArquivo(Integer idArquivo) throws ArqException {
//		EnvioArquivoUtils arq = new EnvioArquivoUtils();
//		
//		try {    	
//        	arq.conectar();
//			arq.recuperaArquivo(getCurrentResponse(), idArquivo, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            addMsgError("Erro ao recuperar o arquivo!");
//        } finally {
//        	arq.desconectar();
//        }
//		
//        FacesContext.getCurrentInstance().responseComplete();
//        
//		return null;
//	}
    /**
     * M�todo utilizado para fazer download de arquivos.
     *
     * @throws IOException
     */
//	public String baixarArquivo(UploadedFile arquivo) throws ArqException, IOException {
//        
//		byte[] bytes = arquivo.getContents();
//		String nome = arquivo.getFileName();
//		
//		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"" + nome + "\"");
//		getCurrentResponse().getOutputStream().write(bytes);
//		
//		FacesContext.getCurrentInstance().responseComplete();
//        
//		return null;
//	}
}