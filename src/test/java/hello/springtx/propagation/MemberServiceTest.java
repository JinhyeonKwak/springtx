package hello.springtx.propagation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * MemberService : @Transactional : OFF
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON
     */
    @Test
    void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * MemberService : @Transactional : OFF
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON
     */
    @Test
    void outerTxOff_fail() {
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * MemberService : @Transactional : ON
     * MemberRepository : @Transactional : OFF
     * LogRepository : @Transactional : OFF
     */
    @Test
    void singleTx() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * MemberService : @Transactional : ON
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON
     */
    @Test
    void outerTxOn() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * MemberService : @Transactional : ON
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON Exception
     */
    @Test
    void outerTxOn_fail() {
        String username = "로그예외_outerTxOff_success";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * MemberService : @Transactional : ON
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON Exception
     */
    @Test
    void recoverException_fail() {
        String username = "로그예외_outerTxOff_success";

        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * MemberService : @Transactional : ON
     * MemberRepository : @Transactional : ON
     * LogRepository : @Transactional : ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        String username = "로그예외_outerTxOff_success";

        memberService.joinV2(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }

}