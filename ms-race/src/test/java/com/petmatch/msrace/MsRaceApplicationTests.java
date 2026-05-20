package com.petmatch.msrace;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.msrace.repository.RaceRepository;
import com.petmatch.msrace.client.AnimalTypeClient;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsRaceApplicationTests {

    @MockBean RaceRepository raceRepository;
    @MockBean AnimalTypeClient animalTypeClient;

    @Test
    void contextLoads() {}
}
