package com.crossover.trial.journals.controller;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.repository.PublisherRepository;
import com.crossover.trial.journals.service.JournalService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublisherControllerTest {

    @Mock PublisherRepository publisherRepository;
    @Mock JournalService journalService;
    @InjectMocks PublisherController publisherController = new PublisherController();

    @Before
    public void setUp() throws Exception {
        System.setProperty("upload-dir", System.getProperty("java.io.tmpdir"));
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("upload-dir");
    }

    @Test
    public void shouldGetExpectedFileNameGivenPublisherId() throws IOException {
        long publisherId = nextLong();
        Publisher publisher = mock(Publisher.class);
        when(publisher.getId()).thenReturn(publisherId);

        String uuid = randomAlphanumeric(5);

        String expectedFileName = new File(Application.ROOT.get() + File.separator + publisherId + File.separator + uuid + ".pdf").getCanonicalPath();
        String actualFileName = publisherController.fileLocationForPublisher(publisher, uuid).getCanonicalPath();

        assertEquals(expectedFileName, actualFileName);
    }

    @Test public void journalShouldBePublishedAsExpected() {
        String name = randomAlphanumeric(5);
        Long categoryId = nextLong();
        Publisher publisher = mock(Publisher.class);
        String uuid = randomAlphanumeric(5);

        publisherController.publishJournal(name, categoryId, publisher, uuid);

        verify(journalService).publish(eq(publisher), any(Journal.class), eq(categoryId));
    }
}