package com.sadrasamadi.iefinalproject.controller;

import com.sadrasamadi.iefinalproject.model.Event;
import com.sadrasamadi.iefinalproject.model.Participation;
import com.sadrasamadi.iefinalproject.model.User;
import com.sadrasamadi.iefinalproject.repository.EventRepository;
import com.sadrasamadi.iefinalproject.repository.ParticipationRepository;
import com.sadrasamadi.iefinalproject.service.StorageService;
import com.sadrasamadi.iefinalproject.util.SecurityUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/events")
public class EventController {

    private EventRepository eventRepository;

    private StorageService storageService;

    private ParticipationRepository participationRepository;

    @Autowired
    public EventController(EventRepository eventRepository,
                           StorageService storageService,
                           ParticipationRepository participationRepository) {
        this.eventRepository = eventRepository;
        this.storageService = storageService;
        this.participationRepository = participationRepository;
    }

    @GetMapping("/add")
    public String add(Model model) {
        Event event = new Event();
        model.addAttribute("event", event);
        return "event";
    }

    @PostMapping
    public String create(@ModelAttribute @Valid Event event,
                         BindingResult result,
                         @RequestParam MultipartFile file,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("event", event);
            return "event";
        }
        if (file == null || file.isEmpty()) {
            result.rejectValue("poster", "error.poster", "لطفا تصویر را انتخاب کنید");
            model.addAttribute("event", event);
            return "event";
        }
        String name = storageService.save(file);
        event.setPoster(name);
        User user = SecurityUtility.currentUser();
        event.setCreated(new Date());
        event.setUser(user);
        eventRepository.save(event);
        redirect.addFlashAttribute("eventCreated", "رویداد با موفقیت ایجاد شد");
        return "redirect:/events/management";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable long id, Model model) {
        Optional<Event> optional = eventRepository.findById(id);
        if (optional.isPresent()) {
            Event event = optional.get();
            model.addAttribute("event", event);
            if (SecurityUtility.isAuthenticated()) {
                User user = SecurityUtility.currentUser();
                Participation participation = participationRepository.findByEventAndUser(event, user);
                model.addAttribute("participation", participation);
            }
            return "event-detail";
        }
        return "error";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        Optional<Event> optional = eventRepository.findById(id);
        if (optional.isPresent()) {
            Event event = optional.get();
            model.addAttribute("event", event);
            model.addAttribute("edit", true);
            return "event";
        }
        return "error";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable long id,
                         @ModelAttribute @Valid Event event,
                         BindingResult result,
                         @RequestParam MultipartFile file,
                         Model model,
                         RedirectAttributes redirect) {
        Optional<Event> optional = eventRepository.findById(id);
        if (!optional.isPresent())
            return "error";
        Event old = optional.get();
        if (result.hasErrors()) {
            model.addAttribute("event", event);
            model.addAttribute("edit", true);
            return "event";
        }
        if (file != null && !file.isEmpty()) {
            String poster = old.getPoster();
            String name = storageService.save(file);
            old.setPoster(name);
            storageService.delete(poster);
        }
        old.setTitle(event.getTitle());
        old.setDescription(event.getDescription());
        old.setStart(event.getStart());
        old.setFinish(event.getFinish());
        old.setLocation(event.getLocation());
        eventRepository.save(old);
        redirect.addFlashAttribute("eventUpdated", "رویداد با موفقیت ویرایش شد");
        return "redirect:/events/management";
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        Optional<Event> optional = eventRepository.findById(id);
        if (optional.isPresent()) {
            Event event = optional.get();
            String poster = event.getPoster();
            List<Participation> participations = participationRepository.findAllByEvent(event);
            participationRepository.deleteAll(participations);
            eventRepository.delete(event);
            storageService.delete(poster);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/participate/{id}")
    public ResponseEntity<Void> participate(@PathVariable long id) {
        Optional<Event> optional = eventRepository.findById(id);
        if (!optional.isPresent())
            return ResponseEntity.notFound().build();
        Event event = optional.get();
        User user = SecurityUtility.currentUser();
        Participation participation = new Participation();
        participation.setEvent(event);
        participation.setUser(user);
        participation.setStatus(Participation.Status.PENDING);
        participationRepository.save(participation);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/management")
    public String management(Model model) {
        User user = SecurityUtility.currentUser();
        List<Event> events = eventRepository.findAllByUser(user);
        model.addAttribute("events", events);
        return "event-management";
    }

    @GetMapping("/participations/{id}")
    public String participations(@PathVariable long id, Model model) {
        Optional<Event> optional = eventRepository.findById(id);
        if (!optional.isPresent())
            return "error";
        Event event = optional.get();
        model.addAttribute("event", event);
        List<Participation> participations = participationRepository.findAllByEvent(event);
        model.addAttribute("participations", participations);
        return "participations";
    }

    @ResponseBody
    @PostMapping("/participation-status/{id}")
    public ResponseEntity<Void> participationStatus(@PathVariable long id,
                                                    @RequestParam Participation.Status status) {
        Optional<Participation> optional = participationRepository.findById(id);
        if (!optional.isPresent())
            return ResponseEntity.notFound().build();
        Participation participation = optional.get();
        participation.setStatus(status);
        participationRepository.save(participation);
        return ResponseEntity.ok().build();
    }

}
