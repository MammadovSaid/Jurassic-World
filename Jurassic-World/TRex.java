import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a TRex
 * TRex age, move, eat animals, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, and Said Mammadov
 * @version 02/03/2021 (3)
 */
public class TRex extends EatingAnimal
{
    // Characteristics shared by all TRexes (class variables).

    // The age at which a TRex can start to breed.
    private static final int BREEDING_AGE = 60;
    // The age to which a TRex can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a TRex breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The number foodLevel needs to reach before Yinlong wants to eat.
    private static final int FOOD_HUNGER_VALUE = 30;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The TRex's age.
    private int age;
    // The TRex's gender. If it is True, then it is male, otherwise a female.
    private boolean isMale;

    /**
     * Create a TRex. A TRex can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the TRex will have random age.
     */
    public TRex(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 500);
        isMale = (rand.nextDouble() < 0.5);
        setFoodLevel(50);
        setup(randomAge);
    }

    /**
     * Create a TRex. A TRex can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the TRex will have random age.
     * @param isMaleGender If true, the Trex will be male. Otherwise, female.
     */
    public TRex(Field animalField, Field environmentField, Location location, boolean randomAge, boolean isMaleGender)
    {
        super(animalField,environmentField, location, 500);
        isMale = isMaleGender;
        setFoodLevel(50);
        setup(randomAge); 
    }

    /**_____
     * Setup a TRex and what organisms they can eat.
     * 
     * @param randomAge If true, TRex will have a randomAge.
     */
    private void setup(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }

        // Add which organisms the TRex can eat.
        addFoodSet(Allosaurus.class);
        addFoodSet(Ceratosaurus.class);
        addFoodSet(Gastonia.class);
        addFoodSet(Turtle.class);
        addFoodSet(Yinlong.class);
    }

    /**
     * This is what the TRex does most of the time: it hunts for
     * food. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newTrexes A list to return newly born TRexes.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    public void act(List<Animal> newTrexes, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && isDay) {
            if (isMale==false)
                giveBirth(newTrexes);            
            // Move towards a source of food if found.

            Location newLocation = null;

            if (getFoodLevel() < FOOD_HUNGER_VALUE && weather!=WeatherEnum.FOGGY)
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
     * Increase the age. This could result in the TRex's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this TRex is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newTRexes A list to return newly born TRexes.
     */
    private void giveBirth(List<Animal> newTRexes)
    {
        // New TRexes are born into adjacent locations.
        Field field = getAnimalField();

        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        //Try to find a male partner adjacent to breed.
        boolean foundOtherGender = false;
        while (it.hasNext())
        {
            Location currentSearchLocation = it.next();
            Object adjacentAnimal = field.getObjectAt(currentSearchLocation);
            if (adjacentAnimal instanceof TRex)
            {
                TRex otherTRex = (TRex)adjacentAnimal;
                if (otherTRex.getIsMale() && otherTRex.canBreed())
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
                TRex young = new TRex(field, getEnvironmentField(), loc, false);
                newTRexes.add(young);
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
     * A TRex can breed if it has reached the breeding age.
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
