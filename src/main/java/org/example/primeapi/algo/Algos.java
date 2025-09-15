package org.example.primeapi.algo;

import java.util.Optional;

public enum Algos {
    TRIAL, SIEVE, ATKINS, MILLER;

    public static Optional<Algos> from(String value) {
        try {
            return Optional.of(Algos.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static boolean isValidAlgo(String value){
        if(from(value).isEmpty()){
            return false;
        }else return true;
    }
}
