package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "PrimeResponse")
@JsonPropertyOrder({"algorithm", "limit", "threads", "primes", "total", "durationMs"})
public class PrimePayload {

    @JsonProperty("algorithm")
    @JacksonXmlProperty(localName = "algorithm")
    private String algorithm;

    @JsonProperty("limit")
    @JacksonXmlProperty(localName = "limit")
    private int limit;

    @JsonProperty("threads")
    @JacksonXmlProperty(localName = "threads")
    private int threads;

    @JsonProperty("primes")
    @JacksonXmlElementWrapper(localName = "primes")
    @JacksonXmlProperty(localName = "prime")
    private List<Integer> primes;

    @JsonProperty("total")
    @JacksonXmlProperty(localName = "total")
    private int total;

    @JsonProperty("durationMs")
    @JacksonXmlProperty(localName = "durationMs")
    private long durationMs;

    public PrimePayload() {}

}
