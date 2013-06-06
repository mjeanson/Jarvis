package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class JarvisList extends JarvisAtom{
	
	
	
	//todo: JarvisAtom seulement
	private ArrayList<JarvisAtom> data;
	
	
	
	public JarvisList()
	{
		data=new ArrayList<JarvisAtom>();		
	}
	public JarvisList(ArrayList<JarvisAtom> values)
	{
		data=new ArrayList<JarvisAtom>();
		data.addAll(values);
	}
	
	public static JarvisList read(JarvisInterpreter ji)
	{
		JarvisList list = new JarvisList();
		
		JarvisCommand.setDontPut(true);
		JarvisCommand input = ji.readCommandFromInput();
		while (!input.isEndOfList())
		{	
			JarvisCommand.setDontInterpret(true);	
			JarvisAtom atom = input.interpretNoPut(ji);
			JarvisCommand.setDontInterpret(false);	
			list.data.add(atom);
			input = ji.readCommandFromInput();
		}
		JarvisCommand.setDontPut(false);
		
		return list;
	}
	
	
	@Override
	public JarvisAtom interpretNoPut(JarvisInterpreter ji) {
		
		
		return this;
	}
	
	
	public int indexOf(Object obj)
	{
		return data.indexOf(obj);
	}

	
	
	public int size()
	{
		return data.size();
	}
	@Override
	public String makeKey() {
		String s="(";
		for (JarvisAtom obj : data) {
			s+=obj.makeKey()+",";			
		}
		s+=")";
		return s;
	}
	public int find(JarvisAtom selector) {
		
		int i=0;
		for(JarvisAtom atom : data)
		{
			if(atom.makeKey().compareTo(selector.makeKey())==0)
			{
				return i;
			}
			i++;
		}
		
		return -1;
	}
	
	public JarvisAtom get(int pos)
	{
		if(pos<data.size())
		{
			return data.get(pos);
		}
		else
		{
			throw new NoSuchElementException("JarvisList: no such element: "+pos);
		}
	}
	
	public void add(JarvisAtom atom)
	{
		data.add(atom);
	}
	
	

}
