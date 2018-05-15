/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import br.com.ifrn.coapac.model.Copia;
import br.com.ifrn.coapac.model.Limite;
import br.com.ifrn.coapac.model.Usuario;
import br.com.ifrn.coapac.utils.AbstractController;
import br.com.ifrn.coapac.utils.PagingInformation;
import br.com.ifrn.coapac.utils.ValidatorUtil;

/**
 *
 * @author Luan
 */
public class CopiaDAO extends AbstractController implements Serializable{

    public Copia getById(int id) {
        EntityManager em = Database.getInstance().getEntityManager();
        Copia c = em.find(Copia.class, id);
        return c;
    }

    public List<Copia> minhaLista(Usuario usuario, int max) {
        EntityManager em = Database.getInstance().getEntityManager();
        Query query = em.createQuery("SELECT x FROM Copia x WHERE x.usuario.id = :id ORDER BY x.data_copia");
        query.setMaxResults(20);
        if(max != 0)
            query.setMaxResults(max);

        if (ValidatorUtil.isEmpty(usuario)) {
            query.setParameter("id", usuario_session.getId());
        } else {
            query.setParameter("id", usuario.getId());
        }

        List<Copia> lista = query.getResultList();
        return lista;
    }

    public void persist(Copia copia) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            copia.setData_copia(new Date());
            Usuario usuario = copia.getUsuario();
            usuario.setQuantidade_copia(usuario.getQuantidade_copia() - copia.getQuantidade());
            if (usuario_session.getId() == copia.getUsuario().getId()) {
                //--- Mudando a Session
                getCurrentSession().removeAttribute("usuario");
                getCurrentSession().setAttribute("usuario", usuario);
            }
            
            em.merge(usuario);
            em.persist(copia);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public void mergeLimite(Limite limite) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(limite);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public List<Limite> listaLimite() {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            Query query = em.createQuery("SELECT x FROM Limite x WHERE x.id = 1 OR x.id =2");
            List<Limite> lista = query.getResultList();
            return lista;
        } catch (NumberFormatException | javax.persistence.NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Limite getLimite(int id) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            Query query = em.createQuery("SELECT x FROM Limite x WHERE x.id = :id");
            query.setParameter("id", id);
            Limite l = (Limite) query.getSingleResult();
            return l;
        } catch (NumberFormatException | javax.persistence.NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void remove(Copia copia_rm) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Copia copia = em.find(Copia.class, copia_rm.getId());
            Usuario usuario = copia.getUsuario();
            usuario.setQuantidade_copia(usuario.getQuantidade_copia() + copia.getQuantidade());
            //--- Mudando a Session
            usuario_session.setQuantidade_copia(usuario_session.getQuantidade_copia()
                    + copia.getQuantidade());
            getCurrentSession().removeAttribute("usuario");
            getCurrentSession().setAttribute("usuario", usuario_session);

            em.merge(usuario);
            em.remove(copia);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } 
    }

    public List<Copia> buscarFiltro(Usuario usuario, PagingInformation paginacao) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            List<Copia> lista;
            String jpql = "SELECT x FROM Copia x"
                    + " WHERE x.usuario.matricula LIKE :matricula AND"
                    + " x.usuario.nome LIKE :nome ORDER BY x.data_copia DESC";
            Query query = em.createQuery(jpql);
            query.setParameter("matricula", "%" + usuario.getMatricula() + "%");
            query.setParameter("nome", "%" + usuario.getNome() + "%");

            if (paginacao != null) {
                String jpqlPag = jpql;
                int posOrder = jpqlPag.length();
                int posSelect = (jpqlPag.contains("SELECT")) ? jpqlPag.indexOf("FROM") : 0;

                String jpqlPaginacao = "SELECT count(*) " + jpqlPag.substring(posSelect, posOrder);

                Query qPaginacao = em.createQuery(jpqlPaginacao);
                qPaginacao.setParameter("matricula", "%" + usuario.getMatricula() + "%");
                qPaginacao.setParameter("nome", "%" + usuario.getNome() + "%");

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

    public List<Copia> buscarFiltro(Usuario usuario, PagingInformation paginacao, Date inicio, Date fim) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            List<Copia> lista;
            String jpql = "SELECT x FROM Copia x"
                    + " WHERE x.usuario.matricula LIKE :matricula AND"
                    + " x.usuario.nome LIKE :nome AND"
                    + " x.data_copia BETWEEN :inicio AND :fim ORDER BY x.data_copia DESC";
            Query query = em.createQuery(jpql);
            query.setParameter("matricula", "%" + usuario.getMatricula() + "%");
            query.setParameter("nome", "%" + usuario.getNome() + "%");
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);

            if (paginacao != null) {
                String jpqlPag = jpql;
                int posOrder = jpqlPag.length();
                int posSelect = (jpqlPag.contains("SELECT")) ? jpqlPag.indexOf("FROM") : 0;

                String jpqlPaginacao = "SELECT count(*) " + jpqlPag.substring(posSelect, posOrder);

                Query qPaginacao = em.createQuery(jpqlPaginacao);
                qPaginacao.setParameter("matricula", "%" + usuario.getMatricula() + "%");
                qPaginacao.setParameter("nome", "%" + usuario.getNome() + "%");
                qPaginacao.setParameter("inicio", inicio);
                qPaginacao.setParameter("fim", fim);

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
