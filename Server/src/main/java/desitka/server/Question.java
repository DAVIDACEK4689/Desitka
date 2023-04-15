package desitka.server;

import java.util.List;

/**
 * A class representing a question.
 */
public class Question {
    private int id;
    private String text;
    private List<SubQuestion> questions;

    /**
     * Checks if the answer is correct.
     * @param questionID the ID of the sub-question
     * @param answer the answer to check
     * @return true if the answer is correct, false otherwise
     */
    public boolean isAnswerCorrect(int questionID, String answer) {
        return questions.get(questionID).value.equalsIgnoreCase(answer);
    }

    /**
     * Get text of the question.
     * @return the text of the question
     */
    public String getText() {
        return text;
    }

    /**
     * Get sub-questions of question.
     * @return the sub-questions of the question
     */
    public List<SubQuestion> getSubQuestions() {
        return questions;
    }

    /**
     * A class representing a sub-question.
     */
    static class SubQuestion {
        private String name;
        private String value;

        /**
         * Get text of the sub-question.
         * @return the text of the sub-question
         */
        public String getText() {
            return name;
        }

        /**
         * Get the correct answer of the sub-question.
         * @return the correct answer of the sub-question
         */
        public String getAnswer() {
            return value;
        }
    }
}

