package ua.kostenko.recollector.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.CategoryFilter;
import ua.kostenko.recollector.app.dto.response.Response;
import ua.kostenko.recollector.app.exception.CategoryValidationException;
import ua.kostenko.recollector.app.security.AuthService;
import ua.kostenko.recollector.app.service.CategoryService;
import ua.kostenko.recollector.app.util.ResponseHelper;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Response<CategoryDto>> createCategory(@RequestBody CategoryDto category) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = categoryService.createCategory(email, category);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response<List<CategoryDto>>> getAllCategories(CategoryFilter categoryFilter) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = categoryService.getCategoriesByFilters(email, categoryFilter);
        return ResponseHelper.buildPageDtoResponse(dto, HttpStatus.OK);
    }

    @GetMapping("/{category_id}")
    public ResponseEntity<Response<CategoryDto>> getCategory(@PathVariable("category_id") Long categoryId) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = categoryService.getCategory(email, categoryId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }

    @PutMapping("/{category_id}")
    public ResponseEntity<Response<CategoryDto>> updateCategory(@PathVariable("category_id") Long categoryId,
                                                                @RequestBody CategoryDto category) {
        var email = authService.getUserEmailFromAuthContext();
        if (Objects.nonNull(categoryId) && !categoryId.equals(category.getCategoryId())) {
            throw new CategoryValidationException("Category id in path and payload mismatch");
        }

        var dto = categoryService.updateCategory(email, category);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{category_id}")
    public ResponseEntity<Response<String>> deleteCategory(@PathVariable("category_id") Long categoryId) {
        var email = authService.getUserEmailFromAuthContext();
        var dto = categoryService.deleteCategory(email, categoryId);
        return ResponseHelper.buildDtoResponse(dto, HttpStatus.OK);
    }
}
