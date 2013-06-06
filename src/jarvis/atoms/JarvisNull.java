package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

public class JarvisNull extends JarvisAtom{

	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {
		
		return this;
	}

	@Override
	public String makeKey() {
		
		return "null";
	}

}
