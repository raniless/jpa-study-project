package jpql;


import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
    private static EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //기본 문법과 쿼리 API
            //basicApiMethod();

            //프로젝션(SELECT)
            //projectionMethod();

            //페이징
            //pagingMethod();

            //조인
            //joinMethod();

            //JPQL 타입 표현식과 기타식
            expressionMethod();
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void basicApiMethod() {
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
    }

    private static void projectionMethod() {
        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        em.persist(member);
    }

    private static void pagingMethod() {
        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setUsername("member"+i);
            member.setAge(i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                                .setFirstResult(1)
                                .setMaxResults(10)
                                .getResultList();

        System.out.println("result.size = " + result.size());
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    private static void joinMethod() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

//        String query = "select m from Member m join m.team t";  //inner join
//        String query = "select m from Member m left join m.team t"; // outer join
//        String query = "select m from Member m, Team t where m.username = t.name";  //seta join
//        String query = "select m from Member m left join m.team t on t.name = 'teamA'";  //조인 대상 필터링
        String query = "select m from Member m left join Team t on m.username = t.name";  // 연관관계 없는 엔티티 외부 조인
        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();

        System.out.println("result.size = " + result.size());
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    private static void expressionMethod() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("member1");
        member.setAge(10);
        member.setType(MemberType.ADMIN);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        //ENUM을 조건절에 사용할 경우 package명 포함 해서 사용
        String query = "select m.username, 'HELLO', true From Member m"
                     + " where m.type = jpql.MemberType.USER";
        List<Object[]> result = em.createQuery(query)
                                    .getResultList();

        for (Object[] objects : result) {
            System.out.println("objects = " + objects[0]);
            System.out.println("objects = " + objects[1]);
            System.out.println("objects = " + objects[2]);
        }
    }
}