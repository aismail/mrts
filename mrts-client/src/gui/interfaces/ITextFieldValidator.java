package gui.interfaces;

/**
 * Form text field validator interface - validate input from forms
 * 
 * @author cbarca
 */
public interface ITextFieldValidator {
	public boolean validate();
	public String getErrorMessage();
}
