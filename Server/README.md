# Server
This is the server side of the project. It is responsible for handling the requests
from the client and sending the appropriate responses. It is mapped on localhost:4444.
It is used by [DesitkaOnline](../DesitkaOnline). In directory there is also video
showing how to run the server and the client.

## Running
To run the server, you need to have installed Java version 8 or higher.

### Running using jar
```bash
java -jar server-jar-with-dependencies.jar
```

### Running using maven (maven installation is required)
```bash
mvn exec:java
```
