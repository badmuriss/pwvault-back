package com.outis.pwvault.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Secret {

    private String id;
    private String name;
    private String folder;
    private String value;

}
