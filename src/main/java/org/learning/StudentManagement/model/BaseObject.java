package org.learning.StudentManagement.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Generated;
import org.springframework.aot.generate.GenerationContext;

@Data
public abstract class BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
}
