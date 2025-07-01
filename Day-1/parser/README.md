# Person File Parser Console Application

A simple Java console application to parse files containing `Person` data in various formats (JSON, XML, YAML, CSV). The app scans an input directory, parses the first file found, prints the entity, and deletes the file.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Installation

1. **Clone the repository** (if needed) and navigate to the `parser` directory:
    ```sh
    cd parser
    ```

2. **Build the project:**
    ```sh
    mvn clean package
    ```

### Running the Application

Run the console app using Maven:
```sh
mvn exec:java -Dexec.mainClass="com.example.parser.App"
```
Or, if you prefer to run the JAR (if main class is set in the manifest):
```sh
java -cp target/parser-0.0.1-SNAPSHOT.jar com.example.parser.App
```

---

## Usage

1. **Prepare Input Files:**
    - Create a directory named `input_files` in the project root (if it doesn't exist).
    - Place your test files (e.g., `test.json`, `test.xml`, `test.yaml`, `test.csv`) inside `input_files`.

2. **Run the application.**
3. **Menu Options:**
    - `1`: Search and parse the first file in `input_files`. Prints the parsed `Person` and deletes the file.
    - `2`: Exit the application.

---

## Example Input Files

**test.json**
```json
{
  "name": "Mahmoud",
  "age": 21,
  "gender": "Male",
  "address": "Mansoura"
}
```

**test.xml**
```xml
<Person>
  <name>Mahmoud</name>
  <age>21</age>
  <gender>Male</gender>
  <address>Mansoura</address>
</Person>
```

**test.yaml**
```yaml
name: Mahmoud
age: 21
gender: Male
address: Mansoura
```

**test.csv**
```csv
Mahmoud,21,Male,Mansoura
```

---

## Project Structure

- `App.java` - Main console application
- `entity/Person.java` - The Person entity
- `parsers/` - Parsers for each supported format
- `factory/PersonParserFactory.java` - Chooses the correct parser
- `util/FileUtils.java` - File operations
- `util/Constants.java` - Configuration

---

## Notes

- Only the first file found in `input_files` is processed per menu selection.