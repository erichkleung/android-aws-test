package com.fivestars.awstest;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;

public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    private static final String developerProvider = "auth.fivestars.consumer";
    private String ID = "us-east-1:e24855c6-958a-4b4e-a4f0-f8a0e4c7d5cc";
    private String TOKEN = "eyJraWQiOiJ1cy1lYXN0LTExIiwidHlwIjoiSldTIiwiYWxnIjoiUlM1MTIifQ.eyJzdWIiOiJ1cy1lYXN0LTE6ZTI0ODU1YzYtOTU4YS00YjRlLWE0ZjAtZjhhMGU0YzdkNWNjIiwiYXVkIjoidXMtZWFzdC0xOjg4MmZkZGQwLWVlYmUtNDYxMS1hMjY1LWI0N2NlOWQ4ZmQ5OCIsImFtciI6WyJhdXRoZW50aWNhdGVkIiwiYXV0aC5maXZlc3RhcnMuY29uc3VtZXIiLCJhdXRoLmZpdmVzdGFycy5jb25zdW1lcjp1cy1lYXN0LTE6ODgyZmRkZDAtZWViZS00NjExLWEyNjUtYjQ3Y2U5ZDhmZDk4OnVpZC11c2VyLWVyaWMzIl0sImlzcyI6Imh0dHBzOi8vY29nbml0by1pZGVudGl0eS5hbWF6b25hd3MuY29tIiwiZXhwIjoxNDQ4MDUxODMzLCJpYXQiOjE0NDgwNTAwMzN9.Fbv70BPSarSh4cCTLnSHoyPTXr7w1IrKXKplPVPDWYoA-peBs9wwVwF_0AEDyf6DAE85SzlK5u4wEjzu11llUgwf2uQ54DIVSB6dU0IS9VfHZw7vmBwetDqMbSXMK_xcwbrAqBe3ga7SWjl6YJx8k4VIOYREp5vyHkrX-umXL7oLicxymnte-RFKDfUDJFdKmxL3ST9HpU6TyQPBtIxUcOyt-orD4MYR26DYIemgKj6p81MKU2Npn9ImyWGZNpAnjcF_RbG8a-5xmTXqTp0c5bVyr5hAiOzYaTAIsf5atlaMu8G1W_Njy9AOzSNKzX6NT1KBqnLXbzsPiC0C41r3TQ";

    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Regions region) {
        super(accountId, identityPoolId, region);
        // Initialize any other objects needed here.
    }

    // Return the developer provider name which you choose while setting up the
    // identity pool in the Amazon Cognito Console

    @Override
    public String getProviderName() {
        return developerProvider;
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.

    @Override
    public String refresh() {

        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.

        identityId = ID;
        token = TOKEN;

        update(identityId, token);
        return token;
    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.
    @Override
    public String getIdentityId() {

        // Load the identityId from the cache
        //identityId = cachedIdentityId;
        return ID;
    }
}