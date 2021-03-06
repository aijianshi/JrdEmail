/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 ==========================================================================
 *HISTORY
 *
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-50010 2014/10/24   zhaotianyong	  Modify the package conflict
 *CONFLICT-20001 2014/10/24   wenggangjin	  Modify the package conflict 
 *CONFLICT-20021 2014/11/3   wenggangjin	Modify the package conflict
 *BUGFIX-964325  2015/04/05    zhaotianyong       [Android5.0][Email][REG]Cannot show background color of a mail.
 ============================================================================ 
 */
package com.tct.mail.utils;

import java.util.List;
//TS: MOD by wenggangjin for CONFLICT_20002 START
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableSet;
import com.tct.fw.google.common.collect.ImmutableList;
import com.tct.fw.google.common.collect.ImmutableSet;
//TS: MOD by wenggangjin for CONFLICT_20002 END
//TS: MOD by zhaotianyong for CONFLICT_50010 START
//import org.external.html.AttributePolicy;
//import org.external.html.CssSchema;
//import org.external.html.ElementPolicy;
//import org.external.html.FilterUrlByProtocolAttributePolicy;
//import org.external.html.Handler;
//import org.external.html.HtmlPolicyBuilder;
//import org.external.html.HtmlStreamRenderer;
//import org.external.html.PolicyFactory;
import com.tct.fw.external.html.AttributePolicy;
import com.tct.fw.external.html.CssSchema;
import com.tct.fw.external.html.ElementPolicy;
import com.tct.fw.external.html.FilterUrlByProtocolAttributePolicy;
import com.tct.fw.external.html.Handler;
import com.tct.fw.external.html.HtmlPolicyBuilder;
import com.tct.fw.external.html.HtmlStreamRenderer;
import com.tct.fw.external.html.PolicyFactory;
import com.tct.mail.perf.Timer;
//TS: MOD by zhaotianyong for CONFLICT_50010 END
import android.os.Looper;
import android.util.Log;
//TS: MOD by wenggangjin for CONFLICT_20021 START
import com.tct.mail.utils.LogTag;
//TS: MOD by wenggangjin for CONFLICT_20021 END

import java.util.List;

/**
 * This sanitizer is meant to strip all scripts and any malicious HTML from untrusted emails. It
 * uses the <a href="https://www.owasp.org/index.php/OWASP_Java_HTML_Sanitizer_Project">OWASP Java
 * HTML Sanitizer Project</a> to whitelist the subset of HTML elements and attributes as well as CSS
 * properties that are considered safe. Any unmatched HTML or CSS is discarded.
 *
 * All URLS are scrubbed to ensure they match the blessed form of "http://the.url.here",
 * "https://the.url.here" or "mailto:address@server.com" and cannot resemble "javascript:badness()"
 * or comparable.
 */
public final class HtmlSanitizer {
    private static final String LOG_TAG = LogTag.getLogTag();

    /**
     * The following CSS properties do not appear in the default whitelist from OWASP, but they
     * improve the fidelity of the HTML display without unacceptable risk.
     */
    private static final CssSchema ADDITIONAL_CSS = CssSchema.withProperties(ImmutableSet.of(
            "float",
            "display"
    ));

    /**
     * Translates the body tag into the div tag
     */
    private static final ElementPolicy TRANSLATE_BODY_TO_DIV = new ElementPolicy() {
        public String apply(String elementName, List<String> attrs) {
            return "div";
        }
    };

    /**
     * Translates <div> tags surrounding quoted text into <div class="elided-text"> which allows
     * quoted text collapsing in ConversationViewFragment.
     */
    private static final ElementPolicy TRANSLATE_DIV_CLASS = new ElementPolicy() {
        public String apply(String elementName, List<String> attrs) {
            boolean showHideQuotedText = false;

            // check if the class attribute is listed
            final int classIndex = attrs.indexOf("class");
            if (classIndex >= 0) {
                // remove the class attribute and its value
                final String value = attrs.remove(classIndex + 1);
                attrs.remove(classIndex);

                // gmail and yahoo use a specific div class name to indicate quoted text
                showHideQuotedText = "gmail_quote".equals(value) || "yahoo_quoted".equals(value);
            }

            // check if the id attribute is listed
            final int idIndex = attrs.indexOf("id");
            if (idIndex >= 0) {
                // remove the id attribute and its value
                final String value = attrs.remove(idIndex + 1);
                attrs.remove(idIndex);

                // AOL uses a specifc id value to indicate quoted text
                showHideQuotedText = value.startsWith("AOLMsgPart");
            }

            // insert a class attribute with a value of "elided-text" to hide/show quoted text
            if (showHideQuotedText) {
                attrs.add("class");
                attrs.add("elided-text");
            }

            return "div";
        }
    };

