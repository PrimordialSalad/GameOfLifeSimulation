import java.awt.Point;
import java.util.BitSet;
import java.util.Random;
/*
 * The AI's shouldn't continue once the stop button has been hit.
 * Also they should be reset when the clear button has been hit.
 */
public class BlueAIEngine
{
//	The instantiation of the game of life class.    
	private GameOfLife m_game;
//	The column count of the grid.	
	private int m_columnCount;
//	The row count of the grid.	
	private int m_rowCount;
//	The instantiation of the core AI class.	
	private CoreAIEngine core;
//	The collection of safe indexes.  The indexes of the "hazard" line.	
	private int[] m_safeIndexes = new int[15];
//	The collection of random points that can be the central point.  Used for spaceship creation.	
	private int[] m_randomCentralPoint = new int[2];
//	The random variable for use when a random number is needed.	
	private Random m_randInt = new Random();
//	The point that all the sprites created by the AI are created around this point.	
	private Point m_centralPoint;
//	The amount of turns left before the AI can create another spaceship or block.	
	private int turnsLeft = m_randInt.nextInt(6);
	
	public BlueAIEngine(GameOfLife game, int columnCount, int rowCount)
	{
//	This is where the instantiation of the game is set to the game of life passing in itself.	    
		m_game = game;
//	The column count is set to the passed in column count.	
		m_columnCount = columnCount;
//	The row count is set to the passed in row count.		
		m_rowCount = rowCount;
//	The instantiation of the core class being set.		
		core = new CoreAIEngine(m_columnCount, m_rowCount);
		
//	Adds the central index.  If it was in the for loop it would have been added twice.		
		m_safeIndexes[0] = core.indexFinder(m_rowCount / 2, 17);
		
//		Used to add the "hazard" line indexes to the array.		
		for(int r = 1; r < 8; r++)
		{
//		Creates all indexes above the central point.		    
		    m_safeIndexes[2 * r - 1] = core.indexFinder((m_rowCount / 2) + r, 17);
//		Creates all indexes below the central point.		    
		    m_safeIndexes[2 * r] = core.indexFinder((m_rowCount / 2) - r, 17);
		}
		
//		Adds the two indexes where the spaceship can be randomly created.		
		m_randomCentralPoint[0] = m_columnCount / 2;
		m_randomCentralPoint[1] = (m_columnCount / 2) + 1;
	}
	
	public void run(BitSet original)
	{   
//	Checks to make sure it won, first.  Setting the turns left is just a way to break from the function.	    
	    if(won())
	    {
		turnsLeft = 1000;
	    }
	    
	    if(turnsLeft == 0)
	    {
//		If it hasn't won, then is checks to see if it has 0 turns left, before it can
//		create a sprite.
		
//		This is the possible index of a breach in the "hazard" line		
		int possibleIndex = checkSafeIndexes(original);
		
//		If there is no breach than the function returns negative 1.		
		if(possibleIndex == -1)
		{
//		Creates a point around one of the two random points it can be created.		    
		    m_centralPoint = new Point(m_randomCentralPoint[m_randInt.nextInt(2)], 19);
		    
//		Sets the data to the return of the AI creating a spaceship.		    
		    m_game.setData(core.createSpaceship(m_centralPoint, original));
//		Sets the turns left to a random number.		   
		    turnsLeft = 35 + m_randInt.nextInt(20);
	    	}
		else
		{
//		If it has been breached then it creates a point in front of where
//		the sprite got trough.		    
		    m_centralPoint = new Point(((possibleIndex - 12) / m_columnCount), 15);
//		Then creates a block there to obstruct the path of the incoming sprite.		    
		    m_game.setData(core.createBlock(m_centralPoint, original));
//		Sets the turns left to a random number.		    
		    turnsLeft = 35 + m_randInt.nextInt(20);
		}
	    }
	    
	    else
	    {
//		Decrements the turns left if it still does.		
		turnsLeft--;
	    }
	}
	
	public BitSet createBlueCore() // The blue core is on the left hand side.
	{
//	The core is what the AI is trying to destroy and protect.	    
//	A temp variable that is set in the game of life class to be the new data BitSet	    
	   BitSet result =  m_game.getData();
	   
//	This is where the core is set.  Turning on and off the specific cells.	   
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2), 0));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 1, 0));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2), 1));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 1, 1));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 3, 2));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 2, 2));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 3, 3));
	   result.set(core.indexFinder(m_rowCount - (m_rowCount / 2) + 2, 3));
	   
//	Returns the result of the temp variable.	   
	   return result;
	}
	
	public boolean won()
	{
//	This is the function to find out whether or not it has won yet.
	    boolean result = false;
	    
	    if(!(m_game.getData().get(core.indexFinder(m_rowCount - (m_rowCount / 2) , m_columnCount - 1))))
		    {
//			Used to exit the loop in the game of life class so that the program stops.		
				m_game.setWon();
				result = true;
		    }
//	Returns the boolean as to whether or not it has won yet.	    
	    return result;
	}
	
	public int checkSafeIndexes(BitSet original)
	{
//	Checks to make sure the none of the safe indexes are true.
//	Sets the initial result to -1.	    
	    int result = -1;
	    
	    for(int i = 0; i < m_safeIndexes.length; i++)
	    {
//		Loops through the whole array checking each value which corresponds to the "hazard" line.		
		if(original.get(m_safeIndexes[i]))
		{
//		If one result is found that index is set as the result.		    
		    result = m_safeIndexes[i];
		}
	    }
//	Returns the result.	    
	    return result;
	}
	
	public int[] giveSafeIndexes()
	{
//	Returns the array which houses the safe indexes.	    
	    return m_safeIndexes;
	}
}
