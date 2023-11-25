# Consumer First API Example

This repository contains a `consumer driven contract` based Client and Server API which implementation using [pact](https://docs.pact.io/implementation_guides/jvm).

## Starting the application

1. Start the application running:
`./gradlew bootRun`

````
./gradlew :client:pactPublish
````

````
./gradlew :client:canIDeploy -Ppacticipant='RecipesClient' -Platest=true
````

````
./gradlew :service:pactVerify -Dpact.verifier.publishResults=true
````