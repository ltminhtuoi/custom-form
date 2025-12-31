package dev.tuoi.customforms.repo;

import dev.tuoi.customforms.model.ResponseEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends MongoRepository<ResponseEntry, String> {
    List<ResponseEntry> findByFormId(String formId);
}
