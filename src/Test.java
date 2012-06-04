import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

public class Test extends RouteBuilder{
  public void configure(){
    from("direct:start").process(new Processor(){
        public void process(Exchange exchange) throws Exception {
          System.out.println(exchange.getIn().getBody());
          exchange.getIn().setBody("this is a test");
        }
      });
    from("file:///Users/go/dev/camel/work/inbox?noop=true")
      .process(new Processor(){
          public void process(Exchange exchange) throws Exception {
            exchange.setProperty("CamelBatchSize", 10);
            System.out.println(exchange.getIn().getHeader("CamelFileName"));
          }
        })
      .to("file:///Users/go/dev/camel/work/outbox?autoCreate=true");
  }
  public static void main(String[] args) throws Exception{
    CamelContext context = new DefaultCamelContext();
    context.addRoutes(new Test());
    ProducerTemplate template = context.createProducerTemplate();
    context.start();
    template.sendBody("direct:start", "hoge?");
    context.stop();
  }
}