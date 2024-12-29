import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncDatabaseProcessing  {
    static Random random = new Random();

    public static void main(String[] args) {

        CompletableFuture<String> task1 =  CompletableFuture.supplyAsync(() -> {
            System.out.println("task1 getting data from db");
            try {
                // імітація отримання данних з бд
                TimeUnit.SECONDS.sleep(random.nextInt(2));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("task1 getting data ended without errors");
            return "Data from DB" + (int)(Math.random()*10);
        }).thenCompose(data -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(random.nextInt(2));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return data.toLowerCase();
            });
        });

        CompletableFuture<String> task2 =  CompletableFuture.supplyAsync(() -> {
            System.out.println("task2 getting data from db");
            try {
                // імітація отримання данних з бд
                TimeUnit.SECONDS.sleep(random.nextInt(3));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("task2 getting data ended without errors");
            return "Data from DB" + (int)(Math.random()*10);
        });

        CompletableFuture<String> result =  task1.thenCombine(task2, (data1, data2) -> data1 + " and " + data2 + " combined");

        System.out.println("result of all async tasks: " + result.join());
    }
}