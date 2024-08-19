package ua.kostenko.recollector.app.controller;

import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.entity.Category;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @PostMapping
    public String createCategory(@RequestBody Category category) {
        // Handle category creation
        return "Category created";
    }

    @GetMapping
    public List<Category> getAllCategories() {
        // Retrieve all categories
        return new ArrayList<>();
    }

    @GetMapping("/{category_id}")
    public Category getCategory(@PathVariable("category_id") Long categoryId) {
        // Retrieve a specific category
        return new Category();
    }

    @PutMapping("/{category_id}")
    public String updateCategory(@PathVariable("category_id") Long categoryId, @RequestBody Category category) {
        // Update a specific category
        return "Category updated";
    }

    @DeleteMapping("/{category_id}")
    public String deleteCategory(@PathVariable("category_id") Long categoryId) {
        // Delete a specific category
        return "Category deleted";
    }
}
