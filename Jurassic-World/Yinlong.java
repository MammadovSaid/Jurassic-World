import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Yinlong.
 * Yinlongs age, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammadov
 * @version 2021.02.03 (3)
 */
public class Yinlong extends EatingAnimal
{
    // Characteristics shared by all Yinlongs (class variables).

    // The age at which a Yinlong can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Yinlong can live.
    private static final int MAX_AGE = 250;
    // The likelihood of a Yinlong breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The number foodLevel needs to reach before Yinlong wants to eat.
    private static final int FOOD_HUNGER_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Yinlong's age.
    private int age;
    // The Yinlongs's gender. If it is True, then it is male, otherwise a female.
    private boolean isMale;

    /**
     * Create a Yinlong. A Yinlong can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Yinlong will have random age.
     */
    public Yinlong(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 50);
        isMale = (rand.nextDouble() < 0.5);
        setFoodLevel(20);
        setup(randomAge);
    }

    /**
     * Create a Yinlong. A Yinlong can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Yinlong will have random age.
     * @param isMaleGender If true, the Yinlong will be male. Otherwise, female.
     */
    public Yinlong(Field animalField, Field environmentField, Location location, boolean randomAge, boolean isMaleGender)
    {
        super(animalField,environmentField, location, 50);
        isMale = isMaleGender;
        setFoodLevel(20);
        setup(randomAge); 
    }

    /**
     * Setup a Yinlong and what plants they can eat.
     * 
     * @param randomAge If true, Yinlong will have a randomAge.
     */
    private void setup(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }

        // Add which plants the Yinlong can eat.
        addFoodSet(Bush.class);
    }

    /**
     * This is what the Yinlong does most of the time - it runs 
     * around. Sometimes it will breed, eat plants or die of either old age or starvation.
     * @param newYinlongs A list to return newly born Yinlongs.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    public void act(List<Animal> newYinlongs, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (getIsMale()==false)
                giveBirth(newYinlongs);            

            if (getFoodLevel() < FOOD_HUNGER_VALUE)
                findFood();

            Location newLocation = getAnimalField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Yinlong's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Yinlong is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newYinlongs A list to return newly born Yinlongs.
     */
    private void giveBirth(List<Animal> newYinlongs)
    {
        // New Yinlongs are born into adjacent locations.
        Field field = getAnimalField();

        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        //Try to find a male partner adjacent to breed
        boolean foundOtherGender = false;
        while (it.hasNext())
        {
            Location currentSearchLocation = it.next();
            Object adjacentAnimal = field.getObjectAt(currentSearchLocation);
            if (adjacentAnimal instanceof Yinlong)
            {
                Yinlong otherYinlong = (Yinlong)adjacentAnimal;
                if (otherYinlong.getIsMale() && otherYinlong.canBreed())
                {
                    foundOtherGender=true;
                    break;
                }
            }

        }

        // Get a list of adjacent free locations.
        if (foundOtherGender)
        {
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Yinlong young = new Yinlong(field, getEnvironmentField(), loc, false);
                newYinlongs.add(young);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Yinlong can breed if it has reached the breeding age.
     * @return true if the Yinlong can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Returns the gender of the TRex.
     * @return If true, it is a male. Otherwise, female
     */
    public boolean getIsMale()
    {
        return isMale;
    }
}
