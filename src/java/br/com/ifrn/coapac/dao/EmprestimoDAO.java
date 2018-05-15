/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import br.com.ifrn.coapac.model.Emprestimo;
import br.com.ifrn.coapac.model.Material;
import br.com.ifrn.coapac.model.TipoSituacao;
import static br.com.ifrn.coapac.model.TipoSituacao.AGUARDANDO;
import br.com.ifrn.coapac.model.TipoSolicitacao;
import static br.com.ifrn.coapac.model.TipoSolicitacao.PENDENTE;
import br.com.ifrn.coapac.model.Usuario;
import br.com.ifrn.coapac.utils.AbstractController;
import br.com.ifrn.coapac.utils.PagingInformation;
import br.com.ifrn.coapac.utils.ValidatorUtil;

/**
 *
 * @author Luan
 */
public class EmprestimoDAO extends AbstractController implements Serializable{

    public Emprestimo getById(int id) {
        EntityManager em = Database.getInstance().getEntityManager();
        Emprestimo e = em.find(Emprestimo.class, id);
        return e;
    }

    public List<Emprestimo> minhaLista(Usuario usuario, int limite) {
        EntityManager em = Database.getInstance().getEntityManager();
        Query query = em.createQuery("SELECT x FROM Emprestimo x WHERE x.usuario_sol.id = :id");
        if (limite != 0) {
            query.setMaxResults(limite);
        }

        if (ValidatorUtil.isEmpty(usuario)) {
            query.setParameter("id", usuario_session.getId());
        } else {
            query.setParameter("id", usuario.getId());
        }

        List<Emprestimo> lista = query.getResultList();
        return lista;
    }

