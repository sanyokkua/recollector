package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.ItemDto;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.ItemValidationException;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.service.ItemService;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/categories/{categoryId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final AuthService authService;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<Response<ItemDto>> createItem(@PathVariable("categoryId") Long categoryId,
                                                        @RequestBody ItemDto itemDto) {
        if (Objects.isNull(categoryId) || (Objects.nonNull(itemDto) && !categoryId.equals(itemDto.getCategoryId()))) {
            throw new ItemValidationException("Category id cannot be empty or different in path and body");
        }
        var email = authService.getUserEmailFromAuthContext();
        var dto = itemService.createItem(email, itemDto);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response<List<ItemDto>>> getAllItems(@PathVariable("categoryId") Long categoryId) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = itemService.getAllItems(email, categoryId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Response<ItemDto>> getItem(@PathVariable("categoryId") Long categoryId,
                                                     @PathVariable("itemId") Long itemId) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = itemService.getItem(email, categoryId, itemId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<Response<ItemDto>> updateItem(@PathVariable("categoryId") Long categoryId,
                                                        @PathVariable("itemId") Long itemId,
                                                        @RequestBody ItemDto itemDto) {
        if (Objects.isNull(itemDto)) {
            throw new ItemValidationException("Item cannot be null");
        }
        if (!categoryId.equals(itemDto.getCategoryId()) || !itemId.equals(itemDto.getItemId())) {
            throw new ItemValidationException(
                    "Path categoryId and Path itemId should be equal to values in item payload");
        }

        var email = authService.getUserEmailFromAuthContext();
        var dto = itemService.updateItem(email, itemDto);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Response<String>> deleteItem(@PathVariable("categoryId") Long categoryId,
                                                       @PathVariable("itemId") Long itemId) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = itemService.deleteItem(email, categoryId, itemId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }
}
