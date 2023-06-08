package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

/**
 * @Authwored MockMvc 가 선언되지 않는 문제 개선: JUnit4에서 지원하나봄.. 버전 충돌의 문제
 *  1) @RunWith(SpringJUnit4ClassRunner.class) 추가
 */
@SpringBootTest()
@AutoConfigureMockMvc //MockMvc 테스트를 위해 @AutoConfigureMockMvc 어노테이션 선언
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberControllerTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password) {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("딩코");
        memberFormDto.setAddress("서울시 마포구 신사동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        String email = "dingko@email.com";
        String password = "1234";
        this.createMember(email, password);

        mockMvc.perform(
                formLogin().userParameter("email") //username을 email로 인식하도록 바꿔줌
                .loginProcessingUrl("/members/login") //로그인 프로세스 url (로그인 실행 시 동작하는 url)
                .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception {
        String email = "dingko@email.com";
        String password = "1234";
        this.createMember(email, password);
        mockMvc.perform(
                formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password("12345"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}