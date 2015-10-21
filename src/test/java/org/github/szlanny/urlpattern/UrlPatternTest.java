package org.github.szlanny.urlpattern;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

public class UrlPatternTest {

	@Test
	public void test() {
		UrlPattern up = new UrlPattern();
		up.addUrlPattern("/org/github/szlanny/abcd", "1");
		up.addUrlPattern("/org/github/{name}/abcd", "2");
		up.addUrlPattern("/org/github/szlanny1/abcd", "3");
		
		
		System.out.println(up);
		UrlMatcher um = up.parser("/org/github/szlanny1/abcd");
		Assert.assertEquals("3", um.handler);
		
		um = up.parser("/org/github/xtf/abcd");
		Assert.assertEquals("2", um.handler);
		Assert.assertEquals("xtf", um.group("name"));
	}
	
	@Test
	public void test_2() {
		UrlPattern up = new UrlPattern();
		up.addUrlPattern("/org/github/szlanny/abcd", "1");
		up.addUrlPattern("/org/github/{name}/abcd", "2");
		up.addUrlPattern("/org/github/{name}/22", "22");
		up.addUrlPattern("/org/github/{name}/33", "33");
		up.addUrlPattern("/org/github/szlanny1/abcd", "3");
		
		
		System.out.println(up);
		UrlMatcher um = up.parser("/org/github/szlanny1/abcd");
		Assert.assertEquals("3", um.handler);
		
		um = up.parser("/org/github/xtf/abcd");
		Assert.assertEquals("2", um.handler);
		Assert.assertEquals("xtf", um.group("name"));
		
		um = up.parser("/org/github/xtf/22");
		Assert.assertEquals("22", um.handler);
		Assert.assertEquals("xtf", um.group("name"));
		
		um = up.parser("/org/github/xtf/33");
		Assert.assertEquals("33", um.handler);
		Assert.assertEquals("xtf", um.group("name"));
	}
	
	@Test
	public void test_3() {
		UrlPattern up = new UrlPattern();
		up.addUrlPattern("/org/github/szlanny/abcd", "1");
		up.addUrlPattern("/org/github/{name}/abcd", "2");
		up.addUrlPattern("/org/github/{name}/22", "22");
		up.addUrlPattern("/org/github/{name}/33", "33");
		up.addUrlPattern("/org/github/szlanny1/abcd", "3");
		up.addUrlPattern("/org/github1/{<.*>name1}", "44");
		
		
		System.out.println(up);
		UrlMatcher um = up.parser("/org/github/szlanny1/abcd");
		Assert.assertEquals("3", um.handler);
		
		um = up.parser("/org/github1/xtf/33");
		Assert.assertEquals("44", um.handler);
		Assert.assertEquals("xtf/33", um.group("name1"));
	}
	
	@Test
	public void test_4() {
		UrlPattern up = new UrlPattern();
		up.addUrlPattern("/org/github/szlanny/abcd", "1");
		up.addUrlPattern("/org/github/{name}/{name1}", "2");
		up.addUrlPattern("/org/github/{name}/{name1}/abcd", "abcd");
		up.addUrlPattern("/org/github/{name}/22", "22");
		up.addUrlPattern("/org/github/{name}/33", "33");
		up.addUrlPattern("/org/github/szlanny1/abcd", "3");
		
		
		System.out.println(up);
		UrlMatcher um = up.parser("/org/github/szlanny1/abcd");
		Assert.assertEquals("3", um.handler);
		
		um = up.parser("/org/github/szlanny2/44");
		Assert.assertEquals("2", um.handler);
		Assert.assertEquals("szlanny2", um.group("name"));
		Assert.assertEquals("44", um.group("name1"));
		
		um = up.parser("/org/github/szlanny2/44/abcd");
		Assert.assertEquals("abcd", um.handler);
		Assert.assertEquals("szlanny2", um.group("name"));
		Assert.assertEquals("44", um.group("name1"));
	}

}
