Webhook Endpoint Simulation for Google Cloud Healthcare API
===========================================================

This project demonstrates how to set up a simulated webhook endpoint using Google Cloud Functions to test the delivery of healthcare data from Google Cloud Healthcare API via Pub/Sub.

Prerequisites
-------------

-   A Google Cloud Platform project.
-   A dataset and FHIR Store created in Cloud Healthcare API.
-   Google Cloud CLI installed.
-   Pub/Sub API enabled for your GCP project.
-   Cloud Functions API enabled for your GCP project.
-   A tool like `curl` for making HTTP POST requests.

Deployment
----------
1.  **Clone the repository:**

    ```
    git clone https://github.com/haderceron/gcp-samples.git


2.  **Navigate to the project directory:**

    ```
    cd healthcare/webhook-push-endpoint/

    ```

3.  **Deploy the Cloud Function:**


    ```
    gcloud functions deploy webhook-push-endpoint\
        --gen2\
        --region=us-central1\
        --runtime=java21\
        --source=.\
        --entry-point=webhookendpoint.WebhookEndpointTest\
        --trigger-http\
        --allow-unauthenticated

    ```

Code Structure
--------------

-   **`WebhookEndpointTest.java`:** Defines the core logic of the webhook endpoint.
    -   Receives HTTP POST requests.
    -   Extracts and decodes the message payload.
    -   Logs the received data.
    -   Sends an ACK response to Pub/Sub.

Usage
-----

1.  **Create a Subscription:**
    -   Use a separate API or tool to create a subscription in Pub/Sub that is associated with your FHIR store's topic.
2.  **Trigger an Event:**
    -   Create, update, or delete a FHIR resource in your Cloud Healthcare API to trigger an event.
3.  **Observe the Logs:**
    -   Check the logs of your deployed Cloud Function to verify that the webhook endpoint received and processed the message successfully.

Important Note
--------------

In a real-world application, you would replace the logging functionality in `WebhookEndpointTest.java` with your own custom logic for processing the received healthcare data.