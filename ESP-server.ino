
#include <ESP8266WiFi.h>
 
const char* ssid = "Moje síť"; // Jméno wifi
const char* password = "baslik12"; // Heslo wifi
 #define RELAY 0 // relay connected to  GPIO0
WiFiServer server(80);
 
void setup() 
{
  Serial.begin(115200); // must be same baudrate with the Serial Monitor
 
  pinMode(RELAY,OUTPUT);
  digitalWrite(RELAY, LOW);
 
  // připojování 
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
 
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
 
  // Spouštění serveru
  server.begin();
  Serial.println("Server started");
 
  // Ip adressa na připojení
  Serial.print("Use this URL to connect: ");
  Serial.print("http://");
  Serial.print(WiFi.localIP());
  Serial.println("/");
 
}
 
void loop() 
{
  // Check if a client has connected
  WiFiClient client = server.available();
  if (!client) 
  {
    return;
  }
 
  // Wait until the client sends some data
  Serial.println("new client");
  while(!client.available())
  {
    delay(1);
  }

  // Read the first line of the request
  String request = client.readStringUntil('\r');
  Serial.println(request);
  client.flush();
 
  // Match the request
  int value = HIGH;
  if (request.indexOf("/Rozepnuto") != -1)  
  {
    Serial.println("Rozepnuto");
    digitalWrite(RELAY,HIGH);
    value = HIGH;
    delay (5000);
    Serial.println("Sepnuto");
    digitalWrite(RELAY,LOW);
    value = LOW;
    Serial.println("Rozepnuto");
  }

  
  //Display the HTML web page
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text/html");
  client.println(""); //  this is a must
  client.println("<!DOCTYPE HTML>");
  client.println("<html>");
  client.println("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
  client.println("<title>Ovladani zamku</title>");
  client.println("<style>html { font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}");
  client.println(".button { background-color: #195B6A; border: none; color: white; padding: 16px 40px;");
  client.println("text-decoration: none; font-size: 30px; margin: 2px; cursor: pointer;}");
  client.println(".button2 {background-color: #77878A;}</style>");
  client.println("</head>");
  client.println("<body>");
  client.println("<h1 align = center>Zamek na otisk prstu:</h1>");
 

  client.println("</h2>");
  client.println("<br>");
  client.println("<a href=\"/Sepnuto\"><button class=\"button\">Sepni</button></a>");
  client.println("</body>");
  client.println("</html>");
 
  delay(1);
  Serial.println("Client disonnected");
  Serial.println("");
}
