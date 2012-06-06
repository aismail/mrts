package cassdb.internal;

import java.util.Iterator;
import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.query.SliceQuery;

public class AllColumnsIterator<N, V> implements Iterator<HColumn<N, V>> {
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

	public Iterator<HColumn<N, V>> iterator() {
		return this;
	}

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

	public HColumn<N, V> next() {
		return _columnsIterator.next();
	}

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

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
