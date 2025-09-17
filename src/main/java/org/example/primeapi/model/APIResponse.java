package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@JsonPropertyOrder({"httpStatus", "data", "error", "timestamp"})
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "APIResponse")
@JsonRootName("APIResponse")
public class APIResponse {

    @JsonProperty("httpStatus")
    @JacksonXmlProperty(localName = "httpStatus")
    private int httpStatus;

    @JsonProperty("data")
    @JacksonXmlProperty(localName = "data")
    private PrimePayload data;

    @JsonProperty("error")
    @JacksonXmlProperty(localName = "error")
    private ErrorPayload error;

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