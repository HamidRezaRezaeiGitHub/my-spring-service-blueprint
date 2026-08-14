package com.example.application.persistence.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class SoftDeletableDto extends UpdatableEntityDto {

    @Builder.Default
    @Schema(description = "Whether the entity is included in active results and not soft-deleted", example = "true")
    private boolean included = true;
}
