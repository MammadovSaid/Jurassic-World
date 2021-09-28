import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Gastonia.
 * Gastonias age, move, breed, eat plants and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammadov
 * @version 2021.02.03 (3)
 */
public class Gastonia extends EatingAnimal
{
    // Characteristics shared by all Gastonias (class variables).

    // The age at which a Gastonia can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Gastonia can live.
    private static final int MAX_AGE = 250;
    // The likelihood of a Gastonia breeding in day time.
    private static final double DAY_BREEDING_PROBABILITY = 0.3;
    // The likelihood of a Gastonia breeding in night time.
    private static final double NIGHT_BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The number foodLevel needs to reach before Gastonia wants to eat.
    private static final int FOOD_HUNGER_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Gastonia's age.
    private int age;
    // The Gastonia's gender, true if male, otherwise female.
    private boolean isMale;

    /**
     * Create a Gastonia. A Gastonia can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field a Gastonia is currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Gastonia will have random age.
     */
    public Gastonia(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 50);
        isMale = (rand.nextDouble() < 0.5);
        setFoodLevel(20);
        setup(randomAge);
    }

    /**
     * Create a Gastonia. A Gastonia can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field a Gastonia is currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Gastonia will have random age.
     * @param isMaleGender If true, the Gastonia will be male. Otherwise, female.
     */
    public Gastonia(Field animalField, Field environmentField, Location location, boolean randomAge, boolean isMaleGender)
    {
        super(animalField,environmentField, location, 50);
        isMale = isMaleGender;
        setFoodLevel(20);
        setup(randomAge); 
    }

    /**
     * Setup a Gastonia and what plants they can eat.
     * 
     * @param randomAge If true, Gastonia will have a randomAge.
     */
    private void setup(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }

        // Add which plants the Gastonia can eat.
        addFoodSet(Bush.class);
    }

    /**
     * This is what the Gastonia does most of the time - it runs 
     * around. Sometimes it will breed, eat plants or die of either old age or starvation.
     * @param newGastonias A list to return newly born Gastonias.
     * @param isDay If it means daytime, otherwise night-time.
     * @param weather It shows the state of the weather.
     */
    public void act(List<Animal> newGastonias, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (getIsMale()==false)
                giveBirth(newGastonias,isDay);
                
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
     * This could result in the Gastonia's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Gastonia is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGastonias A list to return newly born Gastonias.
     * @param isDay If it means daytime, otherwise night-time.
     */
    private void giveBirth(List<Animal> newGastonias, boolean isDay)
    {
        // New Gastonias are born into adjacent locations.
        Field field = getAnimalField();

        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        //Try to find a male partner adjacent to breed
        boolean foundOtherGender = false;
        while (it.hasNext())
        {
            Location currentSearchLocation = it.next();
            Object adjacentAnimal = field.getObjectAt(currentSearchLocation);
            if (adjacentAnimal instanceof Gastonia)
            {
                Gastonia otherGastonia = (Gastonia)adjacentAnimal;
                if (otherGastonia.getIsMale() && otherGastonia.canBreed())
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
            int births = breed(isDay);
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Gastonia young = new Gastonia(field, getEnvironmentField(), loc, false);
                newGastonias.add(young);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed. 
     * Gastonias have higher chance of breeding at night.
     * @param isDay If it means daytime, otherwise night-time
     * @return The number of births (may be zero).
     */
    private int breed(boolean isDay)
    {
        int births = 0;
        double breedProbability = 0;

        if (isDay)
            breedProbability = DAY_BREEDING_PROBABILITY;
        else
            breedProbability = NIGHT_BREEDING_PROBABILITY;

        if(canBreed() && rand.nextDouble() <= breedProbability) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Gastonia can breed if it has reached the breeding age.
     * @return true if the Gastonia can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Returns the gender of the Gastonia.
     * @return If true, it is a male. Otherwise, female
     */
    public boolean getIsMale()
    {
        return isMale;
    }
}
