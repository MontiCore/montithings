# Running the server
## Intellij
Two Intellij run configurations are provided:
- with stdio
- on port 3000

## Via jar
The server can be run after Gradle build:
- `java -jar target/libs/minimal-cd4a-lsp-1.0.0-SNAPSHOT-jar-with-dependencies.jar`, with stdio
- `java -jar target/libs/minimal-cd4a-lsp-1.0.0-SNAPSHOT-jar-with-dependencies.jar --socket -port 3000`, on port 3000