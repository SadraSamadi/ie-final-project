package com.sadrasamadi.iefinalproject.controller;

import com.sadrasamadi.iefinalproject.model.Event;
import com.sadrasamadi.iefinalproject.repository.EventRepository;
import com.sadrasamadi.iefinalproject.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping
public class IndexController {

    private EventRepository eventRepository;

    private StorageService storageService;

    @Autowired
    public IndexController(EventRepository eventRepository,
                           StorageService storageService) {
        this.eventRepository = eventRepository;
        this.storageService = storageService;
    }

    @GetMapping
    public String index(@PageableDefault(size = 8, sort = "created", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        Page<Event> events = eventRepository.findAll(pageable);
        model.addAttribute("events", events);
        return "index";
    }

    @ResponseBody
    @GetMapping("/resource/{name:.+}")
    public ResponseEntity<Resource> resource(@PathVariable String name) {
        Resource resource = storageService.load(name);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

}
