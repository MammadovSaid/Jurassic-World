import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Allosaurus
 * Allosauruses age, move, eats animals, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, and Said Mammadov
 * @version 02/03/2021 (3)
 */
public class Allosaurus extends EatingAnimal
{
    // Characteristics shared by all Allotosauruses (class variables).

    // The age at which a Allosaurus can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a Allosaurus can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a Allosaurus breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The likelihood of a Allosaurus eating when its foggy.
    private static final double FOG_EAT_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The number foodLevel needs to reach before Yinlong wants to eat.
    private static final int FOOD_HUNGER_VALUE = 35;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The Allotosaurus's age.
    private int age;
    // The Allotosaurus's gender. If it is True, then it is male, otherwise a female.
    private boolean isMale;

    /**
     * Create a Allosaurus. A Allosaurus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the Allosaurus will have random age.
     */
    public Allosaurus(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 60);
        isMale = (rand.nextDouble() < 0.5);
        setFoodLevel(35);
        setup(randomAge);
    }

    /**
     * Create a Allosaurus. A Allosaurus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param isMaleGender If true, the Allosaurus will be male. Otherwise, female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the Allosaurus will have random age.
     * @param isMaleGender If true, the Allosaurus will be male. Otherwise, female.
     */
    public Allosaurus(Field animalField, Field environmentField, Location location, boolean randomAge, boolean isMaleGender)
    {
        super(animalField,environmentField, location, 60);
        isMale = isMaleGender;
        setFoodLevel(35);
        setup(randomAge); 
    }

    /**
     * Setup a Allosaurus and what organisms they can eat.
     * 
     * @param randomAge If true, Allosaurus will have a randomAge.
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
        addFoodSet(Turtle.class);
        addFoodSet(Gastonia.class);
        addFoodSet(Yinlong.class);
    }

    /**
     * This is what the Allosaurus does most of the time: it hunts for
     * animals. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newAllotosauruses A list to return newly born Allotosauruses.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    public void act(List<Animal> newAllotosauruses, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isMale==false)
                giveBirth(newAllotosauruses);
            Location newLocation = null;
            // Move towards a source of food if found.
            if (getFoodLevel() < FOOD_HUNGER_VALUE && weather != WeatherEnum.FOGGY)
                newLocation = findFood();
            else if (getFoodLevel() < FOOD_HUNGER_VALUE && rand.nextDouble() < FOG_EAT_PROBABILITY)
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
     * Increase the age. This could result in the Allotosaurus's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Allosaurus is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAllotosauruses A list to return newly born Allotosauruses.
     */
    private void giveBirth(List<Animal> newAllotosauruses)
    {
        // New Allotosauruses are born into adjacent locations.
        Field field = getAnimalField();

        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //Try to find a male partner adjacent to breed
        boolean foundOtherGender = false;
        while (it.hasNext())
        {
            Location currentSearchLocation = it.next();
            Object adjacentAnimal = field.getObjectAt(currentSearchLocation);
            if (adjacentAnimal instanceof Allosaurus)
            {
                Allosaurus otherAllosaurus = (Allosaurus)adjacentAnimal;
                if (otherAllosaurus.getIsMale() && otherAllosaurus.canBreed())
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
                Allosaurus young = new Allosaurus(field, getEnvironmentField(), loc, false);
                newAllotosauruses.add(young);
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
     * A Allosaurus can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Returns the gender of the Allosaurus.
     * @return If true, it is a male. Otherwise, female.
     */
    public boolean getIsMale()
    {
        return isMale;
    }
}
