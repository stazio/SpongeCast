package stazthebox.sponge.spongeCast.exceptions;

/**
 * Created by staz on 8/22/16.
 */
public class InvalidMessageModeException extends Throwable
{
	public InvalidMessageModeException(String mode)
	{
		super(mode + " is not a valid mode.");
	}
}
