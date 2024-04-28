package com.atech.calculator.service;

import com.atech.calculator.model.Expense;
import com.atech.calculator.model.Item;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ItemService {

    private Logger LOGGER = Logger.getLogger(ItemService.class);

    public List<Item> getAllItems() throws Exception {
        try{
            List<Item> items = Item.findAll(Sort.by("id")).list();
            if (items.isEmpty()) {
                LOGGER.info("No items found in the database");
            }
            return items;
        }catch (Exception e) {
            LOGGER.error("Error retrieving all items: " + e.getMessage(), e);
            throw new Exception("Error retrieving items from the database.", e);
        }
    }
    public Item getItemById(Long id) throws NotFoundException {
        try {
            Item item = Item.findById(id);
            if (item == null) {
                LOGGER.info("No item found with ID: " + id);
                throw new NotFoundException("Item with ID " + id + " not found.");
            }
            return item;
        } catch (Exception e) {
            LOGGER.error("Error retrieving item by ID " + id + ": " + e.getMessage(), e);
            throw new RuntimeException("Internal server error when fetching item by ID.", e);
        }
    }

    public void createIteam(Item item){
        if(item.name == null || item.name.trim().isEmpty() || item.sale.purchasePrice == null){
            LOGGER.info("Validation failed: Purchase price or Name cannot be empty for the items");
            throw new BadRequestException("Purchase price or Name cannot be empty.");
        }

        try{
            item.persistAndFlush();
            LOGGER.info("A new item is created: " + item.name);
        }catch (Exception error){
            LOGGER.error("Error creating expense: " + error.getMessage());
            throw error;
        }
    }

    public void updateItem(Item receivedItem){
        Item.<Item>findByIdOptional(receivedItem.id).ifPresentOrElse(
                item -> {
                    item.name = receivedItem.name;
                    item.sale.purchasePrice = receivedItem.sale.purchasePrice;
                    item.sale.salePrice = receivedItem.sale.salePrice;
                    item.sale.purchaseDate = receivedItem.sale.purchaseDate;
                    item.sale.saleDate = receivedItem.sale.saleDate;
                    item.persist();
                },
                NotFoundException::new
        );
    }

    public boolean deleteItem(Long id){
        return Item.deleteById(id);
    }
}
