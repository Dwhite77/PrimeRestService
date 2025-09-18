package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Standard error response payload returned when an API request fails")
@JacksonXmlRootElement(localName = "ErrorPayload")
@JsonPropertyOrder({"status", "error", "message", "path"})
public class ErrorPayload {

    @Schema(description = "HTTP status code of the error", example = "400")
    @JsonProperty("status")
    @JacksonXmlProperty(localName = "status")
    private int status;

    @Schema(description = "Short error type or category", example = "Bad Request")
    @JsonProperty("error")
    @JacksonXmlProperty(localName = "error")
    private String error;

    @Schema(description = "Detailed error message explaining the cause", example = "Limit must be non-negative and threads must be >= 1")
    @JsonProperty("message")
    @JacksonXmlProperty(localName = "message")
    private String message;

    @Schema(description = "Request path that triggered the error", example = "/api/primes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("path")
    @JacksonXmlProperty(localName = "path")
    private String path;
}