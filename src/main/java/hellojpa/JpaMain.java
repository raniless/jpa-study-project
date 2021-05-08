package hellojpa;

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
            Member member = new Member();
            member.setUserName("A");
            Member member2 = new Member();
            member.setUserName("B");
            Member member3 = new Member();
            member.setUserName("C");

            System.out.println("==========================");
            em.persist(member);     //1, 51
            em.persist(member2);    //MEM 에서 호출
            em.persist(member3);    //MEM 에서 호출

            System.out.println("member.id = " + member.getId());
            System.out.println("member2.id = " + member2.getId());
            System.out.println("member3.id = " + member3.getId());
            System.out.println("==========================");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}