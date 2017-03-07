package bluffer;

public class Question {
	/**
	 * Defined the question
	 */
	private String questionText= "";
	private String realAnswer= "";
	
	/**
	 * @param questionText the question
	 * @param realAnswer the real answer
	 */
	public Question(String questionText, String realAnswer){
		this.questionText=questionText;
		this.realAnswer=realAnswer;
	}
	
	public String getQuestionText(){
		return questionText;
	}
	
	public String getRealAnswer(){
		return realAnswer;
	}

	
}
