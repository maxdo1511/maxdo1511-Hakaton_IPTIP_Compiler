package ru.hbb.hakaton_3.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SampleModelRequest {

    private String name;
    private String data;
    private boolean isSaveMode;
}
