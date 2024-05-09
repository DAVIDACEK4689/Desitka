package cz.cuni.mff.desitka;

/**
 * This class provides methods to parse command line arguments for the QuestionGenerator.
 */
public class ArgumentParser {

    /**
     * Parses the command line arguments and sets the corresponding properties of the QuestionGenerator.
     * @param args The command line arguments.
     * @param questionGenerator The QuestionGenerator to set the properties for.
     * @return True if the arguments were parsed successfully, false otherwise.
     */
    public static boolean parseArguments(String[] args, QuestionGenerator questionGenerator) {

        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                System.out.println("Missing value for argument: " + args[i]);
                printHelp();
                return false;
            }
            String option = args[i];
            String value = args[i + 1];

            switch (option) {
                case "--startingTitle":
                    questionGenerator.setStartingTitle(value);
                    break;
                case "--categoriesDirectory":
                    questionGenerator.setCategoriesDirectory(value);
                    break;
                case "--questionsDirectory":
                    questionGenerator.setQuestionsDirectory(value);
                    break;
                case "--categoriesViewsLimit":
                    int categoriesViewsLimit = Integer.parseInt(value);
                    questionGenerator.setCategoriesViewsLimit(categoriesViewsLimit);
                    break;
                case "--questionsViewsLimit":
                    String[] values = value.split(",");
                    int[] questionsViewsLimit = new int[values.length];
                    for (int j = 0; j < values.length; j++) {
                        questionsViewsLimit[j] = Integer.parseInt(values[j]);
                    }
                    questionGenerator.setQuestionsViewsLimit(questionsViewsLimit);
                    break;
                default:
                    System.out.println("Unknown option: " + option);
                    printHelp();
                    return false;
            }
        }
        return true;
    }

    /**
     * Prints the help message for the command line arguments.
     */
    private static void printHelp() {
        System.out.println("Usage: java -jar <jarfile> --startingTitle <title> --categoriesDirectory <dir> --questionsDirectory <dir> --categoriesViewsLimit <limit> --questionsViewsLimit <limit1>,<limit2>,...");
    }
}
