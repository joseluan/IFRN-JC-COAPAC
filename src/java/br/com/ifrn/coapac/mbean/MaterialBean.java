/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.mbean;

import br.com.ifrn.coapac.dao.EmprestimoDAO;
import br.com.ifrn.coapac.dao.MaterialDAO;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import br.com.ifrn.coapac.model.Material;
import org.primefaces.context.RequestContext;
import br.com.ifrn.coapac.utils.AbstractController;
import br.com.ifrn.coapac.utils.PagingInformation;
import br.com.ifrn.coapac.utils.ValidatorUtil;

/**
 *
 * @author Luan
 */
@ManagedBean
@ViewScoped
public class MaterialBean extends AbstractController implements Serializable{

    private Material material = new Material();
    private List<Material> materiais;
    private Material materialSelecionado = new Material();
    /**
     * Quantidade de c�digos a serem exibidos por p�gina.
     */
    private static final int QTD_CODIGOS = 10;
    /**
     * Armazena as op��es de pagina��o de consulta a c�digos.
     */
    public PagingInformation paginacao;

    @PostConstruct
    public void init() {
        paginacao = new PagingInformation(0, QTD_CODIGOS);
    }
    
    public int getQtdMaterial() {
        MaterialDAO mDAO = new MaterialDAO();
        int quantidade = mDAO.qtd();
        return quantidade;
    }

    public String listaFiltro() {
        MaterialDAO mDAO = new MaterialDAO();
        if (ValidatorUtil.isNotEmpty(materiais)) {
            if (materiais.size() == 1) {
                 paginacao.setPaginaAtual(0);
            }
        }
        materiais = mDAO.buscarFiltro(material,paginacao);
        return null;
    }
    
    public String remover(Material material_rm) {
        MaterialDAO mDAO = new MaterialDAO();
        mDAO.remove(material_rm.getId());
        listaFiltro();
        return null;
    }

    public String adicionar() throws InterruptedException {
        MaterialDAO mDAO = new MaterialDAO();
        mDAO.persist(material);
        material = new Material();
        return null;
    }
    
    public String adicionarEmprestimo() throws InterruptedException {
        EmprestimoDAO eDAO = new EmprestimoDAO();
        eDAO.persist(materialSelecionado);
        listaFiltro();
        return null;
    }

    public String atualizar() {
        MaterialDAO mDAO = new MaterialDAO();
        mDAO.merge(materialSelecionado);
        listaFiltro();
        return null;
    }
    
    public String selecionar(Material selecionado) {
        this.materialSelecionado = selecionado;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('ModalEditar').show();");
        return null;
    }
   

    /**
     * M�todo chamado para redirecionar para a pr�xima p�gina da pagina��o,
     * referente � listagem de publica��es.
     *
     * @return
    */
    public String next() {
        paginacao.nextPage(null);
        listaFiltro();
        return null;
    }

    /**
     * M�todo chamado para redirecionar para a p�gina anterior da pagina��o,
     * referente � listagem de publica��es.
     * @return 
    */
    public String previous() {
        paginacao.previousPage(null);
        listaFiltro();
        return null;
    }
    
    /**
     * Mude a página atual da paginação de acordo com o parâmetro informado,
     * referente à listagem de publicações.
     *
     * @return String
     */
    public String changePage() {
        int pagina = getParameterInt("pagina");
        paginacao.setPaginaAtual(pagina);
        listaFiltro();
        return null;
    }
    
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<Material> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<Material> materiais) {
        this.materiais = materiais;
    }

    public Material getMaterialSelecionado() {
        return materialSelecionado;
    }

    public void setMaterialSelecionado(Material materialSelecionado) {
        this.materialSelecionado = materialSelecionado;
    }

    public PagingInformation getPaginacao() {
        return paginacao;
    }

    public void setPaginacao(PagingInformation paginacao) {
        this.paginacao = paginacao;
    }
        
}
