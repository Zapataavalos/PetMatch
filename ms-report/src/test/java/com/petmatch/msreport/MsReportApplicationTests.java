package com.petmatch.msreport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.petmatch.msreport.repository.ReportRepository;
import com.petmatch.msreport.client.PetClient;
import com.petmatch.msreport.client.ReportTypeClient;
import com.petmatch.msreport.client.ReportStatusClient;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsReportApplicationTests {

    @MockBean ReportRepository reportRepository;
    @MockBean PetClient petClient;
    @MockBean ReportTypeClient reportTypeClient;
    @MockBean ReportStatusClient reportStatusClient;

    @Test
    void contextLoads() {}
}
