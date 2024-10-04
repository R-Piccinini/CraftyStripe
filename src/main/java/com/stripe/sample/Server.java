package com.stripe.sample;

import java.nio.file.Paths;
import static spark.Spark.*;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Server {

  public static void main(String[] args) {
    port(4242);

    //inserisci la tua stripe secret key
    Stripe.apiKey = "???";

    staticFiles.externalLocation(
        Paths.get("public").toAbsolutePath().toString());

    post("/create-checkout-session", (request, response) -> {
    	//richiede i dati dal carrello
        Gson gson = new Gson();
        JsonObject data = gson.fromJson(request.body(), JsonObject.class);

        String productName = data.get("name").getAsString();
        long productPrice = data.get("price").getAsLong(); 
        
        //inserisci il tuo dominio
        String YOUR_DOMAIN = "http://localhost:4242";
        
        //crea la sessione di checkout
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(YOUR_DOMAIN + "/success.html")
            .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur") 
                            .setUnitAmount(productPrice)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(productName)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        Session session = Session.create(params);
        
        //restituisce l'id della sessione
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("id", session.getId());
        
        response.type("application/json");
        return gson.toJson(responseJson); 
    });
  }
}
