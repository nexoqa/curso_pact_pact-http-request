# contract_testing_pact for languagues non supported
Repository to learn contract testing using Java Pact Library

# Using Pact CLI with docker

The Pact CLI docuemnation is https://github.com/pact-foundation/pact-mock_service

## Starting generic mock server
```
docker run -dit \ 
    --rm \
    --name pact-mock-service \
    -p 1234:1234 \
    -v $(pwd)/pacts:/tmp/pacts \
    -v $(pwd)/logs:/tmp/logs \
    pactfoundation/pact-cli:latest \
    mock-service \
    -p 1234 \
    --host 0.0.0.0 \
    --pact-dir /tmp/pacts \
    --log /tmp/logs/provider.log \
    --log-level DEBUG
```
## Execute script example
```
#This script uses the `consummer-interaction.json` and `consummer-provider.json`
./example
```

## Steps should have your script to create the contract
1. BEFORE SUITE wait for mock service to start up
2. BEFORE EACH TEST clear interactions from previous test
3. BEFORE A TEST set up interaction(s) just for that test
4. IN A TEST execute interaction(s)
5. AFTER EACH TEST verify interaction(s) took place
6. AFTER SUITE write pact


# Publish Pact to Pack Broker
```
docker run --rm -v $(pwd)/pacts:/tmp/pacts -e PACT_BROKER_BASE_URL=http://[YOUR_IP_ADDRESS] pactfoundation/pact-cli:latest publish /tmp/pacts --consumer-app-version 1.0.0
```