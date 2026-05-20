package com.petmatch.msreporttype;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.msreporttype.repository.ReportTypeRepository;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsReportTypeApplicationTests {

    @MockBean ReportTypeRepository reportTypeRepository;

    @Test
    void contextLoads() {}
}
