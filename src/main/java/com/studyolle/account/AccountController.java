package com.studyolle.account;


import com.studyolle.domain.Account;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SingUpFormValidator singUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(singUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if(errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String s = "account/checked-email";
        if (account == null) {
            model.addAttribute("error","wrong.email");
            return s;
        }

        if(!account.getEmailCheckToken().equals(token) ){
            model.addAttribute("error","wrong.token");
            return s;
        }

        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return s;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email",account.getEmail());
        return "account/check-email";

    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if(!account.canSendConfirmEmail()) {
            model.addAttribute("error","?????? ???????????? 1????????? ????????? ?????? ???????????????");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }
        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }


    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname,Model model,@CurrentUser Account account){


        Account accountToView = accountService.getAccount(nickname);

        model.addAttribute(accountToView); //attributename ??? account??? ?????????
        model.addAttribute("isOwner",accountToView.equals(account));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if(account==null) {
            model.addAttribute("error","????????? ????????? ????????? ????????????");
            return "account/email-login";
        }

        if(!account.canSendConfirmEmail()) {
            model.addAttribute("error","????????? ???????????? 1?????? ?????? ????????? ??? ????????????");
            return "account/email-login";
        }
        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message","????????? ?????? ????????? ??????????????????.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error","???????????? ??? ????????????");
            return view;
        }

        accountService.login(account);
        return view;
    }


}
