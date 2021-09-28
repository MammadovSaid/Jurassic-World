import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a turtle.
 * Turtles age, move, breed, drink and die.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammmadov
 * @version 2021.02.03 (3)
 */
public class Turtle extends Animal
{
    // Characteristics shared by all turtles (class variables).

    // The age at which a turtle can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a turtle can live.
    private static final int MAX_AGE = 250;
    // The likelihood of a turtle breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The likelihood of a turtle breeding while it is snowing.
    private static final double SNOW_BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The amount drinking water replinishes the thirst level.
    private static final int WATER_DRINK_VALUE = 15;
    
    // Individual characteristics (instance fields).
    // The turtle's thirst level.
    private int thirstLevel;
    // The turtle's age.
    private int age;

    /**
     * Create a new turtle. A turtle may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param animalField The field a turtle is currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param randomAge If true, the turtle will have a random age.
     */
    public Turtle(Field animalField, Field environmentField, Location location, boolean randomAge)
    {
        super(animalField,environmentField, location, 30);
        thirstLevel = WATER_DRINK_VALUE;
        setup(randomAge);
    }
    
    /**
     * Setup a turtle with 0 or random age.
     * 
     * @param randomAge If true, turtle will have a randomAge.
     */
    private void setup(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }
    
    /**
     * This is what the turtle does most of the time - it runs 
     * around. Sometimes it will breed, drink or die of either old age or thirstiness.
     * @param newTurtles A list to return newly born turtles.
     * @param isDay If it means daytime, otherwise night-time
     * @param weather It shows the state of the weather.
     */
    public void act(List<Animal> newTurtles, boolean isDay, WeatherEnum weather)
    {
        incrementAge();
        incrementThirst();
        if(isAlive()) {
            drink();
            if (isDay==false)
                giveBirth(newTurtles,weather);            
            // Try to move into a free location.
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
     * This could result in the turtle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Decreases the thirst level. This could result in the turtle's death.
     */
    private void incrementThirst()
    {
        thirstLevel--;
        if (thirstLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Tries to look for water at the location it is in.
     * Resets thirstLevel if water is found.
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
     * Check whether or not this turtle is ready to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newTurtles A list to return newly born turtles.
     * @param weather It shows the state of the weather.
     */
    private void giveBirth(List<Animal> newTurtles, WeatherEnum weather)
    {
        // New turtles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getAnimalField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(weather);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Turtle young = new Turtle(field,getEnvironmentField(), loc, false);
            newTurtles.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     * @param weather It shows the state of the weather.
     */
    private int breed(WeatherEnum weather)
    {
        int births = 0;
        double breedProbability = BREEDING_PROBABILITY;
        if (weather == WeatherEnum.SNOWING)
            breedProbability = SNOW_BREEDING_PROBABILITY;
            
        if(canBreed() && rand.nextDouble() <= breedProbability) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A turtle can breed if it has reached the breeding age.
     * @return true if the turtle can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
