package ru.yandex.practicum.commerce.exception;

public class ProductNotFoundInCartException extends RuntimeException {
    public ProductNotFoundInCartException(String message) {
        super(message);
    }
}
