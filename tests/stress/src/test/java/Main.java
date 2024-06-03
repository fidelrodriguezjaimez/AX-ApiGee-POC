import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import com.typesafe.config.Config;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.core.FeederBuilder.*;
import com.typesafe.config.ConfigFactory;
import io.gatling.javaapi.http.*;
import scala.annotation.implicitNotFound;
import io.gatling.core.Predef.*;
import io.gatling.http.Predef.*;
import scala.concurrent.duration.*;
import io.gatling.core.session.Session;
import java.util.HashMap;
import java.util.Map;

public class Main extends Simulation {

  //mvn gatling:test -Dgatling.simulationClass=Main

  Config appConfig = ConfigFactory.load("application");
  String baseUrl = appConfig.getString("api.baseUrl");
  String path = appConfig.getString("api.path");
  String sw = appConfig.getString("api.endPoints.sw");
  String headerContentType = appConfig.getString("api.headers.contentType");
  // Se obtiene las configuraciones de Oauht2
  String baseAuthUrl = appConfig.getString("api.oauth2.baseAuthUrl");
  String scope = appConfig.getString("api.oauth2.scope");
  String clientId = appConfig.getString("api.oauth2.clientId");
  String clientSecret = appConfig.getString("api.oauth2.clientSecret");
  String grantType = appConfig.getString("api.oauth2.grantType");
  String dateRequest = appConfig.getString("api.oauth2.headers.dateRequest");
  String latitude = appConfig.getString("api.oauth2.headers.latitude");
  String longitude = appConfig.getString("api.oauth2.headers.longitude");
  String transactionId = appConfig.getString("api.oauth2.headers.transactionId");
  // Se obtienenlas configuraciones del Archivo gatling.conf y se establecen las propiedades a leer
  Config gatlingConfig = ConfigFactory.load("gatling.conf");
  String proxyHostConfig = "gatling.http.proxy.host";
  String proxyPortConfig = "gatling.http.proxy.port";

  Batchable<String> feeder = csv("data/test-data-qa.csv").random();
  HttpProtocolBuilder httpProtocol;

  {
    if (gatlingConfig.hasPath(proxyPortConfig) && gatlingConfig.hasPath(proxyHostConfig)) {
      httpProtocol = http.baseUrl(baseUrl)
      .proxy(Proxy(
            gatlingConfig.getString(proxyHostConfig),
            gatlingConfig.getInt(proxyPortConfig))
      );
      } else {
            httpProtocol = http.baseUrl(baseUrl);
      }
  }

  private String token = "";
  private Map<String, String> headersLogin = new HashMap<String, String>() {{
        put("X-Coppel-Date-Request", dateRequest);
        put("X-Coppel-Latitude", latitude);
        put("X-Coppel-Longitude", longitude);
        put("X-Coppel-TransactionId", transactionId);
  }};

  private ScenarioBuilder auth = scenario("GetToken")
  .exec(http("authenticate")
        .post(baseAuthUrl)
        .headers(headersLogin)
        .formParam("grant_type", grantType)
        .formParam("scope", scope)
        .formParam("client_id", clientId)
        .formParam("client_secret", clientSecret)
        .check(status().is(200))
        .check(jsonPath("$.access_token").saveAs("access")))
        .exitHereIfFailed()
  .exec(session -> {
        token = session.getString("access");
        return session.set("access", token);
  });

  private Map<String, String> headers = new HashMap<String, String>() {{
        put("Content-Type", headerContentType);
        put("Authorization", "Bearer #{access}");
  }};

  private String value;
  private String valueHealth;

  ChainBuilder getCharacters = feed(feeder)
  .exec(session -> session.set("access", token))
  .exec(http("Get_Characters")
  .get(path + sw + "#{value}")
  .headers(headers)
  .check(        
        status().is(200),
        status().not(500),
        status().not(404)
        ));

  ChainBuilder getHealthCheck = feed(feeder)
  .exec(session -> session.set("access", token))
  .exec(http("Get_HealthCheck")
  .get(path + sw + "#{valuehealth}")
  .headers(headers)
  .check(
        status().is(200),
        status().not(500),
        status().not(404)
  ));
  
  private ScenarioBuilder scn = scenario("SW test")
  .pause(3)
  .exec(getCharacters, getHealthCheck);

  {
        setUp(
                auth.injectOpen(constantUsersPerSec(1).during(1)),
                scn.injectOpen(rampUsers(100).during(30))
        ).protocols(httpProtocol);
  }
  
}