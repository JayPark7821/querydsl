package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member member1 = memberJpaRepository.findById(member.getId()).get();
        assertThat(member).isEqualTo(member1);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).containsExactly(member);

        List<Member> member11 = memberJpaRepository.findByUsername("member1");
        assertThat(member11).containsExactly(member);
    }

    @Test
    void basicQuerydslTest () throws Exception {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member member1 = memberJpaRepository.findById(member.getId()).get();
        assertThat(member).isEqualTo(member1);

        List<Member> all = memberJpaRepository.findAll_Querydsl();
        assertThat(all).containsExactly(member);

        List<Member> member11 = memberJpaRepository.findByUsername_Querydsl("member1");
        assertThat(member11).containsExactly(member);
    }

    @Test
    void searchTest () throws Exception {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository
                .searchBuilder(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");

    }

}