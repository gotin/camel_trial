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
    from("jetty:http://localhost:8080/test").process(new Processor(){
          public void process(Exchange exchange) throws Exception {
            String value = (String)exchange.getIn().getHeader("value");
            System.out.println(value);
            exchange.getOut().setBody("<h1>" + value + "</h1>");
          }
      });
  }
  public static void main(String[] args) throws Exception{
    CamelContext context = new DefaultCamelContext();
    context.addRoutes(new Test());
    ProducerTemplate template = context.createProducerTemplate();
    context.start();
    template.sendBody("direct:start", "hoge?");
    try{
      while(true){
        Thread.sleep(1);
      }
    } catch(Exception ex){
      ex.printStackTrace();
    } finally {
      context.stop();
    }
  }
}