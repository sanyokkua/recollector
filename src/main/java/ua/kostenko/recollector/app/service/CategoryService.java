package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.entity.Category;
import ua.kostenko.recollector.app.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        // Handle category creation logic
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        // Retrieve all categories
        return categoryRepository.findAll();
    }

    public Category getCategory(Long categoryId) {
        // Retrieve a specific category
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public Category updateCategory(Long categoryId, Category category) {
        // Update category logic
        category.setCategoryId(categoryId);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        // Delete category logic
        categoryRepository.deleteById(categoryId);
    }
}
