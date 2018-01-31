#define LEFT_AHEAD 10
#define LEFT_BACK 9 
#define RIGHT_AHEAD 13 
#define RIGHT_BACK 12 

String inString=""; 
float RunInit = 3.0; 
float RUN_LEFT = 3.0;
float RUN_RIGHT = 3.0;
int TURN = 60;
int RUN1 = 170;
int RUN2 = 170;
int CYC = 5;
int duty=7850;
float calibrateNum = 0; 
void stopBack();
void turnLeft(); 
void turnRight(); 
void goAhead(); 
void park(); 
void goBack();  

void setup()
{
  Serial.begin(9600); 
  pinMode(LEFT_AHEAD,OUTPUT); 
  pinMode(LEFT_BACK, OUTPUT); 
  pinMode(RIGHT_AHEAD, OUTPUT); 
  pinMode(RIGHT_BACK, OUTPUT); 
  digitalWrite(LEFT_AHEAD, LOW); 
  digitalWrite(LEFT_BACK, LOW); 
  digitalWrite(RIGHT_AHEAD, LOW); 
  digitalWrite(RIGHT_BACK, LOW);
} 
char incomingByte = ' ';
void loop(){
  while(Serial.available()>0)
  {
    inString += char(Serial.read());
    delay(2);
  }
  if(inString.length()>0)
  {
    check();
    inString = "";
  }
} 



void check()
{
  if('0'<=inString[0] && inString[0]<='9') {accelerate();return;}
  switch(inString[0]){
    case 'f': 
    {
      Serial.println("Go Ahead!");
      goAhead();
      break; 
    }
    case 'l': 
    {
      Serial.println("TURN LEFT"); 
      turnLeft();
      break;
    }
    case 'r': 
    {
      Serial.println("TURN RIGHT"); 
      turnRight();
      break;
    }
    case 'b': 
    {
      Serial.println("GO BACK"); 
      goBack();
      break;
    }
    case 's': 
    {
      Serial.println("PARK"); 
      park();
      break;
    }
    case 'g': 
    {
      Serial.println("Gravity Go!"); 
      g(); 
      break;
    }
    case 'm': 
    {
      Serial.println("Turn Directly Left!"); 
      ml(); 
      break;
    }
    case 'n': 
    {
      Serial.println("Turn Directly Right!");
      nr(); 
      break;
    }
  }
}

void goAhead(){
  stopBack(); 
  analogWrite(LEFT_AHEAD,RUN1); 
  analogWrite(RIGHT_AHEAD,RUN2); 
} 
void turnLeft(){
  stopBack(); 
  analogWrite(RIGHT_AHEAD,RUN2); 
  digitalWrite(LEFT_AHEAD,LOW); 
  analogWrite(LEFT_BACK,RUN1); 
}
void turnRight(){
  stopBack(); 
  analogWrite(LEFT_AHEAD,RUN1); 
  digitalWrite(RIGHT_AHEAD,LOW); 
  analogWrite(RIGHT_BACK,RUN1); 
} 
void park(){
  stopBack(); 
  digitalWrite(LEFT_AHEAD, LOW); 
  digitalWrite(RIGHT_AHEAD, LOW); 
} 
void goBack(){
  digitalWrite(LEFT_AHEAD,LOW); 
  digitalWrite(RIGHT_AHEAD,LOW); 
  analogWrite(LEFT_BACK,RUN1); 
  analogWrite(RIGHT_BACK,RUN2);
}
void stopBack(){ 
  digitalWrite(LEFT_BACK, LOW); 
  digitalWrite(RIGHT_BACK, LOW);
}

void g()
{
  int a = 140, b = 140, c = 1;
  switch(inString[2]){
    case '1': c = 1; break;
    case '2': c = 2; break;
    case '3': c = 3; break;
  }
  a *= c;
  b *= c;
  switch(inString[3]){
    case 'n': break;
    case 'l': a = 0; break;
    case 'r': b = 0; break;
  }
  
  if(inString[1] == 'u')
  {
    Serial.println(a,DEC);
    //goAhead();
    stopBack();
    analogWrite(LEFT_AHEAD,a); 
    analogWrite(RIGHT_AHEAD,b);  
  }
  else
  {
    digitalWrite(LEFT_AHEAD,LOW); 
    digitalWrite(RIGHT_AHEAD,LOW);
    analogWrite(LEFT_BACK,a); 
    analogWrite(RIGHT_BACK,b);
  }
}

void ml(){
  digitalWrite(LEFT_BACK, LOW); 
  digitalWrite(RIGHT_BACK, LOW);
  digitalWrite(LEFT_AHEAD, LOW); 
  analogWrite(RIGHT_AHEAD, RUN2);
}

void nr()
{
  digitalWrite(LEFT_BACK, LOW); 
  digitalWrite(RIGHT_BACK, LOW);
  analogWrite(LEFT_AHEAD, RUN1); 
  digitalWrite(RIGHT_AHEAD, LOW);
}

void accelerate(){
  int a = 0,i=0,b=0;
  for(i=0;i<inString.length();i++){
    a = a*10+inString[i]-'0';
  }
  stopBack(); 
  analogWrite(LEFT_AHEAD,a); 
  analogWrite(RIGHT_AHEAD,a); 
}
