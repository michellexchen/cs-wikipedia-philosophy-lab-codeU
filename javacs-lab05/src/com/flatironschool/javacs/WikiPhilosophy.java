package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		String start = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String goal = "https://en.wikipedia.org/wiki/Philosophy";
		boolean found = false;
		boolean nextLink = false;
		List visited = new ArrayList<String>();
		
		while (!found) {
			Elements paragraphs = wf.fetchWikipedia(start);
			int parens = 0;
			for (Element paragraph: paragraphs) {
				Iterable<Node> iter = new WikiNodeIterable(paragraph);
				for (Node node: iter) {
					if (node instanceof TextNode) { //parenthesis
						for (int x = 0; x<node.toString().length(); x++) {
							if (Character.toString(node.toString().charAt(x)).equals("(")) {
								parens++;
							} else if (Character.toString(node.toString().charAt(x)).equals(")")){
								parens--;
							}
						}
					} else {
						Element url = (Element) node;
						if (url.tagName().equals("a")) { //found link
							String next = url.attr("href");
							if (Character.isUpperCase(url.text().charAt(0)) || parens !=0){ //capital letter, parens
								continue;
							}
							if (!next.contains("/wiki") || start.substring(start.indexOf("/wiki")).equals(next)) { //external link, current page
								continue;
							}
							for (Element parent: url.parents()) { //italics
								if (parent.tagName().equals("em") || parent.tagName().equals("i")) {
									continue;
								}
							}
							start = "https://en.wikipedia.org"+next;
							if (visited.contains(start)) {
								continue;
							}
							System.out.println("START IS: " + start);
							visited.add(start);
							if (start.equals(goal)) {
								System.out.println("congrats! Philosophy page found.");
								visited.add(start);
								System.out.println(visited.toString());
								return;
							}
							nextLink = true;
							break;
						}												
					}
				}
				if (nextLink) {
					break;
				}
			}
		}
		System.out.println("search failed.");
		System.out.println(visited.toString());
	}
}
