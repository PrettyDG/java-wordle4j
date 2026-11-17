package ru.yandex.practicum.exceptions;

public class DictionaryLoadException extends WordleProgramException {
    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}