package jarvis.interpreter;

import jarvis.atoms.JarvisAtom;
import jarvis.atoms.JarvisDictionnary;

//SOUSLECAPOT
/*
 * Implantation d'un environnement.
 * Un environnement contient tout simplement
 * une table de hashage permettant d'associer
 * des symboles � des valeurs.
 * De plus, les environnements sont encha�n�s entre eux.
 * Lorsqu'on �value une fermeture, on entre dans 
 * un sous-environnement. Une fois la fermeture 
 * �valu�e, on revient dans l'environnement pr�c�dent.
 * Un environnement enfant a acc�s � tous les symboles d�finis
 * dans ses environnements parents. 
 * 
 */

public class JarvisEnvironment {

	private JarvisDictionnary dictionnary;

	private JarvisEnvironment parent;
	private JarvisInterpreter interpreter;

	

	public JarvisEnvironment(JarvisInterpreter ji) {

		dictionnary = new JarvisDictionnary();
		parent = null;
		
		interpreter = ji;

	}

	public JarvisEnvironment(JarvisInterpreter ji,JarvisEnvironment p) {

		dictionnary = new JarvisDictionnary();
		parent = p;
		
		interpreter = ji;

	}

	public void put(String id, JarvisAtom val) {

		dictionnary.put(id, val);
	}

	public JarvisAtom getLocal(String id)
	{
		return  dictionnary.get(id);
	}
	public JarvisAtom get(String id) {

		// recurse parent...
		JarvisAtom res = dictionnary.get(id);
		if (res == null) {
			if (hasParent()) {
				return parent.get(id);
			}

		}
		return res;

	}

	public void print() {
		dictionnary.print(interpreter);
		
	}

	public boolean hasParent() {
		
		return parent!=null;
	}

	public JarvisEnvironment getParent() {
		
		return parent;
	}

	public void get(JarvisAtom atom) {
		
		get(atom.makeKey());
		
	}

}
