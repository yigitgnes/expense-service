package com.atech.calculator.service;

import com.atech.calculator.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class TaskService {

    public List<Task> getAllTasks() {
        return Task.findAll().list();
    }

    public Task getTaskById(Long id) {
        return Task.findById(id);
    }

    @Transactional
    public Task createTask(Task task) {
        if (task.title == null || task.description == null) {
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
            existingTask.title = updatedTask.title;
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
