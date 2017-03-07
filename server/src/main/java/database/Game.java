package database;


public interface Game {
	/**
	 * 
	 * @param room is the specific room for the game
	 */
	public void setGame(Room room) ;
	/**
	 * main function that runs the games
	 */
	public void checkStatus() ;
	
	public int getAcceptIntForSelect();



	      
}
