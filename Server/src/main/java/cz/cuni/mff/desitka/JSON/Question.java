package cz.cuni.mff.desitka.JSON;

/**
 * The Question class represents a question in the game.
 */
public class Question {
    private final String text;
    private final SubQuestion[] questions;

    /**
     * Constructs a new Question object.
     *
     * @param text The text of the question.
     * @param questions The sub-questions of the question.
     */
    public Question(String text, SubQuestion[] questions) {
        this.text = text;
        this.questions = questions;
    }

    /**
     * Returns the text of the question.
     *
     * @return The text of the question.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the sub-questions of the question.
     *
     * @return The sub-questions of the question.
     */
    public SubQuestion[] getSubQuestions() {
        return questions;
    }

    /**
     * The SubQuestion class represents a sub-question of a question.
     */
    public static class SubQuestion {
        private final String key;
        private final String[] values;
        private int correctIndex;

        /**
         * Constructs a new SubQuestion object.
         *
         * @param key The key of the sub-question.
         * @param values The values of the sub-question.
         * @param correctIndex The index of the correct answer.
         */
        public SubQuestion(String key, String[] values, int correctIndex) {
            this.key = key;
            this.values = values;
            this.correctIndex = correctIndex;
        }

        /**
         * Returns the key of the sub-question.
         *
         * @return The key of the sub-question.
         */
        public String getKey() {
            return key;
        }

        /**
         * Returns the values of the sub-question.
         *
         * @return The values of the sub-question.
         */
        public String[] getValues() {
            return values;
        }

        /**
         * Returns the index of the correct answer.
         *
         * @return The index of the correct answer.
         */
        public int getCorrectIndex() {
            return correctIndex;
        }

        /**
         * Sets the index of the correct answer.
         *
         * @param correctIndex The index of the correct answer.
         */
        public void setCorrectIndex(int correctIndex) {
            this.correctIndex = correctIndex;
        }
    }
}