package com.jimo.netty.future;

import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GuavaFutureDemo {
    public static final int SLEEP_GAP = 500;

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

    static class HotWaterJob implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            System.out.println("装水");
            TimeUnit.MILLISECONDS.sleep(SLEEP_GAP);
            System.out.println("开火");
            TimeUnit.MILLISECONDS.sleep(SLEEP_GAP);
            return true;
        }
    }

    static class WashJob implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            System.out.println("洗茶杯");
            TimeUnit.MILLISECONDS.sleep(SLEEP_GAP);
            System.out.println("洗茶壶");
            TimeUnit.MILLISECONDS.sleep(SLEEP_GAP);
            return true;
        }
    }

    static class MainJob implements Runnable {
        boolean waterOk = false;
        boolean cupOk = false;
        int gap = SLEEP_GAP / 10;

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(gap);
                    System.out.println("读书中...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (waterOk && cupOk) {
                    drinkTea(waterOk, cupOk);
                    break;
                }
            }
        }

        private void drinkTea(boolean waterOk, boolean cupOk) {
            if (waterOk && cupOk) {
                System.out.println("喝茶吧");
            } else if (waterOk) {
                System.out.println("杯子没了");
            } else if (cupOk) {
                System.out.println("水没了");
            }
        }
    }

    public static void main(String[] args) {
        MainJob mainJob = new MainJob();
        Thread mainThread = new Thread(mainJob);
        mainThread.setName("主线程");
        mainThread.start();
        HotWaterJob hotWaterJob = new HotWaterJob();
        WashJob washJob = new WashJob();
        ExecutorService pool = Executors.newFixedThreadPool(5);
        ListeningExecutorService gPool = MoreExecutors.listeningDecorator(pool);
        ListenableFuture<Boolean> hotFuture = gPool.submit(hotWaterJob);
        Futures.addCallback(hotFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean b) {
                mainJob.waterOk = b != null && b;
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("水没了呀");
            }
        }, pool);
        ListenableFuture<Boolean> washFuture = gPool.submit(washJob);
        Futures.addCallback(washFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean result) {
                mainJob.cupOk = result != null && result;
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("茶杯球了");
            }
        }, pool);
    }
}