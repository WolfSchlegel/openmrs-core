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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.util.Format.FORMAT_TYPE;
import org.openmrs.util.OpenmrsConstants;

/**
 * Tests methods on the {@link WebUtil} class.
 */
public class WebUtilTest extends BaseContextSensitiveTest {
	
	@Nested
	class ContextPathTests {
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
	}

	@Nested
	class LocaleTests {
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
	}

	@Nested
	class DateFormatTests {
		private Date testDate;
		private AdministrationService adminService;
		
		@BeforeEach
		public void setup() {
			adminService = Context.getAdministrationService();
			
			// Set up a fixed test date: 2024-03-15 14:30:45
			Calendar cal = Calendar.getInstance();
			cal.set(2024, Calendar.MARCH, 15, 14, 30, 45);
			cal.set(Calendar.MILLISECOND, 0);
			testDate = cal.getTime();
		}
		
		/**
		 * @see WebUtil#formatDate(Date)
		 */
		@Test
		public void formatDate_shouldUseDefaultLocaleAndDateFormat() {
			// Set default date format
			adminService.saveGlobalProperty(new GlobalProperty(
				OpenmrsConstants.GP_SEARCH_DATE_DISPLAY_FORMAT, "dd/MM/yyyy"));
			
			String formatted = WebUtil.formatDate(testDate);
			assertEquals("15/03/2024", formatted);
		}
		
		/**
		 * @see WebUtil#formatDate(Date)
		 */
		@Test
		public void formatDate_shouldReturnEmptyStringForNullDate() {
			assertEquals("", WebUtil.formatDate(null));
		}
		
		/**
		 * @see WebUtil#formatDate(Date, Locale, FORMAT_TYPE)
		 */
		@Test
		public void formatDate_shouldFormatDateWithCustomLocaleAndType() {
			// Test DATE format
			assertTrue(WebUtil.formatDate(testDate, Locale.FRENCH, FORMAT_TYPE.DATE).contains("15 mars 2024"));
			// Test TIME format (should contain hour and minute)
			String time = WebUtil.formatDate(testDate, Locale.ENGLISH, FORMAT_TYPE.TIME);
			assertTrue(time.contains("14:30") || time.contains("2:30"));
			// Test TIMESTAMP format (should contain date and time)
			String timestamp = WebUtil.formatDate(testDate, Locale.FRENCH, FORMAT_TYPE.TIMESTAMP);
			assertTrue(timestamp.contains("15 mars 2024") && (timestamp.contains("14:30") || timestamp.contains("2:30")));
		}
		
		/**
		 * @see WebUtil#formatDate(Date, Locale, FORMAT_TYPE)
		 */
		@Test
		public void formatDate_shouldUseCustomDateFormatFromGlobalProperty() {
			// Set custom date format
			adminService.saveGlobalProperty(new GlobalProperty(
				OpenmrsConstants.GP_SEARCH_DATE_DISPLAY_FORMAT, "yyyy-MM-dd HH:mm"));
			
			String formatted = WebUtil.formatDate(testDate, Locale.ENGLISH, FORMAT_TYPE.TIMESTAMP);
			assertEquals("2024-03-15 14:30", formatted);
		}
		
		/**
		 * @see WebUtil#formatDate(Date, Locale, FORMAT_TYPE)
		 */
		@Test
		public void formatDate_shouldHandleInvalidDateFormatGracefully() {
			// Set invalid date format (using invalid pattern 'X' which is not supported)
			adminService.saveGlobalProperty(new GlobalProperty(
				OpenmrsConstants.GP_SEARCH_DATE_DISPLAY_FORMAT, "yyyy-MM-dd X"));
			// Should fall back to default format (should contain year, month, and day)
			String formatted = WebUtil.formatDate(testDate, Locale.ENGLISH, FORMAT_TYPE.DATE);
			assertTrue(formatted.contains("2024") && (formatted.contains("Mar") || formatted.contains("03") || formatted.contains("15")));
		}
		
		/**
		 * @see WebUtil#formatDate(Date, Locale, FORMAT_TYPE)
		 */
		@Test
		public void formatDate_shouldReturnEmptyStringForNullDateWithLocaleAndType() {
			assertEquals("", WebUtil.formatDate(null, Locale.ENGLISH, FORMAT_TYPE.DATE));
		}
	}

	@Nested
	class EncodingTests {
		/**
		 * @see WebUtil#escapeHTML(String)
		 */
		@Test
		void escapeHTML_shouldEscapeHtmlSpecialCharacters() {
			assertEquals("&lt;div&gt;Hello &amp; &#34;World&#34;!&lt;/div&gt;", WebUtil.escapeHTML("<div>Hello & \"World\"!</div>"));
		}

		/**
		 * @see WebUtil#encodeForCDATA(String)
		 */
		@Test
		public void encodeForCDATA_shouldEscapeCDATASpecialCharacters() {
			assertEquals("]]]]><![CDATA[>", WebUtil.encodeForCDATA("]]>"));
		}

