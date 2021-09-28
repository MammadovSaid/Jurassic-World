import java.util.List;
/**
 * A class representing shared characteristics of plants.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammadov
 * @version 2021.02.03 (3)
 */

public abstract class Plant extends Organism
{
    // The plant's field.
    private Field environmentField;
    // The plant's position in the field.
    private Location location;
    
    /**
     * Create a new plant at location in field.
     * 
     * @param environmentField The field plants are currently occupying.
     * @param location The location within the field.
     * @param foodValue the amount of hunger it satisfies when it gets eaten.
     */
    public Plant(Field environmentField, Location location, int foodValue)
    {
        super(foodValue);
        this.environmentField = environmentField;
        setLocation(location);
    }
    
     /**
     * Make this Plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born animals.
     * @param isDay If true, it represents daytime. Otherwise nighttime.
     * @param weather An enum that represents different weathers of the simulation.
     */
    abstract public void act(List<Plant> newPlants, boolean isDay, WeatherEnum weather);

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        super.setDead();
        if(location != null) {
            environmentField.clear(location);
            location = null;
            environmentField = null;
        }
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            environmentField.clear(location);
        }
        location = newLocation;
        environmentField.place(this, newLocation);
    }
    
    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    protected Field getEnvironmentField()
    {
        return environmentField;
    }
}
