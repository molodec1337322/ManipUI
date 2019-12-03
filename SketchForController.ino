#include <Servo.h>

#define PIN_MOTOR              9
#define PIN_MOTOR_POWER        8

#define PIN_PNEVMO_ELBOW       10
#define PIN_PNEVMO_ELBOW_POWER 5

#define PIN_PNEVMO_CLAW        11
#define PIN_PNEVMO_CLAW_POWER  6

class Platform
{
  private:
    int pos;
    bool isTurningRight;
    bool isTurningLeft;
    Servo servoEngine;
    short servoPin;
  //пределы
    const int leftLimit = 270;
    const int rightLimit = -90;

  public:
    Platform(Servo engine, short pin)
    {
      pos = 90;
      isTurningRight = false;
      isTurningLeft = false; 
      servoEngine = engine;
      servoPin = pin;
    }
  
    //включает у устанавливает двигатель в начальном положении 
    void start()
    {
      servoEngine.attach(servoPin);
      servoEngine.write(pos);
    }
  
    //поворачивает стол направо
    void turnRight()
    {
      isTurningLeft = false;
      isTurningRight = true;
    }
  
    //поворачивет стол налево
    void turnLeft()
    {
      isTurningRight = false;
      isTurningRight = true;
    }
  
    //останавливает платформу
    void stopPlatform()
    {
      isTurningRight = false;
      isTurningLeft = false;
    }
  
    void turn()
    {
      if(isTurningRight && pos > rightLimit)
      {
        servoEngine.write(pos--);
      }
      if(isTurningLeft && pos < leftLimit)
      {
        servoEngine.write(pos++);
      }
    }
};

class Pnevmo
{
  private:
    bool isOpen;
    short pnevmoPin;
    short pnevmoPower;

   public:
    Pnevmo(short controlPin, short powerPin)
    {
      isOpen = true;
      pnevmoPin = controlPin;
      pnevmoPower = powerPin;
    }

    void start()
    {
      pinMode(pnevmoPin, OUTPUT);
      pinMode(pnevmoPower, OUTPUT);
      digitalWrite(pnevmoPower, HIGH);
    }

    void spread()
    {
      isOpen = true;
    }

    void tight()
    {
      isOpen = false;
    }

    void work()
    {
      if(!isOpen)
      {
        digitalWrite(pnevmoPin, HIGH);
      }
      else
      {
        digitalWrite(pnevmoPin, LOW);
      }
    }
};

Servo engine;
Platform platform(engine, PIN_MOTOR_POWER);
Pnevmo elbow(PIN_PNEVMO_ELBOW, PIN_PNEVMO_ELBOW_POWER);
Pnevmo claw(PIN_PNEVMO_CLAW, PIN_PNEVMO_CLAW_POWER);

char inputSignal;

void setup() {
  Serial1.begin(9600);
  pinMode(PIN_MOTOR, OUTPUT);
  platform.start();
  elbow.start();
  claw.start();
}

void loop() {
  if(Serial1.available() > 0)
  {
    inputSignal = (char) Serial1.read();
  }
  if(inputSignal == '1')
  {
    elbow.spread();
  }
  if(inputSignal == '2')
  {
    elbow.tight();
  }
  if(inputSignal == '5')
  {
    platform.turnLeft();
  }
  if(inputSignal == '6')
  {
    platform.turnRight();
  }
  if(inputSignal == '0');
  {
    platform.stopPlatform();
  }
  if(inputSignal == '7')
  {
    claw.spread();
  }
  if(inputSignal == '8')
  {
    claw.tight();
  }
  platform.turn();
  elbow.work();
  claw.work();
}
