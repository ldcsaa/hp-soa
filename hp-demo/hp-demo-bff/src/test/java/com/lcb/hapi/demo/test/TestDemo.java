package com.lcb.hapi.demo.test;

import com.lcb.soa.application.model.Application;
import com.lcb.soa.application.service.IApplicationService;
import com.lcb.soa.misc.common.service.IAsyncService;
import com.lcb.soa.misc.common.util.GeneralHelper;
import com.lcb.soa.misc.server.spring.main.AppStarter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static com.lcb.soa.misc.common.helper.CommonHelper.printStep;
import static com.lcb.soa.misc.common.helper.CommonHelper.printlnf;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppStarter.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class TestDemo {
    @Autowired
    private IAsyncService asyncService;
    @Autowired
    private IApplicationService applicationService;

    @BeforeClass
    public static void beforeClass() throws Exception {

    }

    @AfterClass
    public static void afterClass() {
        GeneralHelper.waitFor(1, TimeUnit.SECONDS);
        printlnf("bye ~ bye ~");
    }

    @Test
    public void step_1() throws Exception {
        printStep(1, true);

        asyncService.submit(new Runnable() {
            @Override
            public void run() {
                String appId = "100";
                Application app = applicationService.getByAppCode(appId);
                System.out.printf("appId: %s, appName: %s\n", appId, app.getName());
            }
        }).get();

        printStep(1, false);
    }

    @Test
    public void step_2() throws Exception {
        printStep(2, true);


        printStep(2, false);
    }

}
