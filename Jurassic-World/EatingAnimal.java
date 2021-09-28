import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals that eat and experience hunger.
 * 
 * @author David J. Barnes, Michael Kölling, Adnan Salah, Said Mammadov.
 * @version 2021.03.02 (3)
 */
public abstract class EatingAnimal extends Animal
{
    // A set of which organisms this animal can eat.
    private HashSet<Class> foodSet;
    // The amount of steps before the animal needs to eat again.
    private int foodLevel;
    
    /**
     * Create a new animal at location in field with foodLevel at 0.
     * 
     * @param animalField The field animals are currently occupying.
     * @param environmentField The field the environment is currently occupying.
     * @param location The location within the field.
     * @param foodValue the amount of hunger it satisfies when it gets eaten.
     */
    public EatingAnimal(Field animalField, Field plantField, Location location, int foodValue)
    {
        super(animalField, plantField, location, foodValue);
        foodSet = new HashSet<Class>();
        foodLevel = 0;
        setLocation(location);
    }
    
    /**
     * Look for plants in the current location or
     * look for animals adjacent to the current location.
     * Only the first live plant or animal that can be eaten will be eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Object plantFound = getEnvironmentField().getObjectAt(getLocation());
        if (canEat(plantFound))
        {
            Organism organism = (Organism)plantFound;

            if (organism.isAlive())
            {
                organism.setDead();

                if (foodLevel < organism.getFoodValue())
                    foodLevel = organism.getFoodValue();
                return null;
            }
        }
        
        Field field = getAnimalField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object objectFound = field.getObjectAt(where);

            if (canEat(objectFound))
            {
                Organism organism = (Organism)objectFound;

                if (organism.isAlive())
                {
                    organism.setDead();

                    if (foodLevel < organism.getFoodValue())
                        foodLevel = organism.getFoodValue();

                    return where;
                }
            }

        }
        
        return null;
    }
    
    /**
     * Return a set of which organisms this animal eats
     */
    protected HashSet<Class> getFoodSet()
    {
        return foodSet;
    }
    
    /**
     * Add an an organism to the set of which this animal can eat.
     */
    protected void addFoodSet(Class organismClass)
    {
        foodSet.add(organismClass);
    }
    
    /**
     * Checks if this animal can eat the organism
     * @return True if this animal can eat the organism
     */
    protected boolean canEat(Object objectFound)
    {
        return objectFound!=null && objectFound instanceof Organism && foodSet.contains(objectFound.getClass());
    }
    
    /**
     * Make this Animal more hungry. This could result in the Animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Set the foodLevel of this animal to a certain Value.
     * This value represents amount of steps before the animal needs to eat again.
     * @param value The new foodLevel.
     */
    protected void setFoodLevel(int value)
    {
        foodLevel = value;
    }
    
    /**
     * This value represents amount of steps before the animal needs to eat again.
     * @return The foodLevel of the animal
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }
}
