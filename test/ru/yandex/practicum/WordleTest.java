package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private static PrintWriter log;
    private static WordleDictionary dictionary;
    private WordleGame game;

    @BeforeAll
    static void init() throws Exception {
        log = new PrintWriter(System.out, true);
        dictionary = new WordleDictionary(List.of("домик", "отель", "город", "весна", "осень", "ветер"));
    }

    @BeforeEach
    void setUp() {
        game = new WordleGame(dictionary, log, "домик");
    }

    @Test
    void analyzeExample() {
        assertEquals("--^+^", WordleGame.analyze("радио", "домик"));
    }

    @Test
    void analyzeDuplicateLetters() {
        assertEquals("--+++", WordleGame.analyze("сосна", "весна"));
        assertEquals("+++++", WordleGame.analyze("весна", "весна"));
    }

    @Test
    void validGuessSpendsStep() throws Exception {
        game.makeGuess("осень");

        assertEquals(5, game.getSteps());
    }

    @Test
    void yoIsEqualToYe() throws Exception {
        assertEquals("+++++", game.makeGuess("ДОМИК"));
        assertTrue(game.isWon());
    }

    @Test
    void invalidWordDoesNotSpendStep() {
        assertThrows(InvalidWordException.class, () -> game.makeGuess(""));
        assertThrows(InvalidWordException.class, () -> game.makeGuess("abcde"));
        assertThrows(InvalidWordException.class, () -> game.makeGuess("кот"));
        assertEquals(WordleGame.MAX_STEPS, game.getSteps());
    }

    @Test
    void unknownWordThrows() {
        assertThrows(WordNotFoundInDictionary.class, () -> game.makeGuess("ааааа"));
        assertEquals(WordleGame.MAX_STEPS, game.getSteps());
    }

    @Test
    void gameEndsAfterSixSteps() throws Exception {
        for (int i = 0; i < WordleGame.MAX_STEPS; i++) {
            game.makeGuess("осень");
        }

        assertTrue(game.isFinished());
        assertFalse(game.isWon());
        assertThrows(IllegalStateException.class, () -> game.makeGuess("осень"));
    }

    @Test
    void computerCanSolveGameByItself() throws Exception {
        for (int attempt = 0; attempt < 50; attempt++) {
            WordleGame g = new WordleGame(dictionary, log, "домик");
            while (!g.isFinished()) {
                g.makeGuess(g.suggestWord());
            }
            assertTrue(g.isWon());
        }
    }
}