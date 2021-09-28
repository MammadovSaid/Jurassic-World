import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * a Simple model of a Bush that grows and spreads in the simulation.
 *
 * @author Adnan Salah, Said Mammadov
 * @version 1
 */
public class Bush extends Plant
{   
    // The age at which a bush can start spreading.
    private static final int SPREAD_AGE = 5;
    // The age to which a bush can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a bush spreading.
    private static final double SPREAD_PROBABILITY = 0.7;
    // The likelihood of a bush spreading under rain.
    private static final double RAIN_SPREAD_PROBABILITY = 0.9;
    // The likelihood of a bush spreading under snow.
    private static final double SNOW_SPREAD_PROBABILITY = 0.1;
    // The maximum number of spreads.
    private static final int MAX_LITTER_SIZE = 5;

    // A shared random number generator to control spreading.
    private static final Random rand = Randomizer.getRandom();

    // instance variables - replace the example below with your own
    private int age;

     /**
     * Create a bush with age 0.
     * 
     * @param environmentField The field currently occupied.
     * @param location The location within the field.
     */
    public Bush(Field environmentField, Location location)
    {
        super(environmentField, location, 15);
        age = 0;
    }

    /**
     * This is what the bush does most of the time: it might spread, get eaten
     * or die of old age.
     * @param newPlants A list to return new bushes.
     * @param isDay If it means daytime, otherwise night-time
     * @param weather It shows the state of the weather.
     */
    public void act(List<Plant> newPlants, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        if (isAlive())
            spreadSeeds(newPlants,isDay,weather);
    }

    /**
     * Increase the age. This could result in the bush's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this bush is ready to spread at this step.
     * New spreads will be made into free adjacent locations.
     * @param newBushes A list to return new bushes.
     * @param isDay If it means daytime, otherwise night-time
     * @param weather It shows the state of the weather.
     */
    private void spreadSeeds(List<Plant> newBushes, boolean isDay, WeatherEnum weather)
    {
        Field environmentField = getEnvironmentField();
        /*if (getLocation()==null)
            return;*/
        List<Location> free = environmentField.getFreeAdjacentLocations(getLocation());
        int spreads = spread(isDay,weather);
        for(int b = 0; b < spreads && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bush seed = new Bush(getEnvironmentField(), loc);
            newBushes.add(seed);
        }
    }

    /**
     * Generate a number representing the number of spreads,
     * if it can spread.
     * @return The number of spreads (may be zero).
     * @param isDay If it means daytime, otherwise night-time
     * @param weather It shows the state of the weather.
     */
    private int spread(boolean isDay,WeatherEnum weather)
    {
        int spreads = 0;
        double spreadProbability = 0;
        if (weather==WeatherEnum.RAINING)
            spreadProbability = RAIN_SPREAD_PROBABILITY;
        else if (weather==WeatherEnum.SNOWING)
            spreadProbability = SNOW_SPREAD_PROBABILITY;
        else
            spreadProbability = SPREAD_PROBABILITY;

        if(canSpread() && rand.nextDouble() <= spreadProbability) {
            spreads = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return spreads;
    }

    /**
     * A bush can spread if it has reached the spreading age.
     */
    private boolean canSpread()
    {
        return age >= SPREAD_AGE;
    }
}
