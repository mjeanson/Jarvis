package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

public class JarvisInt extends JarvisAtom {

	private int value;

	public JarvisInt(int v) {
		value = v;
	}

	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {

		
		return this;

	}

	public int getValue()
	{
		return value;
	}
		

	@Override
	public String makeKey() {
		
		return ""+value;
	}

}
