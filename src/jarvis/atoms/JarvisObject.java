package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

/*
 * Cette classe implante l'objet de base.
 * L'interpr�teur comprend un objet comme
 * une simple liste de valeurs.
 * L'organisation de ses donn�es est sp�cifi�e
 * par la classe. Celle-ci peut �tre retrouv�e
 * via le lien classReference.
 */
public class JarvisObject extends JarvisAtom {

	/*
	 * Si vous ajoutez des champs � JarvisClass
	 * ces constantes doivent le refl�ter.
	 * Elles sont utilis�es pour retrouver
	 * les membres d'une classe.
	 * 
	 */
	public static final int ATTRIBUTE_FIELD =0;
	public static final int METHOD_FIELD =1;
	public static final int SUPER_FIELD =2;
	
	/*
	 * R�f�rence � la classe de cet objet.
	 */
	private JarvisObject classReference;
	private ArrayList<JarvisAtom> values;
	
	//R�f�rence utile pour faire des reverse lookup
	private JarvisInterpreter ji;

	// Constructeur d'objet g�n�rique
	// Utilis� comme raccourci par les fonctions tricheuses.
	public JarvisObject(JarvisObject theClass, ArrayList<JarvisAtom> vals,JarvisInterpreter ji) {

		classReference = theClass;

		values = new ArrayList<JarvisAtom>();
		values.addAll(vals);
		
		this.ji=ji;
	}
	
	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {	
		return this;
	}

	public JarvisObject getJarvisClass() {
		return classReference;
	}
	
	
	//Cas sp�cial o� le selecteur n'est pas encore encapsul� dans un atome
	//Support� pour all�ger la syntaxe.
	public JarvisAtom message(String selector) {
		
		return message(new JarvisString(selector));
		
	}
	
    //H�RITAGE
	//VARIABLESCLASSE
	/*
	 * Algorithme de gestion des messages.
	 * Ce bout de code a pour responsabilit� de d�terminer si le message
	 * concerne un attribut ou une m�thode. 
	 * Pour implanter l'h�ritage, cet algorithme doit n�cessairement �tre modifi�.
	 */	
	public JarvisAtom message(JarvisAtom selector) {
		
		
		//Va chercher les attributs
		JarvisList members = (JarvisList) classReference.values.get(ATTRIBUTE_FIELD);

		//V�rifie si c'est un attribut 
		int pos = members.find(selector);
		
		
		if (pos == -1) {
			// pas un attribut...
			// Va chercher les m�thodes
			JarvisDictionnary methods = (JarvisDictionnary) classReference.values
					.get(METHOD_FIELD);

			// Cherche dans le dictionnaire
			JarvisAtom res = methods.get(selector.makeKey());

			if (res == null) {
				
				// Rien ne correspond au message
				
				// Allez chercher la super classe dans le classref
				JarvisAtom classrefsuper = (JarvisAtom) classReference.values.get(SUPER_FIELD);
				
				return getsupermethod(classrefsuper, selector);
			} else {
				//C'est une m�thode.
				return res;
			}
		} else {
			//C'est un attribut.
			return values.get(pos);
		}
	}
	
	private JarvisAtom getsupermethod(JarvisAtom superclass, JarvisAtom selector) {
		
		if (superclass instanceof JarvisString) {
			// On a atteint la racine de l'arbre d'h�ritage
			return new JarvisString("ComprendPas "+ selector);
		} else {
			// Va chercher les m�thodes
			JarvisDictionnary methods = (JarvisDictionnary) ((JarvisObject) superclass).values.get(METHOD_FIELD);
		
			// Cherche dans le dictionnaire
			JarvisAtom res = methods.get(selector.makeKey());
			
			if (res == null) {
			
				// Rien ne correspond au message
			
				// Allez chercher la super classe
				JarvisAtom supersuperclass = (JarvisAtom) ((JarvisObject) superclass).values.get(SUPER_FIELD);
			
				return getsupermethod(supersuperclass, selector);
			} else {
				//C'est une m�thode.
				return res;
			}
		}
	}

	public void setClass(JarvisObject theClass) {
		classReference = theClass;
	}

	
	
	//Surtout utile pour l'affichage dans ce cas-ci...
	@Override
	public String makeKey() {
		String s="";
		int i=0;
		
		s += "\""+ji.getEnvironment().reverseLookup(classReference)+"\":";
		
		for (JarvisAtom atom : values) {
			
			s+=" "+((JarvisList)classReference.values.get(0)).get(i).makeKey()+":";
			if(atom instanceof JarvisClosure)
			{
				s+=atom;
			}
			else
			{
				s+=atom.makeKey();
			}
			
			i++;
		}
		
		return s;
	}	

}
