package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.HashMap;

public class JarvisDictionnary extends JarvisAtom {
	
	
	public static boolean nesting=false;

	private HashMap<String, JarvisAtom> data;

	public JarvisDictionnary() {
		data = new HashMap<String, JarvisAtom>();
	}

	public JarvisDictionnary(HashMap<String, JarvisAtom> values) {
		data = new HashMap<String, JarvisAtom>();
		data.putAll(values);
	}

	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {

		
		return this;
	}

	public JarvisAtom get(String key) {
		return data.get(key);
	}

	public void put(String key, JarvisAtom obj) {
		data.put(key, obj);
	}

	
	

	@Override
	public String makeKey() {
		
		if(nesting)
		{
			return toString();
		}
		
		String s= "[";
		
		for (String ref : data.keySet()) {			
			
				nesting = true;
				s+="[" + ref + "     |     ";
				JarvisAtom atom=data.get(ref);
				if(atom instanceof JarvisClosure)
				{						
					s+=atom+ "]\n";
				}
				else
				{
					s+=atom.makeKey()+ "]\n";
				}
				nesting = false;
			
		}	
		s+="]";
		
		
		return s;
	}
	
	
	public static JarvisDictionnary read(JarvisInterpreter ji)
	{
		JarvisDictionnary dict = new JarvisDictionnary();
		
		JarvisCommand.setDontPut(true);
		//Attention aux dictionnaires mal formés! 
		//Vérifie seulement si la clé est la fin.
		JarvisCommand key = ji.readCommandFromInput();
		
		while (!key.isEndOfDictionnary())
		{	
			JarvisCommand value = ji.readCommandFromInput();
			
			//La clé peut être fournie par n'importe quel atome.
			
			JarvisAtom keyAtom = key.interpretNoPut(ji);
			
			JarvisCommand.setDontInterpret(true);		
			JarvisAtom valueAtom = value.interpretNoPut(ji);
			JarvisCommand.setDontInterpret(false);	
			
			dict.data.put(keyAtom.makeKey(),valueAtom);
			key = ji.readCommandFromInput();
		}
		JarvisCommand.setDontPut(false);
		
		
		
		return dict;
		
	}

	public String reverseLookup(JarvisAtom atom) {
		
		
		
		for(String symbol:data.keySet())
		{
			if(data.get(symbol) == atom)
			{
				return symbol;
			}
		}
		
		return null;
	}

}
