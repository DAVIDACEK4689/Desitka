# Question generator
This is questions generator which uses Wikipedia categorization to generate questions.
More details about generating can be found in [thesis](../thesis.pdf).

## Command line parameters
| Parameter | Description |
| --- | --- |
| `--startingTitle <title>` | The name of the initial category on Wikipedia |
| `--categoriesDirectory <dir>` | Path to the directory where the obtained categories will be saved |
| `--questionsDirectory <dir>` | Path to the directory where the generated questions will be saved |
| `--categoriesViewsLimit <limit>` | The limit for considering an article as relevant |
| `--questionsViewsLimit <limit1>,<limit2>,...` | The limit for adding an article to the question |


## Running
To run the generator, you need to have installed Java version 8 or higher.

### Run using jar
Run without parameters. The default parameters will be used.
```bash
java -jar generator-jar-with-dependencies.jar
```

Specify some parameter.
```bash
java -jar generator-jar-with-dependencies.jar --startingTitle Kategorie:Obrazy_podle_malíře
```

### Run using maven
To run the generator using maven, you need to have installed maven version.

This is the same as running the jar file.
```bash
mvn exec:java
```

Run tests. Three small tests are included.
```bash
mvn test
```
