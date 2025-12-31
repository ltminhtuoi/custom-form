package dev.tuoi.customforms.repo;

import dev.tuoi.customforms.model.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends MongoRepository<Form, String> {
    List<Form> findByOwnerId(String ownerId);
}
