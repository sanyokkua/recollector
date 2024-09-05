package ua.kostenko.recollector.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.ItemFilter;
import ua.kostenko.recollector.app.entity.ItemStatus;
import ua.kostenko.recollector.app.exception.ItemAlreadyExistsException;
import ua.kostenko.recollector.app.exception.ItemNotFoundException;
import ua.kostenko.recollector.app.exception.ItemValidationException;
import ua.kostenko.recollector.app.exception.UserNotAuthenticatedException;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.security.JwtUtil;
import ua.kostenko.recollector.app.service.ItemService;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

    private static final String BASE_URL = "/api/v1/categories/{categoryId}/items";
    private static final String ITEM_NAME = "Test Item";
    private static final String VALID_EMAIL = "valid@email.com";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtUtil jwtUtil;

    private static Stream<Arguments> exceptionScenarios() {
        String msg = "Failed";
        return Stream.of(Arguments.of(new UserNotAuthenticatedException(msg), HttpStatus.UNAUTHORIZED, msg),
                         Arguments.of(new ItemAlreadyExistsException(msg), HttpStatus.BAD_REQUEST, msg),
                         Arguments.of(new ItemValidationException(msg), HttpStatus.BAD_REQUEST, msg),
                         Arguments.of(new ItemNotFoundException(msg), HttpStatus.NOT_FOUND, msg));
    }

    @Test
    void createItem_ValidInput_ShouldReturnCreatedItem() throws Exception {
        long itemId = 1L;
        long categoryId = 11L;
        ItemDto requestDto = ItemDto.builder().itemId(itemId).categoryId(categoryId).itemName(ITEM_NAME).build();
        ItemDto responseDto = ItemDto.builder()
                                     .itemId(itemId)
                                     .categoryId(categoryId)
                                     .itemName(ITEM_NAME)
                                     .itemNotes("Notes").itemStatus(ItemStatus.IN_PROGRESS)
                                     .build();
        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.createItem(VALID_EMAIL, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL, categoryId).contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.CREATED.name()))
               .andExpect(jsonPath("$.data.itemId").value(responseDto.getItemId()))
               .andExpect(jsonPath("$.data.categoryId").value(responseDto.getCategoryId()))
               .andExpect(jsonPath("$.data.itemName").value(responseDto.getItemName()))
               .andExpect(jsonPath("$.data.itemStatus").value(responseDto.getItemStatus().name()))
               .andExpect(jsonPath("$.data.itemNotes").value(responseDto.getItemNotes()))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void createItem_InvalidInput_ShouldReturnError() throws Exception {
        ItemDto requestDto = ItemDto.builder().itemId(1L).itemName(ITEM_NAME).build();

        mockMvc.perform(post(BASE_URL, 2L).contentType(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.data.itemId").doesNotExist())
               .andExpect(jsonPath("$.data.categoryId").doesNotExist())
               .andExpect(jsonPath("$.data.itemName").doesNotExist())
               .andExpect(jsonPath("$.data.itemStatus").doesNotExist())
               .andExpect(jsonPath("$.data.itemNotes").doesNotExist())
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.error").value(
                       "ItemValidationException: Category id cannot be empty or different in path and body"))
               .andDo(print());
    }

    @Test
    void getAllItems_ValidInput_ShouldReturnAllItems() throws Exception {
        long itemId1 = 1L;
        long itemId2 = 2L;
        long categoryId = 11L;
        ItemDto requestDto = ItemDto.builder().itemId(itemId1).categoryId(categoryId).itemName(ITEM_NAME).build();

        ItemDto dto1 = ItemDto.builder().itemName(ITEM_NAME).itemId(itemId1).categoryId(1L).build();
        ItemDto dto2 = ItemDto.builder().itemName(ITEM_NAME).itemId(itemId2).categoryId(2L).build();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "itemName"));
        Page<ItemDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.getItemsByFilters(anyString(), anyLong(), any(ItemFilter.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL, categoryId).contentType(MediaType.APPLICATION_JSON)
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
    void getItem_ValidInput_ShouldReturnFoundItem() throws Exception {
        long itemId = 1L;
        long categoryId = 11L;
        ItemDto responseDto = ItemDto.builder()
                                     .itemId(itemId)
                                     .categoryId(categoryId)
                                     .itemName(ITEM_NAME)
                                     .itemNotes("Notes").itemStatus(ItemStatus.IN_PROGRESS)
                                     .build();
        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.getItem(VALID_EMAIL, categoryId, itemId)).thenReturn(responseDto);

        mockMvc.perform(get(BASE_URL + "/{itemId}", categoryId, itemId))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.OK.name()))
               .andExpect(jsonPath("$.data.itemId").value(responseDto.getItemId()))
               .andExpect(jsonPath("$.data.categoryId").value(responseDto.getCategoryId()))
               .andExpect(jsonPath("$.data.itemName").value(responseDto.getItemName()))
               .andExpect(jsonPath("$.data.itemStatus").value(responseDto.getItemStatus().name()))
               .andExpect(jsonPath("$.data.itemNotes").value(responseDto.getItemNotes()))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void updateItem_ValidInput_ShouldReturnUpdatedItem() throws Exception {
        long itemId = 1L;
        long categoryId = 11L;
        ItemDto responseDto = ItemDto.builder()
                                     .itemId(itemId)
                                     .categoryId(categoryId)
                                     .itemName(ITEM_NAME)
                                     .itemNotes("Notes").itemStatus(ItemStatus.IN_PROGRESS)
                                     .build();
        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.updateItem(any(String.class), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(put(BASE_URL + "/{itemId}", categoryId, itemId).contentType(MediaType.APPLICATION_JSON)
                                                                       .content(objectMapper.writeValueAsString(
                                                                               responseDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.ACCEPTED.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.ACCEPTED.name()))
               .andExpect(jsonPath("$.data.itemId").value(responseDto.getItemId()))
               .andExpect(jsonPath("$.data.categoryId").value(responseDto.getCategoryId()))
               .andExpect(jsonPath("$.data.itemName").value(responseDto.getItemName()))
               .andExpect(jsonPath("$.data.itemStatus").value(responseDto.getItemStatus().name()))
               .andExpect(jsonPath("$.data.itemNotes").value(responseDto.getItemNotes()))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @Test
    void updateItem_InvalidInput_ShouldReturnError() throws Exception {
        long itemId = 11L;
        long categoryId = 22L;
        ItemDto requestDto = ItemDto.builder()
                                    .itemId(itemId)
                                    .categoryId(categoryId)
                                    .itemName(ITEM_NAME)
                                    .itemNotes("Notes").itemStatus(ItemStatus.IN_PROGRESS)
                                    .build();
        mockMvc.perform(put(BASE_URL + "/{itemId}", categoryId, 999L).contentType(MediaType.APPLICATION_JSON)
                                                                     .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.data.itemId").doesNotExist())
               .andExpect(jsonPath("$.data.categoryId").doesNotExist())
               .andExpect(jsonPath("$.data.itemName").doesNotExist())
               .andExpect(jsonPath("$.data.itemStatus").doesNotExist())
               .andExpect(jsonPath("$.data.itemNotes").doesNotExist())
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.error").value(
                       "ItemValidationException: Path categoryId and Path itemId should be equal to values in item payload"))
               .andDo(print());

        mockMvc.perform(put(BASE_URL + "/{itemId}", 999L, itemId).contentType(MediaType.APPLICATION_JSON)
                                                                 .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.data.itemId").doesNotExist())
               .andExpect(jsonPath("$.data.categoryId").doesNotExist())
               .andExpect(jsonPath("$.data.itemName").doesNotExist())
               .andExpect(jsonPath("$.data.itemStatus").doesNotExist())
               .andExpect(jsonPath("$.data.itemNotes").doesNotExist())
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.error").value(
                       "ItemValidationException: Path categoryId and Path itemId should be equal to values in item payload"))
               .andDo(print());
    }

    @Test
    void deleteItem_ValidInput_ShouldReturnSuccessText() throws Exception {
        long itemId = 1L;
        long categoryId = 11L;
        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.deleteItem(VALID_EMAIL, categoryId, itemId)).thenReturn("Test Msg");

        mockMvc.perform(delete(BASE_URL + "/{itemId}", categoryId, itemId))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
               .andExpect(jsonPath("$.statusMessage").value(HttpStatus.OK.name()))
               .andExpect(jsonPath("$.data.itemId").doesNotExist())
               .andExpect(jsonPath("$.data.categoryId").doesNotExist())
               .andExpect(jsonPath("$.data.itemName").doesNotExist())
               .andExpect(jsonPath("$.data.itemStatus").doesNotExist())
               .andExpect(jsonPath("$.data.itemNotes").doesNotExist())
               .andExpect(jsonPath("$.data").value("Test Msg"))
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist())
               .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("exceptionScenarios")
    void anyMethod_invalidInput_ShouldThrowExceptionAndAdviceProcessResponse(Exception exception, HttpStatus status,
                                                                             String errorMessage) throws Exception {
        long itemId = 11L;
        long categoryId = 22L;
        ItemDto requestDto = ItemDto.builder()
                                    .itemId(itemId)
                                    .categoryId(categoryId)
                                    .itemName(ITEM_NAME)
                                    .itemNotes("Notes").itemStatus(ItemStatus.IN_PROGRESS)
                                    .build();

        when(authService.getUserEmailFromAuthContext()).thenReturn(VALID_EMAIL);
        when(itemService.updateItem(VALID_EMAIL, requestDto)).thenThrow(exception);

        mockMvc.perform(put(BASE_URL + "/{itemId}", categoryId, itemId).contentType(MediaType.APPLICATION_JSON)
                                                                       .content(objectMapper.writeValueAsString(
                                                                               requestDto)))
               .andExpect(jsonPath("$.statusCode").value(status.value()))
               .andExpect(jsonPath("$.statusMessage").value(status.name()))
               .andExpect(jsonPath("$.data.itemId").doesNotExist())
               .andExpect(jsonPath("$.data.categoryId").doesNotExist())
               .andExpect(jsonPath("$.data.itemName").doesNotExist())
               .andExpect(jsonPath("$.data.itemStatus").doesNotExist())
               .andExpect(jsonPath("$.data.itemNotes").doesNotExist())
               .andExpect(jsonPath("$.meta").doesNotExist())
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.error").value(exception.getClass().getSimpleName() + ": " + errorMessage))
               .andDo(print());
    }
}