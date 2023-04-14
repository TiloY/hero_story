package org.ty.herostory;

import java.util.concurrent.LinkedBlockingDeque;

public class TestDemo {

    public static void main(String[] args) {
        //(new TestDemo()).test1();
        (new TestDemo()).test2();

    }

    private void test1() {
        LinkedBlockingDeque<Integer> blockingDeque = new LinkedBlockingDeque<>();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                blockingDeque.offer(i);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                blockingDeque.offer(i);
            }
        });


        Thread thread3 = new Thread(() -> {
            while (true) {
                try {
                    Integer val = blockingDeque.take();// 阻塞
                    System.out.println("获取数值：" + val);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private void test2() {
        MyExecutorService es = new MyExecutorService();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final int val = i;
                es.submit(() -> {
                    System.out.println("取出数值 = " + val);
                });
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int val = i;
               es.submit(()->{
                   System.out.println("取出数值 = " + val);
               });
            }
        });



        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * 自定义一个 单线程线程池
     */
    class MyExecutorService {
        /**
         * 线程池的最核心的东西  阻塞队列    动态创建线程   +  默认创建  如果不够了 就再创建
         */
        private final LinkedBlockingDeque<Runnable> _blockingDeque = new LinkedBlockingDeque<>();
        // 这个有个大小 |  限流  其实到不了最大值 任务到达多少了之后  直接返回服务器忙 稍后再试
        private final Thread _thread;

        public MyExecutorService() {
            this._thread = new Thread(() -> {
                while (true) {
                    try {
                        Runnable r = _blockingDeque.take();// 锁
                        if (null != r) {
                            r.run();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            this._thread.start();
        }

        public void submit(Runnable r) {
            if (r != null) {
                this._blockingDeque.offer(r);
            }
        }
    }


    // 线程 1 2 3 4 ---》 队列  ---- MianThreadProcessor 取任务
    // 生产者消费者模型   netty  一堆线程 都是IO 线程  我们的业务线程都是内存中的 处理是很快的
}
