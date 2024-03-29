package vn.infogate.ispider.extension.handler;


import vn.infogate.ispider.core.Page;

/**
 * @author code4crafter@gmail.com
 */
public interface SubPageProcessor extends RequestMatcher {

	/**
	 * process the page, extract urls to fetch, extract the data and store
	 *
	 * @param page page
	 *
	 * @return whether continue to match
	 */
	MatchOther processPage(Page page);

}
