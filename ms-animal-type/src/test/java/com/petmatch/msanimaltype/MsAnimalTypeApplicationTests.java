package com.petmatch.msanimaltype;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.msanimaltype.repository.AnimalTypeRepository;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsAnimalTypeApplicationTests {

    @MockBean AnimalTypeRepository animalTypeRepository;

    @Test
    void contextLoads() {}
}
