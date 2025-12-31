package org.example.playground.global.security.handler;

import org.example.playground.domain.refreshtoken.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginSuccessHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void refreshToken_DB저장_테스트() throws Exception{

    }

}