package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity
//@SequenceGenerator(
//        name="MEMBER_SEQ_GENERATOR",
//        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
//        initialValue = 1,
//        allocationSize = 50)
//@TableGenerator(
//        name="MEMBER_TABLE_GENERATOR",
//        table="MY_SEQUENCES",
//        pkColumnName = "MEMBER_SEQ",
//        pkColumnValue = "MEMBER_SEQ",
//        allocationSize = 1)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_TABLE_GENERATOR")
    @Column(name = "MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Column(name = "USERNAME")
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}