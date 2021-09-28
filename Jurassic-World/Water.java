
/**
 * Represents water in a field. Mostly there so that animals can drink.
 *
 * @author Adnan Salah, Said Mammadov.
 * @version 1
 */
public class Water
{
    /**
     * Create new water at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Water(Field enviromentField, Location location)
    {
        if(location != null) {
            enviromentField.clear(location);
        }
        enviromentField.place(this, location);
    }
}
