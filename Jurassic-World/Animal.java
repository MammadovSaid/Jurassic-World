import java.util.List;
/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammadov.
 * @version 2021.03.02 (3)
 */
public abstract class Animal extends Organism
{
    // The animal's field.
    private Field animalField;
    // The environment's field.
    private Field environmentField;
    // The animal's position in the field.
    private Location location;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param foodValue the amount of hunger it satisfies when it gets eaten.
     */
    public Animal(Field animalField, Field environmentField, Location location, int foodValue)
    {
        super(foodValue);
        this.animalField = animalField;
        this.environmentField = environmentField;
        setLocation(location);
    }
    
     /**
     * Make this Animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    abstract public void act(List<Animal> newAnimals, boolean isDay, WeatherEnum weather);

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        super.setDead();
        if(location != null) {
            animalField.clear(location);
            location = null;
            animalField = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            animalField.clear(location);
        }
        location = newLocation;
        animalField.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getAnimalField()
    {
        return animalField;
    }
    
    /**
     * Return the environment's field.
     * @return The environment's field.
     */
    protected Field getEnvironmentField()
    {
        return environmentField;
    }
}
