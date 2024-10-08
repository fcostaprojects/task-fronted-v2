package com.empresa.tasksfrontend.controllers;

import com.empresa.tasksfrontend.models.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class TasksController {

    @Value("${backend.host}")
    private String BACKEND_HOST;

    @Value("${backend.port}")
    private String BACKEND_PORT;

    @Value("${app.version}")
    private String VERSION;

    @Value("${backend.path}")
    private String CONTEXT_PATH;

    public String getBackendURL() {
        return "http://" + BACKEND_HOST + ":" + BACKEND_PORT + "/" + CONTEXT_PATH;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", getTasks());
        if(VERSION.startsWith("build"))
            model.addAttribute("version", VERSION);
        return "index";
    }

    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("task", new Task());
        return "add";
    }

    @PostMapping("save")
    public String save(Task task, Model model) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(
                    getBackendURL() + "/tasks", task, Object.class);
            model.addAttribute("sucess", "Sucess!");
            return "index";
        } catch(Exception e) {
            Pattern compile = Pattern.compile("message\":\"(.*)\",");
            Matcher m = compile.matcher(e.getMessage());
            m.find();
            model.addAttribute("error", m.group(1));
            model.addAttribute("task", task);
            return "add";
        } finally {
            model.addAttribute("tasks", getTasks());
        }
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable UUID id, Model model) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(getBackendURL() + "/tasks/" + id);
        model.addAttribute("success", "Success!");
        model.addAttribute("tasks", getTasks());
        return "index";
    }


    @SuppressWarnings("unchecked")
    private List<Task> getTasks() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                getBackendURL() + "/tasks", List.class);
    }
}
