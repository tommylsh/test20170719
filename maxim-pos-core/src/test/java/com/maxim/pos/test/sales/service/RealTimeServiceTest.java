package com.maxim.pos.test.sales.service;

import com.maxim.pos.sales.service.RealTimeService;
import com.maxim.pos.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class RealTimeServiceTest extends BaseTest {

    @Autowired
    private RealTimeService realTimeService;

    @Test
    @Transactional
    @Rollback(false)
    public void testProcessMasterDataToStg() {
        realTimeService.processStgRealTimeDataToEdw("6666", "06666", logger);
    }

    // public static void main(String[] args) {
    //
    //     Logger logger = LoggerFactory.getLogger(RealTimeServiceTest.class);
    //
    //     ApplicationContext applicationContext = new ClassPathXmlApplicationContext("pos-core-context.xml");
    //
    //     RealTimeService realTimeService = applicationContext.getBean(RealTimeService.class);
    //
    //     final ExecutorService threadPool = Executors.newCachedThreadPool();
    //     final CountDownLatch latch = new CountDownLatch(1);
    //     for (int i = 0; i < 2; i++) {
    //         final int seq = i;
    //         threadPool.execute(new Runnable() {
    //             @Override
    //             public void run() {
    //                 try {
    //                     latch.await();
    //                     if (seq == 0) {
    //                         realTimeService.processStgRealTimeDataToEdw("0000", logger);
    //                     } else {
    //                         realTimeService.processStgRealTimeDataToEdw("8888", logger);
    //                     }
    //                 } catch (InterruptedException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //         });
    //     }
    //     latch.countDown();
    //     threadPool.shutdown();
    // }

}