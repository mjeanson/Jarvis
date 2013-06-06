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
	 * La plupart ont un nombre fixe de param�tres, il est donc v�rifi� ici.
	 * Dans certains cas, on veut laisser cette responsabilit� � la sous-classe.
	 * Celle-ci sp�cifiera donc un nombre de param�tres � -1 dans sa fonction init().
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
		
		//Les fonctions tricheuses sont toujours des m�thodes. Elles ne doivent pas �tre
		//appel�es autrement que par l'envoi d'un message � un objet.
		//Ainsi, le symbole self devrait �tre d�fini lorsqu'une telle fonction est appel�e.
		JarvisObject self = (JarvisObject)ji.getEnvironment().get("self");
		
		
		JarvisAtom res =execute(ji,self);
		
		return res;
		
	}

}
