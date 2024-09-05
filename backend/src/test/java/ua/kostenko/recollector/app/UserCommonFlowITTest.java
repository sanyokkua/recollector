package ua.kostenko.recollector.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.UserDto;
import ua.kostenko.recollector.app.dto.auth.AccountDeleteRequestDto;
import ua.kostenko.recollector.app.dto.auth.ChangePasswordRequestDto;
import ua.kostenko.recollector.app.dto.auth.LoginRequestDto;
import ua.kostenko.recollector.app.dto.auth.RegisterRequestDto;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.entity.ItemStatus;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = {TestApplicationContextInitializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class UserCommonFlowITTest {

    private static final String CATEGORY_ID = "{categoryId}";
    private static final String ITEM_ID = "{itemId}";
    private static final String BASE_API_URL = "/api/v1";
    private static final String BASE_AUTH_URL = BASE_API_URL + "/auth";
    private static final String BASE_HELPER_URL = BASE_API_URL + "/helper";
    private static final String BASE_CATEGORY_URL = BASE_API_URL + "/categories";
    private static final String BASE_ITEM_URL = BASE_CATEGORY_URL + "/" + CATEGORY_ID + "/items";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_TOKEN = "Bearer ";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_USER_1_EMAIL = "testUser1@email.com";
    private static final String TEST_USER_1_PASSWORD = "testUser1Password";
    private static final String TEST_USER_2_EMAIL = "testUser2@email.com";
    private static final String TEST_USER_2_PASSWORD = "testUser2Password";
    private static final String TEST_USER_2_NEW_PASSWORD = "NewPassword";

    private static String user1Token = "";
    private static String user2Token = "";
    private static Long user1Category1Id, user1Category2Id, user1Category3Id;
    private static Long user1Category1Item1, user1Category1Item2, user1Category1Item3;
    private static Long user2Category1Id, user2Category2Id;

    @Autowired
    private MockMvc mockMvc;

    @Order(1)
    @Test
    void root_GET_returnsOK() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andDo(print());
    }

    @Order(2)
    @Test
    void protectedEndpoints_GET_returnsUnauthorized() throws Exception {
        performUnauthorizedGet(BASE_CATEGORY_URL);
        performUnauthorizedGet(BASE_ITEM_URL, 1L);
        performUnauthorizedGet(BASE_HELPER_URL + "/itemStatuses");
        performUnauthorizedGet(BASE_HELPER_URL + "/statistics");
    }

    private void performUnauthorizedGet(String url, Object... uriVars) throws Exception {
        mockMvc.perform(get(url, uriVars)).andExpect(status().isUnauthorized()).andDo(print());
    }

    @Order(3)
    @Test
    void unprotectedEndpoints_GET_returnsOK() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andDo(print());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk()).andDo(print());
    }

    @Order(4)
    @Test
    void registerUser_POST_createsUserOne() throws Exception {
        registerUser(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD, HttpStatus.CREATED);
    }

    @Order(5)
    @Test
    void registerUser_POST_duplicateUserOne_returnsBadRequest() throws Exception {
        registerUser(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    private void registerUser(String email, String password, HttpStatus expectedStatus) throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                                                       .email(email)
                                                       .password(password)
                                                       .passwordConfirm(password)
                                                       .build();

        mockMvc.perform(post(BASE_AUTH_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                                                         .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().is(expectedStatus.value()))
               .andDo(print());
    }

    @Order(6)
    @Test
    void loginUser_POST_authenticatesUserOne() throws Exception {
        user1Token = loginUser(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD, HttpStatus.OK);
    }

    @Order(7)
    @Test
    void registerUser_POST_createsUserTwo() throws Exception {
        registerUser(TEST_USER_2_EMAIL, TEST_USER_2_PASSWORD, HttpStatus.CREATED);
    }

    @Order(8)
    @Test
    void loginUser_POST_invalidCredentials_returnsUnauthorized() throws Exception {
        loginUser(TEST_USER_2_EMAIL, TEST_USER_1_PASSWORD, HttpStatus.UNAUTHORIZED);
    }

    @Order(9)
    @Test
    void loginUser_POST_authenticatesUserTwo() throws Exception {
        user2Token = loginUser(TEST_USER_2_EMAIL, TEST_USER_2_PASSWORD, HttpStatus.OK);
    }

    private String loginUser(String email, String password, HttpStatus expectedStatus) throws Exception {
        LoginRequestDto request = LoginRequestDto.builder().email(email).password(password).build();

        var result = mockMvc.perform(post(BASE_AUTH_URL + "/login").contentType(MediaType.APPLICATION_JSON)
                                                                   .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().is(expectedStatus.value()))
                            .andDo(print())
                            .andReturn();

        if (expectedStatus == HttpStatus.OK) {
            var content = result.getResponse().getContentAsString();
            Response<UserDto> response = objectMapper.readValue(content, new TypeReference<>() {});
            return response.getData().getJwtToken();
        }
        return "";
    }

    @Order(10)
    @Test
    void getUserCategories_GET_returnsEmptyList() throws Exception {
        performGetCategories(user1Token);
    }

    private void performGetCategories(String token) throws Exception {
        var result = mockMvc.perform(get(BASE_CATEGORY_URL).header(AUTH_HEADER, BEARER_TOKEN + token))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<CategoryDto>>>() {});

        assertTrue(response.getData().isEmpty());
    }

    @Order(11)
    @Test
    void getNonExistingCategory_GET_returnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_CATEGORY_URL + "/" + CATEGORY_ID, 999L).header(AUTH_HEADER, BEARER_TOKEN + user1Token))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Order(12)
    @Test
    void createMultipleCategories_POST_userOne() throws Exception {
        user1Category1Id = createCategory("User 1 Category 1", user1Token);
        user1Category2Id = createCategory("User 1 Category 2", user1Token);
        user1Category3Id = createCategory("User 1 Category 3", user1Token);
        verifyCategoryCount(user1Token, 3);
    }

    @Order(13)
    @Test
    void createMultipleCategories_POST_userTwo() throws Exception {
        user2Category1Id = createCategory("User 2 Category 1", user2Token);
        user2Category2Id = createCategory("User 2 Category 2", user2Token);
        verifyCategoryCount(user2Token, 2);
    }

    private Long createCategory(String categoryName, String token) throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().categoryName(categoryName).build();
        var result = mockMvc.perform(post(BASE_CATEGORY_URL).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .content(objectMapper.writeValueAsString(categoryDto)))
                            .andExpect(status().isCreated())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<CategoryDto>>() {});
        return response.getData().getCategoryId();
    }

    private void verifyCategoryCount(String token, int expectedCount) throws Exception {
        var result = mockMvc.perform(get(BASE_CATEGORY_URL).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                           .param("size", "10"))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<CategoryDto>>>() {});
        assertEquals(expectedCount, response.getData().size());
    }

    @Order(14)
    @Test
    void getAllItemStatuses_GET_returnsOK() throws Exception {
        var result = mockMvc.perform(get(BASE_HELPER_URL + "/itemStatuses").header(AUTH_HEADER,
                                                                                   BEARER_TOKEN + user1Token))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<ItemStatus>>>() {});
        assertEquals(ItemStatus.values().length, response.getData().size());
    }

    @Order(15)
    @Test
    void createItems_POST_userOne() throws Exception {
        var item1 = createItem(user1Category1Id, "User 1 Item 1", ItemStatus.TODO_LATER, user1Token);
        var item2 = createItem(user1Category1Id, "User 1 Item 2", ItemStatus.IN_PROGRESS, user1Token);
        var item3 = createItem(user1Category1Id, "User 1 Item 3", ItemStatus.FINISHED, user1Token);
        user1Category1Item1 = item1.getItemId();
        user1Category1Item2 = item2.getItemId();
        user1Category1Item3 = item3.getItemId();
    }

    private ItemDto createItem(Long categoryId, String itemName, ItemStatus itemStatus, String token) throws Exception {
        ItemDto itemDto = ItemDto.builder().categoryId(categoryId).itemName(itemName).itemStatus(itemStatus).build();
        var response = mockMvc.perform(post(BASE_ITEM_URL, categoryId).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                      .content(objectMapper.writeValueAsString(itemDto)))
                              .andExpect(status().isCreated())
                              .andExpect(jsonPath("$.data.itemId").exists())
                              .andDo(print())
                              .andReturn();
        var content = response.getResponse().getContentAsString();
        var item = objectMapper.readValue(content, new TypeReference<Response<ItemDto>>() {});
        return item.getData();
    }

    @Order(16)
    @Test
    void getAllUserItems_GET_returnsAllItems() throws Exception {
        verifyItemCount(user1Category1Id, 3, user1Token);
        verifyItemCount(user1Category2Id, 0, user1Token);
    }

    private void verifyItemCount(Long categoryId, int expectedCount, String token) throws Exception {
        var result = mockMvc.perform(get(BASE_ITEM_URL, categoryId).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                                   .param("size", "10"))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<ItemDto>>>() {});
        assertEquals(expectedCount, response.getData().size());
    }

    @Order(17)
    @Test
    void createItems_POST_userTwo() throws Exception {
        createItem(user2Category1Id, "User 2 Item 1", ItemStatus.IN_PROGRESS, user2Token);
        createItem(user2Category1Id, "User 2 Item 2", ItemStatus.FINISHED, user2Token);
    }

    @Order(18)
    @Test
    void getAllUserItems_GET_returnsAllItems_after_update() throws Exception {
        verifyItemCount(user1Category1Id, 3, user1Token);
        verifyItemCount(user1Category2Id, 0, user1Token);
        verifyItemCount(user1Category3Id, 0, user1Token);
        verifyItemCount(user2Category1Id, 2, user2Token);
        verifyItemCount(user2Category2Id, 0, user2Token);
    }

    @Order(19)
    @Test
    void updateMultipleCategories_PUT_userOne() throws Exception {
        updateCategory(user1Category1Id, "A new category name", user1Token);
        updateCategory(user1Category2Id, "Focus", user1Token);
        updateCategory(user1Category3Id, "Fortuna", user1Token);
        verifyCategoryCount(user1Token, 3);
    }

    private void updateCategory(Long categoryId, String newName, String token) throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().categoryId(categoryId).categoryName(newName).build();
        var result = mockMvc.perform(put(BASE_CATEGORY_URL + "/" + CATEGORY_ID, categoryId).header(AUTH_HEADER,
                                                                                                   BEARER_TOKEN + token)
                                                                                           .contentType(MediaType.APPLICATION_JSON)
                                                                                           .content(objectMapper.writeValueAsString(
                                                                                                   categoryDto)))
                            .andExpect(status().isAccepted())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<CategoryDto>>() {});

        assertEquals(categoryId, response.getData().getCategoryId());
        assertEquals(newName, response.getData().getCategoryName());
    }

    @Order(20)
    @Test
    void getUserCategories_GET_filtered() throws Exception {
        var res = performGetCategoriesWithFilter(user1Token, 1, 3, "", "ASC");
        assertEquals(3, res.getData().size());
        var res2 = performGetCategoriesWithFilter(user1Token, 2, 3, "", "ASC");
        assertEquals(0, res2.getData().size());
        var res3 = performGetCategoriesWithFilter(user1Token, 1, 1, "", "ASC");
        assertEquals(1, res3.getData().size());
        var res4 = performGetCategoriesWithFilter(user1Token, 2, 1, "", "ASC");
        assertEquals(1, res4.getData().size());
        var res5 = performGetCategoriesWithFilter(user1Token, 3, 1, "", "ASC");
        assertEquals(1, res5.getData().size());
        var res6 = performGetCategoriesWithFilter(user1Token, 4, 1, "", "ASC");
        assertEquals(0, res6.getData().size());
        var res7 = performGetCategoriesWithFilter(user1Token, 1, 10, "A", "ASC");
        assertEquals(1, res7.getData().size());
        var res8 = performGetCategoriesWithFilter(user1Token, 1, 10, "Fo", "ASC");
        assertEquals(2, res8.getData().size());
        var res9 = performGetCategoriesWithFilter(user1Token, 1, 10, "For", "ASC");
        assertEquals(1, res9.getData().size());

        var sortedAsc = performGetCategoriesWithFilter(user1Token, 1, 10, "", "ASC");
        var sortedDesc = performGetCategoriesWithFilter(user1Token, 1, 10, "", "DESC");
        assertTrue(sortedAsc.getData().getFirst().getCategoryName().startsWith("A"));
        assertTrue(sortedDesc.getData().getFirst().getCategoryName().startsWith("For"));
    }

    private Response<List<CategoryDto>> performGetCategoriesWithFilter(String token, int page, int size, String name,
                                                                       String sortDirection) throws Exception {
        var result = mockMvc.perform(get(BASE_CATEGORY_URL).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                           .param("page", String.valueOf(page))
                                                           .param("size", String.valueOf(size))
                                                           .param("categoryName", name)
                                                           .param("direction", sortDirection))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<CategoryDto>>>() {});

        assertEquals(page, response.getMeta().getPagination().getCurrentPage());
        assertEquals(sortDirection, response.getMeta().getPagination().getSortDirection());
        return response;
    }

    @Order(21)
    @Test
    void updateMultipleItems_PUT_userOne() throws Exception {
        updateItem(user1Category1Id, user1Category1Item1, "A first item", ItemStatus.TODO_LATER.name(), user1Token);
        updateItem(user1Category1Id, user1Category1Item2, "Focus", ItemStatus.IN_PROGRESS.name(), user1Token);
        updateItem(user1Category1Id, user1Category1Item3, "Fortuna", ItemStatus.FINISHED.name(), user1Token);
        verifyItemCount(user1Category1Id, 3, user1Token);
    }

    private void updateItem(Long categoryId, Long itemId, String newName, String status,
                            String token) throws Exception {
        ItemDto itemDto = ItemDto.builder()
                                 .categoryId(categoryId)
                                 .itemId(itemId)
                                 .itemName(newName).itemStatus(ItemStatus.valueOf(status))
                                 .build();
        var result = mockMvc.perform(put(BASE_ITEM_URL + "/" + ITEM_ID, categoryId, itemId).header(AUTH_HEADER,
                                                                                                   BEARER_TOKEN + token)
                                                                                           .contentType(MediaType.APPLICATION_JSON)
                                                                                           .content(objectMapper.writeValueAsString(
                                                                                                   itemDto)))
                            .andExpect(status().isAccepted())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<ItemDto>>() {});

        assertEquals(categoryId, response.getData().getCategoryId());
        assertEquals(itemId, response.getData().getItemId());
        assertEquals(newName, response.getData().getItemName());
    }

    @Order(22)
    @Test
    void getUserItems_GET_filtered() throws Exception {
        var res = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 3, "", "", "ASC");
        assertEquals(3, res.getData().size());
        var res2 = performGetItemsWithFilter(user1Token, user1Category1Id, 2, 3, "", "", "ASC");
        assertEquals(0, res2.getData().size());
        var res3 = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 1, "", "", "ASC");
        assertEquals(1, res3.getData().size());
        var res4 = performGetItemsWithFilter(user1Token, user1Category1Id, 2, 1, "", "", "ASC");
        assertEquals(1, res4.getData().size());
        var res5 = performGetItemsWithFilter(user1Token, user1Category1Id, 3, 1, "", "", "ASC");
        assertEquals(1, res5.getData().size());
        var res6 = performGetItemsWithFilter(user1Token, user1Category1Id, 4, 1, "", "", "ASC");
        assertEquals(0, res6.getData().size());
        var res7 = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 10, "A", "", "ASC");
        assertEquals(1, res7.getData().size());
        var res8 = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 10, "Fo", "", "ASC");
        assertEquals(2, res8.getData().size());
        var res9 = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 10, "For", "", "ASC");
        assertEquals(1, res9.getData().size());

        var sortedAsc = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 10, "", "", "ASC");
        var sortedDesc = performGetItemsWithFilter(user1Token, user1Category1Id, 1, 10, "", "", "DESC");
        assertTrue(sortedAsc.getData().getFirst().getItemName().startsWith("A"));
        assertTrue(sortedDesc.getData().getFirst().getItemName().startsWith("For"));

        var res10 = performGetItemsWithFilter(user1Token,
                                              user1Category1Id,
                                              1,
                                              10,
                                              "",
                                              ItemStatus.TODO_LATER.name(),
                                              "ASC");
        assertEquals(1, res10.getData().size());
        var res11 = performGetItemsWithFilter(user1Token,
                                              user1Category1Id,
                                              1,
                                              10,
                                              "",
                                              ItemStatus.FINISHED.name(),
                                              "ASC");
        assertEquals(1, res11.getData().size());
        var res12 = performGetItemsWithFilter(user1Token,
                                              user1Category1Id,
                                              1,
                                              10,
                                              "",
                                              ItemStatus.IN_PROGRESS.name(),
                                              "ASC");
        assertEquals(1, res12.getData().size());
    }

    private Response<List<ItemDto>> performGetItemsWithFilter(String token, long categoryId, int page, int size,
                                                              String name, String itemStatus,
                                                              String sortDirection) throws Exception {
        var result = mockMvc.perform(get(BASE_ITEM_URL, categoryId).header(AUTH_HEADER, BEARER_TOKEN + token)
                                                                   .param("page", String.valueOf(page))
                                                                   .param("size", String.valueOf(size))
                                                                   .param("itemName", name)
                                                                   .param("itemStatus", itemStatus)
                                                                   .param("direction", sortDirection))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();

        var content = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(content, new TypeReference<Response<List<ItemDto>>>() {});

        assertEquals(page, response.getMeta().getPagination().getCurrentPage());
        assertEquals(sortDirection, response.getMeta().getPagination().getSortDirection());
        return response;
    }

    @Order(23)
    @Test
    void deleteItem_Delete_UserOne() throws Exception {
        var result = mockMvc.perform(delete(BASE_ITEM_URL + "/" + ITEM_ID,
                                            user1Category1Id,
                                            user1Category1Item3).header(AUTH_HEADER, BEARER_TOKEN + user1Token))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();
        String msg = "Item with id '" + user1Category1Item3 + "'";
        String contentAsString = result.getResponse().getContentAsString();
        assertNotNull(contentAsString);
        assertTrue(contentAsString.contains(msg));
        verifyItemCount(user1Category1Id, 2, user1Token);
    }

    @Order(24)
    @Test
    void deleteCategory_Delete_UserOne() throws Exception {
        var result = mockMvc.perform(delete(BASE_CATEGORY_URL + "/" + CATEGORY_ID, user1Category3Id).header(AUTH_HEADER,
                                                                                                            BEARER_TOKEN + user1Token))
                            .andExpect(status().isOk())
                            .andDo(print())
                            .andReturn();
        String msg = "Category with id '" + user1Category3Id + "'";
        String contentAsString = result.getResponse().getContentAsString();
        assertNotNull(contentAsString);
        assertTrue(contentAsString.contains(msg));
        verifyCategoryCount(user1Token, 2);
    }

    @Order(25)
    @Test
    void change_password_userTwo_fail() throws Exception {
        ChangePasswordRequestDto requestDto = ChangePasswordRequestDto.builder()
                                                                      .email(TEST_USER_2_EMAIL)
                                                                      .passwordCurrent(TEST_USER_2_PASSWORD + "incorrect")
                                                                      .password(TEST_USER_2_NEW_PASSWORD)
                                                                      .passwordConfirm(TEST_USER_2_NEW_PASSWORD)
                                                                      .build();

        mockMvc.perform(post(BASE_AUTH_URL + "/change-password").header(AUTH_HEADER, BEARER_TOKEN + user2Token)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isBadRequest())
               .andDo(print())
               .andReturn();
    }

    @Order(26)
    @Test
    void change_password_userTwo_success() throws Exception {
        ChangePasswordRequestDto requestDto = ChangePasswordRequestDto.builder()
                                                                      .email(TEST_USER_2_EMAIL)
                                                                      .passwordCurrent(TEST_USER_2_PASSWORD)
                                                                      .password(TEST_USER_2_NEW_PASSWORD)
                                                                      .passwordConfirm(TEST_USER_2_NEW_PASSWORD)
                                                                      .build();

        mockMvc.perform(post(BASE_AUTH_URL + "/change-password").header(AUTH_HEADER, BEARER_TOKEN + user2Token)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn();

        user2Token = loginUser(TEST_USER_2_EMAIL, TEST_USER_2_NEW_PASSWORD, HttpStatus.OK);
    }

    @Order(27)
    @Test
    void delete_account_userTwo_success() throws Exception {
        AccountDeleteRequestDto requestDto = AccountDeleteRequestDto.builder()
                                                                    .email(TEST_USER_2_EMAIL)
                                                                    .password(TEST_USER_2_NEW_PASSWORD)
                                                                    .passwordConfirm(TEST_USER_2_NEW_PASSWORD)
                                                                    .build();

        mockMvc.perform(post(BASE_AUTH_URL + "/delete-account").header(AUTH_HEADER, BEARER_TOKEN + user2Token)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn();

        LoginRequestDto request = LoginRequestDto.builder()
                                                 .email(TEST_USER_2_EMAIL)
                                                 .password(TEST_USER_2_NEW_PASSWORD)
                                                 .build();
        mockMvc.perform(post(BASE_AUTH_URL + "/login").contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound())
               .andDo(print())
               .andReturn();
    }

    @Order(28)
    @Test
    void getStatistics_UserOne_Get() throws Exception {
        mockMvc.perform(get(BASE_HELPER_URL + "/statistics").header(AUTH_HEADER, BEARER_TOKEN + user1Token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.OK.name()))
               .andExpect(jsonPath("$.data.totalNumberOfCategories").value(2))
               .andExpect(jsonPath("$.data.totalNumberOfItems").value(2))
               .andExpect(jsonPath("$.data.totalNumberOfItemsTodo").value(1))
               .andExpect(jsonPath("$.data.totalNumberOfItemsInProgress").value(1))
               .andExpect(jsonPath("$.data.totalNumberOfItemsFinished").value(0))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Order(29)
    @Test
    void getListOfStatuses_UserOne_Get() throws Exception {

        mockMvc.perform(get(BASE_HELPER_URL + "/itemStatuses").header(AUTH_HEADER, BEARER_TOKEN + user1Token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.statusCode").value(200))
               .andExpect(jsonPath("$.statusMessage").value("OK"))
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data").value(containsInAnyOrder("FINISHED", "IN_PROGRESS", "TODO_LATER")))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

}
