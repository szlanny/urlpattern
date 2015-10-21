package org.github.szlanny.urlpattern;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlNode {
	final static String ALPHA = "a-zA-Z";
	final static String DIGIT = "\\d";
	final static String ALPHA_DIGIT = ALPHA + DIGIT;
	final static String HEXA = DIGIT + "ABCDEFabcdef";
	final static String URI_UNRESERVED = ALPHA_DIGIT + "\\-\\.\\_\\~";
	final static String URI_GEN_DELIMS = "\\:\\/\\?\\#\\[\\]\\@";
	final static String URI_SUB_DELIMS = "\\!\\$\\&\\'\\(\\)\\*\\+\\,\\;\\=";
	final static String URI_RESERVED = URI_GEN_DELIMS + URI_SUB_DELIMS;

	final static String PCT_ENCODED = "\\%[" + HEXA + "][" + HEXA + "]";
	final static String PCHAR = "[" + URI_UNRESERVED + URI_SUB_DELIMS
			+ "\\:\\@]|(?:" + PCT_ENCODED + ")";
	public final static String QUERY = PCHAR + "|\\/|\\?";
	public final static String FRAGMENT = QUERY;
	public final static String URI_PATH = PCHAR + "|\\/";
	public final static String URI_ALL = "[" + URI_RESERVED + URI_UNRESERVED
			+ "]|(?:" + PCT_ENCODED + ")";

	// 节点的原始值
	private String node;

	// 节点的名字
	private String name;

	// 节点的正则表达式
	private Pattern regex;

	// 节点对应的数据
	private Object handler;

	// 子节点，形成一棵树
	private List<UrlNode> subNodes = new LinkedList<UrlNode>();

	public void addPath(PathInfo pathInfo, Object handler) {
		if (pathInfo.overflow()) {
			return;
		}

		String p = pathInfo.item();
		UrlNode un = find(p);
		if (un == null) {
			un = new UrlNode();
			un.setNode(p);
			subNodes.add(un);
		}

		if (pathInfo.end()) {
			un.handler = handler;
		} else {
			pathInfo.idx++;
			un.addPath(pathInfo, handler);
		}

	}

	public void match(PathInfo pathInfo, UrlMatcher matcher) {
		if (pathInfo.overflow()) {
			return;
		}

		String p = pathInfo.item();
		// System.out.println(p);
		for (UrlNode un : subNodes) {
			// 字符串值相同的具有优先权
			if (un.name == null && un.node.equals(p)) {
				// System.out.println(un.toStr());
				lastMatched(pathInfo, matcher, un);
				return;
			}
		}

		for (UrlNode un : subNodes) {
			// System.out.println(un.toStr());
			if (un.regex != null) {
				Matcher m = null;
				boolean end = un.subNodes.isEmpty();
				m = un.regex.matcher(end ? pathInfo.remain() : p);
				if (m != null && m.find()) {
					// System.out.println(m.group(0));
					matcher.addVar(un.name, m.group(0));
					if (end) {
						pathInfo.setEnd();
					}
					lastMatched(pathInfo, matcher, un);
					return;
				}

			}
		}
	}

	private void lastMatched(PathInfo pathInfo, UrlMatcher matcher, UrlNode un) {
		if (pathInfo.end()) {
			matcher.handler = un.handler;
		} else {
			pathInfo.idx++;
			un.match(pathInfo, matcher);
		}
	}

	private UrlNode find(String p) {
		for (UrlNode un : subNodes) {
			if (un.node.equals(p)) {
				return un;
			}
		}
		return null;
	}

	public void setNode(String node) {
		this.node = node;
		// {<.*>name}
		// {name}
		if (!node.startsWith("{")) {
			return;
		}

		if (!node.endsWith("}")) {
			throw new IllegalArgumentException(node
					+ " starts with '{' but not ends with '}'");
		}

		StringBuilder regex = new StringBuilder();
		StringBuilder name = new StringBuilder();
		int len = node.length();
		boolean existRegex = false;
		for (int idx = 1; idx < len - 1; ++idx) {
			char c = node.charAt(idx);
			if (c == '<') {
				existRegex = true;
			} else if (c == '>') {
				existRegex = false;
			} else {
				if (!existRegex) {
					name.append(c);
				} else {
					regex.append(c);
				}
			}
		}
		this.name = name.toString();

		// 取配置的正则表达式
		if (regex.length() != 0) {
			this.regex = Pattern.compile("(" + regex.toString() + ")");
		}
		// 取默认的正则表达式
		else {
			this.regex = Pattern.compile("(" + PCHAR + ")*");
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			dump(sb, "");
		} catch (IOException e) {

		}
		return sb.toString();
	}

	private String toStr() {
		return "UrlNode [node=" + node + ", name=" + name + ", regex=" + regex
				+ ", handler=" + handler + "]";
	}

	public void dump(Appendable out, String indent) throws IOException {
		out.append(indent).append(" +- ");
		out.append(toStr()).append("\n");
		for (UrlNode un : subNodes) {
			un.dump(out, indent + " |  ");
		}

	}
}

class PathInfo {
	String[] path;
	int idx;

	PathInfo(String[] path, int idx) {
		this.path = path;
		this.idx = idx;
	}

	boolean overflow() {
		return idx >= path.length;
	}

	boolean end() {
		return idx == path.length - 1;
	}

	void setEnd() {
		idx = path.length - 1;
	}

	String item() {
		return path[idx];
	}

	String remain() {
		StringBuilder sb = new StringBuilder();
		for (int x = idx; x < path.length; ++x) {
			if (sb.length() != 0) {
				sb.append("/");
			}
			sb.append(path[x]);
		}
		return sb.toString();
	}
}
