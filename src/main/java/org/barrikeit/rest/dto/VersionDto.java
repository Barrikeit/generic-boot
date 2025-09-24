package org.barrikeit.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionDto {
    private String name;
    private String version;
    private String build;
    private String environment;
}
