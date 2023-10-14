package org.example.exceptions.weatherApi;

public class JsonException extends RuntimeException{
    public JsonException(String message){
        super(message);
    }
}