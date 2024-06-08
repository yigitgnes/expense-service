package com.atech.calculator.service;

import com.atech.calculator.model.Item;
import com.atech.calculator.model.dto.MonthlySalesDataDTO;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ApplicationScoped
public class ItemService {

    @PersistenceContext
    EntityManager entityManager;

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

    public List<MonthlySalesDataDTO> getMonthlySalesForCurrentYear() {
        int currentYear = Year.now().getValue();
        LOGGER.info("Current Year: " + currentYear);

        String queryStr = "SELECT EXTRACT(MONTH FROM s.saleDate) AS sale_month, COUNT(s) AS record_count " +
                "FROM Sale s " +
                "WHERE EXTRACT(YEAR FROM s.saleDate) = ?1 " +
                "GROUP BY EXTRACT(MONTH FROM s.saleDate) " +
                "ORDER BY EXTRACT(MONTH FROM s.saleDate)";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1, currentYear);
        List<Object[]> results = query.getResultList();
        List<HashMap<String, Object>> monthlySalesData = new ArrayList<>();

        for (Object[] result : results) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("sale_month", ((Number) result[0]).intValue());
            map.put("record_count", ((Number) result[1]).longValue());
            monthlySalesData.add(map);
        }

        List<MonthlySalesDataDTO> monthlySales = new ArrayList<>();

        for (HashMap<String, Object> data : monthlySalesData) {
            int month = (int) data.get("sale_month");
            long sales = (long) data.get("record_count");

            Month monthEnum = Month.of(month);
            String formattedMonth = monthEnum.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            monthlySales.add(new MonthlySalesDataDTO(formattedMonth, sales));
        }

        return monthlySales;
    }
    public boolean deleteItem(Long id){
        return Item.deleteById(id);
    }
}
