package vn.infogate.ispider.handler;


import vn.infogate.ispider.Page;

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