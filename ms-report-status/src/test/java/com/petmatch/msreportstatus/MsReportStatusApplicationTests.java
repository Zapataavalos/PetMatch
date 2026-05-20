package com.petmatch.msreportstatus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.msreportstatus.repository.ReportStatusRepository;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsReportStatusApplicationTests {

    @MockBean ReportStatusRepository reportStatusRepository;

    @Test
    void contextLoads() {}
}
