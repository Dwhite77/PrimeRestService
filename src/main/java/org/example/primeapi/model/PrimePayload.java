package org.example.primeapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Payload containing prime generation results")
@Builder
@AllArgsConstructor
@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "PrimeResponse")
@JsonPropertyOrder({"algorithm", "limit", "threads", "primes", "total", "durationMs"})
public class PrimePayload {

    @Schema(description = "Algorithm used for prime generation", example = "sieve")
    @JsonProperty("algorithm")
    @JacksonXmlProperty(localName = "algorithm")
    private String algorithm;

    @Schema(description = "Upper limit for prime generation", example = "1000")
    @JsonProperty("limit")
    @JacksonXmlProperty(localName = "limit")
    private int limit;

    @Schema(description = "Number of threads used for computation", example = "4")
    @JsonProperty("threads")
    @JacksonXmlProperty(localName = "threads")
    private int threads;

    @Schema(description = "List of prime numbers generated")
    @JsonProperty("primes")
    @JacksonXmlElementWrapper(localName = "primes")
    @JacksonXmlProperty(localName = "prime")
    private List<Integer> primes;

    @Schema(description = "Total number of primes found", example = "168")
    @JsonProperty("total")
    @JacksonXmlProperty(localName = "total")
    private int total;

    @Schema(description = "Time taken to compute primes in milliseconds", example = "12")
    @JsonProperty("durationMs")
    @JacksonXmlProperty(localName = "durationMs")
    private long durationMs;

    public PrimePayload() {}
}