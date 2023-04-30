package api.exception;

public class SubredditNotFoundException extends RuntimeException{
	public SubredditNotFoundException(String msg) {
		super(msg);
	}
}
