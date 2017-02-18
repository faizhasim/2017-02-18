package com.crossover.trial.journals.service.messaging;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.crossover.trial.journals.config.ApplicationConfig;
import com.crossover.trial.journals.model.*;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SnsMessageService implements MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(SnsMessageService.class);
    private final String journalChangesArn;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String awsRegion;
    private final SubscriptionRepository subscriptionRepository;
    private AmazonSNS snsClient;

    @Autowired
    public SnsMessageService(SubscriptionRepository subscriptionRepository, ApplicationConfig applicationConfig) {
        this.subscriptionRepository = subscriptionRepository;
        this.journalChangesArn = applicationConfig.getAws().getJournalChangesArn();
        this.accessKeyId = applicationConfig.getAws().getAccessKeyId();
        this.secretAccessKey = applicationConfig.getAws().getSecretAccessKey();
        this.awsRegion = applicationConfig.getAws().getRegion();

        AWSCredentials awsCredentials = new BasicAWSCredentials(
            accessKeyId,
            secretAccessKey
        );

        setSnsClient(
            AmazonSNSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(awsRegion)
                .build()
        );
    }

    @Override
    public void publishJournalChanges(Journal journal) throws MessageDeliveryException {
        PublishResult publishResult  = publishSns(journalChangesArn, journalMessageBuilder(journal));
        logger.info("SNS Message sent with message id: " + publishResult.getMessageId());
    }

    private PublishResult publishSns(String topicArn , String msg) throws MessageDeliveryException {
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        try {
            return getSnsClient().publish(publishRequest);
        } catch (Exception e) {
            throw new MessageDeliveryException(e);
        }
    }

    private AmazonSNS getSnsClient() {
        return snsClient;
    }

    void setSnsClient(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    private Map<String, Object> toUserObj(User user) {
        return ImmutableMap.of(
            "id", user.getId(),
            "loginName", user.getLoginName(),
            "enabled", user.getEnabled(),
            "role", user.getRole().name(),
            "email", user.getEmail()
        );
    }

    private Map<String, Object> toCategoryObj(Category category) {
        return ImmutableMap.of(
            "id", category.getId(),
            "name", category.getName()
        );
    }

    private Map<String, Object> toSubscriptionObj(Subscription subscription) {
        return ImmutableMap.of(
            "id", subscription.getId(),
            "date", subscription.getDate(),
            "user", toUserObj(subscription.getUser())
        );
    }

    private String journalMessageBuilder(Journal journal) throws MessageDeliveryException {
        ObjectMapper mapper = new ObjectMapper();
        Publisher publisher = journal.getPublisher();
        List<Subscription> subscriptions = subscriptionRepository.findByCategory(journal.getCategory());
        List<Map<String, Object>> subscriptionObjects = subscriptions
                .parallelStream()
                .map(this::toSubscriptionObj)
                .collect(Collectors.toList());


        Map<String, Object> publisherObj = ImmutableMap.of(
            "id", publisher.getId(),
            "user", toUserObj(publisher.getUser()),
            "name", publisher.getName()
        );

        try {
            return mapper.writeValueAsString(
                    ImmutableMap
                        .<String, Object>builder()
                        .put("id", journal.getId())
                        .put("name", journal.getName())
                        .put("publishDate", journal.getPublishDate())
                        .put("publisher", publisherObj)
                        .put("uuid", journal.getUuid())
                        .put("category", toCategoryObj(journal.getCategory()))
                        .put("subscriptions", subscriptionObjects)
                        .build()
            );
        } catch (JsonProcessingException e) {
            throw new MessageDeliveryException(e);
        }
    }
}
