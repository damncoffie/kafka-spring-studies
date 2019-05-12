package com.cooper.controllers;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class KafkaTest {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @RequestMapping(value = "/dre", produces = APPLICATION_JSON_VALUE, method = POST)
    public ResponseEntity<String> post(@RequestBody Map<String, Object> rawRequest)
            throws ServletException {
        return new ResponseEntity<>("Helllloooouui", HttpStatus.OK);
    }

    @RequestMapping(value = "/gogo", method = GET)
    public void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, ExecutionException, InterruptedException {
        AdminClient client = AdminClient.create(kafkaAdmin.getConfig());
        NewTopic newTopic = new NewTopic("myTopec", 2, (short) 2);
        client.createTopics(Collections.singletonList(newTopic));
        ListTopicsResult listTopicsResult = client.listTopics();
        Set<String> topicNames = listTopicsResult.names().get();

        System.out.println(topicNames);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send("myTopec", "sf");

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(SendResult<Integer, String> integerStringSendResult) {

            }
        });

        client.close();
    }

    @RequestMapping(value = "/delete", method = GET)
    public void get(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, ExecutionException, InterruptedException {
        AdminClient client = AdminClient.create(kafkaAdmin.getConfig());
        Set<String> names = client.listTopics().names().get();
        client.deleteTopics(names);
        System.out.println("DONE");
    }
}
