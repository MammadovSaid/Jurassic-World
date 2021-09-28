import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field.
 * this simulator simulates various animals, some plants, water, 
 * time of day, and weather.
 * 
 * @author David J. Barnes, Michael Kölling and Said Mammadov.
 * @version 2021.03.02 (3)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    //CREATION PROBABILITIES
    // The probability that a TRex will be created in any given grid position.
    private static final double TREX_CREATION_PROBABILITY = 0.005;
    // The probability that a Ceratosaurus will be created in any given grid position.
    private static final double CERATOSAURUS_CREATION_PROBABILITY = 0.03;
    // The probability that a Allosaurus will be created in any given grid position.
    private static final double ALLOSAURUS_CREATION_PROBABILITY = 0.02;
    // The probability that a Turtle will be created in any given grid position.
    private static final double TURTLE_CREATION_PROBABILITY = 0.08;
    // The probability that a Gastonia will be created in any given grid position.
    private static final double GASTONIA_CREATION_PROBABILITY = 0.08;
    // The probability that a Yinlong will be created in any given grid position.
    private static final double YINLONG_CREATION_PROBABILITY = 0.08;
    // The probability that a bush will be created in any given grid position.
    private static final double BUSH_CREATION_PROBABILITY = 0.3;
    // The probability that water will be created in any given grid position.
    private static final double WATER_CREATION_PROBABILITY = 0.2;

    //WEATHER PROOBABILITIES
    // The probability that the weather will be snowing.
    private static final double SNOWING_PROBABILITY = 0.05;
    // The probability that the weather will be raining.
    private static final double RAINING_PROBABILITY = 0.2;
    // The probability that the weather will be foggy.
    private static final double FOGGY_PROBABILITY = 0.07;

    // List of animals in the field.
    private List<Animal> animals;
    // List of environments in the field. Water, plants, etc.
    private List<Object> environment;
    // The current state of the field animals are occupying.
    private Field animalField;
    // The current state of the field the environment is occupying.
    private Field environmentField;
    // The current step of the simulation.
    private int step;
    // The weather condition.
    private WeatherEnum weather;
    // A graphical view of the animal simulation.
    private SimulatorView view;
    // A graphical view of the environment simulaton
    private SimulatorView environmentView;
    // A shared random number generator to control conditions
    private Random rand = Randomizer.getRandom();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        environment = new ArrayList<>();
        animalField = new Field(depth, width);
        environmentField = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(TRex.class, Color.GREEN);
        view.setColor(Ceratosaurus.class, Color.RED);
        view.setColor(Allosaurus.class, Color.BLACK);
        view.setColor(Turtle.class, Color.CYAN);
        view.setColor(Gastonia.class, Color.GRAY);
        view.setColor(Yinlong.class,Color.ORANGE);
        view.setInfoText("Animal Map");

        environmentView = new SimulatorView(depth, width);
        environmentView.setColor(Bush.class,Color.GREEN);
        environmentView.setColor(Water.class,Color.CYAN);
        environmentView.setInfoText("Environment Map");

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(animalField); step++) {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal and plant.
     */
    public void simulateOneStep()
    {
        step++;
        boolean isDay = (step%2 == 0);
         
        // Let all plants act.
        List<Plant> newPlants = new ArrayList<>(); 
        for(Iterator<Object> it = environment.iterator(); it.hasNext(); ) {
            Object fieldObject = it.next();
            if (fieldObject instanceof Plant)
            {
                Plant plant = (Plant)fieldObject;
                plant.act(newPlants,isDay,weather);
                if (!plant.isAlive()) {
                    it.remove();
                }
            }

        }
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>(); 
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals,isDay,weather);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        //Change weather based on chance.
        if (rand.nextDouble() <= SNOWING_PROBABILITY)
            weather = WeatherEnum.SNOWING;
        else if (rand.nextDouble() <= RAINING_PROBABILITY)
            weather = WeatherEnum.RAINING;
        else if (rand.nextDouble() <= FOGGY_PROBABILITY)
            weather = WeatherEnum.FOGGY;
        else
            weather = WeatherEnum.CLEAR;

        // Add the newly born animals to the animal list.
        animals.addAll(newAnimals);
        
        //Add the newly grown plants to the environment list.
        environment.addAll(newPlants);

        view.showStatus(step, animalField, weather);
        environmentView.showStatus(step, environmentField, weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        environment.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, animalField, weather);
        environmentView.showStatus(step, environmentField, weather);
    }

    /**
     * Randomly populate the field with various animals and environments.
     */
    private void populate()
    {
        animalField.clear();
        environmentField.clear();
        for(int row = 0; row < animalField.getDepth(); row++) {
            for(int col = 0; col < animalField.getWidth(); col++) {
                //Spawn Animals
                if (rand.nextDouble() <= TREX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    TRex tRex = new TRex(animalField, environmentField, location, true);
                    animals.add(tRex);
                }
                else if (rand.nextDouble() <= CERATOSAURUS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Ceratosaurus ceratosaurus = new Ceratosaurus(animalField, environmentField, location, true);
                    animals.add(ceratosaurus);
                }
                else if (rand.nextDouble() <= ALLOSAURUS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Allosaurus allosaurus = new Allosaurus(animalField, environmentField, location, true);
                    animals.add(allosaurus);
                }
                else if (rand.nextDouble() <= TURTLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Turtle turtle = new Turtle(animalField, environmentField, location, true);
                    animals.add(turtle);
                }
                else if (rand.nextDouble() <= GASTONIA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Gastonia gastonia = new Gastonia(animalField, environmentField, location, true);
                    animals.add(gastonia);
                }
                else if (rand.nextDouble() <= YINLONG_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Yinlong yinlong = new Yinlong(animalField, environmentField, location, true);
                    animals.add(yinlong);
                }
                // else leave the location empty.

                //Spawn enviormnets
                if (rand.nextDouble() <= BUSH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bush bush = new Bush(environmentField, location);
                    environment.add(bush);
                }
                else if (rand.nextDouble() <= WATER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Water water = new Water(environmentField, location);
                    environment.add(water);
                }
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
