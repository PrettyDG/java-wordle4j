package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionary extends WordleGameException {
    public WordNotFoundInDictionary(String word) {
        super("Слова «" + word + "» нет в словаре");
    }
}