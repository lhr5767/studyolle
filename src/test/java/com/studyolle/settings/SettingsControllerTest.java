package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("lhr")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

    }

    @WithAccount("lhr")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
            .param("bio", bio)
            .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account lhr = accountRepository.findByNickname("lhr");
        assertEquals(bio,lhr.getBio());
    }


    @WithAccount("lhr")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길개 소개 수정하는 경우길개 소개 수정하는 경우길개 소개 수정하는 경우길개 소개 수정하는 경우길개 소개 수정하는 경우길개 소개 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account lhr = accountRepository.findByNickname("lhr");
        assertNull(lhr.getBio());
    }

    @WithAccount("lhr")
    @DisplayName("패스워듣 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("lhr")
    @DisplayName("패스워듣 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword" , "123456789")
            .param("newPasswordConfirm","123456789")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(flash().attributeExists("message"));

        Account lhr = accountRepository.findByNickname("lhr");
        assertTrue(passwordEncoder.matches("123456789",lhr.getPassword()));
    }

    @WithAccount("lhr")
    @DisplayName("패스워듣 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword" , "123456789")
                .param("newPasswordConfirm","123455555555")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("passwordForm"))
            .andExpect(model().attributeExists("account"));

    }
}