/**
 * A class representing shared characteristics of organisms.
 * 
 * @author David J. Barnes Michael Kölling, Adnan Salah, Said Mammadov.
 * @version 2021.03.02 (3)
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's food value. The amount of hunger it satisfies when it gets eaten.
    private int foodValue;
    
    /**
     * Create a new organism
     * 
     * @param foodValue the amount of hunger it satisfies when it gets eaten.
     */
    public Organism(int foodValue)
    {
        this.foodValue = foodValue;
        alive=true;
    }
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the organism is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
    }
    
     /**
     * Return the organism's food value.
     * @return the organism's food value.
     */
    public int getFoodValue()
    {
        return foodValue;
    }
    
    /**
     * set the organism's food value.
     */
    protected void setFoodValue(int newFoodValue)
    {
        foodValue=newFoodValue;
    }
}
