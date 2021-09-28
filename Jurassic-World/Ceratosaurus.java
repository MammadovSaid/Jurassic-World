import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Ceratosaurus
 * Ceratosaurus age, move, eat animals, drinks water, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, and Said Mammadov
 * @version 02/03/2021 (3)
 */
public class Ceratosaurus extends EatingAnimal
{
    // Characteristics shared by all Ceratosauruses (class variables).

    // The age at which a Ceratosaurus can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a Ceratosaurus can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a Ceratosaurus breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The number foodLevel needs to reach before Yinlong wants to eat.
    private static final int FOOD_HUNGER_VALUE = 35;
    // The amount drinking water replinishes the thirst level.
    private static final int WATER_DRINK_VALUE = 30;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The Ceratosaurus's age.
    private int age;
    // The Ceratosaurus's gender. If it is True, then it is male, otherwise a female.
    private boolean isMale;
    // The thirst of the Ceratosaurus. 
    // Represents the amount of steps this animal needs to drink.
    private int thirstLevel;

    /**
     * Create a Ceratosaurus. A Ceratosaurus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Ceratosaurus will have random age.
     */
    public Ceratosaurus(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 90);
        isMale = (rand.nextDouble() < 0.5);
        setFoodLevel(35);
        thirstLevel = WATER_DRINK_VALUE;
        setup(randomAge);
    }

    /**
     * Create a Ceratosaurus. A Ceratosaurus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Ceratosaurus will have random age.
     * @param isMaleGender If true, the Ceratosaurus will be male. Otherwise, female.
     */
    public Ceratosaurus(Field animalField, Field environmentField, Location location, boolean randomAge, boolean isMaleGender)
    {
        super(animalField,environmentField, location, 90);
        isMale = isMaleGender;
        setFoodLevel(35);
        thirstLevel = WATER_DRINK_VALUE;
        setup(randomAge); 
    }

    /**
     * Setup a Ceratosaurus and what organisms they can eat.
     * 
     * @param randomAge If true, Ceratosaurus will have a randomAge.
     */
    private void setup(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }

        // Add which organisms the Ceratosaurus can eat.
        addFoodSet(Gastonia.class);
        addFoodSet(Yinlong.class);
        addFoodSet(Turtle.class);
    }

    /**
     * This is what the Ceratosaurus does most of the time: it hunts for
     * food and drinks water. In the process, it might breed, die of hunger,
     * die of thirst, or die of old age.
     * @param newCeratosauruses A list to return newly born Ceratosaurus.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    public void act(List<Animal> newCeratosauruses, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementHunger();
        incrementThirst();
        if(isAlive() && isDay==false) {
            drink();
            if (isMale==false)
                giveBirth(newCeratosauruses);            
            // Move towards a source of food if found.
            Location newLocation = null;
            if (getFoodLevel() < FOOD_HUNGER_VALUE)
                newLocation = findFood();
            
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getAnimalField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Increase the age. This could result in the Ceratosaurus's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Decreases the thirst level. This could result in the Ceratosaurus's death.
     */
    private void incrementThirst()
    {
        thirstLevel--;
        if (thirstLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for water in the location the Ceratosaurus is occupying.
     * If found, it will drink it to replinish its thirst.
     */
    private void drink()
    {
        
        Object objectFound = getEnvironmentField().getObjectAt(getLocation());
        if (objectFound instanceof Water)
        {
            thirstLevel = WATER_DRINK_VALUE;
        }
    }

    /**
     * Check whether or not this Ceratosaurus is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCeratosauruses A list to return newly born Ceratosauruses.
     */
    private void giveBirth(List<Animal> newCeratosauruses)
    {
        // New Ceratosauruses are born into adjacent locations.
        Field field = getAnimalField();
        
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //Try to find a male partner adjacent to breed.
        boolean foundOtherGender = false;
        while (it.hasNext())
        {
            Location currentSearchLocation = it.next();
            Object adjacentAnimal = field.getObjectAt(currentSearchLocation);
            if (adjacentAnimal instanceof Ceratosaurus)
            {
                Ceratosaurus otherCeratosaurus = (Ceratosaurus)adjacentAnimal;
                if (otherCeratosaurus.getIsMale() && otherCeratosaurus.canBreed())
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
                Ceratosaurus young = new Ceratosaurus(field, getEnvironmentField(), loc, false);
                newCeratosauruses.add(young);
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
     * A Ceratosaurus can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Returns the gender of the Ceratosaurus.
     * @return If true, it is a male. Otherwise, female.
     */
    public boolean getIsMale()
    {
        return isMale;
    }
}
