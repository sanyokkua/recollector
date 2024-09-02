package ua.kostenko.recollector.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.kostenko.recollector.app.security.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
@AutoConfigureMockMvc(addFilters = false)
class IndexControllerTest {

    private static final String BASE_URL = "/";

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get Request to Index Page - Should return HTML page with html template")
    void index_validRequest_shouldReturnHtml() throws Exception {
        mockMvc.perform(get(BASE_URL))
               .andExpect(status().is(HttpStatus.OK.value()))
               .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
               .andDo(print());
    }
}