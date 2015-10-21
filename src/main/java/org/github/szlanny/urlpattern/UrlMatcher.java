package org.github.szlanny.urlpattern;

import java.util.HashMap;
import java.util.Map;

public class UrlMatcher {

	Object handler;

	Map<String, String> vars = new HashMap<String, String>();

	public void addVar(String name, String value) {
		vars.put(name, value);
	}

	public Object getHandler() {
		return handler;
	}

	public Map<String, String> getVars() {
		return vars;
	}

	public String group(String name) {
		return vars.get(name);
	}

	@Override
	public String toString() {
		return "UrlMatcher [handler=" + handler + ", vars=" + vars + "]";
	}

}
