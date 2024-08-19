package ua.kostenko.recollector.app.controller;

import org.springframework.web.bind.annotation.*;
import ua.kostenko.recollector.app.entity.Item;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories/{categoryId}/items")
public class ItemController {

    @PostMapping
    public String createItem(@PathVariable("category_id") Long categoryId, @RequestBody Item item) {
        // Handle item creation
        return "Item created";
    }

    @GetMapping
    public List<Item> getAllItems(@PathVariable("category_id") Long categoryId) {
        // Retrieve all items in a category
        return new ArrayList<>();
    }

    @GetMapping("/{item_id}")
    public Item getItem(@PathVariable("category_id") Long categoryId, @PathVariable("item_id") Long itemId) {
        // Retrieve a specific item
        return new Item();
    }

    @PutMapping("/{item_id}")
    public String updateItem(@PathVariable("category_id") Long categoryId, @PathVariable("item_id") Long itemId,
                             @RequestBody Item item) {
        // Update a specific item
        return "Item updated";
    }

    @DeleteMapping("/{item_id}")
    public String deleteItem(@PathVariable("category_id") Long categoryId, @PathVariable("item_id") Long itemId) {
        // Delete a specific item
        return "Item deleted";
    }
}
