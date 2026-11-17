package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    static final int MAX_STEPS = 6;
    private final String answer;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final Random random = new Random();

    private int steps = MAX_STEPS;
    private boolean won = false;
    private final List<String> guesses = new ArrayList<>();
    private final List<String> hints = new ArrayList<>();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, log, dictionary.getRandomWord());
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        if (!dictionary.contains(answer)) {
            throw new IllegalArgumentException("Загаданного слова нет в словаре: " + answer);
        }

        this.dictionary = dictionary;
        this.log = log;
        this.answer = answer;

        log.println("Новая игра, загадано слово: " + answer);
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isFinished() {
        return won || steps == 0;
    }

    public String makeGuess(String input) throws InvalidWordException, WordNotFoundInDictionary {
        if (isFinished()) {
            throw new IllegalStateException("Игра закончилась");
        }

        String word = WordleDictionary.normalize(input);
        if (!WordleDictionary.isValidWord(word)) {
            throw new InvalidWordException("Нужно слово из " + WordleDictionary.WORD_LENGTH + " русских букв");
        }

        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionary(word);
        }

        String hint = analyze(word, answer);
        steps--;
        guesses.add(word);
        hints.add(hint);
        won = word.equals(answer);

        if (steps < 0) {
            throw new IllegalStateException("Шаги закончились -  " + steps);
        }

        log.println("Ход: " + word + " -> " + hint + ", осталось шагов: " + steps + ", победа: " + won);

        return hint;
    }

    public static String analyze(String guess, String answer) {
        int n = answer.length();
        char[] result = new char[n];
        Arrays.fill(result, '-');
        Map<Character, Integer> remaining = new HashMap<>();
        for (int i = 0; i < n; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
            } else {
                remaining.merge(answer.charAt(i), 1, Integer::sum);
            }
        }
        for (int i = 0; i < n; i++) {
            if (result[i] == '+') {
                continue;
            }
            Integer count = remaining.get(guess.charAt(i));
            if (count != null && count > 0) {
                result[i] = '^';
                remaining.put(guess.charAt(i), count - 1);
            }
        }
        return new String(result);
    }

    public String suggestWord() {
        List<String> candidates = new ArrayList<>();
        for (String word : dictionary.getWords()) {
            if (isConsistent(word)) {
                candidates.add(word);
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("Нет подходящих слов для предложения. Ответ должен быть: " + answer);
        }
        String suggestion = candidates.get(random.nextInt(candidates.size()));

        log.println("Подсказка: " + suggestion + " (кандидатов: " + candidates.size() + ")");

        return suggestion;
    }

    private boolean isConsistent(String candidate) {
        for (int i = 0; i < guesses.size(); i++) {
            if (!analyze(guesses.get(i), candidate).equals(hints.get(i))) {
                return false;
            }
        }

        return true;
    }
}