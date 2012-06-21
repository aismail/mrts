package fnn.dataio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import au.com.bytecode.opencsv.CSVReader;

public class PatternBuffIterator implements Iterator<Pattern>{
	private PatternList _pl;
	private int _buffSize, _currFile, _nin, _nout;
	private File[] _files;
	private CSVReader _currReader;
	boolean _lastFetch;
	private Iterator<Pattern> _it;
	
	public PatternBuffIterator(int buffSize, String dirPath, int nin, int nout) {
		_pl = new PatternList();
		_buffSize = buffSize;
		
		_nin = nin;
		_nout = nout;
		
		_files = new File(dirPath).listFiles();
		_lastFetch = false;
		_currFile = 0;
	}
	
	@Override
	public boolean hasNext() {
		if (_it == null || !_it.hasNext()) {
			try {
				if (!fetchMore()) {
					return false;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}

	@Override
	public Pattern next() {
		return _it.next();
	}
	
	public Iterator<Pattern> iterator() {
		return this;
	}
	
	private boolean fetchMore() throws IOException {
		int size = 0;
		String[] values = null;
			
		if (_lastFetch) {
			return false;
		}
		
		_pl = new PatternList();
		
		if (_currReader == null) {
			_currReader = new CSVReader(new FileReader(_files[_currFile]));
			System.out.println("Open file: " + _files[_currFile].getName());
			_currReader.readNext();
		}
		
		while (size < _buffSize && !_lastFetch) {
			while (size < _buffSize && (values = _currReader.readNext()) != null) {
				size++;
				
				double[] input = new double[_nin];
				double[] output = new double[_nout];
				
				for (int i = 0; i < _nin; i++) {
					input[i] = Double.parseDouble(values[i]); 
				}
				for (int i = _nin; i < values.length; i++) {
					output[i - _nin] = Double.parseDouble(values[i]);
				}
				
				_pl.add(input, output);
			}
				
			if (values == null) {
				if (_currFile == _files.length - 1) {
					_lastFetch = true;
					System.out.println("Close file: " + _files[_currFile].getName());
					_currReader.close();
					continue;
				}
				
				System.out.println("Close file: " + _files[_currFile].getName());
				_currReader.close();
				_currReader = new CSVReader(new FileReader(_files[++_currFile])); 
				System.out.println(_files[_currFile].getName());
				_currReader.readNext();
			} 
		}
		
		_it = _pl.getIterator();
		System.out.println("fetch");
		
		return true;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) {
		PatternBuffIterator bIt = new PatternBuffIterator(1000, "./dataset/demo", 301, 2);
		int c = 0;
		
		while (bIt.hasNext()) {
			bIt.next();
			System.out.println(++c);
		}
	}
}
