package edu.napier.foodel.utils;
/*
 * Neil Urquhart 2019
 * In order to ensure that stochastic methods use a seeded random number generator this
 * class wraps the Java RNG and ensures that the seeded version is returned each time.
 * 
 * We use the Singleton design pattern.
 * 
 */
import java.util.Random;

public class RandomSingleton {
	private static  RandomSingleton instance= null;
	
	private RandomSingleton() { rnd = new Random();	}
	
	public static RandomSingleton getInstance() {
		if (instance == null)
			instance = new RandomSingleton();
		return instance;
	}
	
	private Random rnd = null;
	
    public void setSeed(int seed) {
    	rnd = new Random(seed);
    }
    
    public Random getRnd() {
    	return rnd;
    }
}
