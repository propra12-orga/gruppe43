package bombi;

public class IllegalFormatException extends Exception{

	// automatisch erzeugt
	private static final long serialVersionUID = 1L;
	private static final String IFE = "IllegalFormatException";
	
	public IllegalFormatException(){
		super(IFE);
	}
	
	public IllegalFormatException(final String errorMsg){
		super(IFE + errorMsg);
	}
}
