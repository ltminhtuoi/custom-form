package dev.tuoi.customforms.service;

import dev.tuoi.customforms.common.ApiException;
import dev.tuoi.customforms.dto.SubmitResponseRequest;
import dev.tuoi.customforms.model.Form;
import dev.tuoi.customforms.model.ResponseEntry;
import dev.tuoi.customforms.repo.FormRepository;
import dev.tuoi.customforms.repo.ResponseRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/** Service for submitting responses to forms and retrieving responses for owners. */
@Service
public class ResponseService {

    private final ResponseRepository repo;
    private final FormRepository formRepo;

    /** Injects response and form repositories. */
    public ResponseService(ResponseRepository repo, FormRepository formRepo) {
        this.repo = repo;
        this.formRepo = formRepo;
    }

    /** Submits a response to a public form; validates form existence and visibility. */
    public ResponseEntry submit(String formId, SubmitResponseRequest req, String responderEmail) {
        Form f = formRepo.findById(formId)
                .orElseThrow(() -> new ApiException("Form not found", 404));
        if (!f.isPublic()) throw new ApiException("Form is not public", 403);

        ResponseEntry entry = ResponseEntry.builder()
                .formId(formId)
                .submittedAt(Instant.now())
                .responderEmail(responderEmail)
                .answers(req.getAnswers())
                .build();

        return repo.save(entry);
    }

    /** Returns all responses for a form owned by the specified user. */
    public List<ResponseEntry> listForOwner(String ownerId, String formId) {
        Form f = formRepo.findById(formId)
                .orElseThrow(() -> new ApiException("Form not found", 404));
        if (!f.getOwnerId().equals(ownerId)) throw new ApiException("Forbidden", 403);

        return repo.findByFormId(formId);
    }
}