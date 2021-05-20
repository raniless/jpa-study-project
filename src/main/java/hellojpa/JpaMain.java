package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    private static EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //8-1 프록시
            //proxyMethod(puu, em);
            //8-2 즉시 로딩과 지연 로딩
            //lazyAndEagerLoadingMethod();
            //8-3 영속성전이(CASCADE)
            //cascadeAndOrphanMethod();

            //9-2 임베디드 타입
            //embeddedMethod();
            //9-3 값 타입과 불변 객체
            //valueTypeAndImmutableMethod();
            //9-5 값 타입 컬렉션
            //valueTypeCollectionMethod();

            //10-1 객체지향쿼리 소개
            objectOrientedQueryMethod();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void proxyMethod() {
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

        PersistenceUnitUtil persistenceUnitUtil = emf.getPersistenceUnitUtil();
        Member refMember = em.getReference(Member.class, member1.getId());
        System.out.println("refMember isLoaded : " + persistenceUnitUtil.isLoaded(refMember));

//        refMember.getUserName();
        Hibernate.initialize(refMember);    //강제 초기화

        System.out.println("refMember isLoaded : " + persistenceUnitUtil.isLoaded(refMember));
        System.out.println("Proxy Class Name : " + refMember.getClass().getName());
    }

    private static void lazyAndEagerLoadingMethod() {
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

    private static void cascadeAndOrphanMethod() {
        Child child1 = new Child();
        child1.setName("child1");
        Child child2 = new Child();
        child2.setName("child2");

        Parent parent = new Parent();
        parent.addChild(child1);
        parent.addChild(child2);

        em.persist(parent);

        em.flush();
        em.clear();

        //고아객체
        Parent findParent = em.find(Parent.class, parent.getId());
        findParent.getChildList().remove(0);    //부모엔티티와 연관관계가 끊어짐 -> DELETE문 수행
    }

    private static void embeddedMethod() {
        Member2 member = new Member2();
        member.setUserName("Hyoks");
        member.setHomeAddress(new Address("city", "street", "zipcode"));
        member.setPeriod(new Period());
        em.persist(member);
    }

    private static void valueTypeAndImmutableMethod() {
        Address address = new Address("city", "street", "10000");

        Member2 member = new Member2();
        member.setUserName("member1");
        member.setHomeAddress(address);
        em.persist(member);

        Member2 member2 = new Member2();
        member2.setUserName("member2");
        member2.setHomeAddress(address);
        em.persist(member2);

        //임베디드 값 타입 공유 시 Side Effect 발생 -> 복사해서 사용해야 함
        member.getHomeAddress().setCity("newCity");
    }

    private static void valueTypeCollectionMethod() {
        Member member = new Member();
        member.setUserName("member1");
        member.setHomeAddress(new Address("homeCity", "street", "10000"));
        
        member.getFavoriteFoods().add("치킨");
        member.getFavoriteFoods().add("족발");
        member.getFavoriteFoods().add("피자");

        member.getAddressHistory().add(new AddressEntity("old1", "streeet", "10000"));
        member.getAddressHistory().add(new AddressEntity("old2", "streeet", "10000"));

        em.persist(member);

        em.flush();
        em.clear();

        System.out.println("=============== START ==============");
        Member findMember = em.find(Member.class, member.getId());

        //homeCity -> newCity
        Address a = findMember.getHomeAddress();
        findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

        //치킨 -> 한식
        findMember.getFavoriteFoods().remove("치킨");
        findMember.getFavoriteFoods().add("한식");

        findMember.getAddressHistory().remove(new AddressEntity("old1", "streeet", "10000"));
        findMember.getAddressHistory().add(new AddressEntity("newCity1", "streeet", "10000"));
    }

    private static void objectOrientedQueryMethod() {
        //JPQL
        String jpql = "select m From Member m where m.username like '%hello%'";
        List<Member> result = em.createQuery(jpql, Member.class)
                .getResultList();

        for(Member member : result) {
            System.out.println(member);
        }

        //Criteria
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        Root<Member> m = query.from(Member.class);

        CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
        List<Member> resultList = em.createQuery(cq).getResultList();
    }
}