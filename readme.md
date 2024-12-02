## How to start an app:

* **Application tested using the following tools:**
    * **openjdk version "17.0.2" 2022-01-18"**
    * **mvn commands should be executed using same jdk version**
    * docker version  "4.16.3 (96739)"
  * Windows 11 Home - 22621.2428


* './mvnw clean install'- to compile the app and run tests
* './mvnw spring-boot:build-image' - to build a docker image "io-task:0.0.1-SNAPSHOT"

* run 'docker compose up' to start a container with io-task app. The app uses port 8080

There is a swagger integration:
http://localhost:8080/swagger-ui/index.html

You can check available rest calls on that page. Every method has a description and a list of parameters.