package com.crossover.trial.journals.service.messaging;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.crossover.trial.journals.config.ApplicationConfig;
import com.crossover.trial.journals.model.*;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SnsMessageServiceTest {
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock ApplicationConfig applicationConfig;

    @Mock ApplicationConfig.Aws awsConfig;
    @Mock AmazonSNS amazonSnsClient;

    SnsMessageService snsMessageService;

    @Before
    public void setUp() throws Exception {
        when(awsConfig.getAccessKeyId()).thenReturn(randomAlphanumeric(5));
        when(awsConfig.getSecretAccessKey()).thenReturn(randomAlphanumeric(5));
        when(awsConfig.getRegion()).thenReturn(randomAlphanumeric(5));
        when(awsConfig.getJournalChangesArn()).thenReturn(randomAlphanumeric(5));
        when(applicationConfig.getAws()).thenReturn(awsConfig);
        when(amazonSnsClient.publish(any(PublishRequest.class))).thenReturn(mock(PublishResult.class));
        snsMessageService = new SnsMessageService(subscriptionRepository, applicationConfig);
        snsMessageService.setSnsClient(amazonSnsClient);
    }

    @Test
    public void publishJournalChanges() throws Exception {
        User user = new User();
        user.setId(nextLong());
        user.setRole(Role.PUBLISHER);
        user.setLoginName(randomAlphanumeric(5));
        user.setEnabled(true);
        user.setEmail(randomAlphanumeric(5));

        Publisher publisher = new Publisher();
        publisher.setId(nextLong());
        publisher.setUser(user);
        publisher.setName(randomAlphanumeric(5));

        Subscription subscription = new Subscription();
        subscription.setId(nextLong());
        subscription.setUser(user);

        Category category = new Category();
        category.setId(nextLong());
        category.setName(randomAlphanumeric(5));

        Journal journal = mock(Journal.class);
        when(journal.getPublishDate()).thenReturn(new Date());
        when(journal.getId()).thenReturn(nextLong());
        when(journal.getPublisher()).thenReturn(publisher);
        when(journal.getName()).thenReturn(randomAlphanumeric(5));
        when(journal.getUuid()).thenReturn(randomAlphanumeric(5));
        when(journal.getCategory()).thenReturn(category);

        snsMessageService.publishJournalChanges(journal);
        verify(amazonSnsClient).publish(any(PublishRequest.class));
    }

}