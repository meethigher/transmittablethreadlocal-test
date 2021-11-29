package top.meethigher.transmittablethreadlocaltest.threadlocaltest;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenchuancheng
 * @since 2021/11/26 9:26
 */
public class ThreadLocalBugTest {
//    public static void main(String[] args) {
//        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
//        threadLocal.set(1);
//        /**
//         * 父线程中，给threadLocal赋值，父线程的ThreadLocalMap中存储了该threadLocal
//         * 在子线程的map中，没有threadLocal对象这个key，所以直接threadLocal.get()是拿不到值的
//         * 如果想要在异步线程中获取父线程的值，需要通过InheritableThreadLocal
//         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Integer integer = threadLocal.get();
//                threadLocal.remove();
//                System.out.println(integer);
//            }
//        }).start();
//    }

//    public static void main(String[] args) {
//        ThreadLocal<Integer> threadLocal=new InheritableThreadLocal<>();
//        threadLocal.set(1);
//        /**
//         * InheritableThreadLocal只适用于显示创建线程（new Thread()）
//         * 如果用线程池，由于是线程重复使用，就需要使用TransmittableThreadLocal
//         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Integer integer = threadLocal.get();
//                threadLocal.remove();
//                System.out.println(integer);
//            }
//        }).start();
//    }


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

//    public static void main(String[] args) throws InterruptedException {
//        /**
//         * 使用InheritableThreadLocal在使用线程池时，会存在父子线程间传递数据混乱的问题
//         * 使用TransmittableThreadLocal解决
//         */
//        ThreadLocal<Integer> threadLocal = new InheritableThreadLocal<>();
//        ThreadPoolExecutor executor = getExecutor();
//        CountDownLatch firstCountDownLatch = new CountDownLatch(1);
//        CountDownLatch secondCountDownLatch = new CountDownLatch(1);
//        threadLocal.set(1);
//        executor.execute(() -> {
//            System.out.println("子线程获取" + threadLocal.get());
//            threadLocal.remove();
//            System.out.println("异步任务1");
//            firstCountDownLatch.countDown();
//        });
//        firstCountDownLatch.await();
//        System.out.println("父线程获取" + threadLocal.get());
//
//        threadLocal.set(2);
//        executor.execute(() -> {
//            System.out.println("子线程获取" + threadLocal.get());
//            threadLocal.remove();
//            System.out.println("异步任务2");
//            secondCountDownLatch.countDown();
//        });
//        secondCountDownLatch.await();
//        System.out.println("父线程获取" + threadLocal.get());
//
//        executor.shutdown();
//
//        /**
//         * 输出结果：
//         * 子线程获取1
//         * 异步任务1
//         * 父线程获取1
//         * 子线程获取null
//         * 异步任务2
//         * 父线程获取2
//         */
//    }


    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<Integer> threadLocal = new TransmittableThreadLocal<>();
        ThreadPoolExecutor executor = getExecutor();
        CountDownLatch firstCountDownLatch = new CountDownLatch(1);
        CountDownLatch secondCountDownLatch = new CountDownLatch(1);
        threadLocal.set(1);
        executor.execute(TtlRunnable.get(() -> {
            System.out.println("子线程获取" + threadLocal.get());
            threadLocal.remove();
            System.out.println("异步任务1");
            firstCountDownLatch.countDown();
        }));
        firstCountDownLatch.await();
        System.out.println("父线程获取" + threadLocal.get());

        threadLocal.set(2);
        executor.execute(TtlRunnable.get(() -> {
            System.out.println("子线程获取" + threadLocal.get());
            threadLocal.remove();
            System.out.println("异步任务2");
            secondCountDownLatch.countDown();
        }));
        secondCountDownLatch.await();
        System.out.println("父线程获取" + threadLocal.get());

        executor.shutdown();

        /**
         * 运行结果：
         *
         * 子线程获取1
         * 异步任务1
         * 父线程获取1
         * 子线程获取2
         * 异步任务2
         * 父线程获取2
         */
    }


}
