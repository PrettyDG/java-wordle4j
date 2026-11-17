package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordleGameException;
import ru.yandex.practicum.exceptions.WordleProgramException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8))) {
            try {
                run(log);
            } catch (Exception e) {
                log.println("Критическая ошибка:");
                e.printStackTrace(log);
                System.out.println("Произошла ошибка программы, подробности в файле " + LOG_FILE);
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл " + LOG_FILE);
        }
    }

    private static void run(PrintWriter log) throws WordleProgramException {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
        WordleGame game = new WordleGame(dictionary, log);

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Угадайте слово из 5 букв. Отправьте пустую строку, чтобы получить подсказку.");
            System.out.println("'-' нет буквы, '+' буква на месте, '^' буква в другом месте.");
            while (!game.isFinished() && scanner.hasNextLine()) {
                System.out.println("Осталось попыток: " + game.getSteps());
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    System.out.println("Подсказка: " + game.suggestWord());
                    continue;
                }
                try {
                    System.out.println(game.makeGuess(line));
                } catch (WordleGameException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        System.out.println(game.isWon() ? "Победа!" : "Попытки закончились.");
        System.out.println("Загаданное слово: " + game.getAnswer());
    }
}