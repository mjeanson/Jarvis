package jarvis.atoms.cheatcode;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisObject;
import jarvis.interpreter.JarvisInterpreter;

public abstract class JarvisFunction extends JarvisAtom{
	
	protected int argCount;
	protected abstract JarvisAtom execute(JarvisInterpreter ji,JarvisObject self);
	
	//FONCTIONSTRICHEUSES
	/*
	 * Gabarit pour les fonctions tricheuses.
	 * La plupart ont un nombre fixe de paramètres, il est donc vérifié ici.
	 * Dans certains cas, on veut laisser cette responsabilité à la sous-classe.
	 * Celle-ci spécifiera donc un nombre de paramètres à -1 dans sa fonction init().
	 * 
	 */	
	
	public JarvisFunction()
	{
		init();
	}
	
	protected abstract void init();
	
	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {
		
		if (argCount != ji.getArgCount() && argCount!=-1)
		{
			throw new IllegalArgumentException("Bad number of arguments, expected "+argCount+" got "+ji.getArgCount());			
		}
		
		//Les fonctions tricheuses sont toujours des méthodes. Elles ne doivent pas être
		//appelées autrement que par l'envoi d'un message à un objet.
		//Ainsi, le symbole self devrait être défini lorsqu'une telle fonction est appelée.
		JarvisObject self = (JarvisObject)ji.getEnvironment().get("self");
		
		
		JarvisAtom res =execute(ji,self);
		
		return res;
		
	}

}