		/**
		 * @see WebUtil#encodeForCssString(String)
		 */
		@Test
		void encodeForCssString_shouldEscapeCssStringSpecialCharacters() {
			assertEquals("\\22test\\22", WebUtil.encodeForCssString("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForCssUrl(String)
		 */
		@Test
		public void encodeForCssUrl_shouldEscapeCssUrlSpecialCharacters() {
			assertEquals("\\22test\\22", WebUtil.encodeForCssUrl("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForHtmlAttribute(String)
		 */
		@Test
		public void encodeForHtmlAttribute_shouldEscapeHtmlAttributeSpecialCharacters() {
			assertEquals("&#34;test&#34;", WebUtil.encodeForHtmlAttribute("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForHtmlContent(String)
		 */
		@Test
		public void encodeForHtmlContent_shouldEscapeHtmlContentSpecialCharacters() {
			assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", WebUtil.encodeForHtmlContent("<script>alert('xss')</script>"));
		}

		/**
		 * @see WebUtil#encodeForHtmlUnquotedAttribute(String)
		 */
		@Test
		public void encodeForHtmlUnquotedAttribute_shouldEscapeHtmlUnquotedAttributeSpecialCharacters() {
			assertEquals("&#34;", WebUtil.encodeForHtmlUnquotedAttribute("\""));
		}

		/**
		 * @see WebUtil#encodeForJava(String)
		 */
		@Test
		void encodeForJava_shouldEscapeJavaSpecialCharacters() {
			assertEquals("\\\"test\\\"", WebUtil.encodeForJava("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForJavaScript(String)
		 */
		@Test
		void encodeForJavaScript_shouldEscapeJavaScriptSpecialCharacters() {
			assertEquals("\\x22test\\x22", WebUtil.encodeForJavaScript("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForJavaScriptAttribute(String)
		 */
		@Test
		void encodeForJavaScriptAttribute_shouldEscapeJavaScriptAttributeSpecialCharacters() {
			assertEquals("<test>", WebUtil.encodeForJavaScriptAttribute("<test>"));
		}

		/**
		 * @see WebUtil#encodeForJavaScriptBlock(String)
		 */
		@Test
		void encodeForJavaScriptBlock_shouldEscapeJavaScriptBlockSpecialCharacters() {
			assertEquals("<test>", WebUtil.encodeForJavaScriptBlock("<test>"));
		}

		/**
		 * @see WebUtil#encodeForJavaScriptSource(String)
		 */
		@Test
		void encodeForJavaScriptSource_shouldEscapeJavaScriptSourceSpecialCharacters() {
			// OWASP Encoder returns </> for JavaScript source encoding
			assertEquals("</>", WebUtil.encodeForJavaScriptSource("</>"));
		}

		/**
		 * @see WebUtil#encodeForUri(String)
		 */
		@Test
		public void encodeForUri_shouldEncodeUriSpecialCharacters() {
			assertEquals("hello%20world", WebUtil.encodeForUri("hello world"));
		}

		/**
		 * @see WebUtil#encodeForUriComponent(String)
		 */
		@Test
		public void encodeForUriComponent_shouldEncodeUriComponentSpecialCharacters() {
			assertEquals("hello%20world", WebUtil.encodeForUriComponent("hello world"));
		}

		/**
		 * @see WebUtil#encodeForXml(String)
		 */
		@Test
		public void encodeForXml_shouldEncodeXmlSpecialCharacters() {
			assertEquals("&lt;tag&gt;&#34;&amp;&#39;&lt;/tag&gt;", WebUtil.encodeForXml("<tag>\"&'\u003c/tag>"));
		}

		/**
		 * @see WebUtil#encodeForXmlAttribute(String)
		 */
		@Test
		public void encodeForXmlAttribute_shouldEncodeXmlAttributeSpecialCharacters() {
			assertEquals("&#34;test&#34;", WebUtil.encodeForXmlAttribute("\"test\""));
		}

		/**
		 * @see WebUtil#encodeForXmlComment(String)
		 */
		@Test
		public void encodeForXmlComment_shouldEncodeXmlCommentSpecialCharacters() {
			assertEquals("->", WebUtil.encodeForXmlComment("->"));
		}

		/**
		 * @see WebUtil#encodeForXmlContent(String)
		 */
		@Test
		public void encodeForXmlContent_shouldEncodeXmlContentSpecialCharacters() {
			assertEquals("&lt;tag&gt;\"&amp;'&lt;/tag&gt;", WebUtil.encodeForXmlContent("<tag>\"&'\u003c/tag>"));
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
	}

	@Nested
	class UtilityTests {
		/**
		 * @see WebUtil#stripFilename(String)
		 */
		@Test
		public void stripFilename_shouldStripPathFromFilename() {
			assertEquals("file.doc", WebUtil.stripFilename("/home/user/file.doc"));
			assertEquals("file.doc", WebUtil.stripFilename("C:\\documents\\file.doc"));
			assertEquals("file.doc", WebUtil.stripFilename("file.doc"));
			assertEquals("file with spaces.txt", WebUtil.stripFilename("/tmp/file with spaces.txt"));
		}
	}
}
