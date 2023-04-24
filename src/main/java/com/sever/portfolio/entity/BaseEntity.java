package com.sever.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"internalId", "version"})
public class BaseEntity implements Serializable {
    public static final String DEFAULT_DELETED_VALUE = "0";
    public static final String DELETED_VALUE = "1";

    @Transient
    private String internalId;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Access(AccessType.PROPERTY)
    private String id;

    @Version
    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String deleted = DEFAULT_DELETED_VALUE;

    BaseEntity() {
        this.internalId = UUID.randomUUID().toString();
    }

    public void setId(String id) {
        this.id = id;
        this.internalId = id;
    }

    @PrePersist
    void onPersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
            updateTime = createTime;
        }
    }

    @PreUpdate
    void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public void markDeleted() {
        deleted = DELETED_VALUE;
    }
}
