package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import bluffer.Bluffer;

public class DatabaseSingelton {
    /**
     * manage all the data bases for all the games
     */
	  private ArrayList<String> listOfGames = new ArrayList<String>();
	  private ArrayList<String> listOfPeople = new ArrayList<String>();
	  private Hashtable<String ,Room> mapOfRooms = new Hashtable<String ,Room>();	
	  private Map<String, GameCreator> GameCreators = new HashMap<>();

	  // singelton
	  private static class DatabaseSingeltonHolder {
	        private static DatabaseSingelton instance = new DatabaseSingelton();
	    }
	    private DatabaseSingelton() {
	    	getListOfGames().add("BLUFFER");
	    	GameCreators.put("BLUFFER", Bluffer::new);
	    }
	    public static DatabaseSingelton getInstance() {
	        return DatabaseSingeltonHolder.instance;
	    }
	    
	    public Game getGame (String name){
	    	GameCreator creator = GameCreators.get(name);
	    	return creator.create();
	    }
	    
	   	public ArrayList<String> getListOfPeople() {
			return listOfPeople;
		}

		public Hashtable<String ,Room> getMapOfRooms() {
			return mapOfRooms;
		}

		public ArrayList<String> getListOfGames() {
			return listOfGames;
		}

		public interface GameCreator {
	   	    Game create();
	   	}


	    
	    
}
