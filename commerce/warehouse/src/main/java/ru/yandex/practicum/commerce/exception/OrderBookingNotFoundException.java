package ru.yandex.practicum.commerce.exception;

public class OrderBookingNotFoundException extends RuntimeException {
    public OrderBookingNotFoundException(String message) {
        super(message);
    }
}
