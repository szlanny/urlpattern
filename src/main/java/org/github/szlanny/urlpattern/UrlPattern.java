package org.github.szlanny.urlpattern;

public class UrlPattern {
	private UrlNode root = new UrlNode();

	public UrlPattern() {
		root.setNode("root");
	}

	private String normalize(String str) {
		if (str.startsWith("/")) {
			str = str.substring(1);
		}

		if (str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public void addUrlPattern(String urlpattern, Object handler) {
		String[] path = normalize(urlpattern).split("/");
		root.addPath(new PathInfo(path, 0), handler);
	}

	public UrlMatcher parser(String urlpath) {
		UrlMatcher m = new UrlMatcher();
		String[] path = normalize(urlpath).split("/");
		root.match(new PathInfo(path, 0), m);
		return m;
	}
	

	@Override
	public String toString() {
		return root.toString();
	}

}
