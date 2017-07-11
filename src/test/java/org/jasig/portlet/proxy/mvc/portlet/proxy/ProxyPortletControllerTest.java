package org.jasig.portlet.proxy.mvc.portlet.proxy;

import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.mvc.portlet.json.JsonPortletController;
import org.jasig.portlet.proxy.mvc.portlet.proxy.ProxyPortletController;
import org.jasig.portlet.proxy.service.ClasspathResourceContentService;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.proxy.document.HeaderFooterFilter;
import org.jasig.portlet.proxy.service.proxy.document.IDocumentFilter;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.portlet.ModelAndView;

import javax.portlet.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author Richard Good, chodski@gmail.com
 */
public class ProxyPortletControllerTest {

    ProxyPortletController controller;

    IContentService contentService;

    URLRewritingFilter filter;

    private static final String testScript = "<script language='JavaScript'>\n" +
            "function helloWorld()\n" +
            "{ alert (\"Hello from WebProxyPortlet!\"); }\n" +
            "</script>";

    @Mock
    RenderRequest request;

    @Mock
    PortletSession session;

    @Mock
    MockRenderResponse response;

    @Mock
    PortletPreferences preferences;

    @Mock
    IViewSelector viewSelector;

    @Mock
    ApplicationContext applicationContext;

    @Mock ByteArrayOutputStream out;

    @Mock
    ConcurrentMap<String, String> rewrittenUrls;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        controller = new ProxyPortletController();
        controller.setApplicationContext(applicationContext);
        response = new MockRenderResponse();
        filter = spy(new URLRewritingFilter());
        contentService = new ClasspathResourceContentService();
        String[] filterArray = {"headerFooterFilter","urlRewritingFilter"};

        final Map<String, Set<String>> urlAttributes = new HashMap<String, Set<String>>();
        urlAttributes.put("a", Collections.singleton("href"));
        urlAttributes.put("img", Collections.singleton("src"));
        urlAttributes.put("script",Collections.singleton("src"));
        urlAttributes.put("form", Collections.singleton("action"));
        filter.setActionElements(urlAttributes);

        filter.setResourceElements(new HashMap<String, Set<String>>());

        when(request.getPreferences()).thenReturn(preferences);
        when(viewSelector.isMobile(request)).thenReturn(false);
        when(request.getPortletSession()).thenReturn(session);
        when(session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY)).thenReturn(rewrittenUrls);
        when(request.getPreferences()).thenReturn(preferences);
        when(preferences.getValues(URLRewritingFilter.WHITELIST_REGEXES_KEY, new String[]{})).thenReturn(new String[]{});
        when(preferences.getValue(GenericContentRequestImpl.CONTENT_LOCATION_PREFERENCE, null)).thenReturn("script-sample.html");
        when(preferences.getValue(ProxyPortletController.CONTENT_SERVICE_KEY, null)).thenReturn("classpath");
        when(applicationContext.getBean("classpath", IContentService.class)).thenReturn(contentService);
        when(preferences.getValues(ProxyPortletController.FILTER_LIST_KEY, new String[]{})).thenReturn(filterArray);
        when(preferences.getValue(ProxyPortletController.PREF_CHARACTER_ENCODING, ProxyPortletController.CHARACTER_ENCODING_DEFAULT)).thenReturn("UTF8");
        when(applicationContext.getBean("headerFooterFilter", IDocumentFilter.class)).thenReturn(new HeaderFooterFilter());
        when(applicationContext.getBean("urlRewritingFilter", IDocumentFilter.class)).thenReturn(filter);
        when(preferences.getValue(HeaderFooterFilter.HEADER_KEY, null)).thenReturn(testScript);

    }

    @Test
    public void test() throws Exception {

        controller.showContent(request, response);

        final String expectedHtml="<scriptlanguage=\"JavaScript\">functionhelloWorld(){alert(\"HellofromWebProxyPortlet!\");}</script><html><head><title>test</title></head><body><scriptlanguage=\"JavaScript\">functionhelloWorld(){alert(\\\"HellofromWebProxyPortlet!\\\");}</script><div>Blahblah</div></body></html>";

        final String output = response.getContentAsString().replace(" ", "").replace("\n", "");

        assertEquals(expectedHtml,output);

    }

}
