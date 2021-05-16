package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUserName("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            /*
            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember.getClass());

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass());

            System.out.println("refMember == findMember: " + (refMember == findMember));
            */

            /*
            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass());

            Member findMember = em.find(Member.class, member1.getId());
            System.out.println("findMember = " + findMember.getClass());

            System.out.println("refMember == findMember: " + (refMember == findMember));
            */

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember isLoaded : " + emf.getPersistenceUnitUtil().isLoaded(refMember));

//            refMember.getUserName();
            Hibernate.initialize(refMember);    //강제 초기화

            System.out.println("refMember isLoaded : " + emf.getPersistenceUnitUtil().isLoaded(refMember));
            System.out.println("Proxy Class Name : " + refMember.getClass().getName());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}