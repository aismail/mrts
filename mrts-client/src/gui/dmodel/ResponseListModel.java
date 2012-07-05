package gui.dmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Data model (responsive - observable property) for JList
 * 
 * @author cbarca
 * @param <E>
 */
public class ResponseListModel<E> extends DefaultListModel implements Iterable<E> {
	private static final long serialVersionUID = -5870004502646512422L;
	private List<E> delegate = new ArrayList<E>();

	@Override
	public int getSize() {
		return delegate.size();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Object getElementAt(int index) {
		return delegate.get(index);
	}

	public void add(E e){
		int index = delegate.size();
		delegate.add(e);
		fireIntervalAdded(this, index, index);
	}

	public void remove(E e) {
		int index = delegate.indexOf(e);
		delegate.remove(e);
		fireIntervalRemoved(this, index, index);
	}

	public E get(int i) {
		return delegate.get(i);
	}

	@Override
	public Iterator<E> iterator() {
		return delegate.iterator();
	}
}
