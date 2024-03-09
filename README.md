# Quarkus State Machine
This project is a fun experiment in creating a state machine for Quarkus applications. Currently, there isn't a readily available state machine implementation specifically tailored for Quarkus, so I decided to create one. I plan to push this to Maven Central soon for wider use.

## How to Use
Extend the StateMachine class: Start by extending the StateMachine class and provide the following parameters:

1. ActionTypeEnum class
2. StateEnumClass
3. Entity class for which these states and actions are applicable.
Define Rules: In your Quarkus application, either add the rules for the states in the Main Class (using addRule()) or implement the addRulesOnStartUp method and annotate it with @Startup.

## Usage:

Check if the current state of the entity is acceptable for the action you want to perform. The functionality provided includes:
1. Get the final State on completing the action.
2. Check the validity of the action on current state
3. Implement the process() function as per your requirement and call the performAction() function provided.
Integration with State Design Pattern
I'm using this state machine in conjunction with a state design pattern in my project, considering the complexity of states and aiming for loose coupling.

## Feedback and Contributions
This project is a work in progress, and I welcome feedback and contributions. Feel free to get in touch with me at abhi.pachpande@gmail.com.

## Disclaimer
Please note that this code is still in development, and while it's functional, it may not be suitable for production use without thorough testing and refinement.


## Quarkus -

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/state-machine-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
