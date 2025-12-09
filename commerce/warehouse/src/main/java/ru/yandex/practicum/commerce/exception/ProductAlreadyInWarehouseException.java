package ru.yandex.practicum.commerce.exception;

public class ProductAlreadyInWarehouseException extends RuntimeException {
    public ProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}
