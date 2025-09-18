package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Schema(description = "Standard API response wrapper containing either data or error details")
@JsonPropertyOrder({"httpStatus", "data", "error", "timestamp"})
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "APIResponse")
@JsonRootName("APIResponse")
public class APIResponse {

    @Schema(description = "HTTP status code of the response", example = "200")
    @JsonProperty("httpStatus")
    @JacksonXmlProperty(localName = "httpStatus")
    private int httpStatus;

    @Schema(description = "Payload containing prime generation results. Present only on success.")
    @JsonProperty("data")
    @JacksonXmlProperty(localName = "data")
    private PrimePayload data;

    @Schema(description = "Error details if the request failed. Present only on failure.")
    @JsonProperty("error")
    @JacksonXmlProperty(localName = "error")
    private ErrorPayload error;

    @Schema(description = "Timestamp when the response was generated", example = "18-09-2025 15:09:00")
    @JsonProperty("timestamp")
    @JacksonXmlProperty(localName = "timestamp")
    private String timestamp;

    public boolean isSuccessful() {
        return data != null && error == null && httpStatus == 200;
    }

    public static APIResponse success(PrimePayload data, int httpStatus) {
        return APIResponse.builder()
                .data(data)
                .httpStatus(httpStatus)
                .timestamp(nowFormatted())
                .build();
    }

    public static APIResponse error(ErrorPayload error, int httpStatus) {
        return APIResponse.builder()
                .error(error)
                .httpStatus(httpStatus)
                .timestamp(nowFormatted())
                .build();
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    private static String nowFormatted() {
        return TIMESTAMP_FORMATTER.format(Instant.now());
    }
}