package br.com.ifrn.coapac.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

/**
 * Singleton que controla a comunicação com o banco de dados, inclusive o
 * gerenciamento do EntityManager.
 *
 * @author Renan
 */
public class Database {

    private static final Database SINGLETON = new Database();
    protected static EntityManager em;

    static {
        criarEM();
    }

    protected static void criarEM() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("COAPAC-PU");
        em = emf.createEntityManager();
    }

    public static Database getInstance() {
        return SINGLETON;
    }

    public EntityManager getEntityManager() {
        if (!em.isOpen()) {
            criarEM();
        }
        return em;
    }

    public Session getSession() {
        return (Session) em.getDelegate();
    }

}
