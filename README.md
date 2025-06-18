# 🧪 Provider Platform QA Automation

This project contains automated tests for verifying core patient management functionality in the DTX+ Provider Platform. The tests are written in **Java**, use **Selenium WebDriver**, and are run with **TestNG** via **Maven**.

---

## 🚀 Setup Instructions

1. **Install Java 11+**
2. **Install Maven**
3. **Clone the Repository**

```bash
git clone https://github.com/Ragavpatel/demo.git
cd DemoTest

```

## 🚀 Running Tests
``` 
mvn test                                                           - run all tests
mvn test -Dtest=ProviderPlatformTest                               - run a specific test class
mvn test -Dtest=ProviderPlatformTest#loginWithValidCredentials     - run a specific test method 

```

## Project Structure
````
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── tests
│   │   │       └── AddPatientDOBTest.java`
````

## Tools & Dependencies
````
Selenium WebDriver
TestNG
WebDriverManager (for auto ChromeDriver setup)
Maven (build and test runner)
````
