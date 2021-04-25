package com.flair.bi.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealmDTO {
    private Long id;
    private String name;
    private String token;

    public RealmDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
