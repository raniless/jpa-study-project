package jpql;


import javax.persistence.*;

public class JpqlMain {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
    private static EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

//            TypedQuery<Member> query = em.createQuery("select m from Member m where id=10L", Member.class);
//            Member result = query.getSingleResult();
//            System.out.println("result = " + result);
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//            Query query3 = em.createQuery("select m.username, m.age from Member m");

            Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
                                .setParameter("username", "member1")
                                .getSingleResult();
            System.out.println("singleResult.getUsername() = " + result.getUsername());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}