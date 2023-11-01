package com.shop.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    private static final String CLIENT_ID ="AZzsyYIHq05MACwG30URD0RB2W6D-ZMV0LfaZcNqM4xWhIXRiEH__SP0aIeXHI8pj9Zxs5J5TlkB1PZ1";

    private static final String CLIENT_SECRET ="ELo40Z65WNAx1JeiaMtz7CZ2WPkVGkU8MqjTw5TSBmzLvlSbzjq937sv8PPKUU3IPK92iYvntVXYZWLg";

    @Test
    public void testGetOrderDetails()
    {
        String orderId = "72G88528J6052562G";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();
        System.out.println("Order ID: "+ orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));

    }

}
