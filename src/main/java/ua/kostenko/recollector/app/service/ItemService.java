package ua.kostenko.recollector.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kostenko.recollector.app.entity.Item;
import ua.kostenko.recollector.app.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item createItem(Item item) {
        // Handle item creation logic
        return itemRepository.save(item);
    }

    public List<Item> getAllItems(Long categoryId) {
        // Retrieve all items in a category
        return itemRepository.findAll(); // Add filtering based on categoryId
    }

    public Item getItem(Long itemId) {
        // Retrieve a specific item
        return itemRepository.findById(itemId).orElse(null);
    }

    public Item updateItem(Long itemId, Item item) {
        // Update item logic
        item.setItemId(itemId);
        return itemRepository.save(item);
    }

    public void deleteItem(Long itemId) {
        // Delete item logic
        itemRepository.deleteById(itemId);
    }
}
