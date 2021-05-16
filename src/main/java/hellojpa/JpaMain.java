package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.*;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        PersistenceUnitUtil puu = emf.getPersistenceUnitUtil();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            /* Chapter 8 */
            //8-1 프록시
            //proxyMethod(puu, em);
            //8-2 즉시 로딩과 지연 로딩
            lazyAndEagerLoadingMethod(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void proxyMethod(PersistenceUnitUtil puu, EntityManager em) {
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
        System.out.println("refMember isLoaded : " + puu.isLoaded(refMember));

//            refMember.getUserName();
        Hibernate.initialize(refMember);    //강제 초기화

        System.out.println("refMember isLoaded : " + puu.isLoaded(refMember));
        System.out.println("Proxy Class Name : " + refMember.getClass().getName());
    }

    private static void lazyAndEagerLoadingMethod(EntityManager em) {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member1 = new Member();
        member1.setUserName("member1");
        member1.setTeam(team);
        em.persist(member1);

        em.flush();
        em.clear();

        Member m = em.find(Member.class, member1.getId());

        //Proxy 객체
        System.out.println("m = " + m.getTeam().getClass());

        System.out.println("============================");
        //실제 사용하는 시점에 초기화(DB 조회)
        m.getTeam().getName();
        System.out.println("============================");
    }
}