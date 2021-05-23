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
            //expressionMethod();

            //조건식
            //conditionalMethod();

            //JPQL 함수
            //jpqlFunctionMethod();

            //경로표현식
            //pathExprMethod();

            //패치 조인(fetch join)
            fetchJoinMethod();

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

    private static void conditionalMethod() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("관리자");
        member.setAge(10);
        member.setType(MemberType.ADMIN);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        //CASE(기본 CASE 식)
        String query = "select case when m.age <= 10 then '학생요금' "
                     + "            when m.age >= 60 then '경로요금' "
                     + "            else '일반요금' end "
                     + "  from Member m";
        List<String> result = em.createQuery(query, String.class)
                                .getResultList();

        for (String s : result) {
            System.out.println("s = " + s);
        }

        //COALESCE
        String query1 = "select coalesce(m.username, '이름 없는 회원') from Member m";
        List<String> result1 = em.createQuery(query1, String.class)
                                .getResultList();

        for (String s : result1) {
            System.out.println("s = " + s);
        }

        //NULLIF
        String query2 = "select nullif(m.username, '관리자') from Member m";
        List<String> result2 = em.createQuery(query2, String.class)
                .getResultList();

        for (String s : result2) {
            System.out.println("s = " + s);
        }
    }

    private static void jpqlFunctionMethod() {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("관리자");
        member.setAge(10);
        member.setType(MemberType.ADMIN);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

//        String concatQuery = "select 'a' || 'b' From Member m";
        String concatQuery = "select concat('a', 'b') From Member m";
        List<String> concatResult = em.createQuery(concatQuery, String.class)
                                .getResultList();

        for (String s : concatResult) {
            System.out.println("s = " + s);
        }

        String locateQuery = "select locate('de', 'abcdef') From Member m";
        List<Integer> locateResult = em.createQuery(locateQuery, Integer.class)
                .getResultList();
        for (Integer s : locateResult) {
            System.out.println("s = " + s);
        }

        //제대로 안된다?? 확인 필요
        String sizeQuery = "select size(t.members) From Team t";
        List<Integer> sizeResult = em.createQuery(sizeQuery, Integer.class)
                                    .getResultList();
        for (Integer integer : sizeResult) {
            System.out.println("integer = " + integer);
        }
    }

    private static void pathExprMethod() {

    }

    private static void fetchJoinMethod() {
        Team teamA = new Team();
        teamA.setName("팀A");
        em.persist(teamA);

        Team teamB = new Team();
        teamB.setName("팀B");
        em.persist(teamB);

        Member member1 = new Member();
        member1.setUsername("회원1");
        member1.setTeam(teamA);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("회원2");
        member2.setTeam(teamA);
        em.persist(member2);

        Member member3 = new Member();
        member3.setUsername("회원3");
        member3.setTeam(teamB);
        em.persist(member3);

        em.flush();
        em.clear();

//        String query = "select m from Member m";
//        String query = "select m from Member m join fetch m.team";
//        List<Member> result = em.createQuery(query, Member.class)
//                                .getResultList();
//
//        for (Member member : result) {
//            System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
//        }

        String query = "select distinct t from Team t join fetch t.members";
        List<Team> result = em.createQuery(query, Team.class)
                                .getResultList();

        for (Team team : result) {
            System.out.println("team = " + team.getName()+ ", members= " + team.getMembers().size());
        }
    }
}