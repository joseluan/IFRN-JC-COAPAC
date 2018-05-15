/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import br.com.ifrn.coapac.model.Material;
import br.com.ifrn.coapac.model.TipoMaterial;
import br.com.ifrn.coapac.model.TipoUsuario;
import br.com.ifrn.coapac.model.Usuario;
import br.com.ifrn.coapac.utils.AbstractController;
import br.com.ifrn.coapac.utils.PagingInformation;

/**
 *
 * @author Luan
 */
public class MaterialDAO extends AbstractController implements Serializable {

    public Material getById(int id) {
        EntityManager em = Database.getInstance().getEntityManager();
        Material m = em.find(Material.class, id);
        return m;
    }

    public int qtd() {
        EntityManager em = Database.getInstance().getEntityManager();
        Query query = em.createQuery("SELECT x.id FROM Material x");
        int quantidade = query.getResultList().size();
        return quantidade;
    }

    public void persist(Material material) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(material);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public void merge(Material material) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(material);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public void remove(int id) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Emprestimo x WHERE x.material.id = :id ");
            q.setParameter("id", id);
            q.executeUpdate();
            Material material = em.find(Material.class, id);
            em.remove(material);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public List<Material> buscarFiltro(Material material, PagingInformation paginacao) {
        List<Material> lista;
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            String jpql = "SELECT x FROM Material x WHERE x.nome LIKE :nome AND x.codigo LIKE :cod ";
            if (usuario_session.getTipo() == TipoUsuario.ALUNO) {
                jpql += " AND x.tipo != :tipo";
            }
            if (material.getTipo() != TipoMaterial.TODOS) {
                jpql += " AND x.tipo = :tipo_filter";
            }
            Query query = em.createQuery(jpql);

            query.setParameter("nome", "%" + material.getNome() + "%");
            query.setParameter("cod", "%" + material.getCodigo() + "%");
            if (usuario_session.getTipo() == TipoUsuario.ALUNO) {
                query.setParameter("tipo", TipoMaterial.ARMARIO);
            }
            if (material.getTipo() != TipoMaterial.TODOS) {
                query.setParameter("tipo_filter", material.getTipo());
            }

            if (paginacao != null) {
                String jpqlPag = jpql;
                int posOrder = jpqlPag.length();
                int posSelect = (jpqlPag.contains("SELECT")) ? jpqlPag.indexOf("FROM") : 0;

                Query qPaginacao = em.createQuery("SELECT count(*) "
                        + jpqlPag.substring(posSelect, posOrder));

                qPaginacao.setParameter("nome", "%" + material.getNome() + "%");
                qPaginacao.setParameter("cod", "%" + material.getCodigo() + "%");
                if (usuario_session.getTipo() == TipoUsuario.ALUNO) {
                    qPaginacao.setParameter("tipo", TipoMaterial.ARMARIO);
                }
                if (material.getTipo() != TipoMaterial.TODOS) {
                    qPaginacao.setParameter("tipo_filter", material.getTipo());
                }

                paginacao.setTotalRegistros((int) ((Long) qPaginacao.getSingleResult()).longValue());
                query.setFirstResult(paginacao.getPaginaAtual() * paginacao.getTamanhoPagina());
                query.setMaxResults(paginacao.getTamanhoPagina());
                lista = query.getResultList();
                return lista;
            }

            lista = query.getResultList();
            return lista;
        } catch (NumberFormatException | javax.persistence.NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Usuario getUsuario_session() {
        return usuario_session;
    }

    public void setUsuario_session(Usuario usuario_session) {
        this.usuario_session = usuario_session;
    }

}
