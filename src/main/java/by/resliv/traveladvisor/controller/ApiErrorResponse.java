package by.resliv.traveladvisor.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private String timestamp;
    private String status;
    private int code;
    private List<String> messages;
    private String path;
}
