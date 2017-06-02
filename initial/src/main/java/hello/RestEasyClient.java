package hello;

import groovy.util.logging.Log4j
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder

import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation.Builder
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer;

class RestEasyClient {
  Client client;
  WebTarget webTarget;
  Builder clientBuilder;

  RestEasyClient(String apiKey) {
    this.client = new ResteasyClientBuilder().establishConnectionTimeout(30, TimeUnit.SECONDS).socketTimeout(30, TimeUnit.SECONDS).build();
  }

   Builder get(String path, Map queryParams) {
    webTarget = client.target(path);

    //register ClientResponse and ClientRequest Filters
     BiConsumer<String,String> biConsumer = (String key,String value) -> {webTarget = webTarget.queryParam(key, value);};
     queryParams.forEach(biConsumer);

    clientBuilder = webTarget.request();

    return clientBuilder;
  }

  public Builder setHeaders(String authKey, Builder restClientBuilder = this.clientBuilder) {
    getHeaders(authKey).each { restClientBuilder.header(it.key, it.value) }
    return restClientBuilder
  }

  Map getHeaders(String authKey) {
    Map<String,String> myMap = new HashMap<String,String>();
    
  }

  void close() {
    try {
      client.close();
    } catch (Exception e) {
      System.err.print("ERROR CLOSING");
    }

  }
}
