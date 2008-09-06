package hudson.pages;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import hudson.model.PageDecorator;

import net.sf.json.JSONObject;

import org.jvnet.hudson.test.HudsonTestCase;
import org.kohsuke.stapler.StaplerRequest;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SystemConfigurationTestCase extends HudsonTestCase {

    private PageDecoratorImpl pageDecoratorImpl;

    protected void tearDown() throws Exception {
        if (pageDecoratorImpl != null) {
            PageDecorator.ALL.remove(pageDecoratorImpl);
        }
        super.tearDown();
    }
    
    /**
     * Asserts that bug#2289 is fixed.
     */
    public void testPageDecoratorIsListedInPage() throws Exception {
        pageDecoratorImpl = new PageDecoratorImpl();
        PageDecorator.ALL.add(pageDecoratorImpl);
        
        HtmlPage page = new WebClient().goTo("configure");
        assertElementPresent(page, "hudson-pages-SystemConfigurationTestCase$PageDecoratorImpl");
        
        HtmlForm form = page.getFormByName("config");
        form.getInputByName("_.decoratorId").setValueAttribute("this_is_a_profile");
        form.submit((HtmlButton)last(form.getHtmlElementsByTagName("button")));
        assertEquals("The decorator field was incorrect", "this_is_a_profile", pageDecoratorImpl.getDecoratorId());
    }

    /**
     * PageDecorator for bug#2289
     */
    private static class PageDecoratorImpl extends PageDecorator {
        private String decoratorId;

        protected PageDecoratorImpl() {
            super(PageDecoratorImpl.class);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            decoratorId = json.getString("decoratorId");
            return true;
        }

        @Override
        public String getDisplayName() {
            return "PageDecoratorImpl";
        }
        
        public String getDecoratorId() {
            return decoratorId;
        }
    }
}