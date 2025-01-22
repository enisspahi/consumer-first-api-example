# Consumer First API Example

This repository contains a `consumer driven contract` based Client and Server API which implementation using [pact](https://docs.pact.io/implementation_guides/jvm).

## Starting Pact Broker and verify on [http://localhost:9292/](http://localhost:9292/)
````
docker compose -f .docker/docker-compose.yml up -d
````
### Create PROD as environment
````
pact-broker  create-environment --broker-base-url http://localhost:9292/ --name prod --display-name PROD --production
````

## Consumer First API Development process

### 1. Consumer implements and performs consumer contract testing
``
./gradlew :client:test
``

### 2. Client publishes generated pacts to broker
``
./gradlew :client:pactPublish
``

### 3. Provider verify API implementation

At this point provider implementation may still be in-progress.

``
./gradlew :service:test
``

### 4. Provider deploy API

Provider implementation done. Therefore we can deploy

``
./gradlew :service:bootRun
``

### 5. Provider performs Pact Verify against deployed application and publishes verification results 
``
./gradlew :service:pactVerify -Dpact.verifier.publishResults=true
``

#### Optional: Record production deployment
````
pact-broker record-deployment --broker-base-url http://localhost:9292/ \
                              --pacticipant RecipesAPI \
                              --version 0.1.0 \
                              --environment prod
````

### 6. Consumer performs `Can I deploy?` check

At this point provider has fulfilled the Pact and deployed the API. Therefore, the consumer is good-to-go with client deployment

``
./gradlew :client:canIDeploy -Ppacticipant='RecipesClient' -Platest=true
``

#### Optional: Can I deploy to production check 
````
pact-broker can-i-deploy --broker-base-url http://localhost:9292/ \
                         --pacticipant RecipesClient \
                         --version 0.1.0 \
                         --to-environment prod
````

#### Optional: Record production deployment for client
````
pact-broker record-deployment --broker-base-url http://localhost:9292/ \
                              --pacticipant RecipesClient \
                              --version 0.0.1-SNAPSHOT \
                              --environment prod                         
````
