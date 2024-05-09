package cz.cuni.mff.desitka.question;

/**
 * This class represents a question with its text and sub-questions.
 */
public class Question {
    private final String text;
    private final SubQuestion[] questions;

    /**
     * Constructs a Question with the specified text and sub-questions.
     * @param text the text of the question
     * @param questions the sub-questions of the question
     */
    public Question(String text, SubQuestion[] questions) {
        this.text = text;
        this.questions = questions;
    }

    /**
     * Returns the text of the question.
     * @return the text of the question
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the sub-questions of the question.
     * @return the sub-questions of the question
     */
    public SubQuestion[] getSubQuestions() {
        return questions;
    }

    /**
     * This class represents a sub-question with its key, values, and the index of the correct answer.
     */
    public static class SubQuestion {
        private final String key;
        private final String[] values;
        private final int correctIndex;

        /**
         * Constructs a SubQuestion with the specified key, values, and the index of the correct answer.
         * @param key the key of the sub-question
         * @param values the values of the sub-question
         * @param correctIndex the index of the correct answer
         */
        public SubQuestion(String key, String[] values, int correctIndex) {
            this.key = key;
            this.values = values;
            this.correctIndex = correctIndex;
        }

        /**
         * Returns the key of the sub-question.
         * @return the key of the sub-question
         */
        public String getKey() {
            return key;
        }

        /**
         * Returns the values of the sub-question.
         * @return the values of the sub-question
         */
        public String[] getValues() {
            return values;
        }

        /**
         * Returns the index of the correct answer.
         * @return the index of the correct answer
         */
        public int getCorrectIndex() {
            return correctIndex;
        }
    }
}