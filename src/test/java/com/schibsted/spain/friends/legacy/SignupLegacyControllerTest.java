package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.repository.FriendshipRepository;
import com.schibsted.spain.friends.service.FriendshipService;
import com.schibsted.spain.friends.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.schibsted.spain.friends.utils.Utils.SIGN_UP_MAPPING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SignupLegacyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    FriendshipService friendshipService;

    @MockBean
    FriendshipRepository friendshipRepository;

    @Test
    @DisplayName("Signup with invalid data")
    void signupErrorTestCases() throws Exception {
        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "john_doe")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johndoe")
                .header("X-Password", "j12-45678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johndoe")
                .header("X-Password", "j1234"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johndoe")
                .header("X-Password", "j1234567890123"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johnisaniceguy")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Signup with valid data")
    void signupWithValidData() throws Exception {
        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johndoe")
                .header("X-Password", "j12345678"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "johndoe")
                .header("X-Password", "j12345678"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "roseanne")
                .header("X-Password", "r3456789"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "peter")
                .header("X-Password", "p4567890"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "jessica")
                .header("X-Password", "j5678901"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post(SIGN_UP_MAPPING)
                .param("username", "robert")
                .header("X-Password", "r0123456"))
                .andExpect(status().is2xxSuccessful());
    }
}