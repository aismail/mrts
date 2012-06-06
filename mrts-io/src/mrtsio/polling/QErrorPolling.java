package mrtsio.polling;

import java.util.List;

import cassdb.interfaces.IHashClient;
import cassdb.internal.HashClient;
import mrtsio.dbconx.MrtsConnector;

/**
 * Mean squared error polling class
 * 
 * @author cbarca
 */
public class QErrorPolling {
	// Private members
	private MrtsConnector _conx;
	private IHashClient _hash;
	private Long _last = null; 
	
	/**
	 * Default constructor
	 */
	public QErrorPolling() {
		_conx = new MrtsConnector();
		_hash = new HashClient(_conx.getKeyspace());
	}
	
	/**
	 * Get last 'cnt' qerrors
	 * @param keyL row key
	 * @param cnt maximum count
	 * @return list of object representing 'cnt+1' qerrors
	 */
	public List<Object> getLastQErrors(String keyL, int cnt) {
		List<Object> list = _hash.getTimestampSeries(
				MrtsConnector.NET_QERR_COLFAM, keyL, _last, null, cnt + 1);
		
		_last = (Long)list.get(list.size() - 1);
		
		return list.subList(0, list.size() - 1);
	}
}
