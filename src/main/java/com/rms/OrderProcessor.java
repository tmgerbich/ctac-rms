package com.rms;

import java.time.Duration;
import java.util.concurrent.*;

public class OrderProcessor {
    private final ExecutorService executorService;
    private final BlockingQueue<Order> orderQueue;


    public OrderProcessor(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.orderQueue = new LinkedBlockingQueue<>();
        startProcessing();
    }

    public void submitOrder(Order order) {
        order.setStatus(OrderStatus.WAITING);
        orderQueue.add(order);
    }

    private void startProcessing() {
        for (int i = 0; i < 4; i++) {
            executorService.submit(this::processOrders);
        }
    }

    private void processOrders() {
        try {
            while (true) {
                Order order = orderQueue.take();
                order.setStatus(OrderStatus.PREPARING);
                System.out.println("Preparing order ID: " + order.getOrderID());

                Duration prepTime = order.getPrepTime();
                Thread.sleep(prepTime.toMillis());

                order.setStatus(OrderStatus.COMPLETED);
                System.out.println("Completed order ID: " + order.getOrderID());

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
