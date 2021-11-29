package top.meethigher.transmittablethreadlocaltest.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenchuancheng
 * @since 2021/11/26 13:54
 */
public class CountDownLatchTest {

    public static ThreadPoolExecutor getExecutor() {
        return new ThreadPoolExecutor(
                1,//核心
                2,//最大
                20,//keepAlive
                TimeUnit.SECONDS,//keepAlive的单位
                new LinkedBlockingDeque<>(2),//容量为2的阻塞队列
                new ThreadPoolExecutor.DiscardOldestPolicy()//拒绝策略
        );
    }
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ThreadPoolExecutor executor = getExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("A准备捐1块钱");
                countDownLatch.countDown();
                System.out.println("A捐了一块钱");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("B准备捐1块钱");
                countDownLatch.countDown();
                System.out.println("B捐了一块钱");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("C准备捐1块钱");
                countDownLatch.countDown();
                System.out.println("C捐了一块钱");
            }
        });

        System.out.println("等人给我捐钱");
        countDownLatch.await();
        System.out.println("3块钱到手，直接跑路");

        executor.shutdown();
    }
}
