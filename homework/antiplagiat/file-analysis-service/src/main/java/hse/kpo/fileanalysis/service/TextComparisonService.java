package hse.kpo.fileanalysis.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис сравнения текстов для определения плагиата.
 *
 * Алгоритм: подсчет совпадающих слов между двумя текстами.
 * Процент схожести = (количество совпадающих слов * 2) / (общее количество слов в обоих текстах) * 100
 */
@Service
public class TextComparisonService {

    private static final double PLAGIARISM_THRESHOLD = 50.0;

    /**
     * Извлекает множество нормализованных слов из текста.
     * Убирает знаки пунктуации, приводит к нижнему регистру.
     */
    public Set<String> extractWords(String text) {
        if (text == null || text.isBlank()) {
            return new HashSet<>();
        }

        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-zа-яё0-9\\s]", " ")
                        .split("\\s+"))
                .filter(word -> word.length() > 2)
                .collect(Collectors.toSet());
    }

    /**
     * Вычисляет процент схожести между двумя текстами.
     * Используется коэффициент Жаккара: |A ∩ B| / |A ∪ B| * 100
     */
    public double calculateSimilarity(String text1, String text2) {
        Set<String> words1 = extractWords(text1);
        Set<String> words2 = extractWords(text2);

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size() * 100;
    }

    /**
     * Проверяет, является ли схожесть достаточной для определения плагиата.
     */
    public boolean isPlagiarism(double similarityPercentage) {
        return similarityPercentage >= PLAGIARISM_THRESHOLD;
    }

    /**
     * Возвращает детальную информацию о сравнении.
     */
    public String getComparisonDetails(String text1, String text2, double similarity) {
        Set<String> words1 = extractWords(text1);
        Set<String> words2 = extractWords(text2);

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        return String.format(
                "Уникальных слов в работе 1: %d, в работе 2: %d. Совпадающих слов: %d. Схожесть: %.2f%%",
                words1.size(), words2.size(), intersection.size(), similarity
        );
    }

    public double getPlagiarismThreshold() {
        return PLAGIARISM_THRESHOLD;
    }
}

