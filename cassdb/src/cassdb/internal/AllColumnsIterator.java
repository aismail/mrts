package cassdb.internal;

import java.util.Iterator;
import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.query.SliceQuery;

/**
 * Columns Iterator
 * Iterate through columns of Cassandra database 
 * 
 * @author cbarca
 *
 * @param <N>
 * @param <V>
 */
public class AllColumnsIterator<N, V> implements Iterator<HColumn<N, V>> {
	// Private members
	private N _start;
	private int _count;
	private Iterator<HColumn<N, V>> _columnsIterator;
	private SliceQuery<?, N, V> _query;
	private boolean _isLastIteration;

	public AllColumnsIterator(SliceQuery<?, N, V> query, int pageCount) {
		_start = null;
		_count = pageCount;
		_columnsIterator = null;
		_query = query;
		_isLastIteration = false;
	}

	/**
	 * Return the iterator
	 * @return iterator (this)
	 */
	public Iterator<HColumn<N, V>> iterator() {
		return this;
	}

	/**
	 * Iterator has next method
	 * @return true/false - if a next value exists
	 */
	public boolean hasNext() {
		if (_columnsIterator == null || !_columnsIterator.hasNext()) {
			if (_isLastIteration) {
				return false;
			}

			if (!fetchMore()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Iterator next method
	 * @return Cassandra column structure
	 */
	public HColumn<N, V> next() {
		return _columnsIterator.next();
	}

	/**
	 * Iterator fetch more method
	 * @return true/false - if is possible to fetch more columns
	 */
	private boolean fetchMore() {
		try {
			_query.setRange(_start, null, false, _count);
			
			ColumnSlice<N, V> slice = _query.execute().get();
			List<HColumn<N, V>> columns = slice.getColumns();
			int origSize = columns.size();

			if (origSize == 0) {
				return false;
			}

			if (origSize >= _count) {
				_start = columns.remove(columns.size()-1).getName();
			}

			_columnsIterator = columns.iterator();

			if (origSize < _count) {
				_isLastIteration = true;
			}

			return true;
		} catch (HectorException e) {
			return false;
		}
	}

	/**
	 * Iterator remove method (unsupported)
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