    /**
     * Disallow the "mailto:" url on images so that "Show pictures" can't be used to start composing
     * a bajillion emails.
     */
    private static final AttributePolicy NO_MAILTO_URL =
            new FilterUrlByProtocolAttributePolicy(ImmutableList.of("file","content","cid", "http", "https"));
    //TS: zhaotianyong 2015-04-02 EMAIL BUGFIX_965927 MOD

    /**
     * This sanitizer policy removes these elements and the content within:
     * <ul>
     *     <li>APPLET</li>
     *     <li>FRAMESET</li>
     *     <li>OBJECT</li>
     *     <li>SCRIPT</li>
     *     <li>STYLE</li>
     *     <li>TITLE</li>
     * </ul>
     *
     * This sanitizer policy removes these elements but preserves the content within:
     * <ul>
     *     <li>BASEFONT</li>
     *     <li>FRAME</li>
     *     <li>HEAD</li>
     *     <li>IFRAME</li>
     *     <li>ISINDEX</li>
     *     <li>LINK</li>
     *     <li>META</li>
     *     <li>NOFRAMES</li>
     *     <li>PARAM</li>
     *     <li>NOSCRIPT</li>
     * </ul>
     *
     * This sanitizer policy removes these attributes from all elements:
     * <ul>
     *     <li>code</li>
     *     <li>codebase</li>
     *     <li>id</li>
     *     <li>for</li>
     *     <li>headers</li>
     *     <li>onblur</li>
     *     <li>onchange</li>
     *     <li>onclick</li>
     *     <li>ondblclick</li>
     *     <li>onfocus</li>
     *     <li>onkeydown</li>
     *     <li>onkeypress</li>
     *     <li>onkeyup</li>
     *     <li>onload</li>
     *     <li>onmousedown</li>
     *     <li>onmousemove</li>
     *     <li>onmouseout</li>
     *     <li>onmouseover</li>
     *     <li>onmouseup</li>
     *     <li>onreset</li>
     *     <li>onselect</li>
     *     <li>onsubmit</li>
     *     <li>onunload</li>
     *     <li>tabindex</li>
     * </ul>
     */
    private static final PolicyFactory POLICY_DEFINITION = new HtmlPolicyBuilder()
            .allowAttributes("dir").matching(true, "ltr", "rtl").globally()
            .allowUrlProtocols("file","content","cid", "http", "https", "mailto")//TS: zhaotianyong 2015-04-02 EMAIL BUGFIX_965927 MOD
            .allowStyling(CssSchema.union(CssSchema.DEFAULT, ADDITIONAL_CSS))
            .disallowTextIn("applet", "frameset", "object", "script", "style", "title")
            .allowElements("a").allowAttributes("coords", "href", "name", "shape").onElements("a")
            .allowElements("abbr").allowAttributes("title").onElements("abbr")
            .allowElements("acronym").allowAttributes("title").onElements("acronym")
            .allowElements("address")
            .allowElements("area")
                .allowAttributes("alt", "coords", "href", "nohref", "name", "shape")
            .onElements("area")
            .allowElements("article")
            .allowElements("aside")
            .allowElements("b")
            .allowElements("base").allowAttributes("href").onElements("base")
            .allowElements("bdi").allowAttributes("dir").onElements("bdi")
            .allowElements("bdo").allowAttributes("dir").onElements("bdo")
            .allowElements("big")
            .allowElements("blockquote").allowAttributes("cite").onElements("blockquote")
            //TS: zhaotianyong 2015-04-05 EMAIL BUGFIX_964325 MOD_S
//            .allowElements(TRANSLATE_BODY_TO_DIV, "body")
            .allowElements("body").allowAttributes("bgcolor","link","vlink","style").onElements("body")
            //TS: zhaotianyong 2015-04-05 EMAIL BUGFIX_964325 MOD_S
            .allowElements("br").allowAttributes("clear").onElements("br")
            .allowElements("button")
                .allowAttributes("autofocus", "disabled", "form", "formaction", "formenctype",
                        "formmethod", "formnovalidate", "formtarget", "name", "type", "value")
            .onElements("button")
            .allowElements("canvas").allowAttributes("width", "height").onElements("canvas")
            .allowElements("caption").allowAttributes("align").onElements("caption")
            .allowElements("center")
            .allowElements("cite")
            .allowElements("code")
            .allowElements("col")
            .allowAttributes("align", "bgcolor", "char", "charoff", "span", "valign", "width")
            .onElements("col")
            .allowElements("colgroup")
                .allowAttributes("align", "char", "charoff", "span", "valign", "width")
            .onElements("colgroup")
            .allowElements("datalist")
            .allowElements("dd")
            .allowElements("del").allowAttributes("cite", "datetime").onElements("del")
            .allowElements("details")
            .allowElements("dfn")
            .allowElements("dir").allowAttributes("compact").onElements("dir")
            .allowElements(TRANSLATE_DIV_CLASS, "div")
                .allowAttributes("align", "background", "class", "id")
            .onElements("div")
            .allowElements("dl")
            .allowElements("dt")
            .allowElements("em")
            .allowElements("fieldset")
                .allowAttributes("disabled", "form", "name")
            .onElements("fieldset")
            .allowElements("figcaption")
            .allowElements("figure")
            .allowElements("font").allowAttributes("color", "face", "size").onElements("font")
            .allowElements("footer")
            .allowElements("form")
                .allowAttributes("accept", "action", "accept-charset", "autocomplete", "enctype",
                        "method", "name", "novalidate", "target")
            .onElements("form")
            .allowElements("header")
            .allowElements("h1").allowAttributes("align").onElements("h1")
            .allowElements("h2").allowAttributes("align").onElements("h2")
            .allowElements("h3").allowAttributes("align").onElements("h3")
            .allowElements("h4").allowAttributes("align").onElements("h4")
            .allowElements("h5").allowAttributes("align").onElements("h5")
            .allowElements("h6").allowAttributes("align").onElements("h6")
            .allowElements("hr")
                .allowAttributes("align", "noshade", "size", "width")
            .onElements("hr")
            .allowElements("i")
            .allowElements("img")
                .allowAttributes("align", "alt", "border", "crossorigin", "height", "hspace",
                        "ismap", "longdesc", "usemap", "vspace", "width")
            .onElements("img")
            .allowAttributes("src").matching(NO_MAILTO_URL).onElements("img")
            .allowElements("input")
                .allowAttributes("accept", "align", "alt", "autocomplete", "autofocus", "checked",
                        "disabled", "form", "formaction", "formenctype", "formmethod",
                        "formnovalidate", "formtarget", "height", "list", "max", "maxlength", "min",
                        "multiple", "name", "pattern", "placeholder", "readonly", "required",
                        "size", "src", "step", "type", "value", "width")
            .onElements("input")
            .allowElements("ins").allowAttributes("cite", "datetime").onElements("ins")
            .allowElements("kbd")
            .allowElements("keygen")
                .allowAttributes("autofocus", "challenge", "disabled", "form", "keytype", "name")
            .onElements("keygen")
            .allowElements("label").allowAttributes("form").onElements("label")
            .allowElements("legend").allowAttributes("align").onElements("legend")
            .allowElements("li").allowAttributes("type", "value").onElements("li")
            .allowElements("main")
            .allowElements("map").allowAttributes("name").onElements("map")
            .allowElements("mark")
            .allowElements("menu").allowAttributes("label", "type").onElements("menu")
            .allowElements("menuitem")
                .allowAttributes("checked", "command", "default", "disabled", "icon", "label",
                        "type", "radiogroup")
            .onElements("menuitem")
            .allowElements("meter")
                .allowAttributes("form", "high", "low", "max", "min", "optimum", "value")
            .onElements("meter")
            .allowElements("nav")
            .allowElements("ol")
                .allowAttributes("compact", "reversed", "start", "type")
            .onElements("ol")
            .allowElements("optgroup").allowAttributes("disabled", "label").onElements("optgroup")
            .allowElements("option")
                .allowAttributes("disabled", "label", "selected", "value")
            .onElements("option")
            .allowElements("output").allowAttributes("form", "name").onElements("output")
            .allowElements("p").allowAttributes("align").onElements("p")
            .allowElements("pre").allowAttributes("width").onElements("pre")
            .allowElements("progress").allowAttributes("max", "value").onElements("progress")
            .allowElements("q").allowAttributes("cite").onElements("q")
            .allowElements("rp")
            .allowElements("rt")
            .allowElements("ruby")
            .allowElements("s")
            .allowElements("samp")
            .allowElements("section")
            .allowElements("select")
                .allowAttributes("autofocus", "disabled", "form", "multiple", "name", "required",
                        "size")
            .onElements("select")
            .allowElements("small")
            .allowElements("source").allowAttributes("media", "src", "type").onElements("source")
            .allowElements("span")
            .allowElements("strike")
            .allowElements("strong")
            .allowElements("sub")
            .allowElements("summary")
            .allowElements("sup")
            .allowElements("table")
                .allowAttributes("align", "bgcolor", "border", "cellpadding", "cellspacing",
                        "frame", "rules", "sortable", "summary", "width")
            .onElements("table")
            .allowElements("tbody")
                .allowAttributes("align", "char", "charoff", "valign").onElements("tbody")
            .allowElements("td")
                .allowAttributes("abbr", "align", "axis", "bgcolor", "char", "charoff", "colspan",
                        "height", "nowrap", "rowspan", "scope", "valign", "width")
            .onElements("td")
            .allowElements("textarea")
                .allowAttributes("autofocus", "cols", "disabled", "form", "maxlength", "name",
                        "placeholder", "readonly", "required", "rows", "wrap")
            .onElements("textarea")
            .allowElements("tfoot")
                .allowAttributes("align", "char", "charoff", "valign").onElements("tfoot")
            .allowElements("th")
                .allowAttributes("abbr", "align", "axis", "bgcolor", "char", "charoff", "colspan",
                        "height", "nowrap", "rowspan", "scope", "sorted", "valign", "width")
            .onElements("th")
            .allowElements("thead")
                .allowAttributes("align", "char", "charoff", "valign").onElements("thead")
            .allowElements("time").allowAttributes("datetime").onElements("time")
            .allowElements("tr")
                .allowAttributes("align", "bgcolor", "char", "charoff", "valign").onElements("tr")
            .allowElements("track")
                .allowAttributes("default", "kind", "label", "src", "srclang").onElements("track")
            .allowElements("tt")
            .allowElements("u")
            .allowElements("ul").allowAttributes("compact", "type").onElements("ul")
            .allowElements("var")
            .allowElements("wbr")
            .toFactory();

