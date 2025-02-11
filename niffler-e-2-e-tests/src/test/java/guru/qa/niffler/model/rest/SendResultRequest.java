package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record SendResultRequest(List<Result> results) {

    public record Result(@JsonProperty("file_name") String fileName,
                         @JsonProperty("content_base64") String contentBase64) {}
}
