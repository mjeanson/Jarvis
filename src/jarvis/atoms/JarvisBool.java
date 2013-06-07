package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

public class JarvisBool extends JarvisAtom{

	private boolean value;
	
	public JarvisBool(boolean v) {
		value = v;
	}
	
	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {		
		
		return this;
	}
	
	public boolean getValue()
	{
		return value;
	}

	@Override
	public String makeKey() {
		
		return ""+value;
	}

}
