package com.petmatch.mssize;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.mssize.repository.SizeRepository;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsSizeApplicationTests {

    @MockBean SizeRepository sizeRepository;

    @Test
    void contextLoads() {}
}
