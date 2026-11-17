package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.DictionaryLoadException;
import ru.yandex.practicum.exceptions.EmptyDictionaryException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String filename) throws DictionaryLoadException, EmptyDictionaryException {
        Set<String> unique = new LinkedHashSet<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                total++;
                String word = WordleDictionary.normalize(line);

                if (WordleDictionary.isValidWord(word)) {
                    unique.add(word);
                }
            }
        } catch (IOException e) {
            throw new DictionaryLoadException("Не удалось прочитать словврь: " + filename, e);
        }

        log.println("Словарь " + filename + ": прочитано строк " + total + ", подходящих слов " + unique.size());

        return new WordleDictionary(new ArrayList<>(unique));
    }
}