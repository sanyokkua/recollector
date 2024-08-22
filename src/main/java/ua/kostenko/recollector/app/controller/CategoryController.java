package ua.kostenko.recollector.app.controller;

import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.dto.CategoryDto;
import ua.kostenko.recollector.app.dto.response.AppResponse;
import ua.kostenko.recollector.app.entity.Category;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @PostMapping
    public AppResponse<CategoryDto> createCategory(@RequestBody Category category) {
        // Handle category creation
        return AppResponse.<CategoryDto>builder().build();
    }

    @GetMapping
    public AppResponse<List<CategoryDto>> getAllCategories() {
        // Retrieve all categories
        return AppResponse.<List<CategoryDto>>builder().build();
    }

    @GetMapping("/{category_id}")
    public AppResponse<CategoryDto> getCategory(@PathVariable("category_id") Long categoryId) {
        // Retrieve a specific category
        return AppResponse.<CategoryDto>builder().build();
    }

    @PutMapping("/{category_id}")
    public AppResponse<CategoryDto> updateCategory(@PathVariable("category_id") Long categoryId,
                                                   @RequestBody Category category) {
        // Update a specific category
        return AppResponse.<CategoryDto>builder().build();
    }

    @DeleteMapping("/{category_id}")
    public AppResponse<Void> deleteCategory(@PathVariable("category_id") Long categoryId) {
        // Delete a specific category
        return AppResponse.<Void>builder().build();
    }
}
