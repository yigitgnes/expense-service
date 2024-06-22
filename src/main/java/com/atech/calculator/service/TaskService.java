package com.atech.calculator.service;

import com.atech.calculator.model.Task;
import com.atech.calculator.model.TaskCategory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class TaskService {

    private Logger LOGGER = Logger.getLogger(TaskService.class);

    public List<Task> getAllTasks() {
        return Task.findAll().list();
    }

    public Task getTaskById(Long id) {
        return Task.findById(id);
    }

    public List<Task> getTasksByCategory(String category){
        TaskCategory taskCategory = TaskCategory.valueOf(category.toUpperCase().replace("-", "_"));
        LOGGER.info("Searching for the category: " + taskCategory);
        return Task.find("category", taskCategory).list();
    }

    @Transactional
    public Task createTask(Task task) {
        if (task.description == null) {
            throw new BadRequestException("Bad Request.!");
        }
        try {
            task.persistAndFlush();
            return task;
        }catch (Exception e){
            throw new RuntimeException("Error while creating task.", e);
        }
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = Task.findById(id);
        if (existingTask != null) {
            existingTask.description = updatedTask.description;
            existingTask.category = updatedTask.category;
            existingTask.completed = updatedTask.completed;
            return existingTask;
        }
        return existingTask;
    }

    @Transactional
    public boolean deleteTask(Long id) {
        return Task.deleteById(id);
    }
}
