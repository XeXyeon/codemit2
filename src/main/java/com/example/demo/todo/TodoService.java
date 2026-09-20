package com.example.demo.todo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse createTodo(TodoCreateRequest request) {
        Todo todo = new Todo(request.getTitle());
        Todo savedTodo = todoRepository.save(todo);

        return TodoResponse.from(savedTodo);
    }

    public List<TodoResponse> getTodos() {
        return todoRepository.findAll()
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    public TodoResponse getTodo(Long id) {
        Todo todo = getTodoEntity(id);

        return TodoResponse.from(todo);
    }

    public TodoResponse updateTodo(Long id, TodoUpdateRequest request) {
        Todo todo = getTodoEntity(id);

        todo.update(request.getTitle(), request.getCompleted());

        Todo savedTodo = todoRepository.save(todo);

        return TodoResponse.from(savedTodo);
    }

    public void deleteTodo(Long id) {
        Todo todo = getTodoEntity(id);

        todoRepository.delete(todo);
    }

    private Todo getTodoEntity(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}