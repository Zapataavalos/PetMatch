package com.petmatch.mspetcolor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.mspetcolor.repository.ColorRepository;
import com.petmatch.mspetcolor.repository.PetColorRepository;
import com.petmatch.mspetcolor.client.PetClient;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsPetColorApplicationTests {

    @MockBean ColorRepository colorRepository;
    @MockBean PetColorRepository petColorRepository;
    @MockBean PetClient petClient;

    @Test
    void contextLoads() {}
}
