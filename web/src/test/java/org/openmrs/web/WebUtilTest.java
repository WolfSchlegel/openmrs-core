/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.openmrs.BaseOpenmrsObject;

/**
 * Tests methods on the {@link WebUtil} class.
 */
public class WebUtilTest {

	/**
	 * @see org.openmrs.web.WebUtil#getContextPath()
	 */
	@Test
	public void getContextPath_shouldReturnEmptyStringWhenWebAppNameIsNull() {
		WebConstants.WEBAPP_NAME = null;
		assertEquals("", WebUtil.getContextPath());
	}
	
	/**
	 * @see org.openmrs.web.WebUtil#getContextPath()
	 */
	@Test
	public void getContextPath_shouldReturnEmptyStringWhenWebAppNameIsEmptyString() {
		WebConstants.WEBAPP_NAME = "";
		assertEquals("", WebUtil.getContextPath());
	}
	
	/**
	 * @see org.openmrs.web.WebUtil#getContextPath()
	 */
	@Test
	public void getContextPath_shouldReturnValueSpecifiedInWebAppName() {
		WebConstants.WEBAPP_NAME = "Value";
		assertEquals("/Value", WebUtil.getContextPath());
	}
	
	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldIgnoreLeadingSpaces() {
		assertEquals(Locale.ITALIAN, WebUtil.normalizeLocale(" it"));
	}
	
	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldAcceptLanguageOnlyLocales() {
		assertEquals(Locale.FRENCH, WebUtil.normalizeLocale("fr"));
	}
	
	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotAcceptInvalidLocales() {
		assertNull(WebUtil.normalizeLocale("ptrg"));
		assertNull(WebUtil.normalizeLocale("usaa"));
	}
	
	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithEmptyStrings() {
		assertNull(WebUtil.normalizeLocale(""));
	}
	
	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithWhitespaceOnly() {
		assertNull(WebUtil.normalizeLocale("      "));
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithTab() throws UnsupportedEncodingException {
		String s = new String(new byte[]{0x9}, "ASCII");
		assertNull(WebUtil.normalizeLocale(s));
	}

	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithUnicode() {
		assertNull(WebUtil.normalizeLocale("Ši"));
	}

	/**
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithSingleChar() {
		assertNull(WebUtil.normalizeLocale("s"));
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @see WebUtil#normalizeLocale(String)
	 */
	@Test
	public void normalizeLocale_shouldNotFailWithUnderline() throws UnsupportedEncodingException {
		String s = new String(new byte[]{0x5f}, "ASCII");
		assertNull(WebUtil.normalizeLocale(s));
	}

	/**
	 * @see WebUtil#sanitizeLocales(String)
	 */
	@Test
	public void sanitizeLocales_shouldSkipOverInvalidLocales() {
		assertEquals("fr_RW, it, en", WebUtil.sanitizeLocales("és, qqqq, fr_RW, it, enñ"));
	}
	
	/**
	 * @see WebUtil#sanitizeLocales(String)
	 */
	@Test
	public void sanitizeLocales_shouldNotFailWithEmptyString() {
		assertNull(null, WebUtil.sanitizeLocales(""));
	}
	
	/**
	 * Utility method to check if a list contains a BaseOpenmrsObject using the id
	 * @param list
	 * @param id
	 * @return true if list contains object with the id else false
	 */
	public static boolean containsId(Collection<? extends BaseOpenmrsObject> list, Integer id) {
		for (BaseOpenmrsObject baseOpenmrsObject : list) {
			if (baseOpenmrsObject.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see WebUtil#escapeHTML(String)
	 */
	@Test
	public void escapeHTML_shouldEscapeHtmlSpecialCharacters() {
		assertEquals("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;", 
			WebUtil.escapeHTML("<script>alert('xss')</script>"));
		assertEquals("&amp;lt;script&amp;gt;", 
			WebUtil.escapeHTML("&lt;script&gt;"));
		assertEquals("", WebUtil.escapeHTML(null));
		assertEquals("", WebUtil.escapeHTML(""));
	}

	/**
	 * @see WebUtil#encodeForCDATA(String)
	 */
	@Test
	public void encodeForCDATA_shouldEscapeCDATASpecialCharacters() {
		assertEquals("]]&gt;", WebUtil.encodeForCDATA("]]>"));
		assertEquals("", WebUtil.encodeForCDATA(null));
		assertEquals("", WebUtil.encodeForCDATA(""));
		assertEquals("normal text", WebUtil.encodeForCDATA("normal text"));
	}

	/**
	 * @see WebUtil#encodeForCssString(String)
	 */
	@Test
	public void encodeForCssString_shouldEscapeCssStringSpecialCharacters() {
		assertEquals("\\22", WebUtil.encodeForCssString("\""));
		assertEquals("\\27", WebUtil.encodeForCssString("'"));
		assertEquals("\\0", WebUtil.encodeForCssString("\0"));
		assertEquals("", WebUtil.encodeForCssString(null));
		assertEquals("", WebUtil.encodeForCssString(""));
		assertEquals("normal text", WebUtil.encodeForCssString("normal text"));
	}

	/**
	 * @see WebUtil#encodeForCssUrl(String)
	 */
	@Test
	public void encodeForCssUrl_shouldEscapeCssUrlSpecialCharacters() {
		assertEquals("%22", WebUtil.encodeForCssUrl("\""));
		assertEquals("%27", WebUtil.encodeForCssUrl("'"));
		assertEquals("%00", WebUtil.encodeForCssUrl("\0"));
		assertEquals("", WebUtil.encodeForCssUrl(null));
		assertEquals("", WebUtil.encodeForCssUrl(""));
		assertEquals("normal%20text", WebUtil.encodeForCssUrl("normal text"));
	}

	/**
	 * @see WebUtil#encodeForHtmlAttribute(String)
	 */
	@Test
	public void encodeForHtmlAttribute_shouldEscapeHtmlAttributeSpecialCharacters() {
		assertEquals("&quot;", WebUtil.encodeForHtmlAttribute("\""));
		assertEquals("&amp;", WebUtil.encodeForHtmlAttribute("&"));
		assertEquals("&lt;", WebUtil.encodeForHtmlAttribute("<"));
		assertEquals("", WebUtil.encodeForHtmlAttribute(null));
		assertEquals("", WebUtil.encodeForHtmlAttribute(""));
		assertEquals("normal text", WebUtil.encodeForHtmlAttribute("normal text"));
	}

	/**
	 * @see WebUtil#encodeForHtmlContent(String)
	 */
	@Test
	public void encodeForHtmlContent_shouldEscapeHtmlContentSpecialCharacters() {
		assertEquals("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;", 
			WebUtil.encodeForHtmlContent("<script>alert('xss')</script>"));
		assertEquals("&amp;lt;script&amp;gt;", 
			WebUtil.encodeForHtmlContent("&lt;script&gt;"));
		assertEquals("", WebUtil.encodeForHtmlContent(null));
		assertEquals("", WebUtil.encodeForHtmlContent(""));
	}

	/**
	 * @see WebUtil#encodeForHtmlUnquotedAttribute(String)
	 */
	@Test
	public void encodeForHtmlUnquotedAttribute_shouldEscapeHtmlUnquotedAttributeSpecialCharacters() {
		assertEquals("&#x22;", WebUtil.encodeForHtmlUnquotedAttribute("\""));
		assertEquals("&#x27;", WebUtil.encodeForHtmlUnquotedAttribute("'"));
		assertEquals("&#x20;", WebUtil.encodeForHtmlUnquotedAttribute(" "));
		assertEquals("", WebUtil.encodeForHtmlUnquotedAttribute(null));
		assertEquals("", WebUtil.encodeForHtmlUnquotedAttribute(""));
		assertEquals("normal&#x20;text", WebUtil.encodeForHtmlUnquotedAttribute("normal text"));
	}

	/**
	 * @see WebUtil#encodeForJava(String)
	 */
	@Test
	public void encodeForJava_shouldEscapeJavaSpecialCharacters() {
		assertEquals("\\\"", WebUtil.encodeForJava("\""));
		assertEquals("\\n", WebUtil.encodeForJava("\n"));
		assertEquals("\\t", WebUtil.encodeForJava("\t"));
		assertEquals("", WebUtil.encodeForJava(null));
		assertEquals("", WebUtil.encodeForJava(""));
		assertEquals("normal text", WebUtil.encodeForJava("normal text"));
	}

	/**
	 * @see WebUtil#encodeForJavaScript(String)
	 */
	@Test
	public void encodeForJavaScript_shouldEscapeJavaScriptSpecialCharacters() {
		assertEquals("\\\"", WebUtil.encodeForJavaScript("\""));
		assertEquals("\\'", WebUtil.encodeForJavaScript("'"));
		assertEquals("\\\\", WebUtil.encodeForJavaScript("\\"));
		assertEquals("", WebUtil.encodeForJavaScript(null));
		assertEquals("", WebUtil.encodeForJavaScript(""));
		assertEquals("normal text", WebUtil.encodeForJavaScript("normal text"));
	}

	/**
	 * @see WebUtil#encodeForJavaScriptAttribute(String)
	 */
	@Test
	public void encodeForJavaScriptAttribute_shouldEscapeJavaScriptAttributeSpecialCharacters() {
		assertEquals("\\x22", WebUtil.encodeForJavaScriptAttribute("\""));
		assertEquals("\\x27", WebUtil.encodeForJavaScriptAttribute("'"));
		assertEquals("\\x3c", WebUtil.encodeForJavaScriptAttribute("<"));
		assertEquals("", WebUtil.encodeForJavaScriptAttribute(null));
		assertEquals("", WebUtil.encodeForJavaScriptAttribute(""));
		assertEquals("normal text", WebUtil.encodeForJavaScriptAttribute("normal text"));
	}

	/**
	 * @see WebUtil#encodeForJavaScriptBlock(String)
	 */
	@Test
	public void encodeForJavaScriptBlock_shouldEscapeJavaScriptBlockSpecialCharacters() {
		assertEquals("\\\"", WebUtil.encodeForJavaScriptBlock("\""));
		assertEquals("\\'", WebUtil.encodeForJavaScriptBlock("'"));
		assertEquals("\\/", WebUtil.encodeForJavaScriptBlock("/"));
		assertEquals("", WebUtil.encodeForJavaScriptBlock(null));
		assertEquals("", WebUtil.encodeForJavaScriptBlock(""));
		assertEquals("normal text", WebUtil.encodeForJavaScriptBlock("normal text"));
	}

	/**
	 * @see WebUtil#encodeForJavaScriptSource(String)
	 */
	@Test
	public void encodeForJavaScriptSource_shouldEscapeJavaScriptSourceSpecialCharacters() {
		assertEquals("\\\"", WebUtil.encodeForJavaScriptSource("\""));
		assertEquals("\\'", WebUtil.encodeForJavaScriptSource("'"));
		assertEquals("\\/", WebUtil.encodeForJavaScriptSource("/"));
		assertEquals("", WebUtil.encodeForJavaScriptSource(null));
		assertEquals("", WebUtil.encodeForJavaScriptSource(""));
		assertEquals("normal text", WebUtil.encodeForJavaScriptSource("normal text"));
	}

	/**
	 * @see WebUtil#encodeForUri(String)
	 */
	@Test
	public void encodeForUri_shouldEncodeUriSpecialCharacters() {
		assertEquals("%20", WebUtil.encodeForUri(" "));
		assertEquals("%3C", WebUtil.encodeForUri("<"));
		assertEquals("%3E", WebUtil.encodeForUri(">"));
		assertEquals("", WebUtil.encodeForUri(null));
		assertEquals("", WebUtil.encodeForUri(""));
		assertEquals("normal%20text", WebUtil.encodeForUri("normal text"));
	}

	/**
	 * @see WebUtil#encodeForUriComponent(String)
	 */
	@Test
	public void encodeForUriComponent_shouldEncodeUriComponentSpecialCharacters() {
		assertEquals("%20", WebUtil.encodeForUriComponent(" "));
		assertEquals("%2F", WebUtil.encodeForUriComponent("/"));
		assertEquals("%3F", WebUtil.encodeForUriComponent("?"));
		assertEquals("", WebUtil.encodeForUriComponent(null));
		assertEquals("", WebUtil.encodeForUriComponent(""));
		assertEquals("normal%20text", WebUtil.encodeForUriComponent("normal text"));
	}

	/**
	 * @see WebUtil#encodeForXml(String)
	 */
	@Test
	public void encodeForXml_shouldEncodeXmlSpecialCharacters() {
		assertEquals("&lt;", WebUtil.encodeForXml("<"));
		assertEquals("&gt;", WebUtil.encodeForXml(">"));
		assertEquals("&amp;", WebUtil.encodeForXml("&"));
		assertEquals("", WebUtil.encodeForXml(null));
		assertEquals("", WebUtil.encodeForXml(""));
		assertEquals("normal text", WebUtil.encodeForXml("normal text"));
	}

	/**
	 * @see WebUtil#encodeForXmlAttribute(String)
	 */
	@Test
	public void encodeForXmlAttribute_shouldEncodeXmlAttributeSpecialCharacters() {
		assertEquals("&quot;", WebUtil.encodeForXmlAttribute("\""));
		assertEquals("&amp;", WebUtil.encodeForXmlAttribute("&"));
		assertEquals("&lt;", WebUtil.encodeForXmlAttribute("<"));
		assertEquals("", WebUtil.encodeForXmlAttribute(null));
		assertEquals("", WebUtil.encodeForXmlAttribute(""));
		assertEquals("normal text", WebUtil.encodeForXmlAttribute("normal text"));
	}

	/**
	 * @see WebUtil#encodeForXmlComment(String)
	 */
	@Test
	public void encodeForXmlComment_shouldEncodeXmlCommentSpecialCharacters() {
		assertEquals("- -", WebUtil.encodeForXmlComment("--"));
		assertEquals("", WebUtil.encodeForXmlComment(null));
		assertEquals("", WebUtil.encodeForXmlComment(""));
		assertEquals("normal text", WebUtil.encodeForXmlComment("normal text"));
	}

	/**
	 * @see WebUtil#encodeForXmlContent(String)
	 */
	@Test
	public void encodeForXmlContent_shouldEncodeXmlContentSpecialCharacters() {
		assertEquals("&lt;", WebUtil.encodeForXmlContent("<"));
		assertEquals("&gt;", WebUtil.encodeForXmlContent(">"));
		assertEquals("&amp;", WebUtil.encodeForXmlContent("&"));
		assertEquals("", WebUtil.encodeForXmlContent(null));
		assertEquals("", WebUtil.encodeForXmlContent(""));
		assertEquals("normal text", WebUtil.encodeForXmlContent("normal text"));
	}

	/**
	 * @see WebUtil#escapeQuotes(String)
	 */
	@Test
	public void escapeQuotes_shouldEscapeQuotes() {
		assertEquals("\\\"", WebUtil.escapeQuotes("\""));
		assertEquals("", WebUtil.escapeQuotes(null));
		assertEquals("", WebUtil.escapeQuotes(""));
		assertEquals("normal text", WebUtil.escapeQuotes("normal text"));
		assertEquals("text with \\\"quotes\\\"", WebUtil.escapeQuotes("text with \"quotes\""));
	}

	/**
	 * @see WebUtil#escapeNewlines(String)
	 */
	@Test
	public void escapeNewlines_shouldEscapeNewlines() {
		assertEquals("\\n", WebUtil.escapeNewlines("\n"));
		assertEquals("", WebUtil.escapeNewlines(null));
		assertEquals("", WebUtil.escapeNewlines(""));
		assertEquals("normal text", WebUtil.escapeNewlines("normal text"));
		assertEquals("text with\\nnewlines", WebUtil.escapeNewlines("text with\nnewlines"));
	}

	/**
	 * @see WebUtil#escapeQuotesAndNewlines(String)
	 */
	@Test
	public void escapeQuotesAndNewlines_shouldEscapeQuotesAndNewlines() {
		assertEquals("\\\"", WebUtil.escapeQuotesAndNewlines("\""));
		assertEquals("\\n", WebUtil.escapeQuotesAndNewlines("\n"));
		assertEquals("\\r\\n", WebUtil.escapeQuotesAndNewlines("\r\n"));
		assertEquals("", WebUtil.escapeQuotesAndNewlines(null));
		assertEquals("", WebUtil.escapeQuotesAndNewlines(""));
		assertEquals("normal text", WebUtil.escapeQuotesAndNewlines("normal text"));
		assertEquals("text with \\\"quotes\\\" and\\nnewlines", 
			WebUtil.escapeQuotesAndNewlines("text with \"quotes\" and\nnewlines"));
	}

	/**
	 * @see WebUtil#stripFilename(String)
	 */
	@Test
	public void stripFilename_shouldStripPathFromFilename() {
		assertEquals("file.doc", WebUtil.stripFilename("C:\\documents\\file.doc"));
		assertEquals("file.doc", WebUtil.stripFilename("/home/file.doc"));
		assertEquals("file.doc", WebUtil.stripFilename("file.doc"));
		assertEquals("", WebUtil.stripFilename(null));
		assertEquals("", WebUtil.stripFilename(""));
		assertEquals("file with spaces.doc", WebUtil.stripFilename("/path/to/file with spaces.doc"));
	}
}
