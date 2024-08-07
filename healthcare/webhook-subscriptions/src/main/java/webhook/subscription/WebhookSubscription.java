package webhook.subscription;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class to manage webhook subscriptions. 
 */
public class WebhookSubscription implements HttpFunction {

  private static final Logger logger = Logger.getLogger(WebhookSubscription.class.getName());

  private static final String PROJECT_ID = System.getenv("PROJECT_ID");

  private static final String TOPIC_ID = System.getenv("TOPIC_ID");

  /**
   * This function is the entry point to the webhook subscription service.
   * 
   * @param request  The HTTP request object.
   * @param response The HTTP response object.
   * @throws IOException If an error occurs during processing.
   */
  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException  {
    // Read the request body
    String responseMessage= "";
    BufferedReader reader = request.getReader();
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
    }
    String requestBody = stringBuilder.toString();

    // Parse the JSON using Gson
    Gson gson = new Gson();
    JsonElement jsonElement = gson.fromJson(requestBody, JsonElement.class);
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    
    try {
      createSUbscriptionPubSub(jsonObject);
      responseMessage = "Webhook subscription created!";
    } catch (AlreadyExistsException aee){
      logger.warning("Subscription already exists!");
      responseMessage = "Subscription already exists!";
    }
 
    BufferedWriter writer = response.getWriter();
    writer.write(responseMessage);
  }

  /**
   * Creates a subscription in Pub/Sub.
   * 
   * @param jsonObject The JSON object containing the subscription details.
   * @throws IOException If an error occurs during processing.
   */
  private void createSUbscriptionPubSub(JsonObject jsonObject) throws IOException{
    String subscriptionName = jsonObject.get("name").getAsString();
    String subscriptionEndpoint = jsonObject.get("endpoint").getAsString();
    String filterValue = createSubscriptionFilter(jsonObject.get("fhir-resources").getAsJsonArray());

    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {

      ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, TOPIC_ID);
      ProjectSubscriptionName projectSubscriptionName =
          ProjectSubscriptionName.of(PROJECT_ID, subscriptionName);

      PushConfig pushConfig = PushConfig.newBuilder().setPushEndpoint(subscriptionEndpoint).build();

      Subscription subscription =
          subscriptionAdminClient.createSubscription(
              Subscription.newBuilder()
                  .setName(projectSubscriptionName.toString())
                  .setTopic(topicName.toString())
                  .setPushConfig(pushConfig)
                  // Receive messages with attribute key "author" and value "unknown".
                  .setFilter(filterValue)
                  .build());

      logger.info("Sunscription created: " + subscription.getAllFields());
    }
  }

  /**
   * Creates a subscription filter string.
   * 
   * @param filtersArray The array of filters.
   * @return The subscription filter string.
   */
  private String createSubscriptionFilter(JsonArray filtersArray) {
    StringBuilder filterValue = new StringBuilder();
    for (int i = 0; i < filtersArray.size(); i++) {
      if (i > 0) {
        filterValue.append(" OR ");
      }
      filterValue.append("attributes.resourceType=").append(filtersArray.get(i));
    }
    return filterValue.toString();
  }
  
}
