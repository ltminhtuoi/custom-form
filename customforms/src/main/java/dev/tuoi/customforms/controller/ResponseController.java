package dev.tuoi.customforms.controller;

import dev.tuoi.customforms.dto.SubmitResponseRequest;
import dev.tuoi.customforms.model.ResponseEntry;
import dev.tuoi.customforms.service.AuthService;
import dev.tuoi.customforms.service.ResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST controller for submitting and viewing responses to forms. */
@RestController
@RequestMapping("/forms")
public class ResponseController {

    private final ResponseService service;
    private final AuthService authService;

    /** Injects response and authentication services. */
    public ResponseController(ResponseService service, AuthService authService) {
        this.service = service; this.authService = authService;
    }

    /** Submits a response to a form as the authenticated user. */
    @PostMapping("/{id}/respond")
    public ResponseEntity<ResponseEntry> submit(Authentication auth,
                                                @PathVariable String id,
                                                @RequestBody SubmitResponseRequest req) {
        String userId = auth.getName();
        String responderEmail = authService.getMe(userId).getEmail();
        ResponseEntry entry = service.submit(id, req, responderEmail);
        return ResponseEntity.ok(entry);
    }

    /** Lists all responses for a form owned by the authenticated user. */
    @GetMapping("/{id}/responses")
    public ResponseEntity<List<ResponseEntry>> list(Authentication auth, @PathVariable String id) {
        String ownerId = auth.getName();
        return ResponseEntity.ok(service.listForOwner(ownerId, id));
    }
}