import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WeatherComparison {

    private static CompletableFuture<WeatherData> getWeatherData(String city) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримання погоди для " + city);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return switch (city) {
                case "Kyiv" -> new WeatherData(5, 80, 10);
                case "Odessa" -> new WeatherData(25, 60, 15);
                case "Lviv" -> new WeatherData(15, 75, 5);
                default -> new WeatherData(0, 0, 0);
            };
        });
    }

    private static String compareTemperatures(WeatherData first, WeatherData second, String city1, String city2) {
        if (first.temperature > second.temperature) {
            return city1 + " тепліше, ніж " + city2 + ".";
        } else if (first.temperature < second.temperature) {
            return city2 + " тепліше, ніж " + city1 + ".";
        } else {
            return "Температура в " + city1 + " і " + city2 + " однакова.";
        }
    }

    public static void main(String[] args) {

        CompletableFuture<WeatherData> kyivWeather = getWeatherData("Kyiv");
        CompletableFuture<WeatherData> odessaWeather = getWeatherData("Odessa");
        CompletableFuture<WeatherData> lvivWeather = getWeatherData("Lviv");

        CompletableFuture<Void> allWeather = CompletableFuture.allOf(kyivWeather, odessaWeather, lvivWeather);

        allWeather.join();

        CompletableFuture<String> combinedResult = kyivWeather.thenCombine(odessaWeather, (kyiv, odessa) -> compareTemperatures(kyiv, odessa, "Київ", "Одеса")).thenCombine(lvivWeather, (result, lviv) -> {
            String finalComparison;
            try {
                finalComparison = compareTemperatures(lvivWeather.get(), odessaWeather.get(), "Львів", "Одеса");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return result + "\t" + finalComparison;
        });


        System.out.println("Результат асинхроних задач: " + combinedResult.join());
    }
}

class WeatherData {
    int temperature; // температура
    int humidity;    // вологість
    int windSpeed;   // швидкість вітру

    public WeatherData(int temperature, int humidity, int windSpeed) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    @Override
    public String toString() {
        return "Temperature: " + temperature + "°C, Humidity: " + humidity + "%, Wind Speed: " + windSpeed + " km/h";
    }
}