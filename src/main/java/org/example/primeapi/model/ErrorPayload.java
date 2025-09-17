package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
@JacksonXmlRootElement(localName = "ErrorPayload")
@JsonPropertyOrder({"status", "error", "message", "path"})
public class ErrorPayload {

    @JsonProperty("status")
    @JacksonXmlProperty(localName = "status")
    private int status;

    @JsonProperty("error")
    @JacksonXmlProperty(localName = "error")
    private String error;

    @JsonProperty("message")
    @JacksonXmlProperty(localName = "message")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("path")
    @JacksonXmlProperty(localName = "path")
    private String path;


}