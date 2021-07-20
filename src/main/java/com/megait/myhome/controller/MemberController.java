package com.megait.myhome.controller;


import com.megait.myhome.domain.Member;
import com.megait.myhome.form.SignupForm;
import com.megait.myhome.repository.MemberRepository;
import com.megait.myhome.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;


    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute(/*"signupForm",*/ new SignupForm());
        return "view/user/signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String signupSubmit(@Valid /*@ModelAttribute*/ SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            logger.info("!!!!!!!!!!!!!!!!");
            return "view/user/signup";
        }

        Member newMember = memberService.processNewMember(signupForm);
        memberService.login(newMember);

        return "redirect:/";
    }

    @Autowired
    MemberRepository memberRepository;

    @GetMapping("/check-email-token")
    @Transactional
    public String checkEmailToken(String token, String email, Model model){
        Member member = memberRepository.findByEmail(email);

        // 이메일이 없는 경우
        if(member == null){
            model.addAttribute("error", "wrong.email");
            return "view/user/checked-email";
        }

        // 잘못된 토큰 값인 경우
        if(!member.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return "view/user/checked-email";
        }
        member.completeSignup();
        memberService.login(member);
        model.addAttribute("email", member.getEmail());
        return "view/user/checked-email";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(){
        return "view/user/change-password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(String email, Model model){

        // 메일 보내기
        memberService.sendMailResetPassword(email);

        // 결과 view 에
        model.addAttribute("email", email);
        model.addAttribute("result_code", "password.reset.send");

        // view/notify 로 이동
        return "view/notify";
    }
    @GetMapping("/reset-password")
    // http://127.0.0.1:8080/reset-password?token=0b79af4f-6e62-489b-9695-97b936ca029d&email=a@a.a
    public String resetPasswordForm(String token, String email, Model model){
        // email 이 유효한 지 확인
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            model.addAttribute("result", false);
            return "view/user/reset-password";
        }

        // 그 emailCheckToken 과 token 을 비교
        String emailCheckToken = member.getEmailCheckToken();

        // 틀리면 -> 에러
        if(!emailCheckToken.equals(token)){
            model.addAttribute("result", false);
            return "view/user/reset-password";
        }

        // 맞으면 -> 비밀번호 재설정 페이지로
        model.addAttribute("email", email);
        model.addAttribute("result", true);
        return "view/user/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(String email,String password, Model model){
        memberService.processResetPassword(email, password);

        model.addAttribute("result_code", "password.reset.complete");

        Member member = memberRepository.findByEmail(email);

        memberService.login(member);

        return "/view/notify";
    }
}








