package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.DictionaryLoadException;
import ru.yandex.practicum.exceptions.EmptyDictionaryException;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryTest {

    @Test
    void normalize() {
        assertEquals("елка", WordleDictionary.normalize("  Ёлка "));
        assertEquals("", WordleDictionary.normalize(null));
    }

    @Test
    void validation() {
        assertTrue(WordleDictionary.isValidWord("осень"));
        assertFalse(WordleDictionary.isValidWord("осен"));
        assertFalse(WordleDictionary.isValidWord("words"));
        assertFalse(WordleDictionary.isValidWord("осен1"));
    }

    @Test
    void emptyDictionaryRejected() {
        assertThrows(EmptyDictionaryException.class, () -> new WordleDictionary(List.of()));
    }

    @Test
    void missingFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(System.out, true));
        assertThrows(DictionaryLoadException.class, () -> loader.load("файл.txt"));
    }
}