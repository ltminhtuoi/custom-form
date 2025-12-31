package dev.tuoi.customforms.service;

import dev.tuoi.customforms.common.ApiException;
import dev.tuoi.customforms.model.Form;
import dev.tuoi.customforms.model.Question;
import dev.tuoi.customforms.model.QuestionType;
import dev.tuoi.customforms.repo.FormRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Service for creating, retrieving, updating, and deleting forms with ownership checks. */
@Service
public class FormService {

    private final FormRepository repo;

    /** Injects the form repository. */
    public FormService(FormRepository repo) {
        this.repo = repo;
    }

    /** Creates a new form owned by the given user; adds default question if none provided. */
    public Form create(String ownerId, Form form) {
        form.setId(null);
        form.setOwnerId(ownerId);

        if (form.getQuestions() == null || form.getQuestions().isEmpty()) {
            form.setQuestions(List.of(createDefaultQuestion()));
        }

        return repo.save(form);
    }

    /** Returns a default untitled multiple-choice question. */
    private static Question createDefaultQuestion() {
        return new Question(
                UUID.randomUUID().toString(),
                "Untitled question",
                "",
                QuestionType.MULTIPLE_CHOICE,
                false,
                List.of("Option 1")
        );
    }

    /** Retrieves a form by ID if it is public. */
    public Form getPublicForm(String id) {
        Form f = repo.findById(id).orElseThrow(() -> new ApiException("Form not found", 404));
        if (!f.isPublic()) throw new ApiException("Form is not public", 403);
        return f;
    }

    /** Retrieves a form by ID if owned by the specified user. */
    public Form getOwnedForm(String id, String ownerId) {
        Form f = repo.findById(id).orElseThrow(() -> new ApiException("Form not found", 404));
        if (!f.getOwnerId().equals(ownerId)) throw new ApiException("Forbidden", 403);
        return f;
    }

    /** Lists all forms owned by the given user. */
    public List<Form> listByOwner(String ownerId) {
        return repo.findByOwnerId(ownerId);
    }

    /** Updates fields of an owned form and sets updated timestamp. */
    public Form update(String ownerId, String id, Form update) {
        Form f = getOwnedForm(id, ownerId);
        f.setTitle(update.getTitle());
        f.setDescription(update.getDescription());
        f.setPublic(update.isPublic());
        f.setQuestions(update.getQuestions());
        f.setUpdatedAt(Instant.now());
        return repo.save(f);
    }

    /** Deletes a form owned by the specified user. */
    public void delete(String ownerId, String id) {
        Form f = getOwnedForm(id, ownerId);
        repo.delete(f);
    }
}