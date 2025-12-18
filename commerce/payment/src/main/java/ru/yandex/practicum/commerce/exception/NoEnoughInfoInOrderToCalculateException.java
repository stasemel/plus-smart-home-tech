package ru.yandex.practicum.commerce.exception;

public class NoEnoughInfoInOrderToCalculateException extends RuntimeException {
    public NoEnoughInfoInOrderToCalculateException(String message) {
        super(message);
    }
}