    public void persist(Material material_ps) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            Emprestimo emp = new Emprestimo();
            emp.setMaterial(material_ps);
            emp.setUsuario_sol(usuario_session);
            emp.setSituacao(TipoSituacao.AGUARDANDO);
            emp.setSolicitacao(TipoSolicitacao.PENDENTE);
            em.persist(emp);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
    }

    public void merge(Emprestimo emprestimo_mg) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(emprestimo_mg);
            em.merge(emprestimo_mg.getMaterial());
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
            Emprestimo emprestimo = em.find(Emprestimo.class, id);

            em.remove(emprestimo);
            if (emprestimo.getSolicitacao() == TipoSolicitacao.DEFERIDO
                    && emprestimo.getSituacao() == TipoSituacao.EMPRESTADO) {
                Material m = emprestimo.getMaterial();
                m.setQuantidade(m.getQuantidade() + 1);
                em.merge(m);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();;
            ex.printStackTrace();
        }
    }

    public List<Emprestimo> buscarFiltro(Emprestimo emp, PagingInformation paginacao) {
        List<Emprestimo> lista;
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            String jpql = "SELECT x FROM Emprestimo x"
                    + " WHERE x.material.nome LIKE :nome AND x.material.codigo LIKE :codigo"
                    + " AND x.usuario_sol.matricula LIKE :matricula ";

            if (emp.getSituacao() != TipoSituacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                jpql += " AND x.situacao = :situacao";
            }
            if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                jpql += " AND x.solicitacao = :solicitacao";
            }

            jpql += " ORDER BY x.data";

            Query query = em.createQuery(jpql);
            query.setParameter("nome", "%" + emp.getMaterial().getNome() + "%");
            query.setParameter("codigo", "%" + emp.getMaterial().getCodigo() + "%");
            query.setParameter("matricula", "%" + emp.getUsuario_sol().getMatricula() + "%");

            if (emp.getSituacao() != TipoSituacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                query.setParameter("situacao", emp.getSituacao());
            }
            if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                query.setParameter("solicitacao", emp.getSolicitacao());
            }

            if (paginacao != null) {
                String jpqlPag = jpql;
                int posOrder = jpqlPag.length();
                int posSelect = (jpqlPag.contains("SELECT")) ? jpqlPag.indexOf("FROM") : 0;

                String jpqlPaginacao = "SELECT count(*) " + jpqlPag.substring(posSelect, posOrder);

                Query qPaginacao = em.createQuery(jpqlPaginacao);
                qPaginacao.setParameter("nome", "%" + emp.getMaterial().getNome() + "%");
                qPaginacao.setParameter("codigo", "%" + emp.getMaterial().getCodigo() + "%");
                qPaginacao.setParameter("matricula", "%" + emp.getUsuario_sol().getMatricula() + "%");

                if (emp.getSituacao() != TipoSituacao.TODOS
                        && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                    qPaginacao.setParameter("situacao", emp.getSituacao());
                }
                if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                        && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                    qPaginacao.setParameter("solicitacao", emp.getSolicitacao());
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

    public List<Emprestimo> buscarSolicitacoes(int max) {
        EntityManager em = Database.getInstance().getEntityManager();
        List<Emprestimo> lista = null;
        try {
            Query query = em.createQuery("SELECT x FROM Emprestimo x "
                    + "WHERE x.usuario_sol.matricula LIKE :matricula AND"
                    + " x.situacao = :situacao AND x.solicitacao = :solicitacao");
            query.setParameter("matricula", "%" + usuario_session.getMatricula() + "%");
            query.setParameter("situacao", AGUARDANDO);
            query.setParameter("solicitacao", PENDENTE);
            query.setMaxResults(5);
            if (max != 0)
                query.setMaxResults(max);
            lista = query.getResultList();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Material> buscarQuantidadeSolicitacoesMaterial(int limite) {
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            Query query = em.createQuery("SELECT x.material, COUNT(x.id) FROM Emprestimo x WHERE x.solicitacao = :sol GROUP BY x.material");
            query.setParameter("sol", TipoSolicitacao.DEFERIDO);
            query.setMaxResults(limite);

            List<Object[]> materiais = query.getResultList();
            List<Material> resultado = new ArrayList();

            if (materiais != null) {
                materiais.stream().map((obj) -> {
                    Material m = (Material) obj[0];
                    m.setQtdEmprestimos((Integer) obj[1]);
                    return m;
                }).forEachOrdered((m) -> {
                    resultado.add(m);
                });
            }
            return resultado;
        } catch (NumberFormatException | javax.persistence.NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Emprestimo> buscarInadimplente() {
        EntityManager em = Database.getInstance().getEntityManager();
        Query query = em.createQuery("SELECT x FROM Emprestimo x"
                + " WHERE x.usuario_sol.id = :id AND x.data < :inicio"
                + " AND x.situacao = :sit ORDER BY x.data DESC");
        query.setParameter("id", usuario_session.getId());
        query.setParameter("sit", TipoSituacao.EMPRESTADO);
        query.setParameter("inicio", new Date(), TemporalType.DATE);
        query.setMaxResults(5);
        List<Emprestimo> lista = query.getResultList();
        return lista;
    }

    public List<Emprestimo> buscarInadimplente(Usuario usuario) {
        EntityManager em = Database.getInstance().getEntityManager();
        Query query = em.createQuery("SELECT x FROM Emprestimo x "
                + "WHERE x.usuario_sol.id = :id AND x.data < :inicio AND"
                + " x.situacao = :sit ORDER BY x.data DESC");
        query.setParameter("id", usuario.getId());
        query.setParameter("sit", TipoSituacao.EMPRESTADO);
        query.setParameter("inicio", new Date(), TemporalType.DATE);
        query.setMaxResults(1);
        List<Emprestimo> lista = query.getResultList();
        return lista;
    }

    public List<Emprestimo> buscarFiltro(Emprestimo emp, PagingInformation paginacao, Date inicio, Date fim) {
        List<Emprestimo> lista;
        EntityManager em = Database.getInstance().getEntityManager();
        try {
            String jpql = "SELECT x FROM Emprestimo x"
                    + " WHERE x.material.nome LIKE :nome AND x.material.codigo LIKE :codigo"
                    + " AND x.usuario_sol.matricula LIKE :matricula"
                    + " AND x.data BETWEEN :inicio AND :fim";

            if (emp.getSituacao() != TipoSituacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                jpql += " AND x.situacao = :situacao";
            }
            if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                jpql += " AND x.solicitacao = :solicitacao";
            }

            jpql += " ORDER BY x.data";

            Query query = em.createQuery(jpql);
            query.setParameter("nome", "%" + emp.getMaterial().getNome() + "%");
            query.setParameter("codigo", "%" + emp.getMaterial().getCodigo() + "%");
            query.setParameter("matricula", "%" + emp.getUsuario_sol().getMatricula() + "%");
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);

            if (emp.getSituacao() != TipoSituacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                query.setParameter("situacao", emp.getSituacao());
            }
            if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                    && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                query.setParameter("solicitacao", emp.getSolicitacao());
            }

            if (paginacao != null) {
                String jpqlPag = jpql;
                int posOrder = jpqlPag.length();
                int posSelect = (jpqlPag.contains("SELECT")) ? jpqlPag.indexOf("FROM") : 0;

                String jpqlPaginacao = "SELECT count(*) " + jpqlPag.substring(posSelect, posOrder);

                Query qPaginacao = em.createQuery(jpqlPaginacao);
                qPaginacao.setParameter("nome", "%" + emp.getMaterial().getNome() + "%");
                qPaginacao.setParameter("codigo", "%" + emp.getMaterial().getCodigo() + "%");
                qPaginacao.setParameter("matricula", "%" + emp.getUsuario_sol().getMatricula() + "%");
                qPaginacao.setParameter("inicio", inicio);
                qPaginacao.setParameter("fim", fim);

                if (emp.getSituacao() != TipoSituacao.TODOS
                        && ValidatorUtil.isNotEmpty(emp.getSituacao())) {
                    qPaginacao.setParameter("situacao", emp.getSituacao());
                }
                if (emp.getSolicitacao() != TipoSolicitacao.TODOS
                        && ValidatorUtil.isNotEmpty(emp.getSolicitacao())) {
                    qPaginacao.setParameter("solicitacao", emp.getSolicitacao());
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
