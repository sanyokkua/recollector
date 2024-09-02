package ua.kostenko.recollector.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.security.JwtUtil;
import ua.kostenko.recollector.app.service.CategoryService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    private static final String BASE_URL = "/api/v1/categories";
    private static final String CATEGORY_NAME = "Test Category";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";
    private static final String VALID_EMAIL = "valid@email.com";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void createCategory_ValidInput_ShouldReturnCreatedCategory() throws Exception {
        CategoryDto requestDto = CategoryDto.builder().categoryName(CATEGORY_NAME).build();
        CategoryDto responseDto = CategoryDto.builder().categoryName(CATEGORY_NAME).categoryId(1L).build();

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(categoryService.createCategory(VALID_EMAIL, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.CREATED.name()))
               .andExpect(jsonPath("$.data.categoryName").value(responseDto.getCategoryName()))
               .andExpect(jsonPath("$.data.categoryId").value(responseDto.getCategoryId()))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void getAllCategories_ValidInput_ShouldReturnAllCategories() throws Exception {
        CategoryFilter requestDto = CategoryFilter.builder()
                                                  .page(1)
                                                  .size(2)
                                                  .name("")
                                                  .direction(Sort.Direction.ASC)
                                                  .build();
        CategoryDto dto1 = CategoryDto.builder().categoryName(CATEGORY_NAME).categoryId(1L).build();
        CategoryDto dto2 = CategoryDto.builder().categoryName(CATEGORY_NAME).categoryId(2L).build();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "categoryName"));
        Page<CategoryDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(categoryService.getCategoriesByFilters(anyString(), any(CategoryFilter.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                                     .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.OK.name()))
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data[*].categoryId").value(containsInAnyOrder(1, 2)))
               .andExpect(jsonPath("$.meta").exists())
               .andExpect(jsonPath("$.meta.pagination").exists())
               .andExpect(jsonPath("$.meta.pagination.currentPage").exists())
               .andExpect(jsonPath("$.meta.pagination.currentPage").value(1))
               .andExpect(jsonPath("$.meta.pagination.itemsPerPage").exists())
               .andExpect(jsonPath("$.meta.pagination.itemsPerPage").value(2))
               .andExpect(jsonPath("$.meta.pagination.totalPages").exists())
               .andExpect(jsonPath("$.meta.pagination.totalPages").value(1))
               .andExpect(jsonPath("$.meta.pagination.totalItems").exists())
               .andExpect(jsonPath("$.meta.pagination.totalItems").value(2))
               .andExpect(jsonPath("$.meta.pagination.sortField").exists())
               .andExpect(jsonPath("$.meta.pagination.sortDirection").exists())
               .andExpect(jsonPath("$.meta.pagination.sortDirection").value("ASC"))
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void getCategory_ValidInput_ShouldReturnCategory() throws Exception {
        CategoryDto responseDto = CategoryDto.builder().categoryName(CATEGORY_NAME).categoryId(1L).build();

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(categoryService.getCategory(VALID_EMAIL, 1L)).thenReturn(responseDto);

        mockMvc.perform(get(BASE_URL + "/{category_id}", 1L))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.OK.name()))
               .andExpect(jsonPath("$.data.categoryName").value(responseDto.getCategoryName()))
               .andExpect(jsonPath("$.data.categoryId").value(responseDto.getCategoryId()))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void updateCategory_ValidInput_ShouldReturnUpdatedCategory() throws Exception {
        //TODO:
    }

    @Test
    void deleteCategory_ValidInput_ShouldReturnDeletedCategory() throws Exception {
        //TODO:
    }
}