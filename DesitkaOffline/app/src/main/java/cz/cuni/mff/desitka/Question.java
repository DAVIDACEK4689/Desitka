package cz.cuni.mff.desitka;

/**
 * This class represents a question with its text and sub-questions.
 */
public class Question {
    private final String text;
    private final SubQuestion[] questions;

    /**
     * Constructor for the Question class.
     * @param text The text of the question.
     * @param questions An array of sub-questions.
     */
    public Question(String text, SubQuestion[] questions) {
        this.text = text;
        this.questions = questions;
    }

    /**
     * Getter for the text of the question.
     * @return The text of the question.
     */
    public String getText() {
        return text;
    }

    /**
     * Getter for the sub-questions of the question.
     * @return An array of sub-questions.
     */
    public SubQuestion[] getSubQuestions() {
        return questions;
    }

    /**
     * This class represents a sub-question with its key, values, and correct index.
     */
    public static class SubQuestion {
        private final String key;
        private final String[] values;
        private int correctIndex;

        /**
         * Constructor for the SubQuestion class.
         * @param key The key of the sub-question.
         * @param values The values of the sub-question.
         * @param correctIndex The correct index of the sub-question.
         */
        public SubQuestion(String key, String[] values, int correctIndex) {
            this.key = key;
            this.values = values;
            this.correctIndex = correctIndex;
        }

        /**
         * Getter for the key of the sub-question.
         * @return The key of the sub-question.
         */
        public String getKey() {
            return key;
        }

        /**
         * Getter for the values of the sub-question.
         * @return The values of the sub-question.
         */
        public String[] getValues() {
            return values;
        }

        /**
         * Getter for the correct index of the sub-question.
         * @return The correct index of the sub-question.
         */
        public int getCorrectIndex() {
            return correctIndex;
        }

        /**
         * Setter for the correct index of the sub-question.
         * @param correctIndex The correct index to be set.
         */
        public void setCorrectIndex(int correctIndex) {
            this.correctIndex = correctIndex;
        }
    }
}