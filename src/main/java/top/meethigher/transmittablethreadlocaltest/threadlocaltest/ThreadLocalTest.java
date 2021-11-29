package top.meethigher.transmittablethreadlocaltest.threadlocaltest;

import java.util.Arrays;

/**
 * @author chenchuancheng
 * @since 2021/11/26 9:23
 */
public class ThreadLocalTest {
//    public static void main(String[] args) {
//        ThreadLocal<Integer> threadLocal=new ThreadLocal<>();
//        //设置当前线程变量
//        threadLocal.set(1);;
//        //获取当前线程变量
//        Integer integer = threadLocal.get();
//        //获取到值之后，进行清空
//        threadLocal.remove();
//        System.out.println(integer);
//    }


    ThreadLocal<String> tl=new ThreadLocal<>();
    private String content;

    public String getContent() {
        //获取当前线程绑定的content
        return tl.get();
    }

    public void setContent(String content) {
        //变量content绑定到当前线程
        tl.set(content);
    }

    public static void main(String[] args) {
        ThreadLocalTest test = new ThreadLocalTest();
        System.out.println(test.tl);
        for(int i=0;i<5;i++){
            @SuppressWarnings("all")
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    String content=Thread.currentThread().getName()+"的数据"+"======获取threadLocal====>"+test.tl;
                    test.setContent(content);
                    System.out.println(Thread.currentThread().getName()+"----->"+test.getContent());
                }
            });
            thread.setName("线程"+i);
            thread.start();
        }
        /**
         * 这些线程里面，每个线程，都有一个ThreadLocalMap
         * 每个ThreadLocalMap，都存储key为java.lang.ThreadLocal@44e81672，数据为自己专有数据
         * 获取的时候，也是去当前线程里的map中，根据java.lang.ThreadLocal@44e81672来拿
         */
        Thread[] threads=new Thread[5];
        Thread.currentThread().getThreadGroup().enumerate(threads);
        System.out.println(Arrays.asList(threads));

        /**
         * 整个过程运行结果：
         *
         * java.lang.ThreadLocal@44e81672
         * 线程0----->线程0的数据======获取threadLocal====>java.lang.ThreadLocal@44e81672
         * [Thread[main,5,main], Thread[线程0,5,main], Thread[线程1,5,main], Thread[线程2,5,main], Thread[线程3,5,main]]
         * 线程2----->线程2的数据======获取threadLocal====>java.lang.ThreadLocal@44e81672
         * 线程1----->线程1的数据======获取threadLocal====>java.lang.ThreadLocal@44e81672
         * 线程3----->线程3的数据======获取threadLocal====>java.lang.ThreadLocal@44e81672
         * 线程4----->线程4的数据======获取threadLocal====>java.lang.ThreadLocal@44e81672
         */
    }
}