    private HtmlSanitizer() {}

    /**
     * Sanitizing email is treated as an expensive operation; this method should be called from
     * a background Thread.
     *
     * @param rawHtml the unsanitized, suspicious html
     * @return the sanitized form of the <code>rawHtml</code>; <code>null</code> if
     *      <code>rawHtml</code> was <code>null</code>
     */
    public static String sanitizeHtml(final String rawHtml) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("sanitizing email should not occur on the main thread");
        }

        if (rawHtml == null) {
            return null;
        }

        // create the builder into which the sanitized email will be written
        final StringBuilder htmlBuilder = new StringBuilder(rawHtml.length());

        // create the renderer that will write the sanitized HTML to the builder
        final HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
                htmlBuilder,
                Handler.PROPAGATE,
                // log errors resulting from exceptionally bizarre inputs
                new Handler<String>() {
                    public void handle(final String x) {
                        Log.wtf(LOG_TAG, "Mangled HTML content cannot be parsed: " + x);
                        throw new AssertionError(x);
                    }
                }
        );
      //TS: MOD by wenggangjin for CONFLICT_20021 START
        // create a thread-specific policy
        final com.tct.fw.external.html.HtmlSanitizer.Policy policy = POLICY_DEFINITION.apply(renderer);

        // run the html through the sanitizer
        Timer.startTiming("sanitizingHTMLEmail");
        try {
        	com.tct.fw.external.html.HtmlSanitizer.sanitize(rawHtml, policy);
        } finally {
            Timer.stopTiming("sanitizingHTMLEmail");
        }
      //TS: MOD by wenggangjin for CONFLICT_20021 END
        // return the resulting HTML from the builder
        return htmlBuilder.toString();
    }
}
