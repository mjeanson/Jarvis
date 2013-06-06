package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

public class JarvisString extends JarvisAtom {

	private String value;
	
	

	public JarvisString(String v) {
		value = v;
	}

	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {

		//ji.output(value);

		return this;

	}
	

	@Override
	public String makeKey() {

		return value;
	}

}
