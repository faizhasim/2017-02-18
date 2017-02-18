package com.crossover.trial.journals.dto;

import com.crossover.trial.journals.model.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDTOTest {
    @Test public void shouldAbleToUseAllGettersAndSetters() throws Exception {
        long id = nextLong();
        String name = randomAlphanumeric(5);
        long id2 = nextLong();
        String name2 = randomAlphanumeric(5);
        SubscriptionDTO dto = new SubscriptionDTO(newCategory(id, name));

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());

        dto.setId(id2);
        dto.setName(name2);
        dto.setActive(true);

        assertEquals(id2, dto.getId());
        assertEquals(name2, dto.getName());
        assertTrue(dto.isActive());
    }

    @Test
    public void equalityBaseOnId() throws Exception {
        long id = nextLong();
        String name1 = randomAlphanumeric(5);
        String name2 = randomAlphanumeric(5);

        SubscriptionDTO dto1 = new SubscriptionDTO(newCategory(id, name1));
        SubscriptionDTO dto2 = new SubscriptionDTO(newCategory(id, name2));
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertEquals(dto1, dto2);
    }

    private Category newCategory(long id, String name) {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(id);
        when(category.getName()).thenReturn(name);
        return category;
    }

}