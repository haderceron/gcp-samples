Webhook Subscription Cloud Function
===================================

This Cloud Function acts as a webhook subscription manager for Google Cloud Healthcare API, allowing external systems to easily subscribe to data changes in FHIR stores.

Functionality
-------------

1.  **Receives Subscription Details:** Parses incoming HTTP requests with subscription details:

    -   `name`: Unique name for the subscription.
    -   `endpoint`: URL where notifications will be sent.
    -   `fhir-resources`: Array of FHIR resources to subscribe to (e.g., "Patient").
2.  **Creates Pub/Sub Subscription:**

    -   Uses the Google Cloud Pub/Sub client library to create a subscription with the provided details.
    -   Sets up a push configuration to deliver data to the specified endpoint.
    -   Handles potential errors like duplicate subscription names.
3.  **Manages Existing Subscriptions:**

    -   If a subscription with the same name already exists, logs a warning message.

Deployment
----------

1.  **Clone the repository:**

    ```
    git clone https://github.com/haderceron/gcp-samples.git

    ```

2.  **Navigate to the project directory:**

    ```
    cd healthcare/webhook-subscriptions/

    ```

3.  **Deploy the Cloud Function:**

     ```
    gcloud functions deploy webhook-subscriptions\
        --gen2\
        --region=us-central1\
        --runtime=java21\
        --source=.\
        --entry-point=webhook.subscription.WebhookSubscription\
        --trigger-http\
        --allow-unauthenticated\
        --set-env-vars PROJECT_ID=YOUR_PROJECT_ID,TOPIC_ID=YOUR_TOPIC_ID

    ```

    Replace:

    -   `YOUR_PROJECT_ID`: Your GCP project ID.
    -   `YOUR_TOPIC_ID`: The Pub/Sub topic ID associated with your FHIR store.

Usage
-----

Make an HTTP POST request to the deployed Cloud Function's URL with a JSON payload similar to this:

JSON

```
{
  "name": "my-subscription",
  "endpoint": "https://your-webhook-endpoint.com",
  "fhir-resources": ["Patient"]
}

```

You should receive a response indicating whether the subscription was created or if it already existed.