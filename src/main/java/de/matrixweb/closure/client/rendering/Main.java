package de.matrixweb.closure.client.rendering;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Joiner;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;

/**
 * @author markusw
 */
@SuppressWarnings("javadoc")
public class Main {

  private static SoyJsSrcOptions jsSrcOptions = new SoyJsSrcOptions();
  static {
    // jsSrcOptions.setCodeStyle(codeStyle);
    jsSrcOptions.setShouldDeclareTopLevelNamespaces(true);
    jsSrcOptions.setShouldGenerateGoogMsgDefs(false);
    jsSrcOptions.setShouldGenerateJsdoc(true);
    jsSrcOptions.setShouldProvideRequireJsFunctions(false);
    jsSrcOptions.setShouldProvideRequireSoyNamespaces(false);
    jsSrcOptions.setShouldProvideBothSoyNamespacesAndJsFunctions(false);
    jsSrcOptions.setUseGoogIsRtlForBidiGlobalDir(false);
  }

  public static void main(final String[] args) throws Exception {
    String temp = ".";
    if (args.length > 0) {
      temp = args[0];
    }
    final File basePath = new File(temp);

    final DefaultCamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("jetty:http://0.0.0.0:8282/?matchOnUriPrefix=true").process(
            new Processor() {
              public void process(final Exchange exchange) throws Exception {
                final HttpServletRequest request = exchange.getIn().getBody(
                    HttpServletRequest.class);
                final String templatePath = request.getRequestURI().replaceAll(
                    "\\..*$", ".soy");

                final File soyFile = new File(basePath, templatePath);
                if (soyFile.exists()) {
                  exchange.getOut().setBody(
                      Joiner.on('\n').join(
                          new SoyFileSet.Builder().add(soyFile).build()
                              .compileToJsSrc(jsSrcOptions, null)));
                } else {
                  final File htmlFile = new File(basePath, request
                      .getRequestURI());
                  if (htmlFile.exists()) {
                    exchange.getOut().setBody(
                        FileUtils.readFileToString(htmlFile));
                  } else {
                    exchange.getIn().getBody(HttpServletResponse.class)
                        .sendError(HttpServletResponse.SC_NOT_FOUND);
                  }
                }
              }
            });
      }
    });
    camelContext.start();
  }

}
