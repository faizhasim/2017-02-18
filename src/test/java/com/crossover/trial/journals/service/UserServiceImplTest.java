package com.crossover.trial.journals.service;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    public void withCategoryId() throws Exception {
        Long expectedCategoryId = nextLong();

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(expectedCategoryId);

        Subscription subscription = mock(Subscription.class);
        when(subscription.getCategory()).thenReturn(category);

        long actualCount = Stream.of(subscription).filter(UserServiceImpl.withCategoryId(expectedCategoryId)).count();
        assertEquals(1, actualCount);

        when(category.getId()).thenReturn(0L);
        actualCount = Stream.of(subscription).filter(UserServiceImpl.withCategoryId(expectedCategoryId)).count();
        assertEquals(0, actualCount);
    }

}