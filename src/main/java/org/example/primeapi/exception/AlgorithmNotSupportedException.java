package org.example.primeapi.exception;


public class AlgorithmNotSupportedException extends RuntimeException {

    public AlgorithmNotSupportedException(String algorithm) {
        super("Unsupported algorithm: " + algorithm);
    }

    public AlgorithmNotSupportedException(String algorithm, Throwable cause) {
        super("Unsupported algorithm: " + algorithm, cause);
    }
}
