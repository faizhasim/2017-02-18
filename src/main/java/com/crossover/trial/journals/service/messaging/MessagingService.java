package com.crossover.trial.journals.service.messaging;

import com.crossover.trial.journals.model.Journal;

public interface MessagingService {
    class MessageDeliveryException extends Exception {
        MessageDeliveryException(Throwable e) {
            super(e);
        }
    }

    void publishJournalChanges(Journal journal) throws MessageDeliveryException;
}
