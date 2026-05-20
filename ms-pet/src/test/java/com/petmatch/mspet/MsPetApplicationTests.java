package com.petmatch.mspet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.mspet.repository.PetRepository;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsPetApplicationTests {

    @MockBean PetRepository petRepository;

    @Test
    void contextLoads() {}
}
