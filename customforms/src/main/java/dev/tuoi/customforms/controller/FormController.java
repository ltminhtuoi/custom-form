package dev.tuoi.customforms.controller;

import dev.tuoi.customforms.service.FormService;
import dev.tuoi.customforms.model.Form;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST controller for managing user-owned forms. */
@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormService service;

    /** Injects the form service */
    public FormController(FormService service) { this.service = service; }

    /** Creates a new form owned by the authenticated user */
    @PostMapping("/create")
    public ResponseEntity<Form> create(Authentication auth, @RequestBody Form form) {
        String ownerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(service.create(ownerId, form));
    }

    /** Retrieves a form by ID (public access) */
    @GetMapping("/{id}")
    public ResponseEntity<Form> getPublic(@PathVariable String id) {
        return ResponseEntity.ok(service.getPublicForm(id));
    }

    /** Lists all forms owned by the authenticated user */
    @GetMapping("/user")
    public ResponseEntity<List<Form>> list(Authentication auth) {
        String ownerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(service.listByOwner(ownerId));
    }

    /** Updates an existing form owned by the authenticated user */
    @PutMapping("/{id}/edit")
    public ResponseEntity<Form> update(Authentication auth,
                                       @PathVariable String id,
                                       @RequestBody Form form) {
        String ownerId = (String) auth.getPrincipal();
        return ResponseEntity.ok(service.update(ownerId, id, form));
    }

    /** Deletes a form owned by the authenticated user */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable String id) {
        String ownerId = (String) auth.getPrincipal();
        service.delete(ownerId, id);
        return ResponseEntity.noContent().build();
    }
}