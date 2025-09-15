package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "ApiResponse")
@JsonRootName("ApiResponse")
public class ApiResponse {

    @JsonProperty("data")
    @JacksonXmlProperty(localName = "data")
    private PrimePayload data;

    @JsonProperty("error")
    @JacksonXmlProperty(localName = "error")
    private ErrorPayload error;

    @JsonProperty("timestamp")
    @JacksonXmlProperty(localName = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Instant timestamp;
}